# RÓRA — Antigravity Complete Project Audit, Repair & Verification Prompt Pack

## Purpose

This prompt pack is for the existing RÓRA Luxury Leather Goods project.

The current project progress report claims:
- Next.js 16 App Router + Turbopack + GSAP frontend
- Spring Boot 3.4.x + Java 21 backend
- PostgreSQL 18.x
- 17 completed phases
- 204/204 backend tests passing
- 18/18 frontend routes compiling
- 300-frame GSAP Canvas hero
- frontend repositories connected to Spring Boot APIs

IMPORTANT:
A successful compile/build/test suite is NOT sufficient proof that the real browser experience is correct.

The goal of this repair is NOT to rebuild the project from scratch.
The goal is to inspect the existing implementation, identify every real integration/runtime/visual/environment problem, repair it safely, and prove the repaired project works in a real browser locally.

---

# MASTER RULES

1. DO NOT rewrite or rebuild the project from scratch.
2. DO NOT replace the existing architecture merely because another architecture is easier.
3. Preserve the existing RÓRA visual direction, design tokens, typography, premium editorial aesthetic, product imagery, responsiveness, GSAP/ScrollTrigger/Lenis work, and existing business logic unless a concrete defect requires a change.
4. Do not introduce AI shopping assistants, AI recommendations, chatbot UI, AI-generated customer-facing content, or other customer-facing AI features.
5. Do not create fake "success" reports.
6. Do not mark a task complete merely because TypeScript compiles, tests pass, or the production build succeeds.
7. Every important frontend feature must be verified in an actual browser.
8. Every API integration must be verified from the browser through the real frontend repository/data layer.
9. Every image, font, icon, animation frame, and static asset used by the UI must be verified for successful loading.
10. Fix root causes instead of hiding errors with excessive fallbacks.
11. Do not remove working features just to make the build pass.
12. Do not redesign the UI during technical repair.
13. Do not silently change API contracts. If a contract mismatch is found, identify both sides and repair the correct boundary.
14. Preserve TypeScript strictness and existing project conventions.
15. Do not use placeholder data where real project data is expected.
16. Do not claim "100% complete" until all verification gates in this document pass.

---

# PHASE A — FORENSIC AUDIT BEFORE MODIFYING CODE

First inspect the entire repository.

Do NOT modify files during the first audit pass.

Inspect:

## Frontend
- package.json
- next.config.*
- tsconfig.json
- eslint configuration
- src/app/
- src/components/
- src/context/
- src/data/
- src/data/repositories/
- src/types/
- src/animations/
- public/
- all environment files and .env.example files
- CSS/global styles/design tokens
- all route files
- all layout files
- all loading/error/not-found files

## Backend
- pom.xml
- src/main/
- src/test/
- application*.yml/properties
- Flyway migrations
- security configuration
- CORS configuration
- API controllers
- DTOs
- services
- repositories
- entities
- exception handling

## Infrastructure
- Dockerfiles
- docker-compose files
- Nginx configuration
- GitHub Actions
- environment templates
- database setup documentation

Create:

docs/ANTIGRAVITY_FORENSIC_AUDIT.md

The audit MUST contain:

1. Actual project architecture discovered from code
2. Actual frontend start command
3. Actual backend start command
4. Actual database requirements
5. Required environment variables
6. Actual ports
7. Actual API base URL
8. Actual frontend-to-backend request flow
9. Route inventory
10. Static asset inventory
11. GSAP/Canvas asset inventory
12. Known runtime risks
13. Known environment risks
14. Known API contract risks
15. Known browser/runtime risks
16. Known responsive risks
17. Existing test coverage
18. What the existing tests DO NOT verify
19. Exact files that are likely responsible for the current local UI problems

Do not fix anything yet.

---

# PHASE B — LOCAL ENVIRONMENT AUDIT

Verify the complete local stack.

Check:

## Frontend
- Node version
- package manager
- Next.js version
- development server
- production build
- required environment variables
- NEXT_PUBLIC_API_URL
- any other NEXT_PUBLIC_* variables

## Backend
- Java version
- Maven wrapper/system Maven
- Spring Boot startup
- server port
- active profile
- CORS
- JWT configuration
- database connection

## Database
- PostgreSQL availability
- database name
- username
- password configuration
- port
- Flyway migration state
- seed data
- expected tables
- expected catalog data

Do not assume localhost:8080 is correct.
Read the actual configuration.

Create:

docs/ANTIGRAVITY_LOCAL_ENVIRONMENT_AUDIT.md

The document must show:

Frontend:
URL:
Port:
Command:

Backend:
URL:
Port:
Command:

Database:
Host:
Port:
Database:
Migration status:

API:
Base URL:

Environment:
Required variables:
Missing variables:
Incorrect variables:

---

# PHASE C — FRONTEND BROWSER RUNTIME AUDIT

