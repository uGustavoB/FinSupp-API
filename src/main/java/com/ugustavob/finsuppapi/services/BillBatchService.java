package com.ugustavob.finsuppapi.services;


import com.ugustavob.finsuppapi.entities.bill.BillEntity;
import com.ugustavob.finsuppapi.entities.bill.BillStatus;
import com.ugustavob.finsuppapi.repositories.BillRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class BillBatchService {
    private final BillRepository billRepository;

    private static final int BATCH_SIZE = 100;

    @Transactional
    public void processBillsStatusUpdates() {
        LocalDate today = LocalDate.now();

        log.info("Iniciando atualização de faturas para {}", today);

        updateBillsInBatches(BillStatus.OPEN, BillStatus.CLOSED,
                (pageable) -> billRepository.findBillsToClose(BillStatus.OPEN, today, pageable));

        updateBillsInBatches(BillStatus.CLOSED, BillStatus.OVERDUE,
                (pageable) -> billRepository.findOverdueBills(today, pageable));

        log.info("Atualização de faturas concluída");
    }

    private void updateBillsInBatches(
            BillStatus currentStatus,
            BillStatus newStatus,
            Function<Pageable, Page<BillEntity>> fetcher) {

        int pageNumber = 0;
        Page<BillEntity> page;

        do {
            log.debug("Processando lote {} de faturas {}", pageNumber, currentStatus);

            page = fetcher.apply(PageRequest.of(pageNumber, BATCH_SIZE));

            List<BillEntity> bills = page.getContent();
            bills.forEach(bill -> bill.setStatus(newStatus));

            billRepository.saveAll(bills);
            pageNumber++;

        } while (page.hasNext());
    }
}