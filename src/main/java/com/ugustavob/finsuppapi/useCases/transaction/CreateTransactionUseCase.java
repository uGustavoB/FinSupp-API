package com.ugustavob.finsuppapi.useCases.transaction;

import com.ugustavob.finsuppapi.dto.transactions.CreateTransactionRequestDTO;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntityFinder;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import com.ugustavob.finsuppapi.entities.transaction.TransactionType;
import com.ugustavob.finsuppapi.repositories.TransactionRepository;
import com.ugustavob.finsuppapi.services.BillService;
import com.ugustavob.finsuppapi.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateTransactionUseCase {
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;
    private final BillService billService;

    public TransactionEntity execute(@Valid CreateTransactionRequestDTO createTransactionRequestDTO) {
        TransactionEntityFinder transactionEntityFinder = transactionService.getAndValidateTransactionEntities(createTransactionRequestDTO);

        TransactionEntity newTransaction = transactionService.getTransactionEntity(createTransactionRequestDTO,
                transactionEntityFinder);

        TransactionType type = createTransactionRequestDTO.type();
        Double accountBalance = transactionEntityFinder.getCard().getAccount().getBalance();
        Double transactionAmount = createTransactionRequestDTO.amount();

        if (type == TransactionType.TRANSFER && accountBalance.compareTo(transactionAmount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

       transactionEntityFinder = transactionService.updateAccountBalance(newTransaction, transactionEntityFinder);
       transactionService.saveAccounts(transactionEntityFinder, type);

        TransactionEntity transaction =  transactionRepository.save(newTransaction);

        billService.addTransactionToBill(transaction);

        return transaction;
    }
}
