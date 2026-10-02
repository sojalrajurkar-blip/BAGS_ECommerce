# RÓRA Luxury Atelier — Production Deployment & Rollback Runbook

This operational runbook governs production deployments, pre-flight verification, zero-downtime rolling updates, and emergency rollback procedures for RÓRA.

---

## 1. Pre-Flight Checklist

Before initiating a production deployment, ensure the following criteria are satisfied:
- [ ] **CI Pipeline Green:** All 202 backend tests and Next.js production builds pass cleanly on GitHub Actions.
- [ ] **Database Migration Review:** All new Flyway migration files (`V*.sql`) have been reviewed for backward compatibility and zero lock-contention risks.
- [ ] **Secrets Validated:** Production credentials in Cloud Secrets Manager are up-to-date and accessible by container IAM roles.
- [ ] **Backup Verification:** Confirmed that a fresh PostgreSQL snapshot was completed within the last 4 hours.

---

## 2. Zero-Downtime Deployment Procedure

```
Step 1: Build & Tag Images (CI/CD)
           │
           ▼
Step 2: Database Migration Check (Flyway pre-boot)
           │
           ▼
Step 3: Rolling Deployment (Backend Replicas 1 -> 2)
           │ (Actuator readiness probes verify health)
           ▼
Step 4: Rolling Deployment (Frontend Next.js Replicas)
           │
           ▼
Step 5: Automated Smoke Test Verification
           │
           ▼
Step 6: Traffic Cutover & Legacy Container Decommission
```

### Command Sequence (Docker Compose / Cloud Orchestrator)

```bash
# 1. Pull latest verified production images
docker compose -f docker-compose.yml -f docker-compose.prod.yml pull

# 2. Perform rolling backend update with zero downtime
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d --no-deps --scale backend=2 backend

# 3. Verify backend health probe
curl -f https://api.rora-luxury.com/actuator/health

# 4. Perform rolling frontend update
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d --no-deps frontend

# 5. Reload Nginx configuration without dropping connections
docker exec rora-nginx nginx -s reload
```

---

## 3. Post-Deployment Smoke Test Suite

Run the following curl checks against production endpoints:

```bash
# 1. Health & Actuator Liveness
curl -s -o /dev/null -w "%{http_code}\n" https://api.rora-luxury.com/actuator/health
# Expected: 200

# 2. Catalog Products Retrieval
curl -s -o /dev/null -w "%{http_code}\n" https://api.rora-luxury.com/api/v1/products/featured
# Expected: 200

# 3. Store Settings
curl -s -o /dev/null -w "%{http_code}\n" https://api.rora-luxury.com/api/v1/settings/public
# Expected: 200

# 4. Frontend Root Page
curl -s -o /dev/null -w "%{http_code}\n" https://rora-luxury.com/
# Expected: 200
```

---

## 4. Emergency Rollback Runbook

If critical regression, high 5xx error rates, or data anomalies are observed post-deployment:

### Scenario A: Application Code Regression (No Database Migration)
```bash
# 1. Rollback backend to previous stable image tag
export PREVIOUS_TAG="v1.0.0"
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d --no-deps backend

# 2. Rollback frontend
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d --no-deps frontend

# 3. Reload Nginx
docker exec rora-nginx nginx -s reload
```

### Scenario B: Database Migration Failure
1. Put storefront in graceful maintenance mode:
   ```bash
   docker exec rora-nginx cp /etc/nginx/maintenance.html /usr/share/nginx/html/index.html
   ```
2. Trigger point-in-time recovery (PITR) via cloud database console to the snapshot captured immediately prior to deployment.
3. Deploy the previous stable container image.
4. Disable maintenance mode and execute post-deployment smoke tests.
