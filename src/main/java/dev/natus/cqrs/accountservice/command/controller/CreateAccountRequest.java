package dev.natus.cqrs.accountservice.command.controller;

import lombok.Data;

@Data
public class CreateAccountRequest {
    private String accountId;
}
