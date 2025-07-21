package project.wallet.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import project.wallet.dto.WalletOperationRequest;
import project.wallet.entity.Wallet;
import project.wallet.exeption.ConcurrentOperationException;
import project.wallet.exeption.InsufficientFundsException;
import project.wallet.exeption.WalletNotFoundException;
import project.wallet.mapper.WalletMapper;
import project.wallet.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class WalletService {
    private static final int MAX_RETRIES = 3;
    private final WalletRepository repository;

    public WalletService( WalletRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void withdraw(UUID walletId, BigDecimal amount) {
        for (int i = 0; i < MAX_RETRIES; i++) {
            // 1. Блокируем запись для чтения
            Wallet wallet = repository.findByIdAndUpdate(walletId)
                    .orElseThrow(() -> new WalletNotFoundException("Кошелька не существует"));

            if (wallet.getBalance().compareTo(amount) <= 0) {
                throw new InsufficientFundsException("Недостаточно средств");
            }
            // 2. Атомарное обновление баланса
            int updated = repository.withdraw(
                    walletId,
                    amount,
                    wallet.getVersion()
            );

            if (updated > 0) return; // Успешное обновление
        }
        throw new ConcurrentOperationException("Вывод средств не удался, повторите позже");
    }
    @Transactional
    public void deposit(UUID walletId, BigDecimal amount) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            // 1. Блокируем запись для чтения
            Wallet wallet = repository.findByIdAndUpdate(walletId)
                    .orElseThrow(() -> new WalletNotFoundException("Кошелька не существует"));

            // 2. Атомарное обновление баланса
            int updated = repository.deposit(
                    walletId,
                    amount,
                    wallet.getVersion()
            );


            if (updated > 0) {
                return; // Успешное обновление
            }
        }
        throw new ConcurrentOperationException("Пополнение средств не удалось , повторите позже");
    }
    public BigDecimal getWallet(UUID walletId){
        return repository.findByIdReadOnly(walletId)
                .map(Wallet::getBalance)
                .orElseThrow(() -> new WalletNotFoundException("Кошелек не найден"));
    }
}
