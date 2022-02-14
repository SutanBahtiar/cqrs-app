package dev.natus.cqrs.common.event;

import dev.natus.cqrs.common.enums.TransactionCode;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class TransactionCreatedEvent {
    private String transactionId;
    private TransactionCode transactionCode;
    private String accountId;
    private String toAccountId;
    private int amount;
    private String refId;
    private Date createdDate;
}
