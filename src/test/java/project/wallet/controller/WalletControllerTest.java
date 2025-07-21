package project.wallet.controller;




import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import project.wallet.controller.WalletController;
import project.wallet.dto.WalletOperationRequest;
import project.wallet.exeption.*;
import project.wallet.service.WalletService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
@AutoConfigureMockMvc(addFilters = false)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WalletService walletService;

    private final UUID walletId = UUID.randomUUID();
    private final String validJson = """
        {
            "walletId": "%s",
            "operationType": "DEPOSIT",
            "amount": 100.00
        }
        """.formatted(walletId);

    @Test
    void updateWalletDepositSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Операция выполнена"));
    }

    @Test
    void updateWalletInvalidJson() throws Exception {
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Невалидный JSON"));
    }

    @Test
    void updateWalletNotFound() throws Exception {
        doThrow(new WalletNotFoundException("Ошибка"))
                .when(walletService).deposit(any(), any());

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void getWalletSuccess() throws Exception {
        when(walletService.getWallet(walletId)).thenReturn(new BigDecimal("100.00"));

        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId))

                .andExpect(status().isOk())
                .andExpect(content().string("100.00"));
    }
}