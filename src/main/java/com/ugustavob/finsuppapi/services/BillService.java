package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.dto.bills.BillFilterDTO;
import com.ugustavob.finsuppapi.dto.bills.BillItemResponseDTO;
import com.ugustavob.finsuppapi.dto.bills.BillResponseDTO;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.bill.BillEntity;
import com.ugustavob.finsuppapi.entities.bill.BillItemEntity;
import com.ugustavob.finsuppapi.entities.bill.BillStatus;
import com.ugustavob.finsuppapi.entities.card.CardEntity;
import com.ugustavob.finsuppapi.entities.card.CardType;
import com.ugustavob.finsuppapi.entities.subscription.SubscriptionEntity;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import com.ugustavob.finsuppapi.exception.BillNotFoundException;
import com.ugustavob.finsuppapi.repositories.BillItemRepository;
import com.ugustavob.finsuppapi.repositories.BillRepository;
import com.ugustavob.finsuppapi.specifications.BillSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

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
                bill.getCard().getAccount().getId(),
                bill.getStartDate(),
                bill.getEndDate(),
                bill.getDueDate()
        );
    }

    public BillItemResponseDTO billItemEntityToResponseDto(BillItemEntity billItem) {
        return new BillItemResponseDTO(
                billItem.getId(),
                billItem.getTransaction() != null ? billItem.getTransaction().getDescription() :
                        billItem.getSubscription().getDescription() + " Subscription",
                billItem.getAmount(),
                billItem.getInstallmentNumber(),
                billItem.getBill().getId(),
                billItem.getTransaction() != null ? billItem.getTransaction().getId() : null,
                billItem.getSubscription() != null ? billItem.getSubscription().getId() : null
        );
    }

    public BillEntity findOrCreateBill(AccountEntity account, CardEntity card, LocalDate transactionDate) {
        int closingDay = account.getClosingDay();
        LocalDate startDate = transactionDate.withDayOfMonth(closingDay).plusDays(1);
        LocalDate endDate = startDate.plusMonths(1).withDayOfMonth(closingDay);
        LocalDate dueDate = endDate.withDayOfMonth(account.getPaymentDueDay());

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
        if (transaction.getCard().getType() == CardType.CREDIT) {
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
    public void addSubscriptionToBill(SubscriptionEntity subscription) {
        if (subscription == null || subscription.getCard() == null || subscription.getCard().getAccount() == null) {
            throw new IllegalArgumentException("Subscription, card or account cannot be null");
        }

        CardEntity card = subscription.getCard();
        AccountEntity account = card.getAccount();

        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(account.getClosingDay()).plusDays(1);

        int totalInstallments = switch (subscription.getInterval()) {
            case MONTHLY -> 1;
            case QUARTERLY -> 3;
            case SEMI_ANNUAL -> 6;
            case YEARLY -> 12;
        };

        addSubscriptionInstallments(subscription, startDate, totalInstallments);
    }

    private void addSubscriptionInstallments(
            SubscriptionEntity subscription,
            LocalDate startDate,
            int totalInstallments
    ) {
        List<BillItemEntity> itemsToSave = new ArrayList<>();

        for (int installmentNumber = 1; installmentNumber <= totalInstallments; installmentNumber++) {
            BillEntity bill = findOrCreateBill(
                    subscription.getCard().getAccount(),
                    subscription.getCard(),
                    startDate
            );

            if (bill.getStatus() != BillStatus.OPEN) {
                startDate = startDate.plusMonths(1);
                totalInstallments += 1;
                continue;
            }

            BillItemEntity billItem = new BillItemEntity();
            billItem.setBill(bill);
            billItem.setAmount(subscription.getPrice());
            billItem.setInstallmentNumber(installmentNumber);
            billItem.setSubscription(subscription);

            itemsToSave.add(billItem);
            bill.setTotalAmount(bill.getTotalAmount() + subscription.getPrice());
            billRepository.save(bill);

            startDate = startDate.plusMonths(1);
        }

        billItemRepository.saveAll(itemsToSave);
    }

    @Transactional
    public void removeSubscriptionFromBill(SubscriptionEntity subscription) {
        List<BillItemEntity> bills = billItemRepository.findBySubscription(subscription);

        removeEntityFromBill(bills);
    }

    @Transactional
    public void revertTransactionBills(TransactionEntity transaction) {
        List<BillItemEntity> bills = billItemRepository.findByTransaction(transaction);

        removeEntityFromBill(bills);
    }

    private void removeEntityFromBill(List<BillItemEntity> bills) {
        if (bills.isEmpty()) {
            return;
        }

        for (BillItemEntity billItem : new ArrayList<>(bills)) {
            BillEntity bill = billItem.getBill();

            billItemRepository.delete(billItem);

            bill.setTotalAmount(bill.getTotalAmount() - billItem.getAmount());

            if (bill.getTotalAmount() == 0) {
                billRepository.delete(bill);
            } else {
                if (bill.getDueDate().isBefore(LocalDate.now())) {
                    bill.setStatus(BillStatus.OVERDUE);
                } else {
                    bill.setStatus(BillStatus.OPEN);
                }

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

    @Transactional
    public void revertPayment(BillEntity bill) {
        if (bill.getStatus() != BillStatus.PAID) {
            throw new IllegalStateException("Bill is not paid");
        }

        if (bill.getDueDate().isBefore(LocalDate.now())) {
            bill.setStatus(BillStatus.OVERDUE);
        } else {
            bill.setStatus(BillStatus.OPEN);
        }

        billRepository.save(bill);
    }

    public List<BillEntity> findAll(BillFilterDTO filter) {
        Specification<BillEntity> specification = BillSpecification.filter(filter);
        return billRepository.findAll(specification);
    }

    public Page<BillItemResponseDTO> findBillItemsByBill(BillEntity bill, UUID userId, int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);

        if (!bill.getCard().getAccount().getUser().getId().equals(userId)) {
            throw new BillNotFoundException("Bill not found.");
        }

        Page<BillItemEntity> billItems = billItemRepository.findByBillId(bill.getId(), pageable);
        return billItems.map(this::billItemEntityToResponseDto);
    }

    public Optional<BillEntity> findByMonthAndYearAndUser(int month, int year, UUID userId) {
        return billRepository.findByStartDateMonthAndStartDateYearAndUserId(month, year, userId);
    }
}
