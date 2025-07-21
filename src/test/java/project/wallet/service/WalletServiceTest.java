package project.wallet.service;

// WalletServiceTest.java


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.wallet.entity.Wallet;
import project.wallet.exeption.*;
import project.wallet.repository.WalletRepository;
import project.wallet.service.WalletService;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository repository;

    @InjectMocks
    private WalletService service;

    private final UUID walletId = UUID.randomUUID();
    private final BigDecimal amount = new BigDecimal("100.00");

    @Test
    void withdrawSuccess() {
        Wallet wallet = new Wallet(walletId,new BigDecimal("200.00"),1L);


        when(repository.findByIdAndUpdate(walletId)).thenReturn(Optional.of(wallet));
        when(repository.withdraw(walletId, amount, 1L)).thenReturn(1);

        assertDoesNotThrow(() -> service.withdraw(walletId, amount));
    }

    @Test
    void withdrawWalletNotFound() {
        when(repository.findByIdAndUpdate(walletId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () ->
                service.withdraw(walletId, amount));
    }

    @Test
    void withdrawInsufficientFunds() {
        Wallet wallet =  new Wallet(walletId,new BigDecimal("50.00"),1L);


        when(repository.findByIdAndUpdate(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(InsufficientFundsException.class, () ->
                service.withdraw(walletId, amount));
    }

    @Test
    void withdrawConcurrentFailure() {
        Wallet wallet =  new Wallet(walletId,new BigDecimal("200.00"),1L);

        when(repository.findByIdAndUpdate(walletId)).thenReturn(Optional.of(wallet));
        when(repository.withdraw(walletId, amount, 1L)).thenReturn(0); // Версия изменилась

        assertThrows(ConcurrentOperationException.class, () ->
                service.withdraw(walletId, amount));
    }

    @Test
    void depositSuccess() {
        Wallet wallet =  new Wallet(walletId,new BigDecimal("0.00"),1L);


        when(repository.findByIdAndUpdate(walletId)).thenReturn(Optional.of(wallet));
        when(repository.deposit(walletId, amount, 1L)).thenReturn(1);

        assertDoesNotThrow(() -> service.deposit(walletId, amount));
    }

    @Test
    void getWalletSuccess() {
        when(repository.findByIdReadOnly(walletId))
                .thenReturn(Optional.of(new Wallet(walletId, amount, 1L)));

        assertEquals(amount, service.getWallet(walletId));
    }

    @Test
    void getWalletNotFound() {
        when(repository.findByIdReadOnly(walletId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () ->
                service.getWallet(walletId));
    }
}