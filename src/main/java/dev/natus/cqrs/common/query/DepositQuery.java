package dev.natus.cqrs.common.query;

import dev.natus.cqrs.common.enums.TransactionCode;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DepositQuery {
    private String transactionId;
    private String accountId;
    private TransactionCode transactionCode;
}
