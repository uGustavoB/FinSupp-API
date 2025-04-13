package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.SuccessResponseDTO;
import com.ugustavob.finsuppapi.dto.subscription.CreateSubscriptionRequestDTO;
import com.ugustavob.finsuppapi.dto.subscription.SubscriptionFilterDTO;
import com.ugustavob.finsuppapi.entities.subscription.SubscriptionEntity;
import com.ugustavob.finsuppapi.entities.subscription.SubscriptionInterval;
import com.ugustavob.finsuppapi.entities.subscription.SubscriptionStatus;
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
                                                                "data": {
                                                                    "id": 1,
                                                                    "description": "Subscription description",
                                                                    "interval": "MONTHLY",
                                                                    "status": "ACTIVE",
                                                                    "accountId": 1,
                                                                    "cardId": 1
                                                                }
                                                            }
                                                            """,
                                                    summary = "Successful response"
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

    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> createSubscription(@Valid @RequestBody CreateSubscriptionRequestDTO createSubscriptionRequestDTO,
                                             HttpServletRequest request
    ) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        SubscriptionEntity subscription = subscriptionService.createSubscription(createSubscriptionRequestDTO, userId);

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

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> updateSubscription(
            @PathVariable Integer id,
            @Valid @RequestBody CreateSubscriptionRequestDTO createSubscriptionRequestDTO,
            HttpServletRequest request
    ) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        SubscriptionEntity subscription = subscriptionService.updateSubscription(id, createSubscriptionRequestDTO, userId);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Subscription updated",
                subscription.entityToResponseDTO()
        ));
    }
}
