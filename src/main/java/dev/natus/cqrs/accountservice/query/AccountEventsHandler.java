package dev.natus.cqrs.accountservice.query;

import dev.natus.cqrs.accountservice.query.data.AccountEntity;
import dev.natus.cqrs.accountservice.query.data.AccountRepository;
import dev.natus.cqrs.common.event.AccountCreatedEvent;
import dev.natus.cqrs.common.event.AccountUpdatedEvent;
import dev.natus.cqrs.common.event.AccountUpdatedRefIdEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AccountEventsHandler {

    private final AccountRepository accountRepository;

    public AccountEventsHandler(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @EventHandler
    public void on(AccountCreatedEvent accountCreatedEvent) {
        log.info("Handling AccountCreatedEvent, Save Account: {}", accountCreatedEvent);

        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setAccountId(accountCreatedEvent.getAccountId());
        accountEntity.setRefId(accountCreatedEvent.getRefId());
        accountEntity.setCreateDate(accountCreatedEvent.getCreateDate());

        try {
            accountRepository.save(accountEntity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @EventHandler
    public void on(AccountUpdatedEvent accountUpdatedEvent) {
        log.info("Handling AccountUpdatedEvent, Update Account: {}", accountUpdatedEvent);

        try {
            AccountEntity accountEntity =
                    accountRepository
                            .findById(accountUpdatedEvent.getAccountId())
                            .get();
            accountEntity.setBalanceAmount(
                    accountEntity.getBalanceAmount() + accountUpdatedEvent.getBalanceAmount()
            );

            if (accountEntity.getBalanceAmount() < 0)
                accountEntity.setOwedToAccountId(accountUpdatedEvent.getOwedToAccountId());

            accountEntity.setTransactionId(accountUpdatedEvent.getTransactionId());
            accountEntity.setUpdatedDate(accountUpdatedEvent.getUpdatedDate());

            accountRepository.save(accountEntity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @EventHandler
    public void on(AccountUpdatedRefIdEvent accountUpdatedRefIdEvent) {
        log.info("Handling AccountUpdatedRefIdEvent, Update Account: {}", accountUpdatedRefIdEvent);

        try {
            AccountEntity accountEntity =
                    accountRepository
                            .findById(accountUpdatedRefIdEvent.getAccountId())
                            .get();
            accountEntity.setRefId(accountUpdatedRefIdEvent.getRefId());
            accountEntity.setUpdatedDate(accountUpdatedRefIdEvent.getUpdatedDate());

            accountRepository.save(accountEntity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
