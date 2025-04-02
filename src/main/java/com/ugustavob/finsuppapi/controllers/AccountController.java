package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.SuccessResponseDTO;
import com.ugustavob.finsuppapi.dto.accounts.AccountResponseDTO;
import com.ugustavob.finsuppapi.dto.accounts.CreateAccountRequestDTO;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.user.UserEntity;
import com.ugustavob.finsuppapi.repositories.AccountRepository;
import com.ugustavob.finsuppapi.services.AccountService;
import com.ugustavob.finsuppapi.services.BaseService;
import com.ugustavob.finsuppapi.services.UserService;
import com.ugustavob.finsuppapi.useCases.account.CreateAccountUseCase;
import com.ugustavob.finsuppapi.useCases.account.DeleteAccountUseCase;
import com.ugustavob.finsuppapi.useCases.account.UpdateAccountUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Accounts", description = "Endpoints for bank account management")
@RequiredArgsConstructor
public class AccountController {
    private final CreateAccountUseCase createAccountUseCase;
    private final AccountRepository accountRepository;
    private final UpdateAccountUseCase updateAccountUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;
    private final AccountService accountService;
    private final BaseService baseService;
    private final UserService userService;


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> getAccount(@PathVariable Integer id, HttpServletRequest request) {
        var userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));
        AccountEntity accountEntity = accountService.getAccountByIdAndCompareWithUserId(id, userId);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Account found",
                accountService.entityToResponseDto(accountEntity)
        ));
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> getAccounts(HttpServletRequest request) {
        var id = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        userService.validateUserByIdAndReturn(id);

        ArrayList<AccountResponseDTO> accounts = new ArrayList<>();

        accountRepository.findAllByUserId(id).forEach(accountEntity -> accounts.add(
                accountService.entityToResponseDto(accountEntity)
        ));

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Accounts found",
                accounts
        ));
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountRequestDTO createAccountRequestDTO, HttpServletRequest request) {
        var id = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        UserEntity userEntity = userService.validateUserByIdAndReturn(id);

        AccountEntity accountEntity = createAccountUseCase.execute(createAccountRequestDTO, userEntity);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{id}")
                .buildAndExpand(accountEntity.getId())
                .toUri();

        return ResponseEntity.created(location).body(new SuccessResponseDTO<>(
                "Account created",
                accountService.entityToResponseDto(accountEntity)
        ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> updateAccount(@PathVariable Integer id,
                                           @RequestBody CreateAccountRequestDTO createAccountRequestDTO, HttpServletRequest request) {
        var userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        AccountEntity accountEntity = accountService.getAccountByIdAndCompareWithUserId(id, userId);

        AccountEntity newAccountEntity = updateAccountUseCase.execute(createAccountRequestDTO, accountEntity);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Account updated",
                accountService.entityToResponseDto(newAccountEntity)
        ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> deleteAccount(@PathVariable Integer id, HttpServletRequest request) {
        var userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        accountService.getAccountByIdAndCompareWithUserId(id, userId);

        deleteAccountUseCase.execute(id);
        return ResponseEntity.ok().body(new SuccessResponseDTO<>("Account deleted"));
    }
}
