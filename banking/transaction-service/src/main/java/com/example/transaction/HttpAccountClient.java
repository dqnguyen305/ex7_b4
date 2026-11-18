package com.example.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Component
public class HttpAccountClient implements AccountClient {

    private static final Logger logger = LoggerFactory.getLogger(HttpAccountClient.class);

    private final RestTemplate restTemplate;
    private final String accountServiceBaseUrl;

    public HttpAccountClient(
            RestTemplate restTemplate,
            @Value("${account-service.base-url}") String accountServiceBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.accountServiceBaseUrl = accountServiceBaseUrl;
    }

    @Override
    public void verifyAccountExists(long accountId) {
        logger.info("Checking source account id={} through account-service", accountId);
        try {
            restTemplate.getForEntity(
                    accountServiceBaseUrl + "/api/accounts/{id}",
                    Void.class,
                    accountId
            );
        } catch (HttpClientErrorException.NotFound exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Source account not found", exception);
        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Could not verify source account with account-service",
                    exception
            );
        }
        logger.info("Source account id={} was verified by account-service", accountId);
    }
}
