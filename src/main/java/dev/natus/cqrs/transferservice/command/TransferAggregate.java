package dev.natus.cqrs.transferservice.command;

import dev.natus.cqrs.common.command.CreateTransferCommand;
import dev.natus.cqrs.common.event.TransferCreatedEvent;
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
public class TransferAggregate {
    @AggregateIdentifier
    private String transferId;
    private String accountId;
    private String toAccountId;
    private String transactionId;
    private int amount;
    private String refId;
    private Date createDate;

    public TransferAggregate() {
        // Required by axon to construct an empty instance to initiate Event Sourcing.
    }

    @CommandHandler
    public TransferAggregate(CreateTransferCommand createTransferCommand) {
        log.info("{}, CreateTransferCommand received {}",
                createTransferCommand.getRefId(),
                createTransferCommand);
        TransferCreatedEvent transferCreatedEvent = TransferCreatedEvent.builder()
                .transferId(UUID.randomUUID().toString())
                .transactionId(createTransferCommand.getTransactionId())
                .accountId(createTransferCommand.getAccountId())
                .toAccountId(createTransferCommand.getToAccountId())
                .amount(createTransferCommand.getAmount())
                .refId(createTransferCommand.getRefId())
                .createDate(new Date())
                .build();
        apply(transferCreatedEvent);
    }

    @EventSourcingHandler
    public void on(TransferCreatedEvent transferCreatedEvent) {
        log.info("{}, TransferCreatedEvent occurred: {}",
                transferCreatedEvent.getRefId(),
                transferCreatedEvent);
        this.transferId = transferCreatedEvent.getTransferId();
        this.accountId = transferCreatedEvent.getAccountId();
        this.toAccountId = transferCreatedEvent.getToAccountId();
        this.transactionId = transferCreatedEvent.getTransactionId();
        this.amount = transferCreatedEvent.getAmount();
        this.refId = transferCreatedEvent.getRefId();
        this.createDate = transferCreatedEvent.getCreateDate();
    }
}
