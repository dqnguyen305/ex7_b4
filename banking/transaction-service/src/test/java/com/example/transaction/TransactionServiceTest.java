package com.example.transaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountClient accountClient;

    @Test
    void verifiesSourceAccountBeforeReturningSuccessfulValidation() {
        TransactionService service = new TransactionService(accountClient);

        TransferResponse response = service.validateTransfer(
                new TransferRequest(1L, 2L, new BigDecimal("25.00"))
        );

        verify(accountClient).verifyAccountExists(1L);
        assertEquals(1L, response.sourceAccountId());
        assertEquals(2L, response.destinationAccountId());
        assertEquals("SOURCE_ACCOUNT_VERIFIED", response.status());
    }

    @Test
    void rejectsTransferToSameAccountWithoutCallingAccountService() {
        TransactionService service = new TransactionService(accountClient);

        assertThrows(
                ResponseStatusException.class,
                () -> service.validateTransfer(
                        new TransferRequest(1L, 1L, new BigDecimal("25.00"))
                )
        );

        verifyNoInteractions(accountClient);
    }
}
