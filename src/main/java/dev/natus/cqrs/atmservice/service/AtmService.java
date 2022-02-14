package dev.natus.cqrs.atmservice.service;

import dev.natus.cqrs.atmservice.controller.AtmRequest;
import dev.natus.cqrs.atmservice.controller.AtmResponse;
import org.springframework.stereotype.Service;

@Service
public class AtmService {

    private final AtmAccountService accountService;
    private final AtmTransactionService transactionService;

    public AtmService(AtmAccountService accountService,
                      AtmTransactionService depositService) {
        this.accountService = accountService;
        this.transactionService = depositService;
    }

    public AtmResponse login(String accountId) {
        return accountService.login(accountId);
    }

    public AtmResponse logout(AtmRequest atmRequest) {
        return accountService
                .logout(
                        atmRequest.getRefId(),
                        atmRequest.getAccountId()
                );
    }

    public String deposit(AtmRequest atmRequest) {
        return transactionService
                .deposit(
                        atmRequest.getRefId(),
                        atmRequest.getAccountId(),
                        atmRequest.getAmount()
                );
    }

    public String withdraw(AtmRequest atmRequest) {
        return transactionService
                .withdraw(
                        atmRequest.getRefId(),
                        atmRequest.getAccountId(),
                        atmRequest.getAmount()
                );
    }

    public String transfer(AtmRequest atmRequest) {
        return transactionService
                .transfer(
                        atmRequest.getRefId(),
                        atmRequest.getAccountId(),
                        atmRequest.getToAccountId(),
                        atmRequest.getAmount()
                );
    }


}
