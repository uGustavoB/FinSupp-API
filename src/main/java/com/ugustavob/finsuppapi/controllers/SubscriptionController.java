package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.ErrorResponseDTO;
import com.ugustavob.finsuppapi.dto.SuccessResponseDTO;
import com.ugustavob.finsuppapi.dto.subscription.CreateSubscriptionRequestDTO;
import com.ugustavob.finsuppapi.dto.subscription.SubscriptionFilterDTO;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.subscription.SubscriptionEntity;
import com.ugustavob.finsuppapi.entities.subscription.SubscriptionInterval;
import com.ugustavob.finsuppapi.entities.subscription.SubscriptionStatus;
import com.ugustavob.finsuppapi.services.AccountService;
import com.ugustavob.finsuppapi.services.BaseService;
import com.ugustavob.finsuppapi.services.SubscriptionService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Tag(name = "7. Subscriptions", description = "Endpoints for subscriptions management")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final BaseService baseService;
    private final AccountService accountService;

    @Operation(summary = "Get subscription by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Subscription retrieved successfully",
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
                                                              "message": "Subscription retrieved",
                                                              "type": "Success",
                                                              "data": {
                                                                "id": 1,
                                                                "description": "Spotify",
                                                                "price": 12,
                                                                "interval": "MONTHLY",
                                                                "status": "ACTIVE",
                                                                "cardId": 1
                                                              }
                                                            }
                                                            """,
                                                    summary = "Subscription retrieved"
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
                                                    name = "Unauthorized",
                                                    value = """
                                                            {
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
                    description = "Subscription not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the subscription ID does not exist",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Subscription not found",
                                                              "type": "Error",
                                                              "dataList": null
                                                            }
                                                            """,
                                                    summary = "Subscription not found"
                                            )
                                    }
                            )
                    }
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> getSubscriptionById(
            @PathVariable Integer id,
            HttpServletRequest request
    ) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        SubscriptionEntity subscription = subscriptionService.getSubscriptionById(id, userId);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Subscription retrieved",
                subscription.entityToResponseDTO()
        ));
    }

    @Operation(summary = "Get all subscriptions")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Subscriptions retrieved successfully",
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
                                                              "message": "Subscriptions retrieved",
                                                              "type": "Success",
                                                              "data": [
                                                                {
                                                                  "id": 1,
                                                                  "description": "Spotify",
                                                                  "price": 12,
                                                                  "interval": "MONTHLY",
                                                                  "status": "ACTIVE",
                                                                  "cardId": 1
                                                                }
                                                              ]
                                                            }
                                                            """,
                                                    summary = "Subscriptions retrieved"
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
                                                    name = "Unauthorized",
                                                    value = """
                                                            {
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
                    description = "No subscriptions found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the user has no subscriptions",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "No subscriptions found",
                                                              "type": "Error",
                                                              "dataList": null
                                                            }
                                                            """,
                                                    summary = "No subscriptions found"
                                            )
                                    }
                            )
                    }
            )
    })
    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> getAllSubscriptions(
            @RequestParam(required = false) Integer accountId,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) SubscriptionInterval interval,
            @RequestParam(required = false) SubscriptionStatus status,
            @RequestParam(required = false) Integer cardId,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            HttpServletRequest request
    ) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        SubscriptionFilterDTO filter = new SubscriptionFilterDTO(
                userId,
                accountId,
                description,
                interval,
                status,
                cardId
        );

        var subscriptions = subscriptionService.getAllSubscriptionsFromUser(filter, page, size);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Subscriptions retrieved",
                subscriptions
        ));
    }

    @Operation(summary = "Create a subscription")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Subscription created successfully",
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
                                                              "message": "Subscription created",
                                                              "type": "Success",
                                                              "data": {
                                                                "id": 1,
                                                                "description": "Spotify",
                                                                "price": 12,
                                                                "interval": "MONTHLY",
                                                                "status": "ACTIVE",
                                                                "cardId": 1
                                                              }
                                                            }
                                                            """,
                                                    summary = "Subscription created"
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
                                                    name = "Unauthorized",
                                                    value = """
                                                            {
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
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the card ID does not exist",
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
                    description = "Invalid request body",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the request body is invalid",
                                                    value = """
                                                            {
                                                              "code": 422,
                                                              "message": "Validation error",
                                                              "type": "Error",
                                                              "dataList": [
                                                                {
                                                                  "description": "Account ID cannot be null",
                                                                  "field": "cardId"
                                                                },
                                                                {
                                                                  "description": "Description cannot be null",
                                                                  "field": "description"
                                                                }
                                                              ]
                                                            }
                                                            """,
                                                    summary = "Invalid request body"
                                            )
                                    }
                            )
                    }
            )
    })
    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> createSubscription(@Valid @RequestBody CreateSubscriptionRequestDTO createSubscriptionRequestDTO,
                                             HttpServletRequest request
    ) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        AccountEntity account =
                accountService.getAccountByIdAndCompareWithUserId(createSubscriptionRequestDTO.accountId(), userId);

        SubscriptionEntity subscription = subscriptionService.createSubscription(
                createSubscriptionRequestDTO,
                account,
                userId
        );

        URI location = URI.create(
                ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/subscriptions/{id}")
                        .buildAndExpand(subscription.getId())
                        .toUriString()
        );

        return ResponseEntity.created(location).body(new SuccessResponseDTO<>(
                "Subscription created",
                subscription.entityToResponseDTO()
        ));
    }

    @Operation(summary = "Update a subscription")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Subscription updated successfully",
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
                                                              "message": "Subscription updated",
                                                              "type": "Success",
                                                              "data": {
                                                                "id": 1,
                                                                "description": "Spotify",
                                                                "price": 12,
                                                                "interval": "MONTHLY",
                                                                "status": "ACTIVE",
                                                                "cardId": 1
                                                              }
                                                            }
                                                            """,
                                                    summary = "Subscription updated"
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
                                                    name = "Unauthorized",
                                                    value = """
                                                            {
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
                    description = "Subscription not found or Card not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the card ID does not exist",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Card not found",
                                                              "type": "Error",
                                                              "dataList": null
                                                            }
                                                            """,
                                                    summary = "Card not found"
                                            ),
                                            @ExampleObject(
                                                    name = "This occurs when the subscription ID does not exist",
                                                    value = """
                                                            {
                                                              "code": 404,
                                                              "message": "Subscription not found",
                                                              "type": "Error",
                                                              "dataList": null
                                                            }
                                                            """,
                                                    summary = "Subscription not found"
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid request body",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponseDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "This occurs when the request body is invalid",
                                                    value = """
                                                            {
                                                              "code": 422,
                                                              "message": "Validation error",
                                                              "type": "Error",
                                                              "dataList": [
                                                                {
                                                                  "description": "Account ID cannot be null",
                                                                  "field": "cardId"
                                                                },
                                                                {
                                                                  "description": "Description cannot be null",
                                                                  "field": "description"
                                                                }
                                                              ]
                                                            }
                                                            """,
                                                    summary = "Invalid request body"
                                            )
                                    }
                            )
                    }
            )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> updateSubscription(
            @PathVariable Integer id,
            @Valid @RequestBody CreateSubscriptionRequestDTO createSubscriptionRequestDTO,
            HttpServletRequest request
    ) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        AccountEntity account =
                accountService.getAccountByIdAndCompareWithUserId(createSubscriptionRequestDTO.accountId(), userId);

        SubscriptionEntity subscription = subscriptionService.updateSubscription(
                id,
                createSubscriptionRequestDTO,
                account,
                userId
        );

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Subscription updated",
                subscription.entityToResponseDTO()
        ));
    }
}
