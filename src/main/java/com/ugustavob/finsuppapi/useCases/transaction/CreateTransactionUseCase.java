package com.ugustavob.finsuppapi.useCases.transaction;

import com.ugustavob.finsuppapi.dto.transactions.CreateTransactionRequestDTO;
import com.ugustavob.finsuppapi.entities.TransactionEntityFinder;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
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

        return transactionRepository.save(newTransaction);
    }
}
