package dev.natus.cqrs.common.query;

import lombok.Value;

@Value
public class DepositSummaryQuery {
    private String accountId;
}
