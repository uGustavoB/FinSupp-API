package com.ugustavob.finsuppapi.dto.bills;

public record BillItemResponseDTO(
    Integer id,
    String description,
    double amount,
    Integer installmentNumber,
    Integer billId,
    Integer transactionId,
    Integer subscriptionId
) {
}
