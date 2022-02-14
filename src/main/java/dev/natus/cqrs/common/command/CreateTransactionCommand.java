package dev.natus.cqrs.common.command;

import dev.natus.cqrs.common.enums.TransactionCode;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateVersion;

import java.util.Date;

@Builder
@Data
public class CreateTransactionCommand {
    @TargetAggregateVersion
    private final String transactionId;
    private final TransactionCode transactionCode;
    private final String accountId;
    private final String toAccountId;
    private final int amount;
    private final String refId;
    private final Date createdDate;
}
