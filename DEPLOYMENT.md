# Deploy SpendWise: Vercel + Render

The frontend sends requests to its own `/api` path. Vercel forwards those requests to Spring Boot. The browser's session cookie stays on the frontend hostname, so this setup does not depend on third-party cookies. Both providers' API keys live only on the backend.

## 1. Backend and database on Render

1. Create a Render PostgreSQL database.
2. Create a Web Service from `eppm27/SpendWise`, using `backend` as the root directory and Docker as the runtime. The included `backend/Dockerfile` builds the Java application.
3. Configure these backend environment variables:

| Variable | Value |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `DB_URL` | `jdbc:postgresql://DATABASE_HOST:5432/DATABASE_NAME` (use Render's internal database hostname when services share a region) |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `APP_ALLOWED_ORIGINS` | Exact frontend origin, e.g. `https://your-project.vercel.app`; comma-separate additional trusted custom domains |
| `GEMINI_API_KEY` | Your Gemini key |
| `UNIRATE_API_KEY` | Your UniRate key |

Render supplies `PORT`. Do not paste a `postgresql://user:password@...` URL into `DB_URL`: use the JDBC format above, with credentials in the separate variables. External database connections should use the provider's required TLS options.

You can reserve the Vercel project first to learn its hostname. Update `APP_ALLOWED_ORIGINS` when that hostname is known and redeploy the backend. Do not allow every `*.vercel.app` origin.

## 2. Configure the production API destination

After Render gives you the actual backend URL, run locally:

```sh
cd frontend
npm run configure:vercel -- https://YOUR-BACKEND.onrender.com
```

Commit and push the changed `frontend/vercel.json`. Its committed initial destination is deliberately a `.invalid` placeholder, because a live backend URL does not exist yet. Replace it before deployment. The script accepts an HTTPS origin only, without `/api`, query parameters, or credentials.

## 3. Frontend on Vercel

Import `eppm27/SpendWise`, select **frontend** as the Root Directory and **Vite** as the framework. Use `npm run build` and the `dist` output directory. The included `vercel.json` provides API proxying, API cache exclusions, and React Router deep-link fallback.

Do not configure any `VITE_*` provider keys on Vercel. Remove any old `VITE_UNIRATE_API_KEY` variable and redeploy; old deployments built with a client-side key should be removed or access-restricted, and that key replaced.

## Cookies and sessions

Production enables `Secure`, `HttpOnly`, and `SameSite=Lax`, with cookie path `/api` and no Domain attribute. This works because the browser calls Vercel's same-origin proxy. Keep this proxy architecture; pointing the browser directly at Render requires a different cross-site cookie design. Local HTTP development continues to work without `Secure`.

API responses use `Cache-Control: no-store, private`; personalized data must never be cached by the CDN. The frontend always uses relative `/api` URLs. Production understands forwarded HTTPS headers from the hosting proxy.

Sessions currently live in one backend instance's memory: redeploys/restarts sign users out. Use one instance unless you add shared session storage. Uploaded profile images currently use local disk; attach persistent storage at `/app/uploads` (writable by UID 10001) or move uploads to object storage for persistence. Vercel only hosts the frontend; PostgreSQL and Spring Boot must also be running.

## Verify after deployment

Register, sign in, refresh `/dashboard`, create an expense, and convert currencies. In browser developer tools, conversion requests should go only to `/api/currency/convert` and contain no provider key. The login response cookie should have Secure, HttpOnly, SameSite=Lax and Path=/api. Verify API responses are not cached, logout ends the session, and a logged-out currency request returns 401.

The repository is configured for deployment but has not been deployed to a hosting account automatically.
