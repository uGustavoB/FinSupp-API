package com.ugustavob.finsuppapi.entities.card;

import com.ugustavob.finsuppapi.dto.card.CardResponseDTO;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "cards")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, length = 4)
    private String lastNumbers;

    @Column(name = "card_limit")
    private Integer limit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType type;

    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    public CardResponseDTO entityToResponseDTO() {
        return new CardResponseDTO(
                this.getId(),
                this.getDescription(),
                this.getLastNumbers(),
                this.getLimit(),
                this.getType(),
                this.getAccount().getId()
        );
    }
}
