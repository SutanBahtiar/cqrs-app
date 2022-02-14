package dev.natus.cqrs.atmservice.controller;

import dev.natus.cqrs.atmservice.service.AtmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/atm")
@Slf4j
public class AtmController {

    private static final String ERROR_MESSAGE = "Your commands can't be processed\nplease try again later";
    private final AtmService atmService;

    public AtmController(AtmService atmService) {
        this.atmService = atmService;
    }

    @PostMapping("/login")
    public ResponseEntity<AtmResponse> login(@RequestBody AtmRequest atmRequest) {
        log.info("Request login for {}", atmRequest.getAccountId());
        try {
            AtmResponse response = atmService.login(atmRequest.getAccountId());
            log.info("Response Request login \n{}", response);
            return getSuccessResponse(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return getErrorResponse(atmRequest.getRefId(), e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<AtmResponse> logout(@RequestBody AtmRequest atmRequest) {
        log.info("Request logout for {}", atmRequest.getAccountId());
        try {
            AtmResponse response = atmService.logout(atmRequest);
            log.info("Response Request logout {}", response);
            return getSuccessResponse(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return getErrorResponse(atmRequest.getRefId(), e.getMessage());
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<AtmResponse> deposit(@RequestBody AtmRequest atmRequest) {
        log.info("{}, Request deposit: {}", atmRequest.getRefId(), atmRequest);
        try {
            String result = atmService.deposit(atmRequest);
            log.info("{}, Response Request deposit: \n{}", atmRequest.getRefId(), result);
            return getSuccessResponse(atmRequest.getRefId(), result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return getErrorResponse(atmRequest.getRefId(), e.getMessage());
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<AtmResponse> transfer(@RequestBody AtmRequest atmRequest) {
        log.info("{}, Request transfer: {}", atmRequest.getRefId(), atmRequest);
        try {
            String result = atmService.transfer(atmRequest);
            log.info("{}, Response Request transfer: \n{}", atmRequest.getRefId(), result);
            return getSuccessResponse(atmRequest.getRefId(), result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return getErrorResponse(atmRequest.getRefId(), e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<AtmResponse> withdraw(@RequestBody AtmRequest atmRequest) {
        log.info("{}, Request withdraw: {}", atmRequest.getRefId(), atmRequest);
        try {
            String result = atmService.withdraw(atmRequest);
            log.info("{}, Response Request withdraw: \n{}", atmRequest.getRefId(), result);
            return getSuccessResponse(atmRequest.getRefId(), result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return getErrorResponse(atmRequest.getRefId(), e.getMessage());
        }
    }

    private ResponseEntity<AtmResponse> getSuccessResponse(AtmResponse atmResponse) {
        return new ResponseEntity<>(atmResponse, HttpStatus.OK);
    }

    private ResponseEntity<AtmResponse> getSuccessResponse(String refId,
                                                           String result) {
        return new ResponseEntity<>(
                new AtmResponse(new Date(), refId, result),
                HttpStatus.OK
        );
    }

    private ResponseEntity<AtmResponse> getErrorResponse(String refId,
                                                         String message) {
        return new ResponseEntity<>(
                new AtmResponse(new Date(), refId, message),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
