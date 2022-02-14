package dev.natus.cqrs.transactionservice.query.controller;

import dev.natus.cqrs.common.model.ResponseMessage;
import dev.natus.cqrs.transactionservice.query.TransactionQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static dev.natus.cqrs.common.model.ResponseMessage.responseMessage;

@RestController
@RequestMapping("/transaction")
public class TransactionQueryController {

    private final QueryGateway queryGateway;

    public TransactionQueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping
    public ResponseEntity<ResponseMessage> getTransactions() {
        try {
            CompletableFuture<List<TransactionModel>> completableFuture =
                    queryGateway.query(TransactionQuery.builder().build(),
                            ResponseTypes.multipleInstancesOf(TransactionModel.class));
            return new ResponseEntity<>(
                    responseMessage("OK", completableFuture.join()),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    responseMessage(e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getTransaction(@PathVariable String id) {
        try {
            CompletableFuture<TransactionModel> completableFuture =
                    queryGateway.query(
                            TransactionQuery.builder().transactionId(id).build(),
                            ResponseTypes.instanceOf(TransactionModel.class));
            return new ResponseEntity<>(
                    responseMessage("OK", completableFuture.join()),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    responseMessage(e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/refId/{id}")
    public ResponseEntity<ResponseMessage> getTransactionByRefId(@PathVariable String id) {
        try {
            CompletableFuture<TransactionModel> completableFuture =
                    queryGateway.query(
                            TransactionQuery.builder().refId(id).build(),
                            ResponseTypes.instanceOf(TransactionModel.class));
            return new ResponseEntity<>(
                    responseMessage("OK", completableFuture.join()),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    responseMessage(e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
