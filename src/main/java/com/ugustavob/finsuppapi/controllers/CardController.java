package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.card.CreateCardRequestDTO;
import com.ugustavob.finsuppapi.repositories.CardRepository;
import com.ugustavob.finsuppapi.services.BaseService;
import com.ugustavob.finsuppapi.services.CardService;
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
@RequestMapping("/cards")
@Tag(name = "Card", description = "Card management")
@RequiredArgsConstructor
public class CardController {
    private final CardRepository cardRepository;
    private final CardService cardService;
    private final BaseService baseService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public void createCard(@Valid @RequestBody CreateCardRequestDTO createCardRequestDTO,
                           HttpServletRequest request) {
        var userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));
        cardService.createCard(createCardRequestDTO, userId);
    }
}
