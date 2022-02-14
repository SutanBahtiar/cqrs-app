package dev.natus.cqrs.transactionservice.query;

import dev.natus.cqrs.common.model.DepositModel;
import dev.natus.cqrs.common.model.TransferModel;
import dev.natus.cqrs.common.query.DepositQuery;
import dev.natus.cqrs.common.query.TransferQuery;
import dev.natus.cqrs.depositservice.query.DepositQueryHandler;
import dev.natus.cqrs.transactionservice.query.controller.TransactionModel;
import dev.natus.cqrs.transactionservice.query.data.TransactionEntity;
import dev.natus.cqrs.transactionservice.query.data.TransactionRepository;
import dev.natus.cqrs.transferservice.query.TransferQueryHandler;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TransactionQueryHandler {

    private final TransactionRepository transactionRepository;
    private final QueryGateway queryGateway;

    public TransactionQueryHandler(TransactionRepository transactionRepository,
                                   QueryGateway queryGateway) {
        this.transactionRepository = transactionRepository;
        this.queryGateway = queryGateway;
    }

    @QueryHandler
    public List<TransactionModel> getTransactions(TransactionQuery transactionQuery) {
        log.info("Query Get Transactions");
        return transactionRepository
                .findAll().stream()
                .map(getTransactionModelFunction())
                .collect(Collectors.toList());
    }

    private Function<TransactionEntity, TransactionModel> getTransactionModelFunction() {
        return transactionEntity -> {
            CompletableFuture<List<DepositModel>> depositModelsCompletableFuture =
                    getDepositModelsCompletableFuture(transactionEntity.getTransactionId());

            CompletableFuture<List<TransferModel>> transfersModelCompletableFuture =
                    getTransferModelCompletableFuture(transactionEntity.getTransactionId());

            CompletableFuture.allOf(depositModelsCompletableFuture, transfersModelCompletableFuture).join();

            try {
                Object details = depositModelsCompletableFuture.get().isEmpty()
                        ? transfersModelCompletableFuture.get()
                        : depositModelsCompletableFuture.get();

                return getTransactionModel(transactionEntity, details);
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.getMessage(), e);
                return new TransactionModel();
            }
        };
    }

    @QueryHandler
    public TransactionModel getTransaction(TransactionQuery transactionQuery) {
        log.info("Query Get Transaction By Id: {}", transactionQuery.getTransactionId());

        CompletableFuture<List<DepositModel>> depositModelsCompletableFuture =
                getDepositModelsCompletableFuture(transactionQuery.getTransactionId());

        CompletableFuture<List<TransferModel>> transfersModelCompletableFuture =
                getTransferModelCompletableFuture(transactionQuery.getTransactionId());

        CompletableFuture.allOf(depositModelsCompletableFuture, transfersModelCompletableFuture).join();

        try {
            Object details = depositModelsCompletableFuture.get().isEmpty()
                    ? transfersModelCompletableFuture.get()
                    : depositModelsCompletableFuture.get();

            return transactionRepository
                    .findById(transactionQuery.getTransactionId())
                    .map(transactionEntity -> getTransactionModel(transactionEntity, details))
                    .orElse(new TransactionModel());
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
            return new TransactionModel();
        }
    }

    /**
     * call deposit-service
     *
     * @see DepositQueryHandler#getDepositsByTransactionId(DepositQuery)
     */
    private CompletableFuture<List<DepositModel>> getDepositModelsCompletableFuture(String transactionId) {
        DepositQuery depositQuery = DepositQuery.builder()
                .transactionId(transactionId)
                .build();
        return queryGateway
                .query(depositQuery,
                        ResponseTypes.multipleInstancesOf(DepositModel.class));
    }

    /**
     * call transfer-service
     *
     * @see TransferQueryHandler#getTransferByTransactionId(TransferQuery)
     */
    private CompletableFuture<List<TransferModel>> getTransferModelCompletableFuture(String transactionId) {
        TransferQuery transferQuery = TransferQuery.builder()
                .transactionId(transactionId)
                .build();
        return queryGateway
                .query(transferQuery,
                        ResponseTypes.multipleInstancesOf(TransferModel.class));
    }

    private TransactionModel getTransactionModel(TransactionEntity transactionEntity,
                                                 Object details) {
        return new TransactionModel(
                transactionEntity.getTransactionId(),
                transactionEntity.getTransactionCode(),
                transactionEntity.getAccountId(),
                transactionEntity.getToAccountId(),
                transactionEntity.getAmount(),
                transactionEntity.getRefId(),
                transactionEntity.getCreateDate(),
                details);
    }
}
