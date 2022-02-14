package dev.natus.cqrs.transactionservice.command.controller;

import dev.natus.cqrs.common.model.ResponseMessage;
import dev.natus.cqrs.transactionservice.command.service.TransactionCommandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionCommandService transactionCommandService;

    public TransactionController(TransactionCommandService transactionCommandService) {
        this.transactionCommandService = transactionCommandService;
    }

    @PostMapping
    public ResponseEntity<ResponseMessage> createTransaction(
            @RequestBody
                    CreateTransactionRequest createTransactionRequest) {
        try {
            CompletableFuture<String> completableFuture = transactionCommandService
                    .createTransaction(createTransactionRequest);
            return new ResponseEntity<>(
                    ResponseMessage.responseMessage("OK", completableFuture.join()),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    ResponseMessage.responseMessage("An error occurred", null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
