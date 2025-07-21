package project.wallet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name="wallets")
@Getter
@Setter
@NoArgsConstructor
public class Wallet {
    @Id
    @Setter(AccessLevel.NONE)
    @Column(columnDefinition = "UUID")
    private UUID id;
    @Column(nullable = false,precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;
    @Setter(AccessLevel.NONE)
    @Version
    @Column(columnDefinition = "integer DEFAULT 0", nullable = false)
    private Long version;

    public Wallet(UUID id, BigDecimal balance, Long version) {
        this.id = id;
        this.balance = balance;
        this.version = version;
    }
}
