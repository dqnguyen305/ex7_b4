package com.example.banking;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AccountService {

    private static final RowMapper<Account> ACCOUNT_ROW_MAPPER = (resultSet, rowNumber) ->
            new Account(
                    resultSet.getLong("id"),
                    resultSet.getString("account_number"),
                    resultSet.getString("account_holder_name"),
                    resultSet.getBigDecimal("balance")
            );

    private final JdbcTemplate jdbcTemplate;

    public AccountService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Account create(CreateAccountRequest request) {
        try {
            jdbcTemplate.update(
                    "INSERT INTO accounts (account_number, account_holder_name, balance) VALUES (?, ?, ?)",
                    request.accountNumber(),
                    request.accountHolderName(),
                    request.balance()
            );
        } catch (DuplicateKeyException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An account with this account number already exists",
                    exception
            );
        }

        return findByAccountNumber(request.accountNumber());
    }

    public List<Account> findAll() {
        return jdbcTemplate.query(
                "SELECT id, account_number, account_holder_name, balance FROM accounts ORDER BY id",
                ACCOUNT_ROW_MAPPER
        );
    }

    public Account findById(long id) {
        List<Account> accounts = jdbcTemplate.query(
                "SELECT id, account_number, account_holder_name, balance FROM accounts WHERE id = ?",
                ACCOUNT_ROW_MAPPER,
                id
        );
        if (accounts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }
        return accounts.get(0);
    }

    private Account findByAccountNumber(String accountNumber) {
        return jdbcTemplate.queryForObject(
                "SELECT id, account_number, account_holder_name, balance FROM accounts WHERE account_number = ?",
                ACCOUNT_ROW_MAPPER,
                accountNumber
        );
    }
}
