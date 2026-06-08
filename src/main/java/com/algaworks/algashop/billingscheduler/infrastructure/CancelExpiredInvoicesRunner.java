package com.algaworks.algashop.billingscheduler.infrastructure;

import com.algaworks.algashop.billingscheduler.application.CancelExpiredInvoicesApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CancelExpiredInvoicesRunner implements ApplicationRunner {

    private final CancelExpiredInvoicesApplicationService applicationService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Task started - Cancelling expired invoices");

        applicationService.cancelExpiredInvoices();

        log.info("Task ended - Cancelling expired invoices");
    }
}
