package com.example.banking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:banking-test",
        "spring.datasource.driver-class-name=org.h2.Driver"
})
@AutoConfigureMockMvc
class BankingApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearAccounts() {
        jdbcTemplate.update("DELETE FROM accounts");
    }

    @Test
    void createsAndRetrievesAccount() throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .contentType("application/json")
                        .content("""
                                {
                                  "accountNumber": "1000000001",
                                  "accountHolderName": "Nguyen Van A",
                                  "balance": 5000000.00
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.accountNumber", is("1000000001")))
                .andExpect(jsonPath("$.accountHolderName", is("Nguyen Van A")))
                .andExpect(jsonPath("$.balance", is(5000000.00)));

        Long id = jdbcTemplate.queryForObject(
                "SELECT id FROM accounts WHERE account_number = ?",
                Long.class,
                "1000000001"
        );
        mockMvc.perform(get("/api/accounts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber", is("1000000001")));
        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountHolderName", is("Nguyen Van A")));
    }

    @Test
    void rejectsNegativeBalance() throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .contentType("application/json")
                        .content("""
                                {
                                  "accountNumber": "1000000002",
                                  "accountHolderName": "Nguyen Van B",
                                  "balance": -1
                                }
                                """))
                .andExpect(status().isBadRequest());

        org.junit.jupiter.api.Assertions.assertEquals(
                0,
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM accounts", Integer.class)
        );
    }
}
