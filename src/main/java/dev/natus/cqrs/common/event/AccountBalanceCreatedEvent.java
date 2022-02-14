package dev.natus.cqrs.common.event;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class AccountBalanceCreatedEvent {
    private String accountBalanceId;
    private String accountId;
    private String owedToAccountId;
    private int balanceAmount;
    private String refId;
    private String transactionId;
    private Date updatedDate;
}