Start the actual local application.

Use a real browser and inspect the application.

Do NOT rely only on:
- npm run build
- npm run lint
- tsc
- backend tests

Check every important route.

At minimum inspect:

/
 /shop
 /category/[slug]
 product detail pages
 search
 cart
 wishlist
 login
 register
 account
 orders
 tracking
 returns
 FAQ
 contact
 admin routes if present

For every route verify:

1. HTTP response
2. page rendering
3. console errors
4. hydration errors
5. network failures
6. API failures
7. missing images
8. missing fonts
9. missing icons
10. broken CSS
11. layout overflow
12. loading state
13. empty state
14. error state
15. responsive behavior
16. animation behavior

Record every failure before fixing.

---

# PHASE D — BROWSER CONSOLE + NETWORK AUDIT

Inspect browser DevTools.

## Console

Find and classify:

- JavaScript exceptions
- React errors
- hydration mismatch
- failed dynamic imports
- GSAP errors
- Canvas errors
- image loading errors
- font loading errors
- undefined values
- null access
- API parsing errors

## Network

Check:

- failed requests
- 404
- 401
- 403
- 500
- CORS failures
- wrong API URL
- wrong HTTP method
- wrong request payload
- wrong response parsing
- duplicate requests
- requests made before required state exists

For each failed request record:

Route:
Frontend file:
Request:
Expected:
Actual:
Root cause:
Fix:

Do not simply suppress errors.

---

# PHASE E — API CONTRACT AUDIT

Trace every important request:

UI component
→ repository
→ apiClient
→ HTTP request
→ Spring controller
→ service
→ database
→ response DTO
→ apiClient unwrapping
→ repository
→ component

Verify that the frontend and backend agree on:

- URL
- HTTP method
- query parameters
- path variables
- request body
- headers
- authentication
- response shape
- pagination
- error shape
- IDs
- slugs
- numeric fields
- dates
- enum values
- nullable fields

Pay special attention to the centralized apiClient and the response unwrapping logic.

Do not assume `response.data.data` is correct everywhere. Verify actual backend responses.

Fix contract mismatches at the correct boundary.

---

# PHASE F — STATIC ASSET AUDIT

Audit ALL assets.

Check:

- logo
- product images
- category images
- icons
- fonts
- SVGs
- WebP/JPG/PNG files
- animation frames
- videos if any
- CSS background images

For every referenced asset verify:

1. file exists
2. path is correct
3. filename case is correct
4. Next.js can serve it
5. browser returns 200
6. correct dimensions
7. correct format
8. no accidental duplicated path
9. no broken import
10. no production-only path issue

Do not replace missing assets with random placeholders.

---

# PHASE G — 300-FRAME GSAP CANVAS AUDIT

This is a high-priority area.

Inspect:

src/animations/ProductImageSequence.tsx
and every related hero/GSAP component.

Verify:

1. all expected frames exist
2. frame naming is correct
3. frame path is correct
4. frame count is correct
5. first frame loads
6. middle frame loads
7. last frame loads
8. canvas dimensions are correct
9. device pixel ratio is handled correctly
10. animation initializes only on client
11. no SSR access to window/document/localStorage
12. ScrollTrigger lifecycle is cleaned up
13. GSAP contexts are cleaned up
14. resize handling works
15. mobile behavior works
16. reduced-motion behavior works
17. slow-network behavior has a usable initial visual state
18. missing frame does not silently break the whole hero
19. memory usage is reasonable
20. browser console remains clean

Verify the hero in:
- desktop
- tablet
- mobile

Do not replace the flagship animation unless it is technically impossible to repair.

---

# PHASE H — HYDRATION / CLIENT-SERVER AUDIT

Search for:

- localStorage
- sessionStorage
- window
- document
- navigator
- Date-dependent rendering
- random values
- browser-only APIs
- client state used during SSR

Verify all browser-only state is handled safely.

Pay special attention to:

- StoreContext
- authentication state
- guest session ID
- cart state
- wishlist state
- customer state
- admin state

A previous hydration mismatch was already encountered in this project.
Do not assume that fixing one occurrence means all hydration problems are gone.

---

# PHASE I — STATE MANAGEMENT AUDIT

Trace:

Guest user
→ session ID
→ cart
→ login
→ JWT
→ customer state
→ cart merge
→ logout
→ new guest session

Verify:

- no stale state
- no race conditions
- no duplicate state sources
- no localStorage hydration problems
- no token parsing crashes
- no infinite request loops
- no state update after unmount

Also verify wishlist state and order/account state.

---

# PHASE J — VISUAL / UI AUDIT

Do not redesign.

Verify the existing RÓRA design.

Check:

- typography
- spacing
- container widths
- image aspect ratios
- buttons
- navigation
- product cards
- price formatting
- badges
- forms
- modals
- drawers
- tables
- empty states
- error states
- skeletons/loading states
- footer
- responsive breakpoints

