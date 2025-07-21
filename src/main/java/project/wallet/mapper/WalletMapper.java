package project.wallet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import project.wallet.dto.WalletOperationRequest;
import project.wallet.entity.Wallet;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    @Mapping(source = "id", target = "walletId")
    WalletOperationRequest toRequest(Wallet entity);

    @Mapping(source = "walletId", target = "id")
    Wallet fromRequest(WalletOperationRequest dto);
}
