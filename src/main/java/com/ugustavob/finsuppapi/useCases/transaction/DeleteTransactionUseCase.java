package com.ugustavob.finsuppapi.useCases.transaction;

import com.ugustavob.finsuppapi.entities.bill.BillEntity;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntityFinder;
import com.ugustavob.finsuppapi.exception.BillNotFoundException;
import com.ugustavob.finsuppapi.exception.BusinessException;
import com.ugustavob.finsuppapi.exception.TransactionNotFoundException;
import com.ugustavob.finsuppapi.repositories.TransactionRepository;
import com.ugustavob.finsuppapi.services.BillService;
import com.ugustavob.finsuppapi.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class DeleteTransactionUseCase {
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;
    private final BillService billService;

    public void execute(Integer id, UUID userId) {
        TransactionEntity transaction = transactionRepository.findById(id).orElseThrow(TransactionNotFoundException::new);

        System.out.println(transaction.getCard().getAccount().getUser().getId());
        System.out.println(userId);

        if (!transaction.getCard().getAccount().getUser().getId().equals(userId)) {
            throw new BusinessException("Transaction does not belong to the user");
        };

        if (transactionRepository.existsBillWithTransactionId(transaction.getId())) {
            throw new BusinessException("Transaction cannot be deleted");
        }

        if (transaction.getDescription().startsWith("Payment of the bill: ")) {
            String description = transaction.getDescription().replace("Payment of the bill: ", "");
            Matcher matcher = Pattern.compile("(\\d{2})/(\\d{4})$").matcher(description);
            if (matcher.find()) {
                int month = Integer.parseInt(matcher.group(1));
                int year = Integer.parseInt(matcher.group(2));

                BillEntity bill = billService.findByMonthAndYearAndUser(month, year, userId)
                        .orElseThrow(() -> new BillNotFoundException("Bill not found"));

                billService.revertPayment(bill);
            }
        }

        billService.revertTransactionBills(transaction);
        transactionRepository.delete(transaction);

        TransactionEntityFinder transactionEntityFinder =
                transactionService.getAndValidateTransactionEntities(transaction.getCard(),
        transaction.getRecipientAccount());

        transactionEntityFinder = transactionService.revertAccountBalance(transaction, transactionEntityFinder);

        transactionService.saveAccounts(transactionEntityFinder, transaction.getTransactionType());
    }
}
