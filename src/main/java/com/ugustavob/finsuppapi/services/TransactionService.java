package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.dto.transactions.CreateTransactionRequestDTO;
import com.ugustavob.finsuppapi.dto.transactions.TransactionResponseDTO;
import com.ugustavob.finsuppapi.entities.account.AccountType;
import com.ugustavob.finsuppapi.entities.bill.BillEntity;
import com.ugustavob.finsuppapi.entities.bill.BillStatus;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntityFinder;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.categories.CategoryEntity;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import com.ugustavob.finsuppapi.entities.transaction.TransactionType;
import com.ugustavob.finsuppapi.exception.AccountNotFoundException;
import com.ugustavob.finsuppapi.exception.CategoryNotFoundException;
import com.ugustavob.finsuppapi.exception.TransactionNotFoundException;
import com.ugustavob.finsuppapi.repositories.AccountRepository;
import com.ugustavob.finsuppapi.repositories.CategoryRepository;
import com.ugustavob.finsuppapi.repositories.TransactionRepository;
import com.ugustavob.finsuppapi.utils.StringFormatUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final BillService billService;

    public TransactionEntity getTransactionById(int id) {
        return transactionRepository.findById(id).orElseThrow(TransactionNotFoundException::new);
    }

    public TransactionEntityFinder getAndValidateTransactionEntities(AccountEntity originAccount,
                                                                     AccountEntity recipientAccount) {
        return new TransactionEntityFinder(originAccount, recipientAccount);
    }

    public TransactionEntityFinder getAndValidateTransactionEntities(CreateTransactionRequestDTO createTransactionRequestDTO) {
        AccountEntity account = accountRepository.findById(createTransactionRequestDTO.accountUuid())
                .orElseThrow(AccountNotFoundException::new);

        AccountEntity recipientAccount = null;

        if (createTransactionRequestDTO.recipientAccountUuid() != null) {
            recipientAccount = accountRepository.findById(createTransactionRequestDTO.recipientAccountUuid())
                    .orElseThrow(() -> new AccountNotFoundException("Recipient account not found"));

            if (createTransactionRequestDTO.recipientAccountUuid().equals(account.getId())) {
                throw new IllegalArgumentException("You can't transfer to the same account");
            }
        }

        CategoryEntity category = categoryRepository.findById(createTransactionRequestDTO.category())
                .orElseThrow(CategoryNotFoundException::new);

        return new TransactionEntityFinder(account, recipientAccount, category);
    }

    public TransactionEntity getTransactionEntity(CreateTransactionRequestDTO createTransactionRequestDTO, TransactionEntityFinder transactionEntityFinder) {
        TransactionEntity newTransaction = new TransactionEntity();
        newTransaction.setDescription(StringFormatUtil.toTitleCase(createTransactionRequestDTO.description()));
        newTransaction.setAmount(createTransactionRequestDTO.amount());
        newTransaction.setTransactionDate(createTransactionRequestDTO.transactionDate());
        newTransaction.setTransactionType(createTransactionRequestDTO.type());
        newTransaction.setCategory(transactionEntityFinder.getCategory());
        newTransaction.setAccount(transactionEntityFinder.getAccount());
        newTransaction.setRecipientAccount(transactionEntityFinder.getRecipientAccount());
        newTransaction.setAddToBill(createTransactionRequestDTO.addToBill());

        if (createTransactionRequestDTO.addToBill() && createTransactionRequestDTO.installments() != null) {
            newTransaction.setInstallments(createTransactionRequestDTO.installments());
        }

        return newTransaction;
    }

    public Page<TransactionResponseDTO> getAllTransactionsFromUser(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionEntity> transactionsPage = transactionRepository.findByUserId(userId, pageable);

        return transactionsPage.map(this::entityToResponseDto);
    }

    public TransactionResponseDTO entityToResponseDto(TransactionEntity transaction) {
        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getInstallments(),
                transaction.isAddToBill(),
                transaction.getTransactionDate(),
                transaction.getTransactionType(),
                transaction.getCategory().getId(),
                transaction.getAccount().getId(),
                transaction.getRecipientAccount() != null ? transaction.getRecipientAccount().getId() : null
        );
    }

    @Transactional
    public TransactionEntityFinder updateAccountBalance(
            TransactionEntity transaction,
            TransactionEntityFinder transactionEntityFinder
    ) {

        switch (transaction.getTransactionType()) {
            case DEPOSIT:
                transactionEntityFinder.getAccount().setBalance(transactionEntityFinder.getAccount().getBalance() + transaction.getAmount());
                break;
            case WITHDRAW:
                transactionEntityFinder.getAccount().setBalance(transactionEntityFinder.getAccount().getBalance() - transaction.getAmount());
                break;
            case TRANSFER:
                transactionEntityFinder.getAccount().setBalance(transactionEntityFinder.getAccount().getBalance() - transaction.getAmount());
                transactionEntityFinder.getRecipientAccount().setBalance(transactionEntityFinder.getRecipientAccount().getBalance() + transaction.getAmount());
                break;
        }
        return transactionEntityFinder;
    }

    @Transactional
    public TransactionEntityFinder revertAccountBalance(
            TransactionEntity transaction,
            TransactionEntityFinder transactionEntityFinder
    ) {

        switch (transaction.getTransactionType()) {
            case DEPOSIT:
                transactionEntityFinder.getAccount().setBalance(transactionEntityFinder.getAccount().getBalance() - transaction.getAmount());
                break;
            case WITHDRAW:
                transactionEntityFinder.getAccount().setBalance(transactionEntityFinder.getAccount().getBalance() + transaction.getAmount());
                break;
            case TRANSFER:
                transactionEntityFinder.getAccount().setBalance(transactionEntityFinder.getAccount().getBalance() + transaction.getAmount());
                if (transaction.getRecipientAccount() != null) {
                    transactionEntityFinder.setRecipientAccount(transaction.getRecipientAccount());
                    transactionEntityFinder.getRecipientAccount().setBalance(transactionEntityFinder.getRecipientAccount().getBalance() - transaction.getAmount());
                }
                break;
        }

        return transactionEntityFinder;
    }

    @Transactional
    public BillEntity billPaymentTransaction(BillEntity bill, AccountEntity account) {
        if (account.getAccountType() == AccountType.CREDIT || account.getAccountType() == AccountType.SAVINGS) {
            throw new IllegalArgumentException("You can't pay a bill with this account type");
        }

        CategoryEntity category = categoryRepository.findByDescription("Bill Payments")
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        TransactionEntity transaction = new TransactionEntity();
        transaction.setDescription("Bill payment");
        transaction.setAmount(bill.getTotalAmount());
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setAccount(account);
        transaction.setAddToBill(false);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setCategory(category);
        transaction.setRecipientAccount(null);
        transaction.setInstallments(1);

        transactionRepository.save(transaction);

        account.setBalance(account.getBalance() - bill.getTotalAmount());
        accountRepository.save(transaction.getAccount());

        return billService.payBill(bill, transaction);
    }

    public void saveAccounts(TransactionEntityFinder transactionEntityFinder, TransactionType type) {
        accountRepository.save(transactionEntityFinder.getAccount());

        if (type == TransactionType.TRANSFER) {
            accountRepository.save(transactionEntityFinder.getRecipientAccount());
        }
    }
}
