package dev.natus.cqrs.transactionservice.command.saga;

import dev.natus.cqrs.common.command.CreateDepositCommand;
import dev.natus.cqrs.common.command.CreateTransferCommand;
import dev.natus.cqrs.common.command.UpdateAccountCommand;
import dev.natus.cqrs.common.enums.TransactionCode;
import dev.natus.cqrs.common.event.AccountUpdatedEvent;
import dev.natus.cqrs.common.event.DepositCreatedEvent;
import dev.natus.cqrs.common.event.TransactionCreatedEvent;
import dev.natus.cqrs.common.event.TransferCreatedEvent;
import dev.natus.cqrs.common.model.AccountModel;
import dev.natus.cqrs.common.query.AccountQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Optional;

@Saga
@Slf4j
public class TransactionSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @Autowired
    private transient QueryUpdateEmitter queryUpdateEmitter;

    @StartSaga
    @SagaEventHandler(associationProperty = "transactionId")
    public void handle(TransactionCreatedEvent transactionCreatedEvent) {
        log.info("{}, Handling TransactionCreatedEvent {}",
                transactionCreatedEvent.getRefId(),
                transactionCreatedEvent.getTransactionCode());

        if (transactionCreatedEvent.getTransactionCode() == TransactionCode.TRANSFER)
            handleTransfer(transactionCreatedEvent);
        else if (transactionCreatedEvent.getTransactionCode() == TransactionCode.DEPOSIT)
            handleDeposit(transactionCreatedEvent);
        else if (transactionCreatedEvent.getTransactionCode() == TransactionCode.WITHDRAW)
            handleWithdraw(transactionCreatedEvent);
    }

    private void handleDeposit(TransactionCreatedEvent transactionCreatedEvent) {
        log.info("{}, Handling Deposit {}",
                transactionCreatedEvent.getRefId(),
                transactionCreatedEvent);

        // check balance amount
        AccountModel accountModel = getAccountModel(transactionCreatedEvent.getAccountId());

        log.info("{}, Get Account Info :{}",
                transactionCreatedEvent.getRefId(),
                accountModel);

        int balanceAmount = accountModel.getBalanceAmount();
        int depositAmount = transactionCreatedEvent.getAmount();
        String owedToAccountId = accountModel.getOwedToAccountId();

        if (balanceAmount < 0 && Optional.ofNullable(owedToAccountId).isPresent()) {
            log.info("{}, Deposit Amount ${}, Owe ${} to {}",
                    transactionCreatedEvent.getRefId(),
                    depositAmount,
                    balanceAmount * -1,
                    owedToAccountId);

            TransactionCreatedEvent transferTransactionCreatedEvent = TransactionCreatedEvent.builder().build();
            BeanUtils.copyProperties(transactionCreatedEvent, transferTransactionCreatedEvent);

            // different amount
            int diffAmount = depositAmount + balanceAmount;

            // process transfer
            if (diffAmount <= 0) {
                log.info("{}, Process Transfer", transactionCreatedEvent.getRefId());

                transferTransactionCreatedEvent.setAmount(depositAmount);
                transferTransactionCreatedEvent.setToAccountId(owedToAccountId);
                handleTransfer(transferTransactionCreatedEvent);
            }
            // process transfer & deposit
            else {
                log.info("{}, Process Transfer & Deposit", transactionCreatedEvent.getRefId());

                // process transfer
                transferTransactionCreatedEvent.setAmount(balanceAmount * -1);
                transferTransactionCreatedEvent.setToAccountId(owedToAccountId);
                handleTransfer(transferTransactionCreatedEvent);

                // process deposit
                TransactionCreatedEvent depositTransactionCreatedEvent = TransactionCreatedEvent.builder().build();
                BeanUtils.copyProperties(transactionCreatedEvent, depositTransactionCreatedEvent);
                depositTransactionCreatedEvent.setAmount(diffAmount);
                processDeposit(depositTransactionCreatedEvent);
            }
        } else {
            processDeposit(transactionCreatedEvent);
        }
    }

    private void processDeposit(TransactionCreatedEvent transactionCreatedEvent) {
        log.info("{}, Process Deposit {}",
                transactionCreatedEvent.getRefId(),
                transactionCreatedEvent);
        CreateDepositCommand createDepositCommand = CreateDepositCommand.builder()
                .transactionId(transactionCreatedEvent.getTransactionId())
                .transactionCode(transactionCreatedEvent.getTransactionCode())
                .accountId(transactionCreatedEvent.getAccountId())
                .amount(transactionCreatedEvent.getAmount())
                .refId(transactionCreatedEvent.getRefId())
                .build();
        commandGateway.sendAndWait(createDepositCommand);
    }

    private void handleWithdraw(TransactionCreatedEvent transactionCreatedEvent) {
        log.info("{}, Process Withdraw {}",
                transactionCreatedEvent.getRefId(),
                transactionCreatedEvent);

        //TODO: Check Balance Amount

        // deduct balance amount accountId
        updateBalanceAccount(
                UpdateAccountCommand.builder()
                        .accountId(transactionCreatedEvent.getAccountId())
                        .refId(transactionCreatedEvent.getRefId())
                        .transactionId(transactionCreatedEvent.getTransactionId())
                        .balanceAmount(transactionCreatedEvent.getAmount() * -1)
                        .updatedDate(new Date())
                        .build()
        );
    }

    private void handleTransfer(TransactionCreatedEvent transactionCreatedEvent) {
        log.info("{}, Handling Transfer {}",
                transactionCreatedEvent.getRefId(),
                transactionCreatedEvent);

        //TODO: Check Balance Amount

        CreateTransferCommand createTransferCommand = CreateTransferCommand.builder()
                .transactionId(transactionCreatedEvent.getTransactionId())
                .accountId(transactionCreatedEvent.getAccountId())
                .toAccountId(transactionCreatedEvent.getToAccountId())
                .refId(transactionCreatedEvent.getRefId())
                .amount(transactionCreatedEvent.getAmount())
                .build();
        commandGateway.sendAndWait(createTransferCommand);
    }

    @SagaEventHandler(associationProperty = "transactionId")
    public void handle(DepositCreatedEvent depositCreatedEvent) {
        log.info("{}, Handling DepositCreatedEvent, UpdateAccountCommand: {}",
                depositCreatedEvent.getRefId(),
                depositCreatedEvent.getAccountId());

        // update balance amount
        UpdateAccountCommand updateAccountCommand = UpdateAccountCommand.builder()
                .accountId(depositCreatedEvent.getAccountId())
                .refId(depositCreatedEvent.getRefId())
                .transactionId(depositCreatedEvent.getTransactionId())
                .balanceAmount(depositCreatedEvent.getAmount())
                .updatedDate(new Date())
                .build();
        commandGateway.sendAndWait(updateAccountCommand);
    }

    @SagaEventHandler(associationProperty = "transactionId")
    public void handle(TransferCreatedEvent transferCreatedEvent) {
        log.info("{}, Handling TransferCreatedEvent, UpdateAccountCommand: {}",
                transferCreatedEvent.getRefId(),
                transferCreatedEvent.getAccountId());

        // check balance amount
        AccountModel accountModel = getAccountModel(transferCreatedEvent.getAccountId());

        log.info("{}, Transfer Amount: {}, Balance Amount: {}, Owed To: {}",
                transferCreatedEvent.getRefId(),
                transferCreatedEvent.getAmount(),
                accountModel.getBalanceAmount(),
                accountModel.getOwedToAccountId());

        // deduct balance amount accountId
        updateBalanceAccount(
                UpdateAccountCommand.builder()
                        .accountId(transferCreatedEvent.getAccountId())
                        .refId(transferCreatedEvent.getRefId())
                        .transactionId(transferCreatedEvent.getTransactionId())
                        .balanceAmount(
                                accountModel.getBalanceAmount() < 0
                                        ? transferCreatedEvent.getAmount()
                                        : transferCreatedEvent.getAmount() * -1)
                        .owedToAccountId(
                                transferCreatedEvent.getAmount() > accountModel.getBalanceAmount()
                                        ? transferCreatedEvent.getToAccountId()
                                        : null)
                        .updatedDate(new Date())
                        .build()
        );

        // add balance amount toAccountId
        updateBalanceAccount(
                UpdateAccountCommand.builder()
                        .accountId(transferCreatedEvent.getToAccountId())
                        .refId(transferCreatedEvent.getRefId())
                        .transactionId(transferCreatedEvent.getTransactionId())
                        .balanceAmount(
                                transferCreatedEvent.getAmount() > accountModel.getBalanceAmount()
                                        ? accountModel.getBalanceAmount() < 0 ? transferCreatedEvent.getAmount() : accountModel.getBalanceAmount()
                                        : transferCreatedEvent.getAmount())
                        .updatedDate(new Date())
                        .build()
        );
    }

    private void updateBalanceAccount(UpdateAccountCommand updateAccountCommand) {
        commandGateway.sendAndWait(updateAccountCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "transactionId")
    public void handle(AccountUpdatedEvent accountUpdatedEvent) {
        log.info("{}, Handling AccountUpdatedEvent, Emit Query get Balance: {}",
                accountUpdatedEvent.getRefId(),
                accountUpdatedEvent);

        // update query get balance amount
        try {
            AccountModel accountModel = getAccountModel(accountUpdatedEvent.getAccountId());

            queryUpdateEmitter.emit(
                    AccountQuery.class,
                    queryUpdateEmitter -> true,
                    accountModel
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private AccountModel getAccountModel(String accountUpdatedEvent) {
        return queryGateway
                .query(
                        new AccountQuery(accountUpdatedEvent),
                        ResponseTypes.instanceOf(AccountModel.class))
                .join();
    }
}
