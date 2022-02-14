package dev.natus.cqrs.common.command;

import dev.natus.cqrs.common.enums.TransactionCode;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.Date;

@Builder
@Data
public class CreateDepositCommand {
    @TargetAggregateIdentifier
    private final String depositId;
    private final TransactionCode transactionCode;
    private final String accountId;
    private final String transactionId;
    private final Date createDate;
    private int amount;
    private String refId;
}
