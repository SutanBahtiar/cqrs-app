package dev.natus.cqrs.transferservice.query.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "TRANSFER")
public class TransferEntity {
    @Id
    @Column(name = "TRANSFER_ID", unique = true)
    private String transferId;

    @Column(name = "ACCOUNT_ID")
    private String accountId;

    @Column(name = "TO_ACCOUNT_ID")
    private String toAccountId;

    @Column(name = "TRX_ID")
    private String transactionId;

    @Column(name = "AMOUNT")
    private int amount;

    @Column(name = "REF_ID")
    private String refId;

    @Column(name = "CREATED_DATE")
    private Date createDate;
}
