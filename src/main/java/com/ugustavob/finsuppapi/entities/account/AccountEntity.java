package com.ugustavob.finsuppapi.entities.account;

import com.ugustavob.finsuppapi.entities.bank.BankEntity;
import com.ugustavob.finsuppapi.entities.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String description;

    private double balance;

    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "bank", nullable = false)
    private BankEntity bank;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private int closingDay;
    private int paymentDueDay;

    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
