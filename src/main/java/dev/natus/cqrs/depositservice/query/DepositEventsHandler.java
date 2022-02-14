package dev.natus.cqrs.depositservice.query;

import dev.natus.cqrs.common.event.DepositCreatedEvent;
import dev.natus.cqrs.depositservice.query.data.DepositEntity;
import dev.natus.cqrs.depositservice.query.data.DepositRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DepositEventsHandler {

    private final DepositRepository depositRepository;

    public DepositEventsHandler(DepositRepository depositRepository) {
        this.depositRepository = depositRepository;
    }

    @EventHandler
    public void on(DepositCreatedEvent depositCreatedEvent) {
        log.info("{}, Handling DepositCreatedEvent, save deposit: {}",
                depositCreatedEvent.getRefId(),
                depositCreatedEvent);
        DepositEntity depositEntity = new DepositEntity();
        depositEntity.setDepositId(depositCreatedEvent.getDepositId());
        depositEntity.setAccountId(depositCreatedEvent.getAccountId());
        depositEntity.setAmount(depositCreatedEvent.getAmount());
        depositEntity.setTransactionCode(depositCreatedEvent.getTransactionCode());
        depositEntity.setTransactionId(depositCreatedEvent.getTransactionId());
        depositEntity.setRefId(depositCreatedEvent.getRefId());
        depositEntity.setCreateDate(depositCreatedEvent.getCreateDate());
        try {
            depositRepository.save(depositEntity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
