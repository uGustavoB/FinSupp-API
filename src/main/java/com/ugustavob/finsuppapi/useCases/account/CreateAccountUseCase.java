package com.ugustavob.finsuppapi.useCases.account;

import com.ugustavob.finsuppapi.dto.accounts.CreateAccountRequestDTO;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.user.UserEntity;
import com.ugustavob.finsuppapi.exception.AccountAlreadyExistsException;
import com.ugustavob.finsuppapi.repositories.account.AccountRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreateAccountUseCase {
    private final AccountRepository accountRepository;

    public AccountEntity execute(@Valid CreateAccountRequestDTO createAccountRequestDTO, UserEntity userId) {
        Optional<AccountEntity> account = accountRepository.findByDescription(createAccountRequestDTO.description());

        if (account.isEmpty()) {
            AccountEntity newAccount = new AccountEntity();
            newAccount.setDescription(createAccountRequestDTO.description());
            newAccount.setBank(createAccountRequestDTO.bank());
            newAccount.setUser(userId);
            newAccount.setBalance(0);
            newAccount.setAccountType(createAccountRequestDTO.accountType());

            return accountRepository.save(newAccount);
        }

        throw new AccountAlreadyExistsException();
    }
}
