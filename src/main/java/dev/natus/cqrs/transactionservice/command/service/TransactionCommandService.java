package dev.natus.cqrs.transactionservice.command.service;

import dev.natus.cqrs.common.command.CreateTransactionCommand;
import dev.natus.cqrs.transactionservice.command.controller.CreateTransactionRequest;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class TransactionCommandService {

    private final CommandGateway commandGateway;

    public TransactionCommandService(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    public CompletableFuture<String> createTransaction(CreateTransactionRequest createTransactionRequest) {
        log.info("CreateTransactionRequest {}", createTransactionRequest);
        CreateTransactionCommand createTransactionCommand = CreateTransactionCommand.builder()
                .transactionId(UUID.randomUUID().toString())
                .transactionCode(createTransactionRequest.getTransactionCode())
                .amount(createTransactionRequest.getAmount())
                .accountId(createTransactionRequest.getAccountId())
                .toAccountId(createTransactionRequest.getToAccountId())
                .createdDate(new Date())
                .build();
        return commandGateway.send(createTransactionCommand);
    }
}
