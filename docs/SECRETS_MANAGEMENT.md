# RÓRA Luxury Atelier — Secrets Management & Security Architecture

This guide details the classification, injection, rotation, and access control policies for all cryptographic secrets, database credentials, and third-party API tokens used across RÓRA.

---

## 1. Secrets Inventory & Classification

| Secret Key | Classification | Environment | Target Service | Rotation Cycle |
| :--- | :--- | :--- | :--- | :--- |
| `DB_PASSWORD` | Restricted / Critical | All | Spring Boot / PostgreSQL | 90 Days |
| `JWT_SECRET` | Restricted / Critical | Staging / Prod | Spring Boot Auth Engine | 180 Days |
| `RAZORPAY_KEY_SECRET` | Restricted / Critical | Prod | Payment Simulation / Gateway | 90 Days |
| `BLUEDART_API_KEY` | Confidential | Prod | Shipment / Logistics Engine | 180 Days |
| `NEXT_PUBLIC_API_URL` | Public / Non-sensitive | All | Next.js Frontend | On infrastructure change |

---

## 2. Secrets Storage & Injection Architecture

```
[ Cloud Secrets Vault ]
(AWS Secrets Manager / GCP Secret Manager / Doppler / Vault)
         │
         │ (IAM Role Authentication / Machine Identity)
         ▼
[ Container Orchestrator ] (K8s Secrets / ECS Task Definition / Docker Swarm)
         │
         ├──► Injects ENV Variables into [ Spring Boot Backend ] (Non-root user)
         └──► Injects ENV Variables into [ Next.js Frontend ] (Non-root user)
```

### Golden Rules:
1. **Never Commit Secrets to Version Control:** The `.gitignore` and `.dockerignore` files explicitly exclude `.env`, `.env.local`, and credential files.
2. **Runtime Memory-Only Injection:** Secrets are loaded directly into environment variables or injected as runtime in-memory mounts; never written to container disk layers.
3. **Least Privilege IAM Roles:** Spring Boot instances authenticate with the secrets vault via instance IAM roles/workload identities with read-only permission to `rora/production/*` secret paths.

---

## 3. Secret Rotation Procedures

### JWT HMAC/RSA Key Rotation
1. Update `JwtTokenProvider` to support dual-key verification (Active Verification Key + Legacy Grace Key).
2. Issue new tokens with the new secret key.
3. Allow a 24-hour grace period for existing active tokens (`expiration-ms: 86400000`) before decommissioning the legacy key.

### Database Password Rotation
1. Create a secondary database user with identical role permissions.
2. Update the secret in Cloud Secrets Manager.
3. Perform a zero-downtime rolling restart of backend container replicas.
4. Revoke privileges from the retired database user.

---

## 4. Container Security Posture

* **Non-Root Execution:** All containers run under dedicated unprivileged users (`nextjs:nodejs` on frontend UID 1001, `rora:rora` on backend).
* **Read-Only Root Filesystem:** Production container definitions specify `readOnlyRootFilesystem: true` with temporary in-memory `/tmp` volumes where required.
* **Vulnerability Scanning:** Automated container vulnerability scanning via GitHub Actions / Trivy on all image pushes.
