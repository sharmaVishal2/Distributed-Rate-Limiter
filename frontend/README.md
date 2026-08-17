# Distributed Rate Limiter Dashboard

A responsive React dashboard for the Distributed Rate Limiter Spring Boot service. It is deliberately API-backed: it uses the service's login, rate-limit check, rules, metrics, health, and Swagger endpoints rather than bundled mock data.

## Features

- Dark infrastructure-console UI that works on desktop and mobile
- JWT login for protected API calls
- Live rate-limit tester with clear 429 handling, response timings, raw response panel, and a session view of the 5-request demo
- Create, edit, list, and delete client-specific rules
- Metrics from `GET /api/metrics`, including top clients and endpoints
- Browser-session request history (the backend currently has no read audit-history endpoint)
- Link to the service's real Swagger UI

## Tech stack

React, Vite, Tailwind CSS, Axios, and Lucide React.

## Local setup

1. Start the Spring Boot service on port 8080 (and its PostgreSQL/Redis dependencies).
2. Copy `.env.example` to `.env` and set `VITE_API_BASE_URL` if the backend is elsewhere.
3. Install and run:

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. The backend CORS configuration explicitly permits this local development origin. The bundled local development account is `admin` / `adminpass` if the backend initializer is enabled.

## Environment variables

| Variable | Default | Purpose |
| --- | --- | --- |
| `VITE_API_BASE_URL` | `http://localhost:8080` | Spring Boot API base URL |

## Build and deployment

```bash
npm run build
```

Deploy the resulting `dist/` directory to a static host. Configure `VITE_API_BASE_URL` at build time and add that deployment origin to the backend CORS allow-list before deploying.

## Screenshots

Add deployed-dashboard screenshots here when available. No static screenshots are bundled because all cards and test output are populated from the live backend.
