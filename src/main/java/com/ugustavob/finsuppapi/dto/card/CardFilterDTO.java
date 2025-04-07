package com.ugustavob.finsuppapi.dto.card;

import com.ugustavob.finsuppapi.entities.card.CardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CardFilterDTO {
    private UUID userId;
    private String description;
    private String lastNumbers;
    private CardType type;
    private Integer accountId;
}
