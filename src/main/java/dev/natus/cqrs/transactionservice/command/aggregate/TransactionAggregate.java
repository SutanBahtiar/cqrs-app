package dev.natus.cqrs.transactionservice.command.aggregate;

import dev.natus.cqrs.common.command.CreateTransactionCommand;
import dev.natus.cqrs.common.enums.TransactionCode;
import dev.natus.cqrs.common.event.TransactionCreatedEvent;
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
public class TransactionAggregate {

    @AggregateIdentifier
    private String transactionId;
    private TransactionCode transactionCode;
    private String accountId;
    private String toAccountId;
    private int amount;
    private String refId;
    private Date createdDate;

    public TransactionAggregate() {
        // Required by axon to construct an empty instance to initiate Event Sourcing.
    }

    @CommandHandler
    public TransactionAggregate(CreateTransactionCommand createTransactionCommand) {
        log.info("{}, CreateTransactionCommand received.. {}",
                createTransactionCommand.getRefId(),
                createTransactionCommand);
        TransactionCreatedEvent transactionCreatedEvent = TransactionCreatedEvent.builder()
                .transactionId(UUID.randomUUID().toString())
                .transactionCode(createTransactionCommand.getTransactionCode())
                .accountId(createTransactionCommand.getAccountId())
                .toAccountId(createTransactionCommand.getToAccountId())
                .amount(createTransactionCommand.getAmount())
                .refId(createTransactionCommand.getRefId())
                .createdDate(new Date())
                .build();
        apply(transactionCreatedEvent);
    }

    @EventSourcingHandler
    public void on(TransactionCreatedEvent transactionCreatedEvent) {
        log.info("{}, TransactionCreatedEvent occurred.. {}",
                transactionCreatedEvent.getRefId(),
                transactionCreatedEvent);
        this.transactionId = transactionCreatedEvent.getTransactionId();
        this.transactionCode = transactionCreatedEvent.getTransactionCode();
        this.accountId = transactionCreatedEvent.getAccountId();
        this.toAccountId = transactionCreatedEvent.getToAccountId();
        this.amount = transactionCreatedEvent.getAmount();
        this.refId = transactionCreatedEvent.getRefId();
        this.createdDate = transactionCreatedEvent.getCreatedDate();
    }
}
