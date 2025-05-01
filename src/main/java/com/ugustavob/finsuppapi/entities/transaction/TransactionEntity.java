package com.ugustavob.finsuppapi.entities.transaction;

import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.card.CardEntity;
import com.ugustavob.finsuppapi.entities.categories.CategoryEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Check(constraints = "amount > 0")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double amount;

    private int installments;

    @Column(name = "add_to_bill")
    private boolean isAddToBill = false;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "account_id")
    private AccountEntity account;

    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "card_id")
    private CardEntity card;

    @ManyToOne()
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "recipient_id")
    private AccountEntity recipientAccount;
}
