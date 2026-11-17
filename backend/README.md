# Meal Savings API

Spring Boot REST API for local account signup/login. The database target is the Aurora PostgreSQL cluster `database-1` in `ap-southeast-2`. The endpoint and database username are configured in `application.yml`; no AWS access keys, database password, or IAM token are stored in this repository.

## IntelliJ setup

1. Open the repository root in IntelliJ IDEA.
2. Open `backend/pom.xml` and choose **Load Maven Project** if IntelliJ does not import it automatically.
3. Make sure AWS CLI credentials are available to the IntelliJ-launched JVM. For a named AWS CLI profile, set `AWS_PROFILE` in **Run → Edit Configurations → Environment variables**. For AWS IAM Identity Center, sign in with `aws sso login --profile <profile-name>` first.
4. Copy `backend/.env.example` values into the IntelliJ run configuration, including `AWS_PROFILE` if it is not `default`.
5. Run `MealSavingsApiApplication`.

Use a generated JWT key for local development, for example `openssl rand -base64 32`. Put the result in the run configuration only. In AWS, give the application runtime an IAM role with the required database connection permission and load the JWT signing key from AWS Secrets Manager or another secret store. Do not commit real values.

## Amazon RDS for PostgreSQL

`application.yml` is configured for the endpoint and database name supplied for this project:

```text
jdbc:aws-wrapper:postgresql://database-1.cluster-cxe8e82imo9p.ap-southeast-2.rds.amazonaws.com:5432/postgres?sslmode=require
```

The AWS Advanced JDBC Wrapper's `iam` plugin creates and refreshes IAM database authentication tokens for new PostgreSQL connections. The JDBC URL keeps TLS enabled with `sslmode=require`. Hikari reuses established connections; the short-lived IAM token is only used when each connection is established. The wrapper uses the AWS SDK credential chain and the configured `AWS_PROFILE`.

IAM DB authentication must be enabled for the Aurora cluster. The PostgreSQL role used by the app must have `rds_iam`, and the AWS identity in the profile needs `rds-db:connect` for this cluster resource ID and database username. If `postgres` is already configured for IAM login, keep `DB_USERNAME=postgres`; otherwise configure IAM for that role before connecting. AWS IAM authentication tokens expire after 15 minutes, and the wrapper obtains refreshed tokens as needed. The cluster security group and network path must also allow the computer running IntelliJ to reach port 5432.

The target database is `postgres`. Run `src/main/resources/db/postgresql/schema-v1.sql` once as the connected database user before starting the API. Hibernate uses `ddl-auto: validate`; it checks the mapping and does not create or alter tables.

### Connect and run SQL with `psql`

The AWS CLI must be logged in as the IAM identity that has `rds-db:connect` permission. Generate a fresh token in the terminal; do not put the token in `.env`, source code, or Git:

```bash
export RDSHOST="database-1.cluster-cxe8e82imo9p.ap-southeast-2.rds.amazonaws.com"
export AWS_PROFILE="default" # replace if using a named profile
export PGPASSWORD="$(aws rds generate-db-auth-token --hostname "$RDSHOST" --port 5432 --username postgres --region ap-southeast-2)"

psql "host=$RDSHOST port=5432 dbname=postgres user=postgres sslmode=require"
```

Inside `psql`, check the connection and list tables:

```sql
SELECT current_database(), current_user, now();

SELECT table_schema, table_name
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;
```

To create the app tables for the first time, exit `psql` with `\q` and run this from the repository root while the generated token is still valid:

```bash
psql "host=$RDSHOST port=5432 dbname=postgres user=postgres sslmode=require" \
  -v ON_ERROR_STOP=1 \
  -f backend/src/main/resources/db/postgresql/schema-v1.sql
```

Reconnect using a fresh token, then query app data:

```sql
SELECT user_id, display_name, email, status, created_at
FROM app_user_account
ORDER BY created_at DESC;

SELECT identity_id, user_id, provider, provider_subject, created_at
FROM app_auth_identity
ORDER BY created_at DESC;
```

Exit `psql` with `\q`, then clear the token from the shell with `unset PGPASSWORD`.

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

The API cannot start until the PostgreSQL schema exists, AWS credentials can assume the configured profile, the database user has `rds_iam`, the IAM identity has `rds-db:connect`, and a Base64 JWT key of at least 32 decoded bytes is configured.
