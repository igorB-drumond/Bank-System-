package com.banco.core_banking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

@Service 
public class TransactionService {
    
    @Autowired 
    private LedgerRepository ledgerRepository;

    public Ledger executeTransfer(String accountOrigin, String accountDestination, BigDecimal amount) {

        String transactionId = "PIX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

       Ledger debit = new Ledger(transactionId, accountOrigin,amount.negate());
       ledgerRepository.save(debit);

       Ledger credit = new Ledger(transactionId, accountDestination, amount);
       ledgerRepository.save(credit);

       System.out.println("Transção " + transactionId + " registrada com sucesso!");

       return  credit;
    }
}
