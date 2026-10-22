# Expense Splitter Backend

An API for groups to record shared expenses and calculate who owes whom.
It tracks repayments, suggests a simplified settle-up plan, and turns text into reviewable expense drafts.

**Live Swagger UI:** [Open the deployed API documentation](https://expense-splitter-xdbm.onrender.com/swagger-ui/index.html#/)

> **Project status:** The backend API is implemented and deployed. The React frontend described in the guide is not part of this project yet.

## Tech stack

- Java 21
- Spring Boot, Spring Data JPA
- Spring Security with JWT authentication
- Spring AI
- PostgreSQL
- Docker
- OpenAPI / Swagger UI

## Backend features

- Register and log in; receive a JWT for protected API requests.
- Create groups, add members, and view group information.
- Record expenses with a payer and the participants who share the cost. Expenses use equal splitting.
- View each member's net balance and record repayments.
- Generate a settle-up plan that reduces the number of repayments.
- Parse a natural-language expense description into a structured draft with Spring AI.
- Persist application data in PostgreSQL.

Protected API routes require authentication, and actions on a specific group are limited to its members. Only the creator can delete a group. A member can be removed or leave only when that member's balance is zero; the creator cannot leave the group.

## API overview

All paths are relative to the deployed server. Use the Swagger UI's **Authorize** button to provide a bearer token for protected routes.

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `POST` | `/api/auth/register` | Register an account |
| `POST` | `/api/auth/login` | Log in and receive a JWT |
| `GET`, `POST` | `/api/groups` | List your groups or create one |
| `GET`, `DELETE` | `/api/groups/{groupId}` | View or delete a group |
| `POST` | `/api/groups/{groupId}/members` | Add a member |
| `DELETE` | `/api/groups/{groupId}/members/{userId}` | Remove a member or leave |
| `GET`, `POST` | `/api/groups/{groupId}/expenses` | List expenses or add one |
| `GET` | `/api/groups/{groupId}/balances` | View net group balances |
| `GET` | `/api/groups/{groupId}/settle-plan` | Get suggested repayments |
| `POST` | `/api/groups/{groupId}/settlements` | Record a repayment |
| `POST` | `/api/ai/groups/{groupId}/parse-expense` | Convert text into an expense draft |

## Run locally

### Prerequisites

- Java 21
- Docker with Docker Compose
- An OpenAI API key, if using the OpenAI Spring AI provider

### 1. Start PostgreSQL

From the repository root, start the PostgreSQL service defined in `docker-compose.yml`:

```bash
docker compose up -d
```

The guide's local configuration uses database `splitter`, user `splitter`, and password `secret`.

### 2. Configure the backend

Set these values in the environment or in a local, uncommitted configuration file:

| Setting | Local value |
| --- | --- |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/splitter` |
| `SPRING_DATASOURCE_USERNAME` | `splitter` |
| `SPRING_DATASOURCE_PASSWORD` | `secret` |
| `JWT_SECRET` | A private signing secret of at least 32 characters |
| `OPENAI_API_KEY` | Your provider key, if using OpenAI |

Never commit passwords, JWT secrets, or AI provider keys. Ollama can be used instead of OpenAI when the application is configured for it.

### 3. Run Spring Boot

From the repository root:

```bash
./mvnw spring-boot:run
```

On Windows, run `mvnw.cmd spring-boot:run`. The local API is normally available at `http://localhost:8080`; open Swagger at `http://localhost:8080/swagger-ui/index.html`.

## What I learned / design decisions

### Why `BigDecimal` for money

Binary floating-point types such as `double` cannot represent many decimal amounts exactly. The app uses `BigDecimal` with a fixed two-decimal currency scale, so rounding is explicit and expense and settlement amounts remain precise. For example, `100.00` split equally among three people becomes `33.34`, `33.33`, and `33.33`; the shares still total exactly `100.00`.

### How equal splitting works

The split helper divides the amount by the participant count using two decimal places and rounds the base share down. It calculates the remaining cents, then gives one extra cent to each of the first participants until the remainder is used. This makes the result deterministic and prevents rounding from creating or losing money. The backend currently supports equal splits, as described in the guide.

### How the settle-up algorithm works

The balance calculation adds what each person paid and subtracts their share of each expense. Positive balances are owed money; negative balances owe money. Recorded settlements reduce those balances.

To simplify repayment, the algorithm repeatedly finds the person owed the most and the person who owes the most. It suggests a transfer for the smaller of those two amounts, updates a copy of the balances, and repeats until there is nothing left to settle. For balances of `+500`, `-100`, and `-400`, the plan is two payments: `400` from the person who owes `400`, then `100` from the person who owes `100`, both to the person owed `500`. This greedy approach is simple and reduces payments; it does not guarantee the globally smallest possible number for every group.

### How AI is used safely

Spring AI parses a sentence such as “Dinner 1200, I paid, with Amit and Priya” into a draft containing the description, amount, payer, and participants. It is a drafting aid, not the source of truth for money calculations. The AI endpoint should receive only the information it needs, such as group member names and the current user's name, never passwords or emails. Validate the returned amount and member names, show the draft for review, and save it only through the normal expense endpoint. If AI is unavailable, users can still add an expense manually.
