package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.dto.card.CardResponseDTO;
import com.ugustavob.finsuppapi.dto.card.CreateCardRequestDTO;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.card.CardEntity;
import com.ugustavob.finsuppapi.exception.CardAlreadyExistsException;
import com.ugustavob.finsuppapi.exception.CardNotFoundException;
import com.ugustavob.finsuppapi.repositories.AccountRepository;
import com.ugustavob.finsuppapi.repositories.CardRepository;
import com.ugustavob.finsuppapi.utils.StringFormatUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.smartcardio.Card;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    public CardEntity createCard(CreateCardRequestDTO createCardRequestDTO, UUID userId) {
        AccountEntity account = accountService.getAccountByIdAndCompareWithUserId(createCardRequestDTO.accountId(), userId);

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

    public List<CardResponseDTO> findAllByUser(UUID userId) {
        List<AccountEntity> accounts = accountRepository.findAllByUserId(userId);
        List<CardResponseDTO> cards = new ArrayList<>();
        accounts.forEach(accountEntity -> {
            List<CardEntity> cardList = cardRepository.findAllByAccountId(accountEntity.getId());
            cardList.forEach(cardEntity -> cards.add(cardEntity.entityToResponseDTO()));
        });
        return cards;
    }
}
