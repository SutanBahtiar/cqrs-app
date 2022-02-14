package dev.natus.cqrs.common.event;

import dev.natus.cqrs.common.enums.TransactionCode;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class DepositCreatedEvent {
    private String depositId;
    private TransactionCode transactionCode;
    private String accountId;
    private String transactionId;
    private int amount;
    private Date createDate;
    private String refId;
}
