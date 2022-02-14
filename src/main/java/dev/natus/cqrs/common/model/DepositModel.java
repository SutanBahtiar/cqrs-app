package dev.natus.cqrs.common.model;

import dev.natus.cqrs.common.enums.TransactionCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DepositModel {
    private String depositId;
    private TransactionCode transactionCode;
    private String accountId;
    private String transactionId;
    private int amount;
    private Date createDate;
}
