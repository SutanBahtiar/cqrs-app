package dev.natus.cqrs.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DepositSummaryModel {
    private String accountId;
    private int amount;
    private Date updatedDate;
}
