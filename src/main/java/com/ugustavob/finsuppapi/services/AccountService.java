package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.dto.accounts.AccountResponseDTO;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.user.UserEntity;
import com.ugustavob.finsuppapi.exception.AccountNotFoundException;
import com.ugustavob.finsuppapi.exception.UserNotFoundException;
import com.ugustavob.finsuppapi.repositories.AccountRepository;
import com.ugustavob.finsuppapi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountEntity getAccountByIdAndCompareWithUserId(Integer accountId, UUID userId) {
        Optional<UserEntity> userEntity = userRepository.findById(userId);

        if (userEntity.isEmpty()) {
            throw new UserNotFoundException();
        }

        Optional<AccountEntity> accountEntity = accountRepository.findById(accountId);

        if (accountEntity.isEmpty() || !accountEntity.get().getUser().getId().equals(userId)) {
            throw new AccountNotFoundException();
        }

        return accountEntity.get();
    }

    public AccountResponseDTO entityToResponseDto(AccountEntity accountEntity) {
        return new AccountResponseDTO(
                accountEntity.getId(),
                accountEntity.getDescription(),
                accountEntity.getBank(),
                accountEntity.getAccountType(),
                accountEntity.getClosingDay(),
                accountEntity.getPaymentDueDay(),
                accountEntity.getBalance()
        );
    }
}
