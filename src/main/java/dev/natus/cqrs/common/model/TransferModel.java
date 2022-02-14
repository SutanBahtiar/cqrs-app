package dev.natus.cqrs.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransferModel {
    private String transferId;
    private String accountId;
    private String toAccountId;
    private String transactionId;
    private int amount;
    private String refId;
    private Date createDate;
}
