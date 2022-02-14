package dev.natus.cqrs.accountservice.query.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@ToString
@Entity
@Table(name = "ACCOUNT")
public class AccountEntity implements Serializable {

    private static final long serialVersionUID = 6409793428918189223L;

    @Id
    @Column(name = "ACCOUNT_ID", unique = true)
    private String accountId;

    @Column(name = "REF_ID")
    private String refId;

    @Column(name = "TRX_ID")
    private String transactionId;

    @Column(name = "BALANCE_AMOUNT")
    private int balanceAmount;

    @Column(name = "OWED_TO_ACCOUNT_ID")
    private String owedToAccountId;

    @Column(name = "CREATE_DATE")
    private Date createDate;

    @Column(name = "UPDATED_DATE")
    private Date updatedDate;
}
