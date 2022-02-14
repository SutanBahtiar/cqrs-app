package dev.natus.cqrs.transactionservice.command.controller;

import dev.natus.cqrs.common.enums.TransactionCode;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateTransactionRequest {
    private TransactionCode transactionCode;
    private String accountId;
    private String toAccountId;
    private int amount;
}