The website must remain:

- premium
- editorial
- product-focused
- human-designed
- original
- non-AI-generic

Do not introduce generic dashboard/UI patterns into the storefront.

---

# PHASE K — RESPONSIVE AUDIT

Test at minimum:

- 375px
- 390px
- 768px
- 1024px
- 1280px
- 1440px

Check:

- horizontal overflow
- navigation
- hero
- product grid
- filters
- product gallery
- cart
- checkout
- modals
- forms
- tables
- admin dashboard

Fix responsive bugs without redesigning the intended visual language.

---

# PHASE L — LOADING / ERROR / EMPTY STATES

Every API-driven page must handle:

Loading:
- skeleton or intentional loading UI

Empty:
- meaningful empty state

Error:
- meaningful recoverable error state

Offline/API unavailable:
- clear fallback

Do NOT hide API failures and render misleading empty content.

---

# PHASE M — REPAIR

Only after the audit:

1. Fix critical runtime errors first.
2. Fix environment/configuration issues.
3. Fix API contract mismatches.
4. Fix asset paths.
5. Fix hydration problems.
6. Fix state-management issues.
7. Fix GSAP/Canvas issues.
8. Fix responsive issues.
9. Fix visual inconsistencies.
10. Fix minor code quality issues.

After each major repair:
- run typecheck
- run lint
- run build where applicable
- restart the relevant service
- re-test in browser

Do not make unrelated refactors.

---

# PHASE N — AUTOMATED + BROWSER VERIFICATION

The final verification must include BOTH:

## Automated

- frontend lint
- frontend typecheck
- frontend production build
- backend compilation
- backend unit/integration tests
- existing E2E tests
- migration validation

## Browser

Actually open the running application and verify:

- homepage
- shop
- PDP
- search
- category
- cart
- wishlist
- login/register
- account
- order flow
- tracking
- returns
- important admin pages
- flagship hero animation

A green build is NOT accepted as browser verification.

---

# PHASE O — FINAL REGRESSION TEST

After all repairs, repeat the complete browser test.

Verify that repairs did not break:

- routing
- navigation
- API calls
- authentication
- cart
- wishlist
- checkout
- orders
- reviews
- animations
- responsive layout

---

# PHASE P — FINAL EVIDENCE REPORT

Create:

docs/ANTIGRAVITY_FINAL_REPAIR_VERIFICATION.md

It MUST contain:

## 1. Problems Found

Table:

| ID | Problem | Severity | Root Cause | File(s) |
|---|---|---|---|---|

## 2. Repairs

| ID | Repair | Files Changed | Verification |
|---|---|---|---|

## 3. Environment Verification

Frontend:
Backend:
Database:
API:
Environment variables:

## 4. Automated Verification

| Check | Result | Evidence |
|---|---|---|
| TypeScript | PASS/FAIL | command/output |
| Lint | PASS/FAIL | command/output |
| Frontend build | PASS/FAIL | command/output |
| Backend build | PASS/FAIL | command/output |
| Backend tests | PASS/FAIL | count |
| Integration tests | PASS/FAIL | count |

## 5. Browser Verification

| Route/Feature | Desktop | Tablet | Mobile | Console | Network |
|---|---|---|---|---|---|

## 6. Asset Verification

| Asset Group | Expected | Loaded | Failed |
|---|---:|---:|---:|

## 7. GSAP/Canvas Verification

Include:
- frame count
- first frame
- middle frame
- last frame
- desktop
- mobile
- resize
- reduced motion
- console status

## 8. Remaining Issues

If anything remains, explicitly list it.

Do NOT write "100% complete" if anything critical remains.

---

# DEFINITION OF DONE

The project is considered repaired only when:

- local frontend starts successfully
- local backend starts successfully
- database starts/connects successfully
- migrations succeed
- seed data is available
- frontend connects to backend
- API calls succeed
- no critical console errors exist
- no hydration errors exist
- no broken asset requests exist
- homepage renders correctly
- hero animation works
- product data renders
- shop works
- PDP works
- cart works
- wishlist works
- authentication works
- account works
- order flow works
- important admin pages work
- responsive layouts work
- loading/empty/error states work
- lint passes
- TypeScript passes
- frontend build passes
- backend tests pass
- browser regression passes

Only after ALL of these are verified may the project be described as fully verified.

---

# IMPORTANT BEHAVIOR

If you encounter a problem:

DO NOT say:
"Everything looks good."

Instead say:
"I found X. The root cause is Y. I will repair Z."

If you cannot verify something:
write:
"NOT VERIFIED"

Do not convert "not tested" into "passed".

If the repository contradicts the progress report:
trust the actual code and runtime behavior, not the report.

The existing PROGRESS.md is documentation, not proof.

The browser and source code are the source of truth.

END OF MASTER PROMPT
