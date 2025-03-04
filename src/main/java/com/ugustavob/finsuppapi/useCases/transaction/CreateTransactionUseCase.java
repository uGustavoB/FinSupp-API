package com.ugustavob.finsuppapi.useCases.transaction;

import com.ugustavob.finsuppapi.dto.transactions.CreateTransactionRequestDTO;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.categories.CategoryEntity;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import com.ugustavob.finsuppapi.exception.AccountNotFoundException;
import com.ugustavob.finsuppapi.exception.CategoryNotFoundException;
import com.ugustavob.finsuppapi.repositories.AccountRepository;
import com.ugustavob.finsuppapi.repositories.CategoryRepository;
import com.ugustavob.finsuppapi.repositories.TransactionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateTransactionUseCase {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public TransactionEntity execute(@Valid CreateTransactionRequestDTO createTransactionRequestDTO) {
        AccountEntity account = accountRepository.findById(createTransactionRequestDTO.accountUuid())
                .orElseThrow(AccountNotFoundException::new);

        AccountEntity recipientAccount = null;

        if (createTransactionRequestDTO.recipientAccountUuid() != null) {
            recipientAccount = accountRepository.findById(createTransactionRequestDTO.recipientAccountUuid())
                    .orElseThrow(AccountNotFoundException::new);
        }

        CategoryEntity category = categoryRepository.findById(createTransactionRequestDTO.category())
                .orElseThrow(CategoryNotFoundException::new);

        TransactionEntity newTransaction = new TransactionEntity();
        newTransaction.setDescription(createTransactionRequestDTO.description());
        newTransaction.setAmount(createTransactionRequestDTO.amount());
        newTransaction.setTransactionType(createTransactionRequestDTO.type());
        newTransaction.setCategory(category);
        newTransaction.setAccount(account);
        newTransaction.setRecipientAccount(recipientAccount);

        return transactionRepository.save(newTransaction);
    }
}
