package com.ugustavob.finsuppapi.dto.transactions;

import com.ugustavob.finsuppapi.entities.transaction.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionFilterDTO {
    private UUID userId;
    private Integer accountId;
    private String description;
    private Integer installments;
    private LocalDate startDate;
    private LocalDate endDate;
    private TransactionType transactionType;
    private Integer categoryId;
    private Integer cardId;
}
