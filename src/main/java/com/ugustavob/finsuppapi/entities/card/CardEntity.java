package com.ugustavob.finsuppapi.entities.card;

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
    private Integer id; // Changed from int to Integer

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, length = 4)
    private String lastNumbers;

    @Column(name = "card_limit") // Renamed to avoid SQL keyword conflict
    private Integer limit;

    @Enumerated(EnumType.STRING) // Explicit enum mapping
    @Column(nullable = false)
    private CardType type;

    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;
}
