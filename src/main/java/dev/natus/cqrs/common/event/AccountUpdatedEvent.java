package dev.natus.cqrs.common.event;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class AccountUpdatedEvent {
    private String accountId;
    private String owedToAccountId;
    private String transactionId;
    private String refId;
    private int balanceAmount;
    private Date createDate;
    private Date updatedDate;
}
