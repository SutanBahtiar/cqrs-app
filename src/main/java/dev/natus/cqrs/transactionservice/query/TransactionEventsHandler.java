package dev.natus.cqrs.transactionservice.query;

import dev.natus.cqrs.common.event.TransactionCreatedEvent;
import dev.natus.cqrs.transactionservice.query.data.TransactionEntity;
import dev.natus.cqrs.transactionservice.query.data.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransactionEventsHandler {

    private final TransactionRepository transactionRepository;

    public TransactionEventsHandler(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @EventHandler
    public void on(TransactionCreatedEvent transactionCreatedEvent) {
        log.info("{}, Handling TransactionCreatedEvent, save transaction: {}",
                transactionCreatedEvent.getRefId(),
                transactionCreatedEvent);
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setTransactionId(transactionCreatedEvent.getTransactionId());
        transactionEntity.setTransactionCode(transactionCreatedEvent.getTransactionCode());
        transactionEntity.setAccountId(transactionCreatedEvent.getAccountId());
        transactionEntity.setToAccountId(transactionCreatedEvent.getToAccountId());
        transactionEntity.setAmount(transactionCreatedEvent.getAmount());
        transactionEntity.setRefId(transactionCreatedEvent.getRefId());
        transactionEntity.setCreateDate(transactionCreatedEvent.getCreatedDate());
        transactionRepository.save(transactionEntity);
    }
}
