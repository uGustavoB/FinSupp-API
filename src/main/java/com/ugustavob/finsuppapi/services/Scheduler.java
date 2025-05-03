package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.entities.categories.CategoryEntity;
import com.ugustavob.finsuppapi.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Scheduler {
    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);
    private final BillBatchService billBatchService;
    private final CategoryRepository categoryRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        log.info("Executando atualização inicial de faturas...");
        try {
            billBatchService.processBillsStatusUpdates();
        } catch (Exception e) {
            log.error("Falha na atualização inicial", e);
        }
    }

    @Scheduled(cron = "${app.bill-processing.cron}", zone = "${app.bill-processing.timezone}")
    public void scheduledBillProcessing() {
        try {
            billBatchService.processBillsStatusUpdates();
        } catch (Exception e) {
            log.error("Falha ao processar faturas", e);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createDefaultCategories() {
        if (categoryRepository.count() == 0) {
            log.info("Creating default categories...");
            List<CategoryEntity> defaultCategories = List.of(
                    new CategoryEntity("Food"),
                    new CategoryEntity("Transport"),
                    new CategoryEntity("Health"),
                    new CategoryEntity("Entertainment"),
                    new CategoryEntity("Education"),
                    new CategoryEntity("Housing"),
                    new CategoryEntity("Clothing"),
                    new CategoryEntity("Bill Payments"),
                    new CategoryEntity("Transfer"),
                    new CategoryEntity("Other")
            );
            categoryRepository.saveAll(defaultCategories);
            log.info("Default categories created successfully");
        } else {
            log.info("Default categories already exist");
        }
    }
}
