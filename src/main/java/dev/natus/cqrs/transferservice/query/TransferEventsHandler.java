package dev.natus.cqrs.transferservice.query;

import dev.natus.cqrs.common.event.TransferCreatedEvent;
import dev.natus.cqrs.transferservice.query.data.TransferEntity;
import dev.natus.cqrs.transferservice.query.data.TransferRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransferEventsHandler {

    private final TransferRepository transferRepository;

    public TransferEventsHandler(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    @EventHandler
    public void on(TransferCreatedEvent transferCreatedEvent) {
        log.info("{}, Handling TransferCreatedEvent.. {}",
                transferCreatedEvent.getRefId(),
                transferCreatedEvent);
        TransferEntity transferEntity = new TransferEntity();
        transferEntity.setTransferId(transferCreatedEvent.getTransferId());
        transferEntity.setAccountId(transferCreatedEvent.getAccountId());
        transferEntity.setToAccountId(transferCreatedEvent.getToAccountId());
        transferEntity.setAmount(transferCreatedEvent.getAmount());
        transferEntity.setTransactionId(transferCreatedEvent.getTransactionId());
        transferEntity.setRefId(transferCreatedEvent.getRefId());
        transferEntity.setCreateDate(transferCreatedEvent.getCreateDate());
        transferRepository.save(transferEntity);
    }
}
