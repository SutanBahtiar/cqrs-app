package dev.natus.cqrs.transactionservice.query.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.natus.cqrs.common.enums.TransactionCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionModel {
    private String transactionId;
    private TransactionCode transactionCode;
    private String accountId;
    private String toAccountId;
    private int amount;
    private String refId;
    private Date createdDate;

    private Object details;
}
