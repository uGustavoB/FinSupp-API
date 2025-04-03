package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.dto.transactions.CreateTransactionRequestDTO;
import com.ugustavob.finsuppapi.dto.transactions.TransactionResponseDTO;
import com.ugustavob.finsuppapi.entities.account.AccountType;
import com.ugustavob.finsuppapi.entities.bill.BillEntity;
import com.ugustavob.finsuppapi.entities.card.CardEntity;
import com.ugustavob.finsuppapi.entities.card.CardType;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntityFinder;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.categories.CategoryEntity;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import com.ugustavob.finsuppapi.entities.transaction.TransactionType;
import com.ugustavob.finsuppapi.exception.AccountNotFoundException;
import com.ugustavob.finsuppapi.exception.CardNotFoundException;
import com.ugustavob.finsuppapi.exception.CategoryNotFoundException;
import com.ugustavob.finsuppapi.exception.TransactionNotFoundException;
import com.ugustavob.finsuppapi.repositories.AccountRepository;
import com.ugustavob.finsuppapi.repositories.CardRepository;
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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final BillService billService;
    private final CardRepository cardRepository;

    public TransactionEntity getTransactionById(int id) {
        return transactionRepository.findById(id).orElseThrow(TransactionNotFoundException::new);
    }

    public TransactionEntityFinder getAndValidateTransactionEntities(CardEntity card,
                                                                     AccountEntity recipientAccount) {
        return new TransactionEntityFinder(card, recipientAccount);
    }

    public TransactionEntityFinder getAndValidateTransactionEntities(CreateTransactionRequestDTO createTransactionRequestDTO) {
        CardEntity card = cardRepository.findById(createTransactionRequestDTO.cardId())
                .orElseThrow(CardNotFoundException::new);

        AccountEntity recipientAccount = null;

        if (createTransactionRequestDTO.recipientAccountId() != null) {
            recipientAccount = accountRepository.findById(createTransactionRequestDTO.recipientAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("Recipient account not found"));

            if (createTransactionRequestDTO.recipientAccountId().equals(card.getAccount().getId())) {
                throw new IllegalArgumentException("You can't transfer to the same account");
            }
        }

        int installments = createTransactionRequestDTO.installments() == null ? 1 :
                createTransactionRequestDTO.installments();

        if (card.getType() == CardType.DEBIT && installments >= 2) {
            throw new IllegalArgumentException("You can't create installments with a debit card");
        }

        CategoryEntity category = categoryRepository.findById(createTransactionRequestDTO.category())
                .orElseThrow(CategoryNotFoundException::new);

        return new TransactionEntityFinder(card, recipientAccount, category);
    }

    public TransactionEntity getTransactionEntity(CreateTransactionRequestDTO createTransactionRequestDTO,
                                                  TransactionEntityFinder transactionEntityFinder) {
        TransactionEntity newTransaction = new TransactionEntity();
        newTransaction.setDescription(StringFormatUtil.toTitleCase(createTransactionRequestDTO.description()));
        newTransaction.setAmount(createTransactionRequestDTO.amount());
        newTransaction.setTransactionDate(createTransactionRequestDTO.transactionDate());
        newTransaction.setTransactionType(createTransactionRequestDTO.type());
        newTransaction.setCategory(transactionEntityFinder.getCategory());
        newTransaction.setCard(transactionEntityFinder.getCard());
        newTransaction.setRecipientAccount(transactionEntityFinder.getRecipientAccount());

        if (createTransactionRequestDTO.installments() != null) {
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
                transaction.getTransactionDate(),
                transaction.getTransactionType(),
                transaction.getCategory().getId(),
                transaction.getCard().getId(),
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
                transactionEntityFinder.getCard().getAccount().setBalance(transactionEntityFinder.getCard().getAccount().getBalance() + transaction.getAmount());
                break;
            case WITHDRAW:
                if (transactionEntityFinder.getCard().getType() == CardType.DEBIT) {
                    transactionEntityFinder.getCard().getAccount().setBalance(transactionEntityFinder.getCard().getAccount().getBalance() - transaction.getAmount());
                }
                break;
            case TRANSFER:
                if (transactionEntityFinder.getCard().getType() == CardType.DEBIT) {
                    transactionEntityFinder.getCard().getAccount().setBalance(transactionEntityFinder.getCard().getAccount().getBalance() - transaction.getAmount());
                    transactionEntityFinder.getRecipientAccount().setBalance(transactionEntityFinder.getRecipientAccount().getBalance() + transaction.getAmount());
                }
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
                transactionEntityFinder.getCard().getAccount().setBalance(transactionEntityFinder.getCard().getAccount().getBalance() - transaction.getAmount());
                break;
            case WITHDRAW:
                transactionEntityFinder.getCard().getAccount().setBalance(transactionEntityFinder.getCard().getAccount().getBalance() + transaction.getAmount());
                break;
            case TRANSFER:
                if (transactionEntityFinder.getCard().getType() == CardType.DEBIT) {
                    transactionEntityFinder.getCard().getAccount().setBalance(transactionEntityFinder.getCard().getAccount().getBalance() + transaction.getAmount());
                    if (transaction.getRecipientAccount() != null) {
                        transactionEntityFinder.setRecipientAccount(transaction.getRecipientAccount());
                        transactionEntityFinder.getRecipientAccount().setBalance(transactionEntityFinder.getRecipientAccount().getBalance() - transaction.getAmount());
                    }
                } else {
//                  Gustavo - Consertar depois, para adicionar o valor na fatura
                    throw new IllegalArgumentException("You can't transfer with a credit card");
                }
                break;
        }

        return transactionEntityFinder;
    }

    @Transactional
    public BillEntity billPaymentTransaction(BillEntity bill, AccountEntity account) {
        CategoryEntity category = categoryRepository.findByDescription("Bill Payments")
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        CardEntity card =
                cardRepository.findDebitCardByAccountId(account.getId()).orElseThrow(() -> new CardNotFoundException("Debit " +
                        "Card not found"));

        TransactionEntity transaction = new TransactionEntity();
        transaction.setDescription("Payment of the bill: " + bill.getStartDate().getMonthValue() + "/" + bill.getStartDate().getYear());
        transaction.setAmount(bill.getTotalAmount());
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setCard(card);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setCategory(category);
        transaction.setRecipientAccount(null);
        transaction.setInstallments(1);

        transactionRepository.save(transaction);

        card.getAccount().setBalance(account.getBalance() - bill.getTotalAmount());
        accountRepository.save(card.getAccount());

        return billService.payBill(bill);
    }

    public void saveAccounts(TransactionEntityFinder transactionEntityFinder, TransactionType type) {
        accountRepository.save(transactionEntityFinder.getCard().getAccount());

        if (type == TransactionType.TRANSFER) {
            accountRepository.save(transactionEntityFinder.getRecipientAccount());
        }
    }
}
