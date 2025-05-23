package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.dto.accounts.AccountFilterDTO;
import com.ugustavob.finsuppapi.dto.accounts.AccountResponseDTO;
import com.ugustavob.finsuppapi.dto.accounts.CreateAccountRequestDTO;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.bank.BankEntity;
import com.ugustavob.finsuppapi.entities.user.UserEntity;
import com.ugustavob.finsuppapi.exception.*;
import com.ugustavob.finsuppapi.repositories.AccountRepository;
import com.ugustavob.finsuppapi.repositories.UserRepository;
import com.ugustavob.finsuppapi.specifications.AccountSpecification;
import com.ugustavob.finsuppapi.utils.StringFormatUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;
    private final TransactionService transactionService;
    private final BankService bankService;

    public AccountEntity createAccount(CreateAccountRequestDTO createAccountRequestDTO, UserEntity userEntity) {
        boolean account =
                accountRepository.existsByDescription(StringFormatUtil.toTitleCase(createAccountRequestDTO.description()),
                        userEntity.getId());

        if (account) {
            throw new AccountAlreadyExistsException();
        }

        BankEntity bankEntity = bankService.getBankById(createAccountRequestDTO.bank());

        AccountEntity newAccount = new AccountEntity();
        newAccount.setDescription(StringFormatUtil.toTitleCase(createAccountRequestDTO.description()));
        newAccount.setBank(bankEntity);
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
        isAccountHaveTransactionsOrSubscriptions(id);

        accountRepository.deleteByIdAndReturnEntity(id)
                .orElseThrow(AccountNotFoundException::new);
    }

    public AccountEntity updateAccount(CreateAccountRequestDTO createAccountRequestDTO, AccountEntity account) {
        if (account == null) {
            throw new AccountNotFoundException("Account not found");
        }

        if (!StringFormatUtil.toTitleCase(createAccountRequestDTO.description()).equals(account.getDescription())) {
            boolean existingAccountWithDescription =
                    accountRepository.existsByDescription(StringFormatUtil.toTitleCase(createAccountRequestDTO.description()),
                            account.getUser().getId());
            if (existingAccountWithDescription) {
                throw new AccountAlreadyExistsException("Description already in use by another account");
            }
        }

        BankEntity bankEntity = bankService.getBankById(createAccountRequestDTO.bank());

        account.setDescription(StringFormatUtil.toTitleCase(createAccountRequestDTO.description()));
        account.setBank(bankEntity);
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

    public Page<AccountResponseDTO> findAll(AccountFilterDTO filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<AccountEntity> specification = AccountSpecification.filter(filter);

        Page<AccountEntity> accountPage = accountRepository.findAll(specification, pageable);

        if (accountPage.isEmpty()) {
            throw new AccountNotFoundException("No accounts found");
        }

        return accountPage.map(this::entityToResponseDto);
    }

    public void isAccountHaveTransactionsOrSubscriptions(Integer accountId) {
        if (subscriptionService.isAccountHaveSubscriptions(accountId) || transactionService.isAccountHaveTransactions(accountId)) {
            throw new AccountCannotBeDeletedException();
        }
    }

    public AccountResponseDTO entityToResponseDto(AccountEntity accountEntity) {
        return new AccountResponseDTO(
                accountEntity.getId(),
                accountEntity.getDescription(),
                accountEntity.getBank().getId(),
                accountEntity.getAccountType(),
                accountEntity.getClosingDay(),
                accountEntity.getPaymentDueDay(),
                accountEntity.getBalance()
        );
    }
}
