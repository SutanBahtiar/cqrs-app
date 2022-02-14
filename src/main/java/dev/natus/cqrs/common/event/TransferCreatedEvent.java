package dev.natus.cqrs.common.event;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class TransferCreatedEvent {
    private String transferId;
    private String accountId;
    private String toAccountId;
    private String transactionId;
    private int amount;
    private String refId;
    private Date createDate;
}
