package dev.natus.cqrs.atmservice.controller;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AtmRequest {
    private String accountId;
    private String toAccountId;
    private String refId;
    private int amount;
}
