package dev.natus.cqrs.atmservice.service;

import dev.natus.cqrs.common.command.CreateTransactionCommand;
import dev.natus.cqrs.common.enums.TransactionCode;
import dev.natus.cqrs.common.model.AccountModel;
import dev.natus.cqrs.common.query.AccountQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class AtmTransactionService {

    private final AtmAccountService accountService;
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public AtmTransactionService(AtmAccountService accountService,
                                 CommandGateway commandGateway,
                                 QueryGateway queryGateway) {
        this.accountService = accountService;
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    public String deposit(String refId,
                          String accountId,
                          int amount) {
        log.info("{}, Handling Deposit Request", refId);

        // check account info
        AccountModel account = checkAccount(accountId, refId);
        int balanceAmount = account.getBalanceAmount();
        String owedToAccountId = account.getOwedToAccountId();

        // create transaction deposit
        CreateTransactionCommand createTransactionCommand =
                CreateTransactionCommand.builder()
                        .transactionCode(TransactionCode.DEPOSIT)
                        .amount(amount)
                        .accountId(accountId)
                        .refId(refId)
                        .build();

        log.info("{}, SubscriptionQuery get balance amount", refId);
        try (SubscriptionQueryResult<AccountModel, AccountModel> subscriptionQueryResult =
                     getSubscriptionQueryResult(accountId)) {
            log.info("{}, Create Transaction Deposit", refId);
            commandGateway.sendAndWait(createTransactionCommand);

            AccountModel accountModel =
                    subscriptionQueryResult.updates().blockFirst();

            if (!Optional.ofNullable(accountModel).isPresent())
                throw new AtmException("Can't get balance info");

            int newBalanceAmount = accountModel.getBalanceAmount();

            if (balanceAmount < 0) {
                StringBuilder sb = new StringBuilder();

//                int transferAmount = amount > (balanceAmount * -1) ? balanceAmount * -1 : amount;
                int transferAmount = Math.min(amount, (balanceAmount * -1));
                sb.append(String.format("Transferred $%d to %s", transferAmount, owedToAccountId));

                if (newBalanceAmount < 0) {
                    sb.append("\n");
                    sb.append(String.format("Your balance is $%d", 0));
                    sb.append("\n");
                    sb.append(String.format("Owed $%d to %s", newBalanceAmount * -1, owedToAccountId));
                } else {
                    sb.append("\n");
                    sb.append(String.format("Your balance is $%d", newBalanceAmount));
                }

                return sb.toString();
            }

            return String.format("Your balance is %d", newBalanceAmount);
        } catch (Exception e) {
            throw new AtmException(e.getMessage(), e);
        }
    }

    public String transfer(String refId,
                           String accountId,
                           String toAccountId,
                           int amount) {
        log.info("{}, Handling Transfer Request", refId);

        // check account info
        checkAccount(accountId, toAccountId, refId);

        // create transaction
        CreateTransactionCommand createTransactionCommand =
                CreateTransactionCommand.builder()
                        .transactionCode(TransactionCode.TRANSFER)
                        .amount(amount)
                        .accountId(accountId)
                        .toAccountId(toAccountId)
                        .refId(refId)
                        .build();

        log.info("{}, SubscriptionQuery get balance amount", refId);
        try (SubscriptionQueryResult<AccountModel, AccountModel> subscriptionQueryResult =
                     getSubscriptionQueryResult(accountId)) {
            log.info("{}, Create Transaction Transfer", refId);
            commandGateway.sendAndWait(createTransactionCommand);

            AccountModel accountModel =
                    subscriptionQueryResult.updates().blockFirst();

            if (!Optional.ofNullable(accountModel).isPresent() ||
                    !Optional.ofNullable(accountModel.getAccountId()).isPresent())
                throw new AtmException("Can't get balance info");

            int balanceAmount = accountModel.getBalanceAmount();

            StringBuilder sb = new StringBuilder();
            if (balanceAmount < 0) {
                sb.append(String.format("Transferred $%d to %s", amount + balanceAmount, toAccountId));
                sb.append("\n");
                sb.append(String.format("Your balance is $%d", 0));
                sb.append("\n");
                sb.append(String.format("Owed $%d to %s", balanceAmount * -1, toAccountId));
            } else {
                sb.append(String.format("Transferred $%d to %s", amount, toAccountId));
                sb.append("\n");
                sb.append(String.format("Your balance is $%d", balanceAmount));
            }

            return sb.toString();
        } catch (Exception e) {
            throw new AtmException(e.getMessage(), e);
        }
    }

    public String withdraw(String refId,
                           String accountId,
                           int amount) {
        // check account info
        AccountModel account = checkAccount(accountId, refId);
        if (account.getBalanceAmount() < amount)
            throw new AtmException("insufficient funds");

        // create transaction
        CreateTransactionCommand createTransactionCommand =
                CreateTransactionCommand.builder()
                        .transactionCode(TransactionCode.WITHDRAW)
                        .amount(amount)
                        .accountId(accountId)
                        .refId(refId)
                        .build();

        log.info("{}, SubscriptionQuery get balance amount", refId);
        try (SubscriptionQueryResult<AccountModel, AccountModel> subscriptionQueryResult =
                     getSubscriptionQueryResult(accountId)) {
            log.info("{}, Create Transaction Withdraw", refId);
            commandGateway.sendAndWait(createTransactionCommand);

            AccountModel accountModel =
                    subscriptionQueryResult.updates().blockFirst();

            if (!Optional.ofNullable(accountModel).isPresent())
                throw new AtmException("Can't get balance info");

            int newBalanceAmount = accountModel.getBalanceAmount();
            return String.format("Your balance is %d", newBalanceAmount);
        } catch (Exception e) {
            throw new AtmException(e.getMessage(), e);
        }
    }

    private AccountModel checkAccount(String accountId,
                                      String refId) {
        AccountModel accountModel = accountService.getAccount(accountId);

        log.info("{}, Get account info: {}", refId, accountModel);

        if (!Optional.ofNullable(accountModel.getAccountId()).isPresent())
            throw new AtmException("Account not exist");

        checkSession(refId, accountModel.getRefId());

        return accountModel;
    }

    private void checkAccount(String accountId,
                              String toAccountId,
                              String refId) {
        if (accountId.equals(toAccountId))
            throw new AtmException("Same account");

        CompletableFuture<AccountModel> accountFrom = accountService.getAccountCompletableFuture(accountId);
        CompletableFuture<AccountModel> accountTo = accountService.getAccountCompletableFuture(toAccountId);

        CompletableFuture.allOf(accountFrom, accountTo).join();

        try {
            AccountModel accountModelFrom = accountFrom.get();
            AccountModel accountModelTo = accountTo.get();

            log.info("{}, Get account info: {}", refId, accountModelFrom);

            if (!Optional.ofNullable(accountModelFrom.getAccountId()).isPresent())
                throw new AtmException("From Account not exist");

            if (!Optional.ofNullable(accountModelTo.getAccountId()).isPresent())
                throw new AtmException("To Account not exist");

            checkSession(refId, accountModelFrom.getRefId());
            checkBalanceAmount(accountModelFrom.getBalanceAmount());

        } catch (InterruptedException | ExecutionException e) {
            throw new AtmException(e.getMessage(), e);
        }
    }

    private void checkBalanceAmount(int balanceAmount) {
        if (balanceAmount <= 0) throw new AtmException("Not have balance amount");
    }

    private void checkSession(String refIdParam,
                              String refIdResponse) {
        if (!refIdResponse.equals(refIdParam)) throw new AtmException("Wrong reference..");
    }

    private SubscriptionQueryResult<AccountModel, AccountModel> getSubscriptionQueryResult(String accountId) {
        return queryGateway
                .subscriptionQuery(
                        new AccountQuery(accountId),
                        AccountModel.class,
                        AccountModel.class
                );
    }

}
