package com.ugustavob.finsuppapi.dto.bills;

import com.ugustavob.finsuppapi.entities.bill.BillStatus;

import java.time.LocalDate;

public record BillResponseDTO(
        Integer id,
        BillStatus status,
        Double totalAmount,
        Integer cardId,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate dueDate
) {
}
