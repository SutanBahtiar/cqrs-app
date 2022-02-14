package dev.natus.cqrs.transferservice.query.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferRepository extends JpaRepository<TransferEntity, String> {
    List<TransferEntity> findByTransactionId(String transactionId);

    List<TransferEntity> findByAccountId(String accountId);

    List<TransferEntity> findByToAccountId(String toAccountId);
}
