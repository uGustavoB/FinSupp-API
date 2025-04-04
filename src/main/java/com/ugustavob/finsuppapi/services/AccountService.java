package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.dto.accounts.AccountResponseDTO;
import com.ugustavob.finsuppapi.dto.accounts.CreateAccountRequestDTO;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.categories.CategoryEntity;
import com.ugustavob.finsuppapi.entities.user.UserEntity;
import com.ugustavob.finsuppapi.exception.AccountAlreadyExistsException;
import com.ugustavob.finsuppapi.exception.AccountNotFoundException;
import com.ugustavob.finsuppapi.exception.CategoryDescriptionAlreadyExistsException;
import com.ugustavob.finsuppapi.exception.UserNotFoundException;
import com.ugustavob.finsuppapi.repositories.AccountRepository;
import com.ugustavob.finsuppapi.repositories.CategoryRepository;
import com.ugustavob.finsuppapi.repositories.UserRepository;
import com.ugustavob.finsuppapi.utils.StringFormatUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountEntity createAccount(CreateAccountRequestDTO createAccountRequestDTO, UserEntity userEntity) {
        Optional<AccountEntity> account = accountRepository.findByDescription(createAccountRequestDTO.description());

        if (account.isPresent()) {
            throw new AccountAlreadyExistsException();
        }

        AccountEntity newAccount = new AccountEntity();
        newAccount.setDescription(StringFormatUtil.toTitleCase(createAccountRequestDTO.description()));
        newAccount.setBank(StringFormatUtil.toTitleCase(createAccountRequestDTO.bank()));
        newAccount.setUser(userEntity);
        if (createAccountRequestDTO.balance() != null) {
            newAccount.setBalance(createAccountRequestDTO.balance());
        } else {
            newAccount.setBalance(0.0);
        }
        newAccount.setAccountType(createAccountRequestDTO.accountType());

        newAccount.setClosingDay(createAccountRequestDTO.closingDay());
        newAccount.setPaymentDueDay(createAccountRequestDTO.paymentDueDay());

        return accountRepository.save(newAccount);
    }

    public void deleteAccount(Integer id) {
        accountRepository.deleteByIdAndReturnEntity(id)
                .orElseThrow(AccountNotFoundException::new);
    }

    public AccountEntity updateAccount(CreateAccountRequestDTO createAccountRequestDTO, AccountEntity account) {
        if (account == null) {
            throw new AccountNotFoundException("Account not found");
        }

        if (!createAccountRequestDTO.description().equals(account.getDescription())) {
            Optional<AccountEntity> existingAccountWithDescription =
                    accountRepository.findByDescription(createAccountRequestDTO.description());
            if (existingAccountWithDescription.isPresent() && !existingAccountWithDescription.get().getId().equals(account.getId())) {
                throw new AccountAlreadyExistsException("Description already in use by another account");
            }
        }

        account.setDescription(StringFormatUtil.toTitleCase(createAccountRequestDTO.description()));
        account.setBank(StringFormatUtil.toTitleCase(createAccountRequestDTO.bank()));
        account.setAccountType(createAccountRequestDTO.accountType());

        if (createAccountRequestDTO.balance() != null) {
            account.setBalance(createAccountRequestDTO.balance());
        }

        account.setClosingDay(createAccountRequestDTO.closingDay());
        account.setPaymentDueDay(createAccountRequestDTO.paymentDueDay());

        return accountRepository.save(account);
    }

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
