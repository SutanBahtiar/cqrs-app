package dev.natus.cqrs.depositservice.query.data;

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
@Table(name = "DEPOSIT")
public class DepositEntity implements Serializable {
    private static final long serialVersionUID = 6615795679320924814L;

    @Id
    @Column(name = "DEPOSIT_ID", unique = true)
    private String depositId;

    @Column(name = "TRANSACTION_CODE")
    @Enumerated(EnumType.ORDINAL)
    private TransactionCode transactionCode;

    @Column(name = "ACCOUNT_ID")
    private String accountId;

    @Column(name = "TRX_ID")
    private String transactionId;

    @Column(name = "AMOUNT")
    private int amount;

    @Column(name = "REF_ID")
    private String refId;

    @Column(name = "CREATED_DATE")
    private Date createDate;
}
