# Banking Spring Boot service

Run the services and PostgreSQL database from this directory:

```powershell
docker compose up -d --build
docker compose logs -f account-service transaction-service
```

The `account-service` connects to `banking_db` through the `core-db` Compose
service and creates the `accounts` table on startup. PostgreSQL is also
available on port `5432`. The `account-service` listens on port `8080`, and
the `transaction-service` listens on port `8081`.

Create an account with `POST http://localhost:8080/api/accounts` and this JSON
body:

```json
{
  "accountNumber": "1000000001",
  "accountHolderName": "Nguyen Van A",
  "balance": 5000000.00
}
```

The service responds with `201 Created` and the saved account, including its
database-generated `id`. Retrieve accounts with `GET
http://localhost:8080/api/accounts` or one account with
`GET http://localhost:8080/api/accounts/{id}`. Account numbers must be unique
and balances cannot be negative.

Submit a transfer request to `POST http://localhost:8081/api/transactions`:

```json
{
  "sourceAccountId": 1,
  "destinationAccountId": 2,
  "amount": 250.00
}
```

The `transaction-service` checks the source account by calling
`http://account-service:8080/api/accounts/{id}` over the internal
`banking-network`. It returns `404 Not Found` when that account does not exist,
and rejects non-positive amounts or transfers to the same account. A successful
response confirms source-account validation; this exercise does not debit or
credit balances because the `account-service` currently exposes no transfer
operation.

The credentials in `docker-compose.yml` are for local exercise use only;
replace them before using this setup outside a local environment.

If host port `5432` is already in use, set `POSTGRES_HOST_PORT` to another
available port before starting Compose. The application still connects to
PostgreSQL internally on port `5432`.

Stop the containers with `docker compose down`. Add `-v` to also remove the
local PostgreSQL data volume.
