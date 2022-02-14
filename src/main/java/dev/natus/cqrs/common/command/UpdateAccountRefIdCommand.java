package dev.natus.cqrs.common.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Builder
@Data
public class UpdateAccountRefIdCommand {
    @TargetAggregateIdentifier
    private String accountId;
    private String refId;
}
