package com.ugustavob.finsuppapi.useCases.account;

import com.ugustavob.finsuppapi.dto.accounts.CreateAccountRequestDTO;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.account.AccountType;
import com.ugustavob.finsuppapi.exception.AccountAlreadyExistsException;
import com.ugustavob.finsuppapi.exception.AccountNotFoundException;
import com.ugustavob.finsuppapi.repositories.AccountRepository;
import com.ugustavob.finsuppapi.utils.StringFormatUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateAccountUseCase {
    private final AccountRepository accountRepository;

    public AccountEntity execute(CreateAccountRequestDTO createAccountRequestDTO, AccountEntity account) {
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

        if (account.getAccountType() != AccountType.CREDIT) {
            account.setClosingDay(createAccountRequestDTO.closingDay());
            account.setPaymentDueDay(createAccountRequestDTO.paymentDueDay());
        }

        return accountRepository.save(account);
    }
}
