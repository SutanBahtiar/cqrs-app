package dev.natus.cqrs.depositservice.query;

import dev.natus.cqrs.common.model.DepositModel;
import dev.natus.cqrs.common.query.DepositQuery;
import dev.natus.cqrs.depositservice.query.data.DepositEntity;
import dev.natus.cqrs.depositservice.query.data.DepositRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DepositQueryHandler {

    private final DepositRepository depositRepository;

    public DepositQueryHandler(DepositRepository depositRepository) {
        this.depositRepository = depositRepository;
    }

    private static DepositModel map(DepositEntity depositEntity) {
        return new DepositModel(depositEntity.getDepositId(),
                depositEntity.getTransactionCode(),
                depositEntity.getAccountId(),
                depositEntity.getTransactionId(),
                depositEntity.getAmount(),
                depositEntity.getCreateDate());
    }

    @QueryHandler
    public int getDepositAmount(DepositQuery depositQuery) {
        log.info("Query Get Deposit Amount By : {}", depositQuery.getAccountId());
        List<DepositEntity> depositEntities = getByAccountId(depositQuery.getAccountId());
        if (depositEntities.isEmpty()) return 0;
        return depositEntities.stream()
                .map(DepositEntity::getAmount).mapToInt(Integer::intValue).sum();
    }

    @QueryHandler
    public List<DepositModel> getDepositsByTransactionId(DepositQuery depositQuery) {
        log.info("Query Get Deposit By : {}", depositQuery);
        List<DepositEntity> depositEntities = null;
        if (Optional.ofNullable(depositQuery.getTransactionId()).isPresent())
            depositEntities = depositRepository.findByTransactionId(depositQuery.getTransactionId());
        else if (Optional.ofNullable(depositQuery.getAccountId()).isPresent())
            depositEntities = getByAccountId(depositQuery.getAccountId());
        else if (Optional.ofNullable(depositQuery.getTransactionCode()).isPresent())
            depositEntities = depositRepository.findByTransactionCode(depositQuery.getTransactionCode());

        if (Optional.ofNullable(depositEntities).isPresent()) {
            return depositEntities.stream().map(DepositQueryHandler::map)
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    private List<DepositEntity> getByAccountId(String accountId) {
        return depositRepository.findByAccountId(accountId);
    }
}
