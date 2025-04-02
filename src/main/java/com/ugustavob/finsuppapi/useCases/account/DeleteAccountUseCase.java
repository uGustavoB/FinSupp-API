package com.ugustavob.finsuppapi.useCases.account;

import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.exception.AccountNotFoundException;
import com.ugustavob.finsuppapi.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteAccountUseCase {
    private final AccountRepository accountRepository;

    public void execute(Integer id) {
        accountRepository.deleteByIdAndReturnEntity(id)
                        .orElseThrow(AccountNotFoundException::new);
    }
}
