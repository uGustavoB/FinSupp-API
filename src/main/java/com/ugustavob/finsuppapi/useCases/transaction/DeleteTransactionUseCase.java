package com.ugustavob.finsuppapi.useCases.transaction;

import com.ugustavob.finsuppapi.exception.TransactionNotFoundException;
import com.ugustavob.finsuppapi.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteTransactionUseCase {
    private final TransactionRepository transactionRepository;

    public void execute(Integer id) {
        transactionRepository.deleteByIdAndReturnEntity(id).orElseThrow(TransactionNotFoundException::new);
    }
}
