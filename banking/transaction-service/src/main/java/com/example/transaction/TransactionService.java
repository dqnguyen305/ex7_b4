package com.example.transaction;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class TransactionService {

    private final AccountClient accountClient;

    public TransactionService(AccountClient accountClient) {
        this.accountClient = accountClient;
    }

    public TransferResponse validateTransfer(TransferRequest request) {
        if (request.sourceAccountId().equals(request.destinationAccountId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Source and destination accounts must be different"
            );
        }

        accountClient.verifyAccountExists(request.sourceAccountId());
        return new TransferResponse(
                UUID.randomUUID(),
                request.sourceAccountId(),
                request.destinationAccountId(),
                request.amount(),
                "SOURCE_ACCOUNT_VERIFIED"
        );
    }
}
