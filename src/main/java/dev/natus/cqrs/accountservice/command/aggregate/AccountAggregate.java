package dev.natus.cqrs.accountservice.command.aggregate;

import dev.natus.cqrs.common.command.CreateAccountCommand;
import dev.natus.cqrs.common.command.UpdateAccountCommand;
import dev.natus.cqrs.common.command.UpdateAccountRefIdCommand;
import dev.natus.cqrs.common.event.AccountCreatedEvent;
import dev.natus.cqrs.common.event.AccountUpdatedEvent;
import dev.natus.cqrs.common.event.AccountUpdatedRefIdEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.Date;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@Slf4j
public class AccountAggregate {

    @AggregateIdentifier
    private String accountId;
    private String owedToAccountId;
    private String transactionId;
    private String refId;
    private int balanceAmount;
    private Date createDate;
    private Date updatedDate;

    public AccountAggregate() {
        // Required by axon to construct an empty instance to initiate Event Sourcing.
    }

    @CommandHandler
    public AccountAggregate(CreateAccountCommand createAccountCommand) {
        log.info("CreateAccountCommand received: {}", createAccountCommand);
        AccountCreatedEvent accountCreatedEvent = AccountCreatedEvent.builder()
                .accountId(createAccountCommand.getAccountId())
                .createDate(new Date())
                .refId(createAccountCommand.getRefId())
                .build();
        apply(accountCreatedEvent);
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent accountCreatedEvent) {
        log.info("AccountCreatedEvent occurred: {}", accountCreatedEvent);
        this.accountId = accountCreatedEvent.getAccountId();
        this.refId = accountCreatedEvent.getRefId();
        this.createDate = accountCreatedEvent.getCreateDate();
    }

    @CommandHandler
    public void handle(UpdateAccountCommand updateAccountCommand) {
        log.info("UpdateAccountCommand received: {}", updateAccountCommand);
        AccountUpdatedEvent accountUpdatedEvent = AccountUpdatedEvent.builder()
                .accountId(updateAccountCommand.getAccountId())
                .balanceAmount(updateAccountCommand.getBalanceAmount())
                .transactionId(updateAccountCommand.getTransactionId())
                .refId(updateAccountCommand.getRefId())
                .owedToAccountId(updateAccountCommand.getOwedToAccountId())
                .updatedDate(new Date())
                .build();
        apply(accountUpdatedEvent);
    }

    @EventSourcingHandler
    public void on(AccountUpdatedEvent accountUpdatedEvent) {
        log.info("AccountUpdatedEvent occurred: {}", accountUpdatedEvent);
        this.refId = accountUpdatedEvent.getRefId();
        this.transactionId = accountUpdatedEvent.getTransactionId();
        this.owedToAccountId = accountUpdatedEvent.getOwedToAccountId();
        this.balanceAmount += accountUpdatedEvent.getBalanceAmount();
        this.updatedDate = accountUpdatedEvent.getUpdatedDate();
    }

    @CommandHandler
    public void handle(UpdateAccountRefIdCommand updateAccountRefIdCommand) {
        log.info("UpdateAccountRefIdCommand received: {}", updateAccountRefIdCommand);
        AccountUpdatedRefIdEvent accountUpdatedEvent = AccountUpdatedRefIdEvent.builder()
                .accountId(updateAccountRefIdCommand.getAccountId())
                .refId(updateAccountRefIdCommand.getRefId())
                .updatedDate(new Date())
                .build();
        apply(accountUpdatedEvent);
    }

    @EventSourcingHandler
    public void on(AccountUpdatedRefIdEvent accountUpdatedRefIdEvent) {
        log.info("AccountUpdatedRefIdEvent occurred: {}", accountUpdatedRefIdEvent);
        this.refId = accountUpdatedRefIdEvent.getRefId();
        this.updatedDate = accountUpdatedRefIdEvent.getUpdatedDate();
    }
}
