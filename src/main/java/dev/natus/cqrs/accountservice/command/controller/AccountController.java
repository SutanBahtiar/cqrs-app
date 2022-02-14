package dev.natus.cqrs.accountservice.command.controller;

import dev.natus.cqrs.accountservice.command.service.AccountCommandService;
import dev.natus.cqrs.common.model.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import static dev.natus.cqrs.common.model.ResponseMessage.responseMessage;

@RestController
@RequestMapping(value = "/account")
@Slf4j
public class AccountController {

    private final AccountCommandService accountCommandService;

    public AccountController(AccountCommandService accountCommandService) {
        this.accountCommandService = accountCommandService;
    }

    @PostMapping
    public ResponseEntity<ResponseMessage> createAccount(@RequestBody CreateAccountRequest createAccountRequest) {
        log.info("Request create account: {}", createAccountRequest);
        try {
            CompletableFuture<String> completableFuture = accountCommandService
                    .createAccount(createAccountRequest);
            return new ResponseEntity<>(
                    responseMessage("OK", completableFuture.join()),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(
                    responseMessage("An error occurred", null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PutMapping
    public ResponseEntity<ResponseMessage> clearRefId(@RequestBody CreateAccountRequest createAccountRequest) {
        log.info("Request clearRefId: {}", createAccountRequest);
        try {
            CompletableFuture<String> completableFuture = accountCommandService
                    .clearRefId(createAccountRequest);
            return new ResponseEntity<>(
                    responseMessage("OK", completableFuture.join()),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(
                    responseMessage("An error occurred", null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
