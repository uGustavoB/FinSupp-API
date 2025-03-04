package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.dto.transactions.TransactionResponseDTO;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import com.ugustavob.finsuppapi.repositories.TransactionRepository;
import com.ugustavob.finsuppapi.exception.TransactionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionEntity getTransactionById(int id) {
        return transactionRepository.findById(id).orElseThrow(TransactionNotFoundException::new);
    }

    public Page<TransactionResponseDTO> getAllTransactions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionEntity> categoriesPage = transactionRepository.findAll(pageable);
        
        UUID recipientAccountUuid = null;

        return categoriesPage.map(transaction -> new TransactionResponseDTO(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getCreatedAt(),
                transaction.getTransactionType(),
                transaction.getCategory().getId(),
                transaction.getAccount().getId(),
                transaction.getRecipientAccount().getId()
        ));
    }

    public TransactionResponseDTO entityToResponseDto(TransactionEntity transaction) {
        UUID recipientAccountUuid = null;

        if (transaction.getRecipientAccount() != null) {
            recipientAccountUuid = transaction.getRecipientAccount().getId();
        }

        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getCreatedAt(),
                transaction.getTransactionType(),
                transaction.getCategory().getId(),
                transaction.getAccount().getId(),
                recipientAccountUuid
        );
    }
}
