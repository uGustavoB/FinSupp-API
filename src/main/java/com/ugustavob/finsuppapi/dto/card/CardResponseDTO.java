package com.ugustavob.finsuppapi.dto.card;

import com.ugustavob.finsuppapi.entities.card.CardType;

public record CardResponseDTO(
        Integer id,
        String description,
        String lastNumbers,
        Integer limit,
        CardType cardType,
        Integer accountId
) {
}
