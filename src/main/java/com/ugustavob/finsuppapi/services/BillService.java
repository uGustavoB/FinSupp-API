package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.dto.bills.BillFilterDTO;
import com.ugustavob.finsuppapi.dto.bills.BillItemResponseDTO;
import com.ugustavob.finsuppapi.dto.bills.BillResponseDTO;
import com.ugustavob.finsuppapi.dto.bills.BillPayRequestDTO;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.account.AccountType;
import com.ugustavob.finsuppapi.entities.bill.BillEntity;
import com.ugustavob.finsuppapi.entities.bill.BillItemEntity;
import com.ugustavob.finsuppapi.entities.card.CardEntity;
import com.ugustavob.finsuppapi.entities.card.CardType;
import com.ugustavob.finsuppapi.specifications.BillSpecification;
import com.ugustavob.finsuppapi.entities.bill.BillStatus;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import com.ugustavob.finsuppapi.repositories.BillItemRepository;
import com.ugustavob.finsuppapi.repositories.BillRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillService {
    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;

    public BillResponseDTO entityToResponseDto(BillEntity bill) {
        System.out.println("Chegou aqui");
        return new BillResponseDTO(
                bill.getId(),
                bill.getStatus(),
                bill.getTotalAmount(),
                bill.getCard().getAccount().getId(),
                bill.getStartDate(),
                bill.getEndDate(),
                bill.getDueDate()
        );
    }

    public BillItemResponseDTO billItemEntityToResponseDto(BillItemEntity billItem) {
        return new BillItemResponseDTO(
                billItem.getId(),
                billItem.getAmount(),
                billItem.getInstallmentNumber(),
                billItem.getBill().getId(),
                billItem.getTransaction().getId()
        );
    }

    public BillEntity findOrCreateBill(AccountEntity account, CardEntity card, LocalDate transactionDate) {
        int closingDay = account.getClosingDay();
        LocalDate startDate = transactionDate.withDayOfMonth(closingDay).plusDays(1);
        LocalDate endDate = startDate.plusMonths(1).withDayOfMonth(closingDay);
        LocalDate dueDate = endDate.withDayOfMonth(10);

        BillEntity bill = billRepository.findByAccountAndDateRange(account, startDate, endDate);

        if (bill == null) {
            bill = new BillEntity();
            bill.setCard(card);
            bill.setStartDate(startDate);
            bill.setEndDate(endDate);
            bill.setDueDate(dueDate);
            bill.setTotalAmount(0.0);
            bill.setStatus(BillStatus.OPEN);

            billRepository.save(bill);
        }

        return bill;
    }

    @Transactional
    public void addTransactionToBill(TransactionEntity transaction) {
        if (transaction.isAddToBill() && transaction.getCard().getType() == CardType.CREDIT) {
            AccountEntity account = transaction.getCard().getAccount();
            LocalDate dueDate = transaction.getTransactionDate();

            int installments = transaction.getInstallments();
            double installmentValue = transaction.getAmount() / installments;

            for (int i = 0; i < installments; i++) {
                LocalDate installmentDate = dueDate.plusMonths(i);

                BillEntity bill = findOrCreateBill(account, transaction.getCard(), installmentDate);

                if (bill.getStatus() != BillStatus.OPEN) {
                    throw new IllegalStateException("Bill is not open");
                }

                BillItemEntity billItem = new BillItemEntity();
                billItem.setBill(bill);
                billItem.setTransaction(transaction);
                billItem.setInstallmentNumber(i + 1);
                billItem.setAmount(installmentValue);

                bill.setTotalAmount(bill.getTotalAmount() + installmentValue);

                billItemRepository.save(billItem);
                billRepository.save(bill);
            }
        }
    }

    @Transactional
    public void revertTransactionBills(TransactionEntity transaction) {
        List<BillItemEntity> bills = billItemRepository.findByTransaction(transaction);

        if (bills.isEmpty()) {
            return;
        }

        for (BillItemEntity billItem : new ArrayList<>(bills)) {
            BillEntity bill = billItem.getBill();
            if (bill.getStatus() != BillStatus.OPEN) {
                throw new IllegalStateException("Bill is not open");
            }

            billItemRepository.delete(billItem);

            bill.setTotalAmount(bill.getTotalAmount() - billItem.getAmount());

            if (bill.getTotalAmount() == 0) {
                billRepository.delete(bill);
            } else {
                billRepository.save(bill);
            }
        }
    }

    @Transactional
    public BillEntity payBill(BillEntity bill) {
        if (bill.getStatus() == BillStatus.PAID) {
            throw new IllegalStateException("Bill is already paid");
        }

        if (bill.getStatus() == BillStatus.CANCELED) {
            throw new IllegalStateException("Bill is canceled");
        }

        bill.setStatus(BillStatus.PAID);

        billRepository.save(bill);

        return bill;
    }

    public List<BillEntity> findAll(BillFilterDTO filter) {
        Specification<BillEntity> specification = BillSpecification.filter(filter);
        return billRepository.findAll(specification);
    }

    public Page<BillItemResponseDTO> findBillItemsByBill(BillEntity bill, int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<BillItemEntity> billItems = billItemRepository.findByBillId(bill.getId(), pageable);
        return billItems.map(this::billItemEntityToResponseDto);
    }
}
