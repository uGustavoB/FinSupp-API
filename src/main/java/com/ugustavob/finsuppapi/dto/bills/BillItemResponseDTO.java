package com.ugustavob.finsuppapi.dto.bills;

public record BillItemResponseDTO(
    Integer id,
    double amount,
    Integer installmentNumber,
    Integer billId,
    Integer transactionId
) {
}
