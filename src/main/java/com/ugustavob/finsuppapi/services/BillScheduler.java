package com.ugustavob.finsuppapi.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BillScheduler {
    private static final Logger log = LoggerFactory.getLogger(BillScheduler.class);
    private final BillBatchService billBatchService;

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        log.info("Executando atualização inicial de faturas...");
        try {
            billBatchService.processBillsStatusUpdates();
        } catch (Exception e) {
            log.error("Falha na atualização inicial", e);
        }
    }

    @Scheduled(cron = "${app.bill-processing.cron:0 0 3 * * ?}")
    public void scheduledBillProcessing() {
        try {
            billBatchService.processBillsStatusUpdates();
        } catch (Exception e) {
            log.error("Falha ao processar faturas", e);
        }
    }
}
