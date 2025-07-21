package project.wallet.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.wallet.dto.WalletOperationRequest;

import project.wallet.exeption.ConcurrentOperationException;
import project.wallet.exeption.InsufficientFundsException;
import project.wallet.exeption.WalletNotFoundException;
import project.wallet.service.WalletService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")

public class WalletController {
    private final WalletService service;

    public WalletController(WalletService service) {
        this.service = service;
    }

    @PostMapping("/wallet")
    public ResponseEntity<String> updateWallet(@Validated @RequestBody WalletOperationRequest request) {
        try {
            switch (request.operationType()) {
                case WITHDRAW -> service.withdraw(request.walletId(), request.amount());
                case DEPOSIT -> service.deposit(request.walletId(), request.amount());
            }
            return ResponseEntity.ok("Операция выполнена");
        } catch (WalletNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InsufficientFundsException | ConcurrentOperationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<?> getWallet(@Validated @PathVariable UUID walletId) {
        try {
            return ResponseEntity.ok(service.getWallet(walletId));
        } catch (WalletNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
