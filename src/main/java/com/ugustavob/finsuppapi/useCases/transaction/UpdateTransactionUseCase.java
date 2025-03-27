package com.ugustavob.finsuppapi.useCases.transaction;

import com.ugustavob.finsuppapi.dto.transactions.CreateTransactionRequestDTO;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntityFinder;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import com.ugustavob.finsuppapi.entities.transaction.TransactionType;
import com.ugustavob.finsuppapi.exception.TransactionNotFoundException;
import com.ugustavob.finsuppapi.repositories.TransactionRepository;
import com.ugustavob.finsuppapi.services.BillService;
import com.ugustavob.finsuppapi.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateTransactionUseCase {
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;
    private final BillService billService;

    public TransactionEntity execute(Integer id, CreateTransactionRequestDTO createTransactionRequestDTO) {
        var transaction = transactionRepository.findById(id).orElseThrow(TransactionNotFoundException::new);

        TransactionEntityFinder transactionEntityFinder = transactionService.getAndValidateTransactionEntities(createTransactionRequestDTO);

        transactionEntityFinder = transactionService.revertAccountBalance(transaction, transactionEntityFinder);
        billService.revertTransactionBills(transaction);

        transaction.setDescription(createTransactionRequestDTO.description());
        transaction.setAmount(createTransactionRequestDTO.amount());
        transaction.setTransactionType(createTransactionRequestDTO.type());
        transaction.setTransactionDate(createTransactionRequestDTO.transactionDate());
        transaction.setCategory(transactionEntityFinder.getCategory());
        transaction.setAccount(transactionEntityFinder.getAccount());
        transaction.setRecipientAccount(transactionEntityFinder.getRecipientAccount());
        transaction.setAddToBill(createTransactionRequestDTO.addToBill());

        if (createTransactionRequestDTO.addToBill() && createTransactionRequestDTO.installments() != null) {
            transaction.setInstallments(createTransactionRequestDTO.installments());
        } else {
            transaction.setInstallments(0);
        }

        TransactionType type = createTransactionRequestDTO.type();

        transactionEntityFinder = transactionService.updateAccountBalance(transaction, transactionEntityFinder);
        billService.addTransactionToBill(transaction);

        transactionService.saveAccounts(transactionEntityFinder, type);

        return transactionRepository.save(transaction);
    }
}
