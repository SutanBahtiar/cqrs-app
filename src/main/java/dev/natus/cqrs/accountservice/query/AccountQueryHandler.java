package dev.natus.cqrs.accountservice.query;

import dev.natus.cqrs.accountservice.query.data.AccountEntity;
import dev.natus.cqrs.accountservice.query.data.AccountRepository;
import dev.natus.cqrs.common.model.AccountModel;
import dev.natus.cqrs.common.query.AccountQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AccountQueryHandler {

    private final AccountRepository accountRepository;

    public AccountQueryHandler(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @QueryHandler
    public List<AccountModel> getAccounts(AccountQuery accountQuery) {
        log.info("Query Get Accounts");
        return accountRepository
                .findAll().stream()
                .map(this::getAccountModel)
                .collect(Collectors.toList());
    }

    @QueryHandler
    public AccountModel getAccount(AccountQuery accountQuery) {
        log.info("Query Get Account By Id: {}", accountQuery.getAccountId());
        return accountRepository
                .findById(accountQuery.getAccountId())
                .map(this::getAccountModel)
                .orElse(AccountModel.builder().build());
    }

    private AccountModel getAccountModel(AccountEntity accountEntity) {
        return AccountModel.builder()
                .accountId(accountEntity.getAccountId())
                .balanceAmount(accountEntity.getBalanceAmount())
                .owedToAccountId(accountEntity.getOwedToAccountId())
                .refId(accountEntity.getRefId())
                .transactionId(accountEntity.getTransactionId())
                .createDate(accountEntity.getCreateDate())
                .updatedDate(accountEntity.getUpdatedDate())
                .build();
    }
}
