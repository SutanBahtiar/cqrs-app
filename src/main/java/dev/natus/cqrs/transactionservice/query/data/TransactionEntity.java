package dev.natus.cqrs.transactionservice.query.data;

import dev.natus.cqrs.common.enums.TransactionCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "TRANSACTION")
public class TransactionEntity implements Serializable {
    private static final long serialVersionUID = -3185598910866303040L;

    @Id
    @Column(name = "TRANSACTION_ID", unique = true)
    private String transactionId;

    @Column(name = "ACCOUNT_ID")
    private String accountId;

    @Column(name = "TO_ACCOUNT_ID")
    private String toAccountId;

    @Column(name = "TRANSACTION_CODE")
    @Enumerated(EnumType.ORDINAL)
    private TransactionCode transactionCode;

    @Column(name = "AMOUNT")
    private int amount;

    @Column(name = "REF_ID")
    private String refId;

    @Column(name = "CREATED_DATE")
    private Date createDate;
}
