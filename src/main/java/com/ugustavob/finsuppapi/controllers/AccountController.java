package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.accounts.CreateAccountRequestDTO;
import com.ugustavob.finsuppapi.dto.accounts.GetAccountResponseDTO;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.user.UserEntity;
import com.ugustavob.finsuppapi.repositories.account.AccountRepository;
import com.ugustavob.finsuppapi.repositories.user.UserRepository;
import com.ugustavob.finsuppapi.useCases.account.CreateAccountUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Accounts", description = "Endpoints for bank account management")
@RequiredArgsConstructor
public class AccountController {
    private final CreateAccountUseCase createAccountUseCase;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountRequestDTO createAccountRequestDTO, HttpServletRequest request) {
        var id = (UUID) request.getAttribute("id");

        if (id == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        try {
            Optional<UserEntity> userEntity = userRepository.findById(id);

            if (userEntity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            AccountEntity account = createAccountUseCase.execute(createAccountRequestDTO, userEntity.get());

            return ResponseEntity.created(null).body(new GetAccountResponseDTO(
                    account.getDescription(),
                    account.getBank(),
                    account.getAccountType(),
                    account.getBalance()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> getAccounts(HttpServletRequest request) {
        var id = (UUID) request.getAttribute("id");

        if (id == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        try {
            Optional<UserEntity> userEntity = userRepository.findById(id);

            if (userEntity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            ArrayList<GetAccountResponseDTO> accounts = new ArrayList<>();

            accountRepository.findAllByUserId(id).forEach(account -> accounts.add(new GetAccountResponseDTO(
                    account.getDescription(),
                    account.getBank(),
                    account.getAccountType(),
                    account.getBalance()
            )));

            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> getAccount(@PathVariable UUID id, HttpServletRequest request) {
        var userId = (UUID) request.getAttribute("id");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        try {
            Optional<UserEntity> userEntity = userRepository.findById(userId);

            if (userEntity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            Optional<AccountEntity> accountEntity = accountRepository.findById(id);

            if (accountEntity.isEmpty() || !accountEntity.get().getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
            }

            return ResponseEntity.ok(new GetAccountResponseDTO(
                    accountEntity.get().getDescription(),
                    accountEntity.get().getBank(),
                    accountEntity.get().getAccountType(),
                    accountEntity.get().getBalance()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
