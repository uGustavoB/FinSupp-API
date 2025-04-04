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
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final UserService userService;
    private final BaseService baseService;


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> getAccount(@PathVariable Integer id, HttpServletRequest request) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));
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
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        userService.validateUserByIdAndReturn(userId);

        ArrayList<AccountResponseDTO> accounts = new ArrayList<>();

        accountRepository.findAllByUserId(userId).forEach(accountEntity -> accounts.add(
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
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        UserEntity userEntity = userService.validateUserByIdAndReturn(userId);

        AccountEntity accountEntity = accountService.createAccount(createAccountRequestDTO, userEntity);

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
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        AccountEntity accountEntity = accountService.getAccountByIdAndCompareWithUserId(id, userId);

        AccountEntity newAccountEntity = accountService.updateAccount(createAccountRequestDTO, accountEntity);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Account updated",
                accountService.entityToResponseDto(newAccountEntity)
        ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> deleteAccount(@PathVariable Integer id, HttpServletRequest request) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        accountService.getAccountByIdAndCompareWithUserId(id, userId);

        accountService.deleteAccount(id);
        return ResponseEntity.ok().body(new SuccessResponseDTO<>("Account deleted"));
    }
}
