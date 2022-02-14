package dev.natus.cqrs.common.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.Date;

@Builder
@Data
public class UpdateAccountCommand {
    @TargetAggregateIdentifier
    private String accountId;
    private String owedToAccountId;
    private String transactionId;
    private String refId;
    private int balanceAmount;
    private Date createDate;
    private Date updatedDate;
}
