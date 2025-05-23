package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.dto.subscription.CreateSubscriptionRequestDTO;
import com.ugustavob.finsuppapi.dto.subscription.SubscriptionFilterDTO;
import com.ugustavob.finsuppapi.dto.subscription.SubscriptionResponseDTO;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.subscription.SubscriptionEntity;
import com.ugustavob.finsuppapi.entities.subscription.SubscriptionStatus;
import com.ugustavob.finsuppapi.exception.SubscriptionNotFoundException;
import com.ugustavob.finsuppapi.repositories.SubscriptionRepository;
import com.ugustavob.finsuppapi.specifications.SubscriptionSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final BillService billService;

    public SubscriptionEntity createSubscription(
            CreateSubscriptionRequestDTO createSubscriptionRequestDTO,
            AccountEntity account,
            UUID userId
    ) {

        SubscriptionEntity subscription = new SubscriptionEntity();
        subscription.setDescription(createSubscriptionRequestDTO.description());
        subscription.setPrice(createSubscriptionRequestDTO.price());
        subscription.setInterval(createSubscriptionRequestDTO.interval());
        subscription.setStatus(createSubscriptionRequestDTO.status());
        subscription.setAccount(account);

        subscriptionRepository.save(subscription);

        if (createSubscriptionRequestDTO.status() == SubscriptionStatus.ACTIVE) {
            billService.addSubscriptionToBill(subscription);
        }

        return subscription;
    }

    public SubscriptionEntity getSubscriptionById(int id, UUID userId) {
        SubscriptionEntity subscription = subscriptionRepository.findById(id).orElseThrow(SubscriptionNotFoundException::new);

        if (!subscription.getAccount().getUser().getId().equals(userId)) {
            throw new SubscriptionNotFoundException();
        }

        return subscription;
    }

    public Page<SubscriptionResponseDTO> getAllSubscriptionsFromUser(SubscriptionFilterDTO filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<SubscriptionEntity> specification = SubscriptionSpecification.filter(filter);

        Page<SubscriptionEntity> subscriptionPage = subscriptionRepository.findAll(specification, pageable);

        if (subscriptionPage.isEmpty()) {
            throw new SubscriptionNotFoundException("No subscriptions found");
        }

        return subscriptionPage.map(SubscriptionEntity::entityToResponseDTO);
    }

    public boolean isAccountHaveSubscriptions(Integer accountId) {
        return subscriptionRepository.existsByAccountId(accountId);
    }

    public SubscriptionEntity updateSubscription(
            int id,
            @Valid CreateSubscriptionRequestDTO dto,
            AccountEntity account,
            UUID userId
    ) {
        SubscriptionEntity subscription = getSubscriptionById(id, userId);
        SubscriptionStatus oldStatus = subscription.getStatus();

        subscription.setDescription(dto.description());
        subscription.setPrice(dto.price());
        subscription.setInterval(dto.interval());
        subscription.setAccount(account);

        billService.syncSubscriptionInBill(subscription, oldStatus, dto.status());

        subscription.setStatus(dto.status());

        return subscriptionRepository.save(subscription);
    }
}
