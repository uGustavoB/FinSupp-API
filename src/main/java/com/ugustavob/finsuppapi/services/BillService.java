package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.dto.bills.BillFilterDTO;
import com.ugustavob.finsuppapi.dto.bills.BillResponseDTO;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.account.AccountType;
import com.ugustavob.finsuppapi.entities.bill.BillEntity;
import com.ugustavob.finsuppapi.entities.bill.BillItemEntity;
import com.ugustavob.finsuppapi.specifications.BillSpecification;
import com.ugustavob.finsuppapi.entities.bill.BillStatus;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import com.ugustavob.finsuppapi.repositories.BillItemRepository;
import com.ugustavob.finsuppapi.repositories.BillRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
        return new BillResponseDTO(
                bill.getId(),
                bill.getStatus(),
                bill.getTotalAmount(),
                bill.getAccount().getId(),
                bill.getStartDate(),
                bill.getEndDate(),
                bill.getDueDate()
        );
    }

    public BillEntity findOrCreateBill(AccountEntity account, LocalDate transactionDate) {
        int closingDay = account.getClosingDay();
        LocalDate startDate = transactionDate.withDayOfMonth(closingDay).plusDays(1);
        LocalDate endDate = startDate.plusMonths(1).withDayOfMonth(closingDay);
        LocalDate dueDate = endDate.withDayOfMonth(10);

        BillEntity bill = billRepository.findByAccountAndDateRange(account, startDate, endDate);

        if (bill == null) {
            bill = new BillEntity();
            bill.setAccount(account);
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
        if (transaction.isAddToBill() && transaction.getAccount().getAccountType() == AccountType.CREDIT) {
            AccountEntity account = transaction.getAccount();
            LocalDate dueDate = transaction.getTransactionDate();

            int installments = transaction.getInstallments();
            double installmentValue = transaction.getAmount() / installments;

            for (int i = 0; i < installments; i++) {
                LocalDate installmentDate = dueDate.plusMonths(i);

                BillEntity bill = findOrCreateBill(account, installmentDate);

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

        for (BillItemEntity billItem : new ArrayList<>(bills)) {
            BillEntity bill = billItem.getBill();

            billItemRepository.delete(billItem);

            bill.setTotalAmount(bill.getTotalAmount() - billItem.getAmount());

            if (bill.getTotalAmount() == 0) {
                billRepository.delete(bill);
            } else {
                billRepository.save(bill);
            }
        }
    }

    public List<BillEntity> findAll(BillFilterDTO filter) {
        Specification<BillEntity> specification = BillSpecification.filter(filter);
        return billRepository.findAll(specification);
    }
}
