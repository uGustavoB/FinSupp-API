package com.ugustavob.finsuppapi.dto.bills;

import com.ugustavob.finsuppapi.entities.bill.BillStatus;

import java.time.LocalDate;
import java.util.UUID;

public record BillResponseDTO(
        Integer id,
        BillStatus status,
        Double totalAmount,
        UUID accountId,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate dueDate
) {
}
