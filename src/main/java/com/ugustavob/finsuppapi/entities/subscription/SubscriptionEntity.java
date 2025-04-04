package com.ugustavob.finsuppapi.entities.subscription;

import com.ugustavob.finsuppapi.dto.subscription.SubscriptionResponseDTO;
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
    private Integer id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionInterval interval;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    @ManyToOne()
    @JoinColumn(name = "card_id", nullable = false)
    private CardEntity card;

    public SubscriptionResponseDTO entityToResponseDTO() {
        return new SubscriptionResponseDTO(
                this.getId(),
                this.getDescription(),
                this.getPrice(),
                this.getInterval(),
                this.getStatus(),
                this.getCard().getId()
        );
        }
}
