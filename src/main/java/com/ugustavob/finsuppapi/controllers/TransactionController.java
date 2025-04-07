package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.SuccessResponseDTO;
import com.ugustavob.finsuppapi.dto.transactions.CreateTransactionRequestDTO;
import com.ugustavob.finsuppapi.dto.transactions.TransactionFilterDTO;
import com.ugustavob.finsuppapi.dto.transactions.TransactionResponseDTO;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import com.ugustavob.finsuppapi.entities.transaction.TransactionType;
import com.ugustavob.finsuppapi.exception.TransactionNotFoundException;
import com.ugustavob.finsuppapi.services.BaseService;
import com.ugustavob.finsuppapi.services.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transactions", description = "Endpoints for transactions management")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final BaseService baseService;

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> getAllTransactions(
            @RequestParam(required = false) Integer accountId,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer installments,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) TransactionType transactionType,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer cardId,
            @RequestParam(defaultValue = "0", required = false) int page ,
            @RequestParam(defaultValue = "10", required = false) int size,
            HttpServletRequest request
    ) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        TransactionFilterDTO filter = new TransactionFilterDTO(
                userId,
                accountId,
                description,
                installments,
                startDate,
                endDate,
                transactionType,
                categoryId,
                cardId
        );

        Page<TransactionResponseDTO> transactions = transactionService.getAllTransactionsFromUser(filter, page, size);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Transactions retrieved",
                transactions
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> getTransactionById(@PathVariable int id, HttpServletRequest request) {
        baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        TransactionEntity transaction = transactionService.getTransactionById(id);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Transaction retrieved",
                transactionService.entityToResponseDto(transaction)
        ));
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    @Transactional
    public ResponseEntity<?> createTransaction(@Valid @RequestBody CreateTransactionRequestDTO createTransactionRequestDTO, HttpServletRequest request) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        TransactionEntity transaction = transactionService.createTransaction(createTransactionRequestDTO, userId);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{id}")
                .buildAndExpand(transaction.getId())
                .toUri();

        return ResponseEntity.created(location).body(new SuccessResponseDTO<>(
                "Transaction created",
                transactionService.entityToResponseDto(transaction)
        ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    @Transactional
    public ResponseEntity<?> updateTransaction(@PathVariable int id, @Valid @RequestBody CreateTransactionRequestDTO createTransactionRequestDTO, HttpServletRequest request) {
        baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        TransactionEntity transaction = transactionService.updateTransaction(id, createTransactionRequestDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{id}")
                .buildAndExpand(transaction.getId())
                .toUri();

        return ResponseEntity.created(location).body(new SuccessResponseDTO<>(
                "Transaction updated",
                transactionService.entityToResponseDto(transaction)
        ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    @Transactional
    public ResponseEntity<?> deleteTransaction(@PathVariable int id, HttpServletRequest request) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        transactionService.deleteTransaction(id, userId);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Transaction deleted"
        ));
    }
}
