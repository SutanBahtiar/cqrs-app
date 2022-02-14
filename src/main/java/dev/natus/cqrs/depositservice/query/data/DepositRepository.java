package dev.natus.cqrs.depositservice.query.data;

import dev.natus.cqrs.common.enums.TransactionCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepositRepository extends JpaRepository<DepositEntity, String> {
    List<DepositEntity> findByTransactionId(String transactionId);

    List<DepositEntity> findByAccountId(String accountId);

    List<DepositEntity> findByTransactionCode(TransactionCode transactionCode);
}
