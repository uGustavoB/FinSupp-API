package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.dto.subscription.CreateSubscriptionRequestDTO;
import com.ugustavob.finsuppapi.dto.subscription.SubscriptionResponseDTO;
import com.ugustavob.finsuppapi.entities.card.CardEntity;
import com.ugustavob.finsuppapi.entities.subscription.SubscriptionEntity;
import com.ugustavob.finsuppapi.entities.subscription.SubscriptionStatus;
import com.ugustavob.finsuppapi.exception.SubscriptionNotFoundException;
import com.ugustavob.finsuppapi.repositories.SubscriptionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
        subscription.setStatus(createSubscriptionRequestDTO.status());
        subscription.setCard(card);

        subscriptionRepository.save(subscription);

        billService.addSubscriptionToBill(subscription);

        return subscription;
    }

    public SubscriptionEntity getSubscriptionById(Integer id, UUID userId) {
        SubscriptionEntity subscription = subscriptionRepository.findById(id)
                .orElseThrow(SubscriptionNotFoundException::new);

        if (!subscription.getCard().getAccount().getUser().getId().equals(userId)) {
            throw new SubscriptionNotFoundException();
        }

        return subscription;
    }

    public Page<SubscriptionResponseDTO> getAllSubscriptionsFromUser(UUID userId, int page, int size) {
        return subscriptionRepository.findAllByCard_Account_User_Id(userId, org.springframework.data.domain.PageRequest.of(page, size))
                .map(SubscriptionEntity::entityToResponseDTO);
    }

    public SubscriptionEntity updateSubscription(Integer id, @Valid CreateSubscriptionRequestDTO createSubscriptionRequestDTO, UUID userId) {
        SubscriptionEntity subscription = getSubscriptionById(id, userId);

        CardEntity card = cardService.getCardById(createSubscriptionRequestDTO.cardId(), userId);

        if (createSubscriptionRequestDTO.status() == SubscriptionStatus.INACTIVE && subscription.getStatus() != createSubscriptionRequestDTO.status()) {
            System.out.println("Subscription is inactive");
            billService.removeSubscriptionFromBill(subscription);
        }

        if (createSubscriptionRequestDTO.status() == SubscriptionStatus.ACTIVE && subscription.getStatus() != createSubscriptionRequestDTO.status()) {
            System.out.println("Subscription is active");
            billService.addSubscriptionToBill(subscription);
        }

        subscription.setDescription(createSubscriptionRequestDTO.description());
        subscription.setPrice(createSubscriptionRequestDTO.price());
        subscription.setInterval(createSubscriptionRequestDTO.interval());
        subscription.setStatus(createSubscriptionRequestDTO.status());
        subscription.setCard(card);

        return subscriptionRepository.save(subscription);
    }
}
