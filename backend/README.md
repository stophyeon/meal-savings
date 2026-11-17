# Meal Savings API

Spring Boot REST API for local account signup/login. The database target is Amazon RDS for Oracle. No RDS endpoint or credentials are stored in this repository.

## IntelliJ setup

1. Open the repository root in IntelliJ IDEA.
2. Open `backend/pom.xml` and choose **Load Maven Project** if IntelliJ does not import it automatically.
3. Set the environment variables from `.env.example` in **Run → Edit Configurations → Environment variables**.
4. Run `MealSavingsApiApplication`.

Use a generated JWT key for local development, for example `openssl rand -base64 32`. Put the result in the run configuration only. In AWS, load database credentials and the JWT signing key from AWS Secrets Manager or another secret store. Do not commit real values.

## Amazon RDS for Oracle

Set `ORACLE_JDBC_URL` to the JDBC Thin URL supplied for the RDS Oracle instance, for example `jdbc:oracle:thin:@//<endpoint>:1521/<service-name>`. The VPC route, subnet routing, and RDS security group must allow the backend host to reach the instance port. If the connection uses TCPS, set the corresponding Oracle TLS descriptor and trust-store/JVM options in the deployment environment.

Create or select an application schema and run `src/main/resources/db/oracle/schema-v1.sql` once as that schema user before starting the API. Hibernate uses `ddl-auto: validate`; it checks the mapping and does not create or alter production tables.

## Auth API

### `POST /api/auth/signup`

Request:

```json
{
  "name": "홍길동",
  "email": "user@example.com",
  "password": "at-least-8-characters"
}
```

Returns HTTP 201 and an application-issued bearer access token. The local account and its `LOCAL` identity are inserted in one transaction.

### `POST /api/auth/login`

Request:

```json
{
  "email": "user@example.com",
  "password": "at-least-8-characters"
}
```

Returns HTTP 200 and the same token response shape. Email is trimmed and lowercased. Passwords are stored with BCrypt; the raw password is never persisted or returned.

Both routes validate request bodies. Duplicate email returns HTTP 409, invalid credentials return HTTP 401, and input validation errors return HTTP 400.

## OAuth extension point

`APP_AUTH_IDENTITY` separates an application account from its login identities. A local account uses `(LOCAL, normalized email)` as the identity key. A future Google/Kakao/Naver login should verify the provider response, use its stable subject ID as `PROVIDER_SUBJECT`, then find or link the application account under an explicit verified-email policy. It can then call `JwtTokenService.issueFor(account, provider)` so OAuth and password login issue the same application access-token format.

OAuth provider callback routes and client secrets are intentionally not implemented yet. The provider enum and schema allow the later flow without making the current local login depend on one social provider.

## Build

From this directory, use IntelliJ's Maven tool window or run:

```bash
mvn -DskipTests package
```

The API cannot start until the Oracle schema, Oracle environment variables, and a Base64 JWT key of at least 32 decoded bytes are configured.
