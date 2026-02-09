# Deployment, Operations & Roadmap – Slack Clone Backend

## 1. Deployment Architecture (v1 – Local with Docker)

### Components in docker-compose

- **chat-backend** (Spring Boot, JDK 21)
  - Exposes HTTP (REST) and WebSocket endpoints.
  - Configured via environment variables (DB URLs, JWT secret, etc.).

- **postgres**
  - Stores users, channels, memberships, DM metadata, presence snapshot.
  - Data persisted via Docker volume.

- **mongo**
  - Stores channel and DM messages.
  - Data persisted via Docker volume.

- **redis** (optional but recommended)
  - Used for caching and pub/sub.

### Local Topology

- Browser → `localhost` (reverse proxy optional) → `chat-backend` container.
- Backend connects to Postgres, Mongo, Redis via docker network.

This setup is simple for development but mirrors how services will be connected in the cloud.

---

## 2. Cloud‑Friendly Design (Future)

When moving to cloud (e.g., AWS, GCP, Azure):

- **Compute**
  - Containerize the Spring Boot app and run on a container service (ECS, GKE, AKS, etc.) or Kubernetes.
  - Run multiple replicas behind a managed load balancer.

- **Databases**
  - Use managed PostgreSQL (e.g., RDS/Aurora, Cloud SQL).
  - Use managed MongoDB (e.g., Atlas) or a cloud Mongo service.
  - Use managed Redis (e.g., ElastiCache, Memorystore).

- **Networking**
  - HTTPS termination at load balancer.
  - WebSockets supported by load balancer (sticky sessions or consistent hashing if needed).

- **Config & Secrets**
  - Use environment variables, secret managers (e.g., AWS Secrets Manager) instead of hardcoding.

---

## 3. Scaling Strategy

### Application Layer

- Scale `chat-backend` horizontally by adding more containers.
- Use Redis pub/sub to:
  - Broadcast messages between instances so each instance can deliver to its own WebSocket clients.
  - Share presence events across instances.

### Databases

- **PostgreSQL**
  - Start with a single instance.
  - Add read replicas if read load grows significantly.
  - Use proper indexing on users, channels, memberships, DMs.

- **MongoDB**
  - Start with a single replica set.
  - Scale vertically (bigger instance) initially.
  - For higher scale, consider sharding by `channelId` or `conversationId`.

- **Redis**
  - Start with a single instance.
  - Move to a small cluster if pub/sub traffic grows.

### WebSockets

- Support sticky sessions or session affinity at load balancer level (optional if using stateless message routing via Redis).
- Alternatively, handle messages in any instance and route via Redis to instance holding a connection.

---

## 4. Availability & Failure Handling

### Single‑Node Failures

- If one `chat-backend` instance fails:
  - Load balancer routes traffic to remaining instances.
  - Connected WebSocket clients reconnect and may land on a different instance.

### DB Failures

- For Postgres or Mongo outage:
  - APIs that depend on them will fail; backend should return clear errors.
  - Logs and metrics highlight DB connectivity issues.

### Graceful Degradation

- If Redis fails:
  - Real‑time cross‑instance message fan‑out may degrade.
  - Single instance flows still work; horizontal scaling benefits are reduced.

---

## 5. Observability & Operations

### Logging

- Structured logs (JSON or key‑value) including:
  - Request IDs, user IDs, channel/DM IDs for important operations.
  - WebSocket connection events (connect, disconnect, errors).
  - DB errors, timeouts, and retries.

### Metrics

- HTTP metrics:
  - Request count, latency distribution (p50/p95/p99), error rates per endpoint.
- WebSocket metrics:
  - Active connections, messages sent/received per second.
- DB metrics:
  - Query latency, connection pool usage, error counts.

### Tracing (Optional for Learning)

- Integrate OpenTelemetry or similar to trace:
  - From REST/WebSocket entrypoints through service logic down to Postgres/Mongo calls.

### Dashboards & Alerts (Conceptual)

- Dashboard panels for:
  - Request rates/latencies, error rates.
  - Active WebSocket connections.
  - DB health.
- Alerts for:
  - High error rates.
  - DB connection failures.
  - Unusually low WebSocket connection counts (possible outage).

---

## 6. Implementation Roadmap

### Phase 1 – Core MVP

- Implement core backend as a monolith:
  - Auth & User Service with JWT.
  - Channel Service (create/list/join/leave).
  - DM Service (create/list conversations).
  - Messaging Service with Mongo persistence (basic REST send + history APIs).
  - Presence Service (simple: mark online/offline on WebSocket connect/disconnect, in memory).
- Wire up local docker-compose with Postgres and Mongo.
- Basic logging and minimal metrics.

### Phase 2 – Real‑Time & Presence

- Add WebSocket endpoint and integrate with Messaging Service:
  - Broadcast messages in real time.
- Enhance Presence Service:
  - More robust online/offline tracking.
  - Simple presence API and WebSocket notifications.
- Introduce Redis for pub/sub (start with single instance).
- Add more metrics (WebSocket, DB) and structured logs.

### Phase 3 – Hardening & Cloud Readiness

- Improve error handling and validation.
- Add pagination, cursor‑based history where appropriate.
- Add configuration profiles for local vs cloud.
- Document how to migrate to cloud (e.g., container registry, managed DBs, load balancer setup).

---

## 7. Risks, Trade‑offs & Open Questions

### Risks & Trade‑offs

- **Polyglot persistence complexity**:
  - Managing consistency between Postgres and Mongo is more complex than a single DB.
  - Trade‑off accepted for educational purposes.

- **WebSocket scaling**:
  - Requires careful handling of connection distribution and pub/sub.
  - For the POC, we accept a simpler, possibly less efficient approach.

- **Single‑region assumption**:
  - Simpler design, but no cross‑region failover.

### Open Questions (for future iterations)

- Should we support **private channels** and more advanced ACLs?
- Do we need **search** across messages soon (requiring search infra)?
- What are the **retention policies** for old messages (cleanup/archival)?
- How strict should **latency targets** be for production use cases?

These questions can guide further enhancements once the core system is working and understood.