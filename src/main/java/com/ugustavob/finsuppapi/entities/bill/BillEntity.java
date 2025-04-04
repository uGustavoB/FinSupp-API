package com.ugustavob.finsuppapi.entities.bill;

import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.card.CardEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "bills")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BillEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "card_id", nullable = false)
    private CardEntity card;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    private double totalAmount;

    @Enumerated(EnumType.STRING)
    private BillStatus status;
}
