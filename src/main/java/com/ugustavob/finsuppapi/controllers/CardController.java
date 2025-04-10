package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.SuccessResponseDTO;
import com.ugustavob.finsuppapi.dto.card.CardFilterDTO;
import com.ugustavob.finsuppapi.dto.card.CardResponseDTO;
import com.ugustavob.finsuppapi.dto.card.CreateCardRequestDTO;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.card.CardEntity;
import com.ugustavob.finsuppapi.entities.card.CardType;
import com.ugustavob.finsuppapi.repositories.CardRepository;
import com.ugustavob.finsuppapi.services.AccountService;
import com.ugustavob.finsuppapi.services.BaseService;
import com.ugustavob.finsuppapi.services.CardService;
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

        CardFilterDTO filter = new CardFilterDTO(userId, description,lastNumbers,type, accountId);

        Page<CardResponseDTO> cards = cardService.findAll(filter, page, size);

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

        accountService.isAccountHaveTransactionsOrSubscriptions(id);

        cardService.deleteCard(id, userId);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Card deleted successfully"
        ));
    }
}
