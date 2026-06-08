package com.algaworks.algashop.billingscheduler.infrastructure;

import com.algaworks.algashop.billingscheduler.application.CancelExpiredInvoicesApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CancelExpiredInvoicesApplicationServiceJDBCImpl implements CancelExpiredInvoicesApplicationService {

    private final JdbcOperations jdbcOperations;

    private static final Duration EXPIRED_SINCE = Duration.ofDays(1);
    private static final String UNPAID_STATUS = "UNPAID";
    private static final String CANCEL_STATUS = "CANCELED";
    private static final String CANCEL_REASON = "Invoice expired";
    private static final String SELECT_EXPIRED_INVOICES_SQL = String.format("""
            SELECT id, expires_at
            FROM invoice i
            WHERE i.expires_at <= NOW() - INTERVAL '%d days'
              AND i.status = ?
            """, EXPIRED_SINCE.toDays());

    private static final String UPDATE_EXPIRED_INVOICES_SQL = """
                UPDATE invoice SET status = ?, canceled_at = NOW(), cancel_reason = ?
                WHERE id = ?
            """;

    @Override
    public void cancelExpiredInvoices() {
        List<UUID> invoiceIds = fetchExpiredInvoices();
        log.info("Task - Total invoices fetched: {}", invoiceIds.size());
        int totalCancelledInvoices = cancelInvoices(invoiceIds);
        log.info("Task - Total invoices cancelled: {}", totalCancelledInvoices);
    }

    private List<UUID> fetchExpiredInvoices() {
        PreparedStatementSetter preparedStatementSetter = ps -> ps.setString(1, UNPAID_STATUS);
        RowMapper<UUID> mapper = (rs, rowNum) -> rs.getObject("id", UUID.class);
        return jdbcOperations.query(SELECT_EXPIRED_INVOICES_SQL, preparedStatementSetter, mapper);
    }

    private int cancelInvoices(List<UUID> invoiceIds) {
        int updatedInvoices = 0;
        for (UUID invoiceId: invoiceIds) {
            try {
                jdbcOperations.update(UPDATE_EXPIRED_INVOICES_SQL, CANCEL_STATUS, CANCEL_REASON, invoiceId);
                updatedInvoices++;
                log.info("Task - Invoice cancelled: {}", invoiceId);
            } catch (DataAccessException ex) {
                log.error("Task - Failed to cancel invoice with id {}", invoiceId, ex);
            }
        }
        return updatedInvoices;
    }
}
