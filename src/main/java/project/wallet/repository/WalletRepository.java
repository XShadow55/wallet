package project.wallet.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.wallet.entity.Wallet;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.id = :id")
    Optional<Wallet> findByIdAndUpdate(UUID id);
    @Query("SELECT w FROM Wallet w WHERE w.id = :id")
    Optional<Wallet> findByIdReadOnly(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Wallet w SET w.balance = w.balance - :amount, w.version = w.version + 1 " +
            "WHERE w.id = :id AND w.balance >= :amount AND w.version = :version")
    int withdraw(
            @Param("id") UUID id,
            @Param("amount") BigDecimal amount,
            @Param("version") Long version
    );
    @Modifying
    @Query("UPDATE Wallet w SET w.balance = w.balance + :amount, w.version = w.version + 1 " +
            "WHERE w.id = :id AND w.version = :version")
    int deposit(
            @Param("id") UUID id,
            @Param("amount") BigDecimal amount,
            @Param("version") Long version
    );

}
