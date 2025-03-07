package com.ugustavob.finsuppapi.useCases.transaction;

import com.ugustavob.finsuppapi.dto.transactions.CreateTransactionRequestDTO;
import com.ugustavob.finsuppapi.entities.TransactionEntityFinder;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import com.ugustavob.finsuppapi.entities.transaction.TransactionType;
import com.ugustavob.finsuppapi.exception.TransactionNotFoundException;
import com.ugustavob.finsuppapi.repositories.TransactionRepository;
import com.ugustavob.finsuppapi.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateTransactionUseCase {
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    public TransactionEntity execute(Integer id, CreateTransactionRequestDTO createTransactionRequestDTO) {
        var transaction = transactionRepository.findById(id).orElseThrow(TransactionNotFoundException::new);

        TransactionEntityFinder transactionEntityFinder = transactionService.getAndValidateTransactionEntities(createTransactionRequestDTO);

        System.out.println("Conta origem: " + transactionEntityFinder.getAccount().getBalance());
        System.out.println("Conta destino: " + transactionEntityFinder.getRecipientAccount().getBalance());

        transactionEntityFinder = transactionService.revertAccountBalance(transaction, transactionEntityFinder);

        System.out.println("Conta origem pós reversão: " + transactionEntityFinder.getAccount().getBalance());
        System.out.println("Conta destino pós reversão: " + transactionEntityFinder.getRecipientAccount().getBalance());

        transaction.setDescription(createTransactionRequestDTO.description());
        transaction.setAmount(createTransactionRequestDTO.amount());
        transaction.setTransactionType(createTransactionRequestDTO.type());
        transaction.setTransactionDate(createTransactionRequestDTO.transactionDate());
        transaction.setCategory(transactionEntityFinder.getCategory());
        transaction.setAccount(transactionEntityFinder.getAccount());
        transaction.setRecipientAccount(transactionEntityFinder.getRecipientAccount());

        TransactionType type = createTransactionRequestDTO.type();

        transactionEntityFinder = transactionService.updateAccountBalance(transaction, transactionEntityFinder);

        System.out.println("Conta origem pós aplicação nova: " + transactionEntityFinder.getAccount().getBalance());
        System.out.println("Conta destino pós aplicação nova: " + transactionEntityFinder.getRecipientAccount().getBalance());

        transactionService.saveAccounts(transactionEntityFinder, type);

        return transactionRepository.save(transaction);
    }
}
