package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.dto.card.CardFilterDTO;
import com.ugustavob.finsuppapi.dto.card.CardResponseDTO;
import com.ugustavob.finsuppapi.dto.card.CreateCardRequestDTO;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.card.CardEntity;
import com.ugustavob.finsuppapi.exception.CardAlreadyExistsException;
import com.ugustavob.finsuppapi.exception.CardNotFoundException;
import com.ugustavob.finsuppapi.repositories.CardRepository;
import com.ugustavob.finsuppapi.specifications.CardSpecification;
import com.ugustavob.finsuppapi.utils.StringFormatUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;

    public CardEntity createCard(CreateCardRequestDTO createCardRequestDTO, AccountEntity account) {
        if (cardRepository.findByDescription(createCardRequestDTO.description()).isPresent()) {
            throw new CardAlreadyExistsException();
        }

        CardEntity card = new CardEntity();
        card.setDescription(StringFormatUtil.toTitleCase(createCardRequestDTO.description()));
        card.setLimit(createCardRequestDTO.limit());
        card.setLastNumbers(createCardRequestDTO.lastNumbers());
        card.setType(createCardRequestDTO.cardType());
        card.setAccount(account);

        return cardRepository.save(card);
    }

    public CardEntity editCard(int id, CreateCardRequestDTO createCardRequestDTO, UUID userId) {
        CardEntity card = cardRepository.findById(id)
                .orElseThrow(CardNotFoundException::new);

        if (!card.getAccount().getUser().getId().equals(userId)) {
            throw new CardNotFoundException();
        }

        card.setDescription(StringFormatUtil.toTitleCase(createCardRequestDTO.description()));
        card.setLimit(createCardRequestDTO.limit());
        card.setLastNumbers(createCardRequestDTO.lastNumbers());
        card.setType(createCardRequestDTO.cardType());

        return cardRepository.save(card);
    }

    public void deleteCard(int id, UUID userId) {
        CardEntity card = cardRepository.findById(id)
                .orElseThrow(CardNotFoundException::new);

        if (!card.getAccount().getUser().getId().equals(userId)) {
            throw new CardNotFoundException();
        }

        cardRepository.delete(card);
    }

    public CardEntity getCardById(int id, UUID userId) {
        CardEntity card = cardRepository.findById(id)
                .orElseThrow(CardNotFoundException::new);

        if (!card.getAccount().getUser().getId().equals(userId)) {
            throw new CardNotFoundException();
        }

        return card;
    }

    public Page<CardResponseDTO> findAll(CardFilterDTO filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<CardEntity> specification = CardSpecification.filter(filter);

        Page<CardEntity> cardPage = cardRepository.findAll(specification, pageable);

        if (cardPage.isEmpty()) {
            throw new CardNotFoundException("No cards found");
        }

        return cardPage.map(CardEntity::entityToResponseDTO);
    }
}
