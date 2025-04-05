package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.SuccessResponseDTO;
import com.ugustavob.finsuppapi.dto.card.CardResponseDTO;
import com.ugustavob.finsuppapi.dto.card.CreateCardRequestDTO;
import com.ugustavob.finsuppapi.entities.card.CardEntity;
import com.ugustavob.finsuppapi.repositories.CardRepository;
import com.ugustavob.finsuppapi.services.BaseService;
import com.ugustavob.finsuppapi.services.CardService;
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
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cards")
@Tag(name = "Card", description = "Card management")
@RequiredArgsConstructor
public class CardController {
    private final CardRepository cardRepository;
    private final CardService cardService;
    private final BaseService baseService;

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

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> getAllCards(HttpServletRequest request) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        List<CardResponseDTO> cards = cardService.findAllByUser(userId);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Cards retrieved successfully",
                cards
        ));
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> createCard(@Valid @RequestBody CreateCardRequestDTO createCardRequestDTO,
                                     HttpServletRequest request) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        CardEntity card = cardService.createCard(createCardRequestDTO, userId);

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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> deleteCard(@PathVariable int id, HttpServletRequest request) {
        UUID userId = baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        cardService.deleteCard(id, userId);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Card deleted successfully"
        ));
    }
}
