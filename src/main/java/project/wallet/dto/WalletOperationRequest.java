package project.wallet.dto;



import lombok.NonNull;
import project.wallet.enums.OperationType;

import java.math.BigDecimal;
import java.util.UUID;

public record  WalletOperationRequest(
        @NonNull UUID walletId,
        @NonNull OperationType operationType,
        @NonNull BigDecimal amount
) {}
