package dev.natus.cqrs.transactionservice.query;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TransactionQuery {
    private String transactionId;
    private String refId;
}
