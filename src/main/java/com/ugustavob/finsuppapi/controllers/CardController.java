package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.ErrorResponseDTO;
import com.ugustavob.finsuppapi.dto.SuccessResponseDTO;
import com.ugustavob.finsuppapi.dto.card.CardFilterDTO;
import com.ugustavob.finsuppapi.dto.card.CardResponseDTO;
import com.ugustavob.finsuppapi.dto.card.CreateCardRequestDTO;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.card.CardEntity;
import com.ugustavob.finsuppapi.entities.card.CardType;
import com.ugustavob.finsuppapi.services.AccountService;
import com.ugustavob.finsuppapi.services.BaseService;
import com.ugustavob.finsuppapi.services.CardService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/cards")
@Tag(name = "4. Card", description = "Card management")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;
    private final BaseService baseService;
    private final AccountService accountService;

    @Operation(summary = "Get card by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Card retrieved successfully",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuccessResponseDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Card retrieved successfully",
                                                    value = """
                                                            {
                                                              "message": "Card retrieved successfully",
                                                              "type": "Success",
                                                              "data": {
                                                                "id": 1,
                                                                "description": "My Card",
                                                                "lastNumbers": "1234",
                                                                "limit": 1000,
                                                                "cardType": "CREDIT",
                                                                "accountId": 1
                                                              }
                                                            }
                                                            """
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
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the user is not authenticated",
                                                    value = """
                                                            {
                                                              "code": 401,
                                                              "message": "Unauthorized",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Unauthorized"
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Card not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the user is not authenticated",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Card not found",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Card not found"
                                            )
                                    }
                            )
                    }
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> getCardById(@PathVariable int id, HttpServletRequest request) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        CardEntity card = cardService.getCardById(id, userId);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Card retrieved successfully",
                card.entityToResponseDTO()
        ));
    }

    @Operation(summary = "Get all cards")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cards retrieved successfully",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuccessResponseDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Cards retrieved successfully",
                                                    value = """
                                                            {
                                                              "message": "Cards retrieved successfully",
                                                              "type": "Success",
                                                              "data": {
                                                                "content": [
                                                                  {
                                                                    "id": 1,
                                                                    "description": "My Card",
                                                                    "lastNumbers": "1234",
                                                                    "limit": 1000,
                                                                    "cardType": "CREDIT",
                                                                    "accountId": 1
                                                                  }
                                                                ],
                                                                "pageable": {
                                                                  "pageNumber": 0,
                                                                  "pageSize": 10,
                                                                  "totalPages": 1,
                                                                  "totalElements": 1
                                                                }
                                                              }
                                                            }
                                                            """
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
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the user is not authenticated",
                                                    value = """
                                                            {
                                                              "code": 401,
                                                              "message": "Unauthorized",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Unauthorized"
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cards not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the cards are not found",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Cards not found",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Cards not found"
                                            )
                                    }
                            )
                    }
            )
    })
    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> getAllCards(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String lastNumbers,
            @RequestParam(required = false) CardType type,
            @RequestParam(required = false) Integer accountId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        CardFilterDTO filter = new CardFilterDTO(userId, description, lastNumbers, type, accountId);

        Page<CardResponseDTO> cards = cardService.findAll(filter, page, size);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Cards retrieved successfully",
                cards
        ));
    }

    @Operation(summary = "Create a new card")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Card created successfully",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuccessResponseDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Card created successfully",
                                                    value = """
                                                            {
                                                              "message": "Card created successfully",
                                                              "type": "Success",
                                                              "data": {
                                                                "id": 1,
                                                                "description": "My Card",
                                                                "lastNumbers": "1234",
                                                                "limit": 1000,
                                                                "cardType": "CREDIT",
                                                                "accountId": 1
                                                              }
                                                            }
                                                            """
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
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the user is not authenticated",
                                                    value = """
                                                            {
                                                              "code": 401,
                                                              "message": "Unauthorized",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Unauthorized"
                                            )
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
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the card is not found",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Card not found",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Card not found"
                                            )
                                    }
                            )
                    }
            )
    })
    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> createCard(@Valid @RequestBody CreateCardRequestDTO createCardRequestDTO,
                                        HttpServletRequest request) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        AccountEntity account = accountService.getAccountByIdAndCompareWithUserId(createCardRequestDTO.accountId(),
                userId);

        CardEntity card = cardService.createCard(createCardRequestDTO, account);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{id}")
                .buildAndExpand(card.getId())
                .toUri();

        return ResponseEntity.created(location).body(new SuccessResponseDTO<>(
                "Card created successfully",
                card.entityToResponseDTO()
        ));
    }

    @Operation(summary = "Edit a card")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Card updated successfully",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuccessResponseDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Card updated successfully",
                                                    value = """
                                                            {
                                                              "message": "Card updated successfully",
                                                              "type": "Success",
                                                              "data": {
                                                                "id": 1,
                                                                "description": "My Card",
                                                                "lastNumbers": "1234",
                                                                "limit": 1000,
                                                                "cardType": "CREDIT",
                                                                "accountId": 1
                                                              }
                                                            }
                                                            """
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
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the user is not authenticated",
                                                    value = """
                                                            {
                                                              "code": 401,
                                                              "message": "Unauthorized",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Unauthorized"
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Card not found or Account not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the card is not found",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Card not found",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Card not found"
                                            ),
                                            @ExampleObject(
                                                    name = "This occurs when the account is not found",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Account not found",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Account not found"
                                            )
                                    }
                            )
                    }
            )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> editCard(@PathVariable int id,
                                      @Valid @RequestBody CreateCardRequestDTO createCardRequestDTO,
                                      HttpServletRequest request) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        CardEntity card = cardService.editCard(id, createCardRequestDTO, userId);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Card updated successfully",
                card.entityToResponseDTO()
        ));
    }

    @Operation(summary = "Delete a card")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Card deleted successfully",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuccessResponseDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Card deleted successfully",
                                                    value = """
                                                            {
                                                              "message": "Card deleted successfully",
                                                              "type": "Success"
                                                            }
                                                            """
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
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the user is not authenticated",
                                                    value = """
                                                            {
                                                              "code": 401,
                                                              "message": "Unauthorized",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Unauthorized"
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Card not found or Account not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the card is not found",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Card not found",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Card not found"
                                            ),
                                            @ExampleObject(
                                                    name = "This occurs when the account is not found",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Account not found",
                                                              "type": "Error"
                                                            }
                                                            """,
                                                    summary = "Account not found"
                                            )
                                    }
                            )
                    }
            )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> deleteCard(@PathVariable int id, HttpServletRequest request) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        accountService.isAccountHaveTransactionsOrSubscriptions(id);

        cardService.deleteCard(id, userId);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Card deleted successfully"
        ));
    }
}
