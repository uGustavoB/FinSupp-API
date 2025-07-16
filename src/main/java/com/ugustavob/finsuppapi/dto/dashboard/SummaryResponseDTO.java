package com.ugustavob.finsuppapi.dto.dashboard;

public record SummaryResponseDTO(
        Double monthlyIncome,
        Double monthlyExpense,
        Double netBalance,
        Double totalSavings,
        Double totalInvestments,
        Double totalValueForActiveSubscriptions
) {
}
