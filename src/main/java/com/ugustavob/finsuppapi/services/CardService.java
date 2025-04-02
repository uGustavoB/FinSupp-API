package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.dto.card.CardResponseDTO;
import com.ugustavob.finsuppapi.dto.card.CreateCardRequestDTO;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.card.CardEntity;
import com.ugustavob.finsuppapi.repositories.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final AccountService accountService;

    public CardResponseDTO entityToResponseDTO(CardEntity card) {
        return new CardResponseDTO(
                card.getId(),
                card.getDescription(),
                card.getLastNumbers(),
                card.getLimit(),
                card.getType(),
                card.getAccount().getId()
        );
    }

    public CardEntity createCard(CreateCardRequestDTO createCardRequestDTO, UUID userId) {
        AccountEntity account = accountService.getAccountByIdAndCompareWithUserId(createCardRequestDTO.accountId(), userId);

        CardEntity card = new CardEntity();
        card.setDescription(createCardRequestDTO.description());
        card.setLimit(createCardRequestDTO.limit());
        card.setLastNumbers(createCardRequestDTO.lastNumbers());
        card.setType(createCardRequestDTO.cardType());
        card.setAccount(account);

        return cardRepository.save(card);
    }
}
