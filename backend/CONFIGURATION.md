# Local configuration

Requires Java 17+, Maven, and a PostgreSQL database named `spendwise`.

Copy `.env.example` to `.env.local` and supply your local credentials. Existing local values have been preserved in `.env.local` when preparing this repository. This file is ignored by Git.

From the backend directory, load the variables before starting Spring Boot:

```sh
set -a
. ./.env.local
set +a
mvn spring-boot:run
```

The API runs at `http://localhost:8080/api`. `DB_URL` and `DB_USERNAME` are optional overrides. `DB_PASSWORD` is required. Set `GEMINI_API_KEY` to enable AI advice and `UNIRATE_API_KEY` for currency conversion. Both keys remain on the backend.

For production, see [DEPLOYMENT.md](../DEPLOYMENT.md).

Run backend tests with `mvn test`. From the frontend directory, install dependencies with `npm ci`, run `npm run build` and `npm run lint`, and start development with `npm run dev`.
