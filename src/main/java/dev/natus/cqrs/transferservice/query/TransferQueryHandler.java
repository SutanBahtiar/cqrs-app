package dev.natus.cqrs.transferservice.query;

import dev.natus.cqrs.common.model.TransferModel;
import dev.natus.cqrs.common.query.TransferQuery;
import dev.natus.cqrs.transferservice.query.data.TransferEntity;
import dev.natus.cqrs.transferservice.query.data.TransferRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TransferQueryHandler {

    private final TransferRepository transferRepository;
    private final QueryGateway queryGateway;

    public TransferQueryHandler(TransferRepository transferRepository,
                                QueryGateway queryGateway) {
        this.transferRepository = transferRepository;
        this.queryGateway = queryGateway;
    }

    private static TransferModel map(TransferEntity transferEntity) {
        return new TransferModel(transferEntity.getTransferId(),
                transferEntity.getAccountId(),
                transferEntity.getToAccountId(),
                transferEntity.getTransactionId(),
                transferEntity.getAmount(),
                transferEntity.getRefId(),
                transferEntity.getCreateDate());
    }

    @QueryHandler
    public List<TransferModel> getTransferByTransactionId(TransferQuery transferQuery) {
        log.info("Query Get Transfer By : {}", transferQuery);
        List<TransferEntity> transferEntities = null;
        if (Optional.ofNullable(transferQuery.getTransactionId()).isPresent())
            transferEntities = transferRepository.findByTransactionId(transferQuery.getTransactionId());
        else if (Optional.ofNullable(transferQuery.getAccountId()).isPresent())
            transferEntities = getByAccountId(transferQuery.getAccountId());
        else if (Optional.ofNullable(transferQuery.getToAccountId()).isPresent())
            transferEntities = transferRepository.findByToAccountId(transferQuery.getToAccountId());

        if (Optional.ofNullable(transferEntities).isPresent()) {
            return transferEntities.stream().map(TransferQueryHandler::map)
                    .collect(Collectors.toList());
        }

        return Collections.EMPTY_LIST;
    }

    private List<TransferEntity> getByAccountId(String accountId) {
        return transferRepository.findByAccountId(accountId);
    }
}
