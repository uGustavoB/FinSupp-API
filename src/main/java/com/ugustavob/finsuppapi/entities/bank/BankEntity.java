package com.ugustavob.finsuppapi.entities.bank;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "banks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BankEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    public BankEntity(String name) {
        this.name = name;
    }
}
