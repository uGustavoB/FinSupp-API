package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.subscription.CreateSubscriptionRequestDTO;
import com.ugustavob.finsuppapi.repositories.SubscriptionRepository;
import com.ugustavob.finsuppapi.services.BaseService;
import com.ugustavob.finsuppapi.services.SubscriptionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Endpoints for subscriptions management")
public class SubscriptionController {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionService subscriptionService;
    private final BaseService baseService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public void createSubscription(@Valid @RequestBody CreateSubscriptionRequestDTO createSubscriptionRequestDTO,
        HttpServletRequest request
    ) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        subscriptionService.createSubscription(createSubscriptionRequestDTO, userId);
    }
}
