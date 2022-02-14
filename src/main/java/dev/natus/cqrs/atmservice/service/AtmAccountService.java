package dev.natus.cqrs.atmservice.service;

import dev.natus.cqrs.atmservice.controller.AtmResponse;
import dev.natus.cqrs.common.command.CreateAccountCommand;
import dev.natus.cqrs.common.command.UpdateAccountRefIdCommand;
import dev.natus.cqrs.common.model.AccountModel;
import dev.natus.cqrs.common.query.AccountQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AtmAccountService {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public AtmAccountService(CommandGateway commandGateway,
                             QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    public AtmResponse login(String accountId) {
        log.info("Handling Login Request {}", accountId);
        AccountModel accountModel = getAccount(accountId);
        String account = accountModel.getAccountId();

        if (Optional.ofNullable(accountModel.getRefId()).isPresent())
            return getAtmResponse(accountModel.getRefId(), "You have already login");

        String refId = UUID.randomUUID().toString();
        log.info("Create Ref Id {} for Account {}", refId, accountId);

        if (Optional.ofNullable(account).isPresent())
            return handleExistingAccount(accountId, refId);

        return handleNewAccount(accountId, refId);
    }

    private AtmResponse handleExistingAccount(String accountId,
                                              String refId) {
        try {
            updateAccount(accountId, refId);
            return getAtmResponse(refId,
                    getMessage(accountId, getBalance(accountId)));
        } catch (Exception e) {
            throw new AtmException(e.getMessage(), e);
        }
    }

    private AtmResponse handleNewAccount(String accountId,
                                         String refId) {
        try {
            createAccount(accountId, refId);
            return getAtmResponse(refId,
                    getMessage(accountId, 0));
        } catch (Exception e) {
            throw new AtmException(e.getMessage(), e);
        }
    }

    public AtmResponse logout(String refId,
                              String accountId) {
        log.info("{}, Handling Logout Request {}", refId, accountId);
        AccountModel accountModel = getAccount(accountId);

        if (!Optional.ofNullable(accountModel.getAccountId()).isPresent())
            throw new AtmException("Account not available");

        // check account
        if (!accountModel.getRefId().equals(refId))
            throw new AtmException("Wrong session..");

        UpdateAccountRefIdCommand updateAccountRefIdCommand =
                UpdateAccountRefIdCommand.builder()
                        .accountId(accountId)
                        .build();
        try {
            commandGateway.sendAndWait(updateAccountRefIdCommand);
            return getAtmResponse(null, String.format("Goodbye, %s!", accountId));
        } catch (Exception e) {
            throw new AtmException(e.getMessage(), e);
        }
    }

    private AtmResponse getAtmResponse(String refId,
                                       String message) {
        return new AtmResponse(new Date(), refId, message);
    }

    private String getMessage(String accountId,
                              int balanceAmount) {
        return String.format("Hello %s!", accountId) +
                "\n" +
                String.format("Your balance is $%d", balanceAmount);
    }

    /**
     * send to account-service
     */
    private int getBalance(String accountId) {
        return queryGateway
                .query(
                        new AccountQuery(accountId),
                        ResponseTypes.instanceOf(AccountModel.class))
                .join()
                .getBalanceAmount();
    }

    /**
     * send to account-service
     */
    public AccountModel getAccount(String accountId) {
        return queryGateway
                .query(
                        new AccountQuery(accountId),
                        ResponseTypes.instanceOf(AccountModel.class))
                .join();
    }

    /**
     * send to account-service
     *
     * @see dev.natus.cqrs.accountservice.query.AccountQueryHandler#getAccounts(AccountQuery)
     */
    public CompletableFuture<AccountModel> getAccountCompletableFuture(String accountId) {
        return queryGateway
                .query(
                        new AccountQuery(accountId),
                        ResponseTypes.instanceOf(AccountModel.class));
    }

    /**
     * send to account-service
     */
    private void createAccount(String accountId,
                               String refId) {
        commandGateway
                .sendAndWait(CreateAccountCommand.builder()
                        .accountId(accountId)
                        .refId(refId)
                        .build()
                );
    }

    /**
     * send to account-service
     */
    private void updateAccount(String accountId,
                               String refId) {
        commandGateway
                .sendAndWait(UpdateAccountRefIdCommand.builder()
                        .accountId(accountId)
                        .refId(refId)
                        .build()
                );
    }
}
