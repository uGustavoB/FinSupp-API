package com.ugustavob.finsuppapi.entities.bill;

import com.ugustavob.finsuppapi.entities.subscription.SubscriptionEntity;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "bill_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BillItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "bill_id", nullable = false)
    private BillEntity bill;

    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "transaction_id")
    private TransactionEntity transaction;

    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "subscription_id")
    private SubscriptionEntity subscription;

    private double amount;
    private int installmentNumber;
}
