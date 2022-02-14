package dev.natus.cqrs.common.event;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class AccountUpdatedRefIdEvent {
    private String accountId;
    private String refId;
    private Date updatedDate;
}
