package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.ErrorResponseDTO;
import com.ugustavob.finsuppapi.dto.SuccessResponseDTO;
import com.ugustavob.finsuppapi.dto.transactions.CreateTransactionRequestDTO;
import com.ugustavob.finsuppapi.dto.transactions.TransactionFilterDTO;
import com.ugustavob.finsuppapi.dto.transactions.TransactionResponseDTO;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import com.ugustavob.finsuppapi.entities.transaction.TransactionType;
import com.ugustavob.finsuppapi.exception.TransactionNotFoundException;
import com.ugustavob.finsuppapi.services.BaseService;
import com.ugustavob.finsuppapi.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "6. Transactions", description = "Endpoints for transactions management")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final BaseService baseService;

    @Operation(
            summary = "Get all transactions",
            description = "Get all transactions from the user. You can filter by account id, description, " +
                    "installments, start date, end date, transaction type, category id and card id."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transactions retrieved successfully",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SuccessResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Success",
                                                    value = """
                                                            {
                                                              "message": "Transactions retrieved",
                                                              "type": "Success",
                                                              "dataList": [
                                                                {
                                                                  "id": 1,
                                                                  "description": "Minecraft Movie",
                                                                  "amount": 50,
                                                                  "installments": 1,
                                                                  "transactionDate": "2025-04-09",
                                                                  "type": "WITHDRAW",
                                                                  "category": 4,
                                                                  "cardId": 1,
                                                                  "recipientAccountId": null
                                                                },
                                                                {
                                                                  "id": 2,
                                                                  "description": "Cookies",
                                                                  "amount": 45,
                                                                  "installments": 1,
                                                                  "transactionDate": "2025-04-13",
                                                                  "type": "WITHDRAW",
                                                                  "category": 1,
                                                                  "cardId": 1,
                                                                  "recipientAccountId": null
                                                                }
                                                              ],
                                                              "pagination": {
                                                                "currentPage": 0,
                                                                "pageSize": 10,
                                                                "totalPages": 1,
                                                                "totalElements": 2
                                                              }
                                                            }
                                                            """,
                                                    summary = "Successful response"
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the user is not authenticated.",
                                                    value = """
                                                            {
                                                              "code": 401,
                                                              "message": "Unauthorized",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Unauthorized"
                                            ),
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transactions not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the user is not found",
                                                    value = """
                                                            {
                                                              "message": "User not found",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "User not found"
                                            ),
                                            @ExampleObject(
                                                    name = "This occurs when no transaction is found according to the filters",
                                                    value = """
                                                            {
                                                              "message": "Transactions not found",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Transactions not found"
                                            )
                                    }
                            )
                    }
            )
    })
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
            @RequestParam(defaultValue = "0", required = false) int page,
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

    @Operation(summary = "Get transaction by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction retrieved successfully",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SuccessResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Success",
                                                    value = """
                                                            {
                                                              "message": "Transaction retrieved",
                                                              "type": "Success",
                                                              "data": {
                                                                "id": 1,
                                                                "description": "Minecraft Movie",
                                                                "amount": 50,
                                                                "installments": 1,
                                                                "transactionDate": "2025-04-09",
                                                                "type": "WITHDRAW",
                                                                "category": 4,
                                                                "cardId": 1,
                                                                "recipientAccountId": null
                                                              }
                                                            }
                                                            """,
                                                    summary = "Successful response"
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the user is not authenticated.",
                                                    value = """
                                                            {
                                                              "code": 401,
                                                              "message": "Unauthorized",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Unauthorized"
                                            ),
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the transaction is not found.",
                                                    value = """
                                                            {
                                                              "message": "Transaction not found",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Transaction not found"
                                            )
                                    }
                            )
                    }
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> getTransactionById(
            @PathVariable int id,
            HttpServletRequest request
    ) {
        baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        TransactionEntity transaction = transactionService.getTransactionById(id);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Transaction retrieved",
                transactionService.entityToResponseDto(transaction)
        ));
    }

    @Operation(summary = "Create transaction")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Transaction created successfully",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SuccessResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Success",
                                                    value = """
                                                            {
                                                              "message": "Transaction created",
                                                              "type": "Success",
                                                              "data": {
                                                                "id": 1,
                                                                "description": "Minecraft Movie",
                                                                "amount": 50,
                                                                "installments": 1,
                                                                "transactionDate": "2025-04-09",
                                                                "type": "WITHDRAW",
                                                                "category": 4,
                                                                "cardId": 1,
                                                                "recipientAccountId": null
                                                              }
                                                            }
                                                            """,
                                                    summary = "Successful response"
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the transaction account ID is identical " +
                                                            "to the recipient's account ID.",
                                                    value = """
                                                            {
                                                              "code": 400,
                                                              "message": "You can't transfer to the same account.",
                                                              "type": "Error",
                                                              "dataList": null
                                                            }
                                                            """,
                                                    summary = "You can't transfer to the same account"
                                            ),
                                            @ExampleObject(
                                                    name = "This occurs when the number of installments is greater " +
                                                            "than 2 and the card type is debit.",
                                                    value = """
                                                            {
                                                              "code": 400,
                                                              "message": "You can't create installments with a debit card.",
                                                              "type": "Error",
                                                              "dataList": null
                                                            }
                                                            """,
                                                    summary = "You can't create installments with a debit card"
                                            ),
                                            @ExampleObject(
                                                    name = "This occurs when the user does not have enough balance " +
                                                            "to create the transaction.",
                                                    value = """
                                                            {
                                                              "code": 400,
                                                              "message": "Insufficient funds.",
                                                              "type": "Error",
                                                              "dataList": null
                                                            }
                                                            """,
                                                    summary = "Insufficient funds"
                                            ),
                                            @ExampleObject(
                                                    name = "This occurs when the bill is not open.",
                                                    value = """
                                                            {
                                                              "code": 400,
                                                              "message": "Bill is not open.",
                                                              "type": "Error",
                                                              "dataList": null
                                                            }
                                                            """,
                                                    summary = "Bill is not open"
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the user is not authenticated.",
                                                    value = """
                                                            {
                                                              "code": 401,
                                                              "message": "Unauthorized",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Unauthorized"
                                            ),
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the category is not found.",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Category not found",
                                                              "type": "Error",
                                                              "dataList": null
                                                            }
                                                            """,
                                                    summary = "Category not found"
                                            ),
                                            @ExampleObject(
                                                    name = "This occurs when the recipient account is not found.",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Recipient account not found",
                                                              "type": "Error",
                                                              "dataList": null
                                                            }
                                                            """,
                                                    summary = "Recipient account not found"
                                            ),
                                            @ExampleObject(
                                                    name = "This occurs when the card is not found.",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Card not found",
                                                              "type": "Error",
                                                              "dataList": null
                                                            }
                                                            """,
                                                    summary = "Card not found"
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Validation Error",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Unprocessable Entity",
                                                    value = """
                                                            {
                                                              "code": 422,
                                                              "message": "Validation error",
                                                              "type": "Error",
                                                              "dataList": [
                                                                {
                                                                  "description": "Transaction amount must be greater than zero",
                                                                  "field": "amount"
                                                                },
                                                                {
                                                                  "description": "Description must be less than 30 characters",
                                                                  "field": "description"
                                                                },
                                                                {
                                                                  "description": "Transaction amount must be greater than zero",
                                                                  "field": "amount"
                                                                }
                                                              ]
                                                            }
                                                            """
                                            )
                                    }
                            )
                    }
            )
    })
    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    @Transactional
    public ResponseEntity<?> createTransaction(
            @Valid @RequestBody CreateTransactionRequestDTO createTransactionRequestDTO,
            HttpServletRequest request
    ) {
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

    @Operation(summary = "Update transaction")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Transaction updated successfully",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SuccessResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Success",
                                                    value = """
                                                            {
                                                              "message": "Transaction updated",
                                                              "type": "Success",
                                                              "data": {
                                                                "id": 1,
                                                                "description": "Minecraft Movie",
                                                                "amount": 50,
                                                                "installments": 1,
                                                                "transactionDate": "2025-04-09",
                                                                "type": "WITHDRAW",
                                                                "category": 4,
                                                                "cardId": 1,
                                                                "recipientAccountId": 8
                                                              }
                                                            }
                                                            """,
                                                    summary = "Successful response"
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the transaction account ID is identical " +
                                                            "to the recipient's account ID.",
                                                    value = """
                                                            {
                                                              "code": 400,
                                                              "message": "You can't transfer to the same account.",
                                                              "type": "Error",
                                                              "dataList": null
                                                            }
                                                            """,
                                                    summary = "You can't transfer to the same account"
                                            ),
                                            @ExampleObject(
                                                    name = "This occurs when the number of installments is greater " +
                                                            "than 2 and the card type is debit.",
                                                    value = """
                                                            {
                                                              "code": 400,
                                                              "message": "You can't create installments with a debit card.",
                                                              "type": "Error",
                                                              "dataList": null
                                                            }
                                                            """,
                                                    summary = "You can't create installments with a debit card"
                                            ),
                                            @ExampleObject(
                                                    name = "This occurs when the user does not have enough balance " +
                                                            "to create the transaction.",
                                                    value = """
                                                            {
                                                              "code": 400,
                                                              "message": "Insufficient funds.",
                                                              "type": "Error",
                                                              "dataList": null
                                                            }
                                                            """,
                                                    summary = "Insufficient funds"
                                            ),
                                            @ExampleObject(
                                                    name = "This occurs when the bill is not open.",
                                                    value = """
                                                            {
                                                              "code": 400,
                                                              "message": "Bill is not open.",
                                                              "type": "Error",
                                                              "dataList": null
                                                            }
                                                            """,
                                                    summary = "Bill is not open"
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the user is not authenticated.",
                                                    value = """
                                                            {
                                                              "code": 401,
                                                              "message": "Unauthorized",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Unauthorized"
                                            ),
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the category is not found.",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Category not found",
                                                              "type": "Error",
                                                              "dataList": null
                                                            }
                                                            """,
                                                    summary = "Category not found"
                                            ),
                                            @ExampleObject(
                                                    name = "This occurs when the recipient account is not found.",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Recipient account not found",
                                                              "type": "Error",
                                                              "dataList": null
                                                            }
                                                            """,
                                                    summary = "Recipient account not found"
                                            ),
                                            @ExampleObject(
                                                    name = "This occurs when the card is not found.",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Card not found",
                                                              "type": "Error",
                                                              "dataList": null
                                                            }
                                                            """,
                                                    summary = "Card not found"
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Validation Error",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Unprocessable Entity",
                                                    value = """
                                                            {
                                                              "code": 422,
                                                              "message": "Validation error",
                                                              "type": "Error",
                                                              "dataList": [
                                                                {
                                                                  "description": "Transaction amount must be greater than zero",
                                                                  "field": "amount"
                                                                },
                                                                {
                                                                  "description": "Description must be less than 30 characters",
                                                                  "field": "description"
                                                                },
                                                                {
                                                                  "description": "Transaction amount must be greater than zero",
                                                                  "field": "amount"
                                                                }
                                                              ]
                                                            }
                                                            """
                                            )
                                    }
                            )
                    }
            )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    @Transactional
    public ResponseEntity<?> updateTransaction(
            @PathVariable int id,
            @Valid @RequestBody CreateTransactionRequestDTO createTransactionRequestDTO,
            HttpServletRequest request
    ) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        TransactionEntity transaction = transactionService.updateTransaction(id, createTransactionRequestDTO, userId);

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

    @Operation(summary = "Delete transaction")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction deleted successfully",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SuccessResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Success",
                                                    value = """
                                                            {
                                                              "message": "Transaction deleted",
                                                              "type": "Success"
                                                            }
                                                            """,
                                                    summary = "Successful response"
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the user is not authenticated.",
                                                    value = """
                                                            {
                                                              "code": 401,
                                                              "message": "Unauthorized",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Unauthorized"
                                            ),
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the transaction is not found.",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Transaction not found",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Transaction not found"
                                            ),
                                            @ExampleObject(
                                                    name = "This occurs when the transaction to be deleted refers to " +
                                                            "the payment of a bill.",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Bill not found.",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Bill not found"
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Transaction does not belong to the user",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the transaction does not belong to the user.",
                                                    value = """
                                                            {
                                                              "code": 422,
                                                              "message": "Transaction does not belong to the user.",
                                                              "type": "Error",
                                                              "dataList": null
                                                            }
                                                            """,
                                                    summary = "Transaction does not belong to the user"
                                            ),
                                            @ExampleObject(
                                                    name = "This occurs when the transaction is linked to a bill that is not open.",
                                                    value = """
                                                            {
                                                              "code": 422,
                                                              "message": "Transaction cannot be deleted.",
                                                              "type": "Error",
                                                              "dataList": null
                                                            }
                                                            """,
                                                    summary = "Transaction cannot be deleted."
                                            )
                                    }
                            )
                    }
            )
    })
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
