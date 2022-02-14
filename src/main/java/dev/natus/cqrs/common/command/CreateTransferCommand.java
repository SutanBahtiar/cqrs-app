package dev.natus.cqrs.common.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.Date;

@Builder
@Data
public class CreateTransferCommand {
    @TargetAggregateIdentifier
    private final String transferId;
    private final String accountId;
    private final String toAccountId;
    private final String transactionId;
    private final int amount;
    private final String refId;
    private final Date createDate;
}
