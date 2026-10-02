# RÓRA Luxury Atelier — Monitoring, Observability & Alerting Architecture

This document defines the production telemetry, logging standards, metrics collection, and alerting policies across RÓRA.

---

## 1. Observability Triad Architecture

```
[ Spring Boot API ] ──(Actuator / Prometheus)──► [ Prometheus Server ] ──► [ Grafana Dashboards ]
       │                                                                         │
       │ (JSON Structured Logs)                                                  ▼
       ▼                                                                   [ Alertmanager ]
[ FluentBit / Vector ] ──► [ OpenSearch / Grafana Loki ]                       │
                                                                               ├──► Slack #alerts
                                                                               └──► PagerDuty (P1)
```

---

## 2. Spring Boot Health Probes & Metrics

### Health Endpoints (Spring Boot Actuator)
* **Liveness Probe:** `GET /actuator/health/liveness` (Checks if Spring context is responsive).
* **Readiness Probe:** `GET /actuator/health/readiness` (Validates PostgreSQL connectivity, Flyway migration state, and connection pool availability).
* **Prometheus Metrics Scrape:** `GET /actuator/prometheus` (Exposes Micrometer counters, timers, and gauges).

### Core Golden Signal Metrics
1. **Traffic & Request Rate:** `http_server_requests_seconds_count{uri=~"/api/v1/.*"}`
2. **Latency (P95, P99):** `http_server_requests_seconds{quantile="0.95"}` (Target: < 150ms for catalog, < 350ms for checkout).
3. **Error Rate (4xx, 5xx):** `sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m]))`
4. **Database Connection Pool Saturation:** `hikaricp_connections_active / hikaricp_connections_max` (Target: < 70%).
5. **JVM Memory & Garbage Collection:** `jvm_memory_used_bytes`, `jvm_gc_pause_seconds_max`.

---

## 3. Structured Logging Standard

All production log entries emit structured single-line JSON format with correlated trace and span IDs:

```json
{
  "@timestamp": "2026-10-02T10:18:49.005Z",
  "level": "INFO",
  "logger": "com.rora.backend.order.service.OrderService",
  "thread": "http-nio-8080-exec-4",
  "traceId": "7a94f83b2e91",
  "spanId": "4c882190",
  "orderNumber": "#RRA45905",
  "customerId": "e8725cc5-e944-4b21-94d9-728f892e7c68",
  "message": "Successfully created order #RRA45905 with 1 items, total: 2969.10"
}
```

---

## 4. Alerting Thresholds & Severity Matrix

| Alert Name | Condition | Severity | Action / Escalation |
| :--- | :--- | :--- | :--- |
| `High5xxErrorRate` | 5xx errors > 1% of total requests over 3m | **P1 (Critical)** | PagerDuty on-call dispatch; inspect error logs |
| `DatabasePoolExhausted` | Active connections > 85% for 2m | **P1 (Critical)** | Scale connection pool; investigate slow queries |
| `HighCheckoutLatency` | P95 latency for `/checkout/place-order` > 2s | **P2 (High)** | Slack alert; analyze lock contention |
| `LowStockWarning` | SKU inventory count <= threshold | **P3 (Medium)** | Daily digest to inventory operations manager |
| `FrequentFailedLogins` | > 20 failed login attempts in 1m from single IP | **P3 (Medium)** | Automated IP rate-limiting & security notification |
