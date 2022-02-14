package dev.natus.cqrs.accountservice.query.controller;

import dev.natus.cqrs.common.model.AccountModel;
import dev.natus.cqrs.common.model.ResponseMessage;
import dev.natus.cqrs.common.query.AccountQuery;
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
@RequestMapping(value = "/account")
public class AccountQueryController {

    private final QueryGateway queryGateway;

    public AccountQueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping
    public ResponseEntity<ResponseMessage> getAccounts() {
        try {
            CompletableFuture<List<AccountModel>> completableFuture =
                    queryGateway.query(new AccountQuery(),
                            ResponseTypes.multipleInstancesOf(AccountModel.class));
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
    public ResponseEntity<ResponseMessage> getAccount(@PathVariable String id) {
        try {
            CompletableFuture<AccountModel> completableFuture =
                    queryGateway.query(
                            new AccountQuery(id),
                            ResponseTypes.instanceOf(AccountModel.class));
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
