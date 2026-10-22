# Expense Splitter

Record shared expenses, see who owes whom, and settle group balances.
The app splits expenses across participants and simplifies balances into fewer repayments.

**Live Swagger UI:** [Open the deployed API docs](https://expense-splitter-xdbm.onrender.com/swagger-ui/index.html#/)

## Tech stack

Java 21 · Spring Boot · Spring Data JPA · Spring Security + JWT · Spring AI · PostgreSQL · Docker

## Features

- Record group expenses and participant shares.
- Calculate net balances and suggest repayments.
- Authenticate API requests with JWT.
- Use AI for assistant-style expense help.
- Explore endpoints in Swagger UI.

## Run locally

Prerequisites: Java 21 and Docker. Start PostgreSQL:

```bash
docker run --name expense-splitter-db -e POSTGRES_DB=expenses \
  -e POSTGRES_USER=expenses -e POSTGRES_PASSWORD=change-me \
  -p 5432:5432 -d postgres:16
```

Configure the datasource, JWT secret, and the API key for the Spring AI provider in your local environment/config. Then run from the project root:

```bash
./mvnw spring-boot:run
```

On Windows, use `./mvnw.cmd spring-boot:run`. Open Swagger at `http://localhost:8080/swagger-ui/index.html` (unless the app uses a different port or path).

## What I learned / design decisions

- **Money:** `BigDecimal` avoids binary floating-point errors. Use a consistent currency scale and distribute any leftover cents/paise deterministically so shares add up to the original amount.
- **Splitting:** Divide the expense according to the selected participants and split rules. For equal splits, assign any remaining minor units in a stable order.
- **Simplifying:** Compute each person’s net as `paid − owed`. Match people who owe money with people who should receive it, transferring the smaller outstanding balance each time. This preserves everyone’s net balance and usually reduces repayments; the greedy method does not guarantee the absolute fewest transfers for every group.
- **AI safety:** Keep money calculations in deterministic backend code. Treat AI responses as suggestions, check authentication and group access before providing context, send only necessary data, and never send secrets or let AI trigger payments.
