package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.SuccessResponseDTO;
import com.ugustavob.finsuppapi.dto.transactions.CreateTransactionRequestDTO;
import com.ugustavob.finsuppapi.dto.transactions.TransactionResponseDTO;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import com.ugustavob.finsuppapi.services.BaseService;
import com.ugustavob.finsuppapi.services.TransactionService;
import com.ugustavob.finsuppapi.useCases.transaction.CreateTransactionUseCase;
import com.ugustavob.finsuppapi.useCases.transaction.DeleteTransactionUseCase;
import com.ugustavob.finsuppapi.useCases.transaction.UpdateTransactionUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final BaseService baseService;
    private final CreateTransactionUseCase createTransactionUseCase;
    private final UpdateTransactionUseCase updateTransactionUseCase;
    private final DeleteTransactionUseCase deleteTransactionUseCase;

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> getAllTransactions(
            @RequestParam(defaultValue = "0", required = false) int page ,
            @RequestParam(defaultValue = "10", required = false) int size,
            HttpServletRequest request
    ) {
        var id = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        Page<TransactionResponseDTO> transactions = transactionService.getAllTransactionsFromUser(id, page, size);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Transactions retrieved",
                transactions
        ));
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    @Transactional
    public ResponseEntity<?> createTransaction(@Valid @RequestBody CreateTransactionRequestDTO createTransactionRequestDTO, HttpServletRequest request) {
        baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        TransactionEntity transaction = createTransactionUseCase.execute(createTransactionRequestDTO);

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

        TransactionEntity transaction = updateTransactionUseCase.execute(id, createTransactionRequestDTO);

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
        baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        deleteTransactionUseCase.execute(id);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Transaction deleted"
        ));
    }
}
