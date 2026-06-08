package com.algaworks.algashop.billingscheduler.infrastructure;

import com.algaworks.algashop.billingscheduler.application.CancelExpiredInvoicesApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CancelExpiredInvoicesScheduler {

    private final CancelExpiredInvoicesApplicationService applicationService;

    @Scheduled(fixedRate = 5000) // a cada 5 sec
    public void runTask() {
        log.info("Task started - Cancelling expired invoices");

        applicationService.cancelExpiredInvoices();

        log.info("Task ended - Cancelling expired invoices");
    }
}
