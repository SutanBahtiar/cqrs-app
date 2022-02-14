package dev.natus.cqrs.accountservice.command.service;

import dev.natus.cqrs.accountservice.command.controller.CreateAccountRequest;
import dev.natus.cqrs.common.command.CreateAccountCommand;
import dev.natus.cqrs.common.command.UpdateAccountRefIdCommand;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AccountCommandService {

    private final CommandGateway commandGateway;

    public AccountCommandService(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    public CompletableFuture<String> createAccount(CreateAccountRequest createAccountRequest) {
        log.info("Create Account: {}", createAccountRequest.getAccountId());
        CreateAccountCommand createAccountCommand = CreateAccountCommand.builder()
                .accountId(createAccountRequest.getAccountId())
                .build();
        return commandGateway.send(createAccountCommand);
    }

    public CompletableFuture<String> clearRefId(CreateAccountRequest createAccountRequest) {
        log.info("Clear RefId Account: {}", createAccountRequest);
        UpdateAccountRefIdCommand updateAccountRefIdCommand = UpdateAccountRefIdCommand.builder()
                .accountId(createAccountRequest.getAccountId())
                .build();
        return commandGateway.send(updateAccountRefIdCommand);
    }
}
