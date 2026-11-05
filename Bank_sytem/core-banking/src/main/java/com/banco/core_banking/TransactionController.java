package com.banco.core_banking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;

@RestController 
public class TransactionController {

    @Autowired 
    private TransactionService transactionService;

    @GetMapping("/pix")
    public String fazerPix(@RequestParam String origem, @RequestParam String destino, @RequestParam  BigDecimal valor){
        transactionService.executeTransfer(origem, destino, valor);

        return "PIX de R$ " + valor + " tranferido de " + origem + " para " + destino + " . Verifique o banco de dados!";
    }
}