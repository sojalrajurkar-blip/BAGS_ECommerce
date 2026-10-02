# RÓRA Luxury Atelier — Production PostgreSQL Architecture & Sizing Plan

This document defines the high-availability architecture, performance tuning, database migration strategy, and backup/recovery procedures for RÓRA's production PostgreSQL infrastructure.

---

## 1. Managed Cloud PostgreSQL Architecture

```
[ Next.js Frontend ]       [ Spring Boot REST API (Replicas) ]
         │                                   │
         │ (Public HTTPS)                    │ (Private VPC / Subnet)
         ▼                                   ▼
[ Cloudflare / CDN ] ──► [ Nginx Gateway ] ──► [ PgBouncer Connection Pooler ]
                                                       │
                                        ┌──────────────┴──────────────┐
                                        ▼                             ▼
                            [ Primary PostgreSQL ]          [ Read Replica ]
                            (Read / Write Master)          (Analytics / BI)
                                        │
                                        ▼ (Streaming Replication / WAL-G)
                            [ Multi-AZ Standby ]
```

### Recommended Managed Providers
1. **AWS RDS / Aurora PostgreSQL 16+** (Primary Option for AWS deployments).
2. **GCP Cloud SQL for PostgreSQL 16+** (Primary Option for GCP / Google Cloud).
3. **Supabase / Neon Enterprise** (Alternative high-velocity cloud-native options).

### Production Sizing Specifications
* **Instance Tier:** `db.r6g.xlarge` (4 vCPUs, 32 GB RAM, Dedicated Network).
* **Storage:** 200 GB Provisioned IOPS SSD (`io2` or `gp3` @ 6,000 IOPS, 250 MB/s throughput) with autoscaling up to 2 TB.
* **High Availability (Multi-AZ):** Synchronous standby replica across distinct Availability Zones with automatic failover (< 60s RTO).
* **Connection Pooling:** PgBouncer instance placed in transaction-pooling mode with `max_client_conn = 2000` and `default_pool_size = 50`.

---

## 2. PostgreSQL Performance Tuning Parameters

```ini
# Memory Configuration
shared_buffers = 8GB                    # 25% of 32GB RAM
effective_cache_size = 24GB             # 75% of 32GB RAM
maintenance_work_mem = 2GB
work_mem = 64MB

# Checkpoints & Write-Ahead Logs (WAL)
wal_level = replica
max_wal_size = 16GB
min_wal_size = 2GB
checkpoint_completion_target = 0.9
checkpoint_timeout = 15min

# Connection Limits & Worker Threads
max_connections = 200
max_worker_processes = 8
max_parallel_workers_per_gather = 4
max_parallel_workers = 8

# Query Planner Cost Model for SSD
random_page_cost = 1.1
seq_page_cost = 1.0
effective_io_concurrency = 200
```

---

## 3. Database Schema Migration Strategy (Flyway)

1. **Version Controlled Migrations:** All 13 canonical migrations (`V1` to `V13`) located in `backend/src/main/resources/db/migration/` are executed automatically at startup.
2. **Zero-Downtime Migration Rules:**
   - Always make column additions `NULL` or with default values to avoid table rewrites.
   - For destructive operations (e.g. drop column), use the Expand-Contract pattern across two consecutive releases.
   - Use `CREATE INDEX CONCURRENTLY` for large existing production tables.
3. **Lock Timeout Protection:** Set `SET lock_timeout = '5s';` in migration scripts to prevent blocking concurrent patron transactions.

---

## 4. Automated Backup & Disaster Recovery Strategy

| Backup Type | Frequency | Retention Window | Storage Target | SLA Impact |
| :--- | :--- | :--- | :--- | :--- |
| **Continuous WAL Archiving** | Real-time (streamed) | 35 Days | Encrypted S3 / GCS | Point-in-time recovery to any second |
| **Full Snapshot** | Daily @ 02:00 UTC | 90 Days | Multi-Region Bucket | System restore point |
| **Weekly Deep Archive** | Every Sunday | 365 Days | AWS Glacier / Coldline | Compliance & financial audit |

### Recovery Objectives
* **Recovery Point Objective (RPO):** **< 1 Minute** (near-zero data loss using continuous streaming WAL replication).
* **Recovery Time Objective (RTO):** **< 15 Minutes** (automated Multi-AZ failover and containerized database restoration).

---

## 5. Security & Network Isolation

* **VPC Isolation:** PostgreSQL placed in a private subnet with zero direct public Internet access.
* **Security Groups:** Strictly allow incoming connections on port `5432` only from the Spring Boot API security group.
* **Encryption in Transit:** `sslmode=verify-full` enforced with TLS 1.3.
* **Encryption at Rest:** AES-256 KMS customer-managed key encryption for all data files, WAL logs, and snapshots.
