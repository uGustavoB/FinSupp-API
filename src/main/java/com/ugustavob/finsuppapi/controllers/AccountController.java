package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.ErrorResponseDTO;
import com.ugustavob.finsuppapi.dto.SuccessResponseDTO;
import com.ugustavob.finsuppapi.dto.accounts.AccountFilterDTO;
import com.ugustavob.finsuppapi.dto.accounts.AccountResponseDTO;
import com.ugustavob.finsuppapi.dto.accounts.CreateAccountRequestDTO;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.account.AccountType;
import com.ugustavob.finsuppapi.entities.user.UserEntity;
import com.ugustavob.finsuppapi.services.AccountService;
import com.ugustavob.finsuppapi.services.BaseService;
import com.ugustavob.finsuppapi.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@Tag(name = "3. Accounts", description = "Endpoints for bank account management")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final UserService userService;
    private final BaseService baseService;

    @Operation(summary = "Get account by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Account found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SuccessResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Account found",
                                                    value = """
                                                            {
                                                              "message": "Account found",
                                                              "type": "Success",
                                                              "data": {
                                                                "id": 1,
                                                                "description": "My Credit Account",
                                                                "bank": "Banco Do Brasil",
                                                                "accountType": "CHECKING",
                                                                "closingDay": 3,
                                                                "paymentDueDay": 10,
                                                                "balance": 1000
                                                              }
                                                            }
                                                            """,
                                                    summary = "Account found"
                                            ),
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
                    description = "Account not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when no account is found.",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Account not found",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Account not found"
                                            ),
                                    }
                            )
                    }
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> getAccount(
            @Parameter(description = "Account ID", required = true)
            @PathVariable Integer id,
            HttpServletRequest request) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));
        AccountEntity accountEntity = accountService.getAccountByIdAndCompareWithUserId(id, userId);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Account found",
                accountService.entityToResponseDto(accountEntity)
        ));
    }

    @Operation(summary = "Get all accounts, with advanced filters")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Accounts found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SuccessResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Accounts found",
                                                    value = """
                                                            {
                                                               "message": "Accounts found",
                                                               "type": "Success",
                                                               "dataList": [
                                                                 {
                                                                   "id": 1,
                                                                   "description": "My Credit Account",
                                                                   "bank": "Banco Do Brasil",
                                                                   "accountType": "CHECKING",
                                                                   "closingDay": 3,
                                                                   "paymentDueDay": 10,
                                                                   "balance": 1000
                                                                 }
                                                               ],
                                                               "pagination": {
                                                                 "currentPage": 0,
                                                                 "pageSize": 10,
                                                                 "totalPages": 1,
                                                                 "totalElements": 1
                                                               }
                                                             }
                                                            """,
                                                    summary = "Accounts found"
                                            ),
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
                    description = "Account not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when no account is found.",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Account not found",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Account not found"
                                            ),
                                    }
                            )
                    }
            )
    })
    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> getAccounts(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String bank,
            @RequestParam(required = false) AccountType accountType,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request
    ) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        AccountFilterDTO filter = new AccountFilterDTO(userId, description, bank, accountType);

        Page<AccountResponseDTO> accounts = accountService.findAll(filter, page, size);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Accounts found",
                accounts
        ));
    }

    @Operation(summary = "Create a new account")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Account created",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SuccessResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Account created",
                                                    value = """
                                                            {
                                                              "message": "Account created",
                                                              "type": "Success",
                                                              "data": {
                                                                "id": 1,
                                                                "description": "My Credit Account",
                                                                "bank": "Banco Do Brasil",
                                                                "accountType": "CHECKING",
                                                                "closingDay": 3,
                                                                "paymentDueDay": 10,
                                                                "balance": 1000
                                                              }
                                                            }
                                                            """,
                                                    summary = "Account created"
                                            ),
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
                    description = "Account not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when no account is found.",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Account not found",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Account not found"
                                            ),
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Account already exists",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This happens when the account description already exists " +
                                                            "for the user.",
                                                    value = """
                                                            {
                                                              "code": 409,
                                                              "message": "Account already exists! Please, try again with a different description.",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Account already exists"
                                            ),
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable Entity",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This happens when the request is well-formed but contains" +
                                                            " semantic errors.",
                                                    value = """
                                                            {
                                                              "code": 422,
                                                              "message": "Validation error",
                                                              "type": "Error",
                                                              "dataList": [
                                                                {
                                                                  "description": "balance",
                                                                  "field": "Agency number must be greater than or equal to 0"
                                                                },
                                                                {
                                                                  "description": "closingDay",
                                                                  "field": "Closing day must be between 1 and 31"
                                                                }
                                                              ]
                                                            }
                                                            """,
                                                    summary = "Unprocessable Entity"
                                            ),
                                    }
                            )
                    }
            )
    })
    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> createAccount(@Valid @RequestBody CreateAccountRequestDTO createAccountRequestDTO,
                                           HttpServletRequest request) {
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

    @Operation(summary = "Update an account")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Account updated",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SuccessResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Account updated",
                                                    value = """
                                                            {
                                                              "message": "Account updated",
                                                              "type": "Success",
                                                              "data": {
                                                                "id": 1,
                                                                "description": "My Credit Account",
                                                                "bank": "Banco Do Brasil",
                                                                "accountType": "CHECKING",
                                                                "closingDay": 3,
                                                                "paymentDueDay": 10,
                                                                "balance": 1000
                                                              }
                                                            }
                                                            """,
                                                    summary = "Account updated"
                                            ),
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
                    description = "Account not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when no account is found.",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Account not found",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Account not found"
                                            ),
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Account already exists",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This happens when the account description already exists " +
                                                            "for the user.",
                                                    value = """
                                                            {
                                                              "code": 409,
                                                              "message": "Account already exists! Please, try again with a different description.",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Account already exists"
                                            ),
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable Entity",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This happens when the request is well-formed but contains" +
                                                            " semantic errors.",
                                                    value = """
                                                            {
                                                              "code": 422,
                                                              "message": "Validation error",
                                                              "type": "Error",
                                                              "dataList": [
                                                                {
                                                                  "description": "balance",
                                                                  "field": "Agency number must be greater than or equal to 0"
                                                                },
                                                                {
                                                                  "description": "closingDay",
                                                                  "field": "Closing day must be between 1 and 31"
                                                                }
                                                              ]
                                                            }
                                                            """,
                                                    summary = "Unprocessable Entity"
                                            ),
                                    }
                            )
                    }
            )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> updateAccount(@PathVariable Integer id,
                                           @Valid @RequestBody CreateAccountRequestDTO createAccountRequestDTO,
                                           HttpServletRequest request) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        AccountEntity accountEntity = accountService.getAccountByIdAndCompareWithUserId(id, userId);

        AccountEntity newAccountEntity = accountService.updateAccount(createAccountRequestDTO, accountEntity);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Account updated",
                accountService.entityToResponseDto(newAccountEntity)
        ));
    }

    @Operation(summary = "Delete an account")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Account deleted",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SuccessResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Account deleted",
                                                    value = """
                                                            {
                                                              "message": "Account deleted",
                                                              "type": "Success"
                                                            }
                                                            """,
                                                    summary = "Account deleted"
                                            ),
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
                    description = "Account not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when no account is found.",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Account not found",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Account not found"
                                            ),
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Account cannot be deleted",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the account has transactions or " +
                                                            "subscriptions associated with it.",
                                                    value = """
                                                            {
                                                              "code": 409,
                                                              "message": "Account cannot be deleted because it has transactions or subscriptions associated with it.",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Account cannot be deleted"
                                            ),
                                    }
                            )
                    }
            )
    })
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
