package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.dto.subscription.CreateSubscriptionRequestDTO;
import com.ugustavob.finsuppapi.entities.card.CardEntity;
import com.ugustavob.finsuppapi.entities.subscription.SubscriptionEntity;
import com.ugustavob.finsuppapi.repositories.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final CardService cardService;
    private final BillService billService;

    public SubscriptionEntity createSubscription(CreateSubscriptionRequestDTO createSubscriptionRequestDTO, UUID userId) {
        CardEntity card = cardService.getCardById(createSubscriptionRequestDTO.cardId(), userId);

        SubscriptionEntity subscription = new SubscriptionEntity();
        subscription.setDescription(createSubscriptionRequestDTO.description());
        subscription.setPrice(createSubscriptionRequestDTO.price());
        subscription.setInterval(createSubscriptionRequestDTO.interval());
        subscription.setCard(card);

        subscriptionRepository.save(subscription);

        billService.addSubscriptionToBill(subscription);

        return subscription;
    }
}
