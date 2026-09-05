# SpendWise

SpendWise is a personal finance and budget tracker built with React and Spring Boot. Track expenses, set spending goals, review charts and PDF reports, and manage account preferences.

## Features

- Registration, session-based sign-in, security-question password recovery, and profile settings
- Expense history, filtering, recurring expenses, and spending goals
- Weekly, monthly, and yearly reports with PDF export
- Achievements, dark mode, and an accessible font option
- Optional Gemini-powered spending suggestions and currency conversion through UniRate

## Stack

React 19, Vite, Tailwind CSS, Java 17, Spring Boot 3.5, Spring Data JPA, and PostgreSQL.

## Run locally

Requires Node.js 20.19+ (or 22.12+), npm, Java 17, and PostgreSQL. Maven is available through the included wrapper.

```sh
git clone git@github.com:eppm27/SpendWise.git
cd SpendWise
createdb spendwise
cd backend
cp .env.example .env.local
```

Edit `backend/.env.local` to match your PostgreSQL username and password. Then, in the backend directory:

```sh
set -a
. ./.env.local
set +a
./mvnw spring-boot:run
```

The backend listens at `http://localhost:8080/api` and creates the database tables and default categories on startup. Demo users are disabled by default; create your own account through Sign Up.

In a second terminal:

```sh
cd SpendWise/frontend
npm ci
npm run dev
```

Open `http://localhost:5173`. Vite forwards `/api` requests to the backend. For a local production-build preview, run `npm run build` followed by `npm run preview`; keep the backend running.

## Optional integrations

Set `GEMINI_API_KEY` and `UNIRATE_API_KEY` in `backend/.env.local`, reload the environment, and restart the backend. Currency conversion is authenticated and handled entirely by Spring Boot. No provider keys belong in frontend variables or browser bundles. External integrations require valid keys and provider access.

Local environment files, generated builds, uploads, and credentials are excluded from this repository. No database contents or old Git history are included.

## Deployment

See [DEPLOYMENT.md](DEPLOYMENT.md) for Vercel frontend routing, Render backend/database setup, production cookies, and required environment variables.

## Checks

```sh
cd backend
./mvnw test
cd ../frontend
npm ci
npm run lint
npm run build
npm audit
```

## Credits

Maintained in this personal repository by Ellis Mon. The original application was developed collaboratively for ELEC5619 by Mingxiang Zhang, Ellis Mon, Shengming Cui, Bing Zhou, and Yuchuan Hu. Their contributions are acknowledged here.
