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
import com.ugustavob.finsuppapi.entities.subscription.SubscriptionStatus;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import com.ugustavob.finsuppapi.exception.BillNotFoundException;
import com.ugustavob.finsuppapi.repositories.BillItemRepository;
import com.ugustavob.finsuppapi.repositories.BillRepository;
import com.ugustavob.finsuppapi.specifications.BillSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
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
                bill.getAccount() != null ? bill.getAccount().getId() : null,
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

    public BillEntity findOrCreateBill(AccountEntity account, LocalDate transactionDate) {
        int closingDay = account.getClosingDay();
        int dueDay = account.getPaymentDueDay();

        LocalDate closingDate;
        if (transactionDate.getDayOfMonth() < closingDay) {
            closingDate = transactionDate.withDayOfMonth(closingDay).minusMonths(1);
        } else {
            closingDate = transactionDate.withDayOfMonth(closingDay);
        }

        LocalDate startDate = closingDate.plusDays(1);
        LocalDate endDate = startDate.plusMonths(1).withDayOfMonth(closingDay);
        LocalDate dueDate = endDate.withDayOfMonth(dueDay);

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
        if (transaction.isAddToBill()) {
            AccountEntity account = transaction.getAccount();
            LocalDate dueDate = transaction.getTransactionDate();

            int installments = transaction.getInstallments();
            double installmentValue = transaction.getAmount() / installments;

            for (int i = 0; i < installments; i++) {
                LocalDate installmentDate = dueDate.plusMonths(i);

                BillEntity bill = findOrCreateBill(account, installmentDate);

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
        if (subscription == null || subscription.getAccount() == null) {
            throw new IllegalArgumentException("Subscription or account cannot be null");
        }

        AccountEntity account = subscription.getAccount();
        LocalDate now = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

        LocalDate referenceDate;
        if (now.getDayOfMonth() < account.getClosingDay()) {
            referenceDate = now.withDayOfMonth(account.getClosingDay()).minusMonths(1);
        } else {
            referenceDate = now.withDayOfMonth(account.getClosingDay());
        }
        LocalDate startDate = referenceDate.plusDays(1);

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
                    subscription.getAccount(),
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

    public void syncSubscriptionInBill(SubscriptionEntity subscription,
                                       SubscriptionStatus oldStatus,
                                       SubscriptionStatus newStatus) {
        if (oldStatus == SubscriptionStatus.ACTIVE && newStatus == SubscriptionStatus.INACTIVE) {
            removeSubscriptionFromBill(subscription);

        } else if (oldStatus == SubscriptionStatus.INACTIVE && newStatus == SubscriptionStatus.ACTIVE) {
            addSubscriptionToBill(subscription);

        } else if (oldStatus == SubscriptionStatus.ACTIVE && newStatus == SubscriptionStatus.ACTIVE) {
            updateSubscriptionInBill(subscription);
        }
    }

    private void updateSubscriptionInBill(SubscriptionEntity subscription) {
        BillItemEntity item = billItemRepository
                .findBySubscriptionId(subscription.getId())
                .orElse(null);

        if (item == null) {
            return;
        }

        BillEntity bill = item.getBill();
        bill.setTotalAmount(bill.getTotalAmount() - item.getAmount() + subscription.getPrice());

        item.setAmount(subscription.getPrice());

        billItemRepository.save(item);
        billRepository.save(bill);
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
                if (bill.getDueDate().isBefore(LocalDate.now(ZoneId.of("America/Sao_Paulo")))) {
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

        if (bill.getDueDate().isBefore(LocalDate.now(ZoneId.of("America/Sao_Paulo")))) {
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

        if (!bill.getAccount().getUser().getId().equals(userId)) {
            throw new BillNotFoundException("Bill not found.");
        }

        Page<BillItemEntity> billItems = billItemRepository.findByBillId(bill.getId(), pageable);
        return billItems.map(this::billItemEntityToResponseDto);
    }

    public Optional<BillEntity> findByMonthAndYearAndUser(int month, int year, UUID userId) {
        return billRepository.findByStartDateMonthAndStartDateYearAndUserId(month, year, userId);
    }
}
