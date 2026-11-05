package com.banco.core_banking;

import jakarta.persistence.*;
import java.math.BigDecimal;
import  java.time.LocalDateTime;
import java.util.UUID;

@Entity 
@Table(name = "ledger")

public class Ledger {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column  (name = "transaction_id", nullable = false)
    private  String transactionId;

    @Column (name = "account_id", nullable = false)
    private String accountId;

    @Column (nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column (name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Ledger() {}

    public  Ledger(String transactionId, String accountId, BigDecimal amount){
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
    }


    public  UUID getId() {return id;}
    public String getTransactionId(){return transactionId;}
    public String getAccountId(){ return accountId;}
    public BigDecimal getAmount(){return amount;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}
