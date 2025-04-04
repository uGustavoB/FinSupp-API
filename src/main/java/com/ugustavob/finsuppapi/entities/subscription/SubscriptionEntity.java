package com.ugustavob.finsuppapi.entities.subscription;

import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.card.CardEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private SubscriptionInterval interval;

    private SubscriptionStatus status;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private CardEntity card;
}
