package dev.natus.cqrs.common.query;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TransferQuery {
    private String transactionId;
    private String accountId;
    private String toAccountId;
}
