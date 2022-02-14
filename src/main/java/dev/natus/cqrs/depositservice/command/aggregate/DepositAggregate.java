package dev.natus.cqrs.depositservice.command.aggregate;

import dev.natus.cqrs.common.command.CreateDepositCommand;
import dev.natus.cqrs.common.enums.TransactionCode;
import dev.natus.cqrs.common.event.DepositCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.Date;
import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@Slf4j
public class DepositAggregate {
    @AggregateIdentifier
    private String depositId;
    private String transactionId;
    private TransactionCode transactionCode;
    private String accountId;
    private int amount;
    private Date createDate;
    private String refId;

    public DepositAggregate() {
        // Required by axon to construct an empty instance to initiate Event Sourcing.
    }

    @CommandHandler
    public DepositAggregate(CreateDepositCommand createDepositCommand) {
        log.info("{}, CreateDepositCommand received {}",
                createDepositCommand.getRefId(),
                createDepositCommand);
        DepositCreatedEvent depositCreatedEvent = DepositCreatedEvent.builder()
                .depositId(UUID.randomUUID().toString())
                .transactionId(createDepositCommand.getTransactionId())
                .accountId(createDepositCommand.getAccountId())
                .transactionCode(createDepositCommand.getTransactionCode())
                .amount(createDepositCommand.getAmount())
                .createDate(new Date())
                .refId(createDepositCommand.getRefId())
                .build();
        apply(depositCreatedEvent);
    }

    @EventSourcingHandler
    public void on(DepositCreatedEvent depositCreatedEvent) {
        log.info("{}, DepositCreatedEvent occurred {}",
                depositCreatedEvent.getRefId(),
                depositCreatedEvent);
        this.depositId = depositCreatedEvent.getDepositId();
        this.transactionCode = depositCreatedEvent.getTransactionCode();
        this.accountId = depositCreatedEvent.getAccountId();
        this.transactionId = depositCreatedEvent.getTransactionId();
        this.amount = depositCreatedEvent.getAmount();
        this.createDate = depositCreatedEvent.getCreateDate();
        this.refId = depositCreatedEvent.getRefId();
    }
}
