package com.ugustavob.finsuppapi.useCases.transaction;

import com.ugustavob.finsuppapi.dto.transactions.CreateTransactionRequestDTO;
import com.ugustavob.finsuppapi.entities.TransactionEntityFinder;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import com.ugustavob.finsuppapi.entities.transaction.TransactionType;
import com.ugustavob.finsuppapi.repositories.AccountRepository;
import com.ugustavob.finsuppapi.repositories.TransactionRepository;
import com.ugustavob.finsuppapi.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateTransactionUseCase {
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    public TransactionEntity execute(@Valid CreateTransactionRequestDTO createTransactionRequestDTO) {
        TransactionEntityFinder transactionEntityFinder = transactionService.getAndValidateTransactionEntities(createTransactionRequestDTO);

        TransactionEntity newTransaction = transactionService.getTransactionEntity(createTransactionRequestDTO,
                transactionEntityFinder);

        TransactionType type = createTransactionRequestDTO.type();
        Double accountBalance = transactionEntityFinder.getAccount().getBalance();
        Double transactionAmount = createTransactionRequestDTO.amount();

        if (transactionAmount <=  0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        if (type == TransactionType.TRANSFER && accountBalance.compareTo(transactionAmount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

       transactionEntityFinder = transactionService.updateAccountBalance(newTransaction, transactionEntityFinder);
       transactionService.saveAccounts(transactionEntityFinder, type);

        return transactionRepository.save(newTransaction);
    }
}
