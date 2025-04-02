package com.ugustavob.finsuppapi.dto.bills;

import com.ugustavob.finsuppapi.entities.bill.BillStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class BillFilterDTO {
    private UUID userId;
    private Integer id;
    private BillStatus status;
    private Integer accountId;
    private Integer month;
    private Integer year;
}
