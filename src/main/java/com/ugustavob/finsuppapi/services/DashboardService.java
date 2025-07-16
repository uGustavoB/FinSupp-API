package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.dto.dashboard.SummaryResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class DashboardService {
    private final UserService userService;
    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final AccountService accountService;
    private final SubscriptionService subscriptionService;

    public SummaryResponseDTO getSummary(UUID userId) {
        double earnings = transactionService.getTotalAmountIncome(userId);
        double expenses = transactionService.getTotalAmountExpenses(userId);

        double netBalance = earnings - expenses;

        double totalSavings = accountService.getTotalSavingsBalanceByUserId(userId);
        double totalInvestments = accountService.getTotalInvestmentsBalanceByUserId(userId);

        double totalValueForActiveSubscriptions = subscriptionService.getTotalValueForActiveSubscriptions(userId);


        return new SummaryResponseDTO(
                earnings,
                expenses,
                netBalance,
                totalSavings,
                totalInvestments,
                totalValueForActiveSubscriptions
        );
    }
}
