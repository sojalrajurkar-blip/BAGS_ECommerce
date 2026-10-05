# RÓRA — Antigravity Local Environment Audit

**Date:** October 5, 2026  
**Auditor:** Antigravity AI Pair Programmer  

---

## 1. Stack Environment Configuration

### Frontend
- **URL:** `http://localhost:3000`
- **Port:** `3000`
- **Node.js Version:** `v24.19.0`
- **Next.js Version:** `16.3.8` (App Router)
- **Start Command (Dev):** `npm.cmd run dev` (or `npx.cmd next dev -p 3000`)
- **Build Command:** `npm.cmd run build`
- **Typecheck Command:** `npx.cmd tsc --noEmit`
- **Lint Command:** `npx.cmd eslint src/`

### Backend
- **URL:** `http://localhost:8080`
- **Port:** `8080`
- **Java Version:** `21.0.7 LTS` (Oracle Corporation)
- **Maven Version:** `3.9.16`
- **Active Profile:** `dev`
- **Start Command:** `mvn spring-boot:run`
- **Test Command:** `mvn test`

### Database
- **Engine:** PostgreSQL
- **Host:** `localhost`
- **Port:** `5432`
- **Database:** `rora_db`
- **Username:** `postgres`
- **Password:** `password`
- **Flyway Migrations:** 14 Migration scripts (`V1` to `V14`) validated

### API
- **Base URL:** `http://localhost:8080/api/v1`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

---

## 2. Environment Variables Verification

| Variable | Location | Current Value | Status |
|---|---|---|---|
| `NEXT_PUBLIC_API_URL` | `.env` | `http://localhost:8080/api/v1` | Verified Correct |
| `NODE_ENV` | `.env` | `development` | Verified Correct |
| `PORT` | `.env` | `3000` | Verified Correct |
| `NEXT_TELEMETRY_DISABLED` | `.env` | `1` | Verified Correct |
| `DB_URL` | `backend/.env` | `jdbc:postgresql://localhost:5432/rora_db` | Verified Correct |
| `DB_USERNAME` | `backend/.env` | `postgres` | Verified Correct |
| `DB_PASSWORD` | `backend/.env` | `password` | Verified Correct |
| `PORT` | `backend/.env` | `8080` | Verified Correct |
| `SPRING_PROFILES_ACTIVE` | `backend/.env` | `dev` | Verified Correct |
| `CORS_ALLOWED_ORIGINS` | `backend/.env` | `http://localhost:3000,http://127.0.0.1:3000,http://localhost:80` | Verified Correct |
| `JWT_SECRET` | `backend/.env` | `404E6352...` (256-bit Hex) | Verified Correct |
| `JWT_EXPIRATION_MS` | `backend/.env` | `86400000` (24 Hours) | Verified Correct |

---

## 3. Findings Summary
- All required environment variables are present and aligned across frontend and backend configurations.
- Windows PowerShell requires invoking `.cmd` wrappers (`npm.cmd`, `npx.cmd`) or bypassing script policy restrictions.
- All 300 hero canvas frames exist in `public/images/rora/product-sequence/`.
