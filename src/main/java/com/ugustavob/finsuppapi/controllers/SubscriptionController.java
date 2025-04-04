package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.SuccessResponseDTO;
import com.ugustavob.finsuppapi.dto.subscription.CreateSubscriptionRequestDTO;
import com.ugustavob.finsuppapi.entities.subscription.SubscriptionEntity;
import com.ugustavob.finsuppapi.repositories.SubscriptionRepository;
import com.ugustavob.finsuppapi.services.BaseService;
import com.ugustavob.finsuppapi.services.SubscriptionService;
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
@Tag(name = "Subscriptions", description = "Endpoints for subscriptions management")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final BaseService baseService;

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
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            HttpServletRequest request
    ) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        var subscriptions = subscriptionService.getAllSubscriptionsFromUser(userId, page, size);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Subscriptions retrieved",
                subscriptions
        ));
    }

    @PostMapping("/create")
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
