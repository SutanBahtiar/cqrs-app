package dev.natus.cqrs.common.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class AccountModel {
    private String accountId;
    private String owedToAccountId;
    private String transactionId;
    private String refId;
    private int balanceAmount;
    private Date createDate;
    private Date updatedDate;
}
