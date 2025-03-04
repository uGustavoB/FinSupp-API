package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.SuccessResponseDTO;
import com.ugustavob.finsuppapi.dto.transactions.CreateTransactionRequestDTO;
import com.ugustavob.finsuppapi.dto.transactions.TransactionResponseDTO;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import com.ugustavob.finsuppapi.repositories.TransactionRepository;
import com.ugustavob.finsuppapi.services.BaseService;
import com.ugustavob.finsuppapi.services.TransactionService;
import com.ugustavob.finsuppapi.useCases.transaction.CreateTransactionUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> createTransaction(@Valid @RequestBody CreateTransactionRequestDTO createTransactionRequestDTO, HttpServletRequest request) {
        baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        TransactionEntity transaction = createTransactionUseCase.execute(createTransactionRequestDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{id}")
                .buildAndExpand(transaction.getId())
                .toUri();

        return ResponseEntity.created(location).body(new SuccessResponseDTO<TransactionResponseDTO>(
                "Transaction created",
                transactionService.entityToResponseDto(transaction)
        ));
    }
}
