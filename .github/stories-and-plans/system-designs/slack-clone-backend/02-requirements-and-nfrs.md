# Requirements & NFRs – Slack Clone Backend

## Functional Requirements

### 1. Authentication & Users
- Users can **register** with email/username and password.
- Users can **log in** and receive a JWT access token.
- Tokens are validated on every protected API/WebSocket connection.
- Basic user profile: id, display name, email, created timestamp.

### 2. Channels
- Create a new channel with a unique name.
- List all channels the user can see (v1: treat as public workspace‑wide channels, plus membership info).
- Join/leave channels.
- Retrieve **paginated message history** for a channel, ordered by time.
- Post a message to a channel.

### 3. Direct Messages (DMs)
- Initiate a DM conversation between two users.
- List existing DM conversations for the current user.
- Retrieve **paginated DM message history**, ordered by time.
- Send a DM message.

### 4. Real‑Time Messaging
- Establish a WebSocket connection from the client after authentication.
- Subscribe to relevant channels/DMs.
- When a user sends a message, all subscribed/online participants receive it in near real time.
- Handle reconnection logic at the protocol level (server side supports reconnect; client is responsible for retrying connections).

### 5. Presence (Online/Offline)
- Track when a user is **online** via active WebSocket connections.
- Mark user as **offline** when their connection closes or after a timeout.
- Expose presence information via:
  - A lightweight API (e.g., get presence for list of user IDs).
  - Real‑time presence updates over WebSockets to interested clients.

### 6. Basic UI Assumptions (for grounding)
- Browser client:
  - Login page.
  - Channel list + DM list view.
  - Chat view (channel or DM) with message history and real‑time updates.
  - Simple presence indicator (online/offline dot next to users).

## Non‑Functional Requirements (Standard Depth)

### 1. Scale & Traffic Assumptions
- **User scale**: up to ~100K registered users.
- **Concurrent active users**: a few hundred concurrently online (e.g., 200–500).
- **Traffic**:
  - REST: up to a few hundred requests/second during peaks.
  - WebSockets: hundreds to a low thousand concurrent connections.
- **Data volume**:
  - Messages stored in MongoDB; can grow to tens or low hundreds of millions of messages over time.
  - User/channel metadata in PostgreSQL stays relatively small.

These are medium‑scale assumptions to practice realistic design without needing very complex sharding or multi‑region setups.

### 2. Availability & Reliability
- Target logical availability around **99.5%+** for the POC (conceptually), with:
  - Single region deployment.
  - Highly available Postgres and Mongo (in practice via managed services or replicated containers later).
- Tolerate **single instance failures** of the app server via horizontal scaling (multiple containers) behind a load balancer.
- WebSocket reconnection support for transient failures (client retries, server supports idempotent re‑subscribe).

### 3. Performance & Latency
- Typical REST API latency: **< 200 ms** p95 for simple operations under normal load.
- Real‑time message delivery:
  - **Sub‑second end‑to‑end** (sender to recipient) in most cases.
  - Messages are written to MongoDB and pushed to all subscribed clients promptly.
- Message history reads should be **paginated** and use indexed queries to keep latency predictable.

### 4. Consistency & Ordering
- **Strong per‑conversation ordering**:
  - Messages in a given channel or DM should appear to participants in the same order they were accepted by the server.
- Write path:
  - Server timestamps and persists messages (Mongo) in a defined order.
  - Broadcasts follow the DB order for each conversation.
- Cross‑entity eventual consistency is acceptable (e.g., presence status slightly lagging, channel list eventual), but **messages within a single channel/DM** must be consistent and ordered.

### 5. Security
- JWT‑based stateless authentication for APIs and WebSockets.
- Passwords are **hashed and salted** (e.g., bcrypt/argon2) and never stored in plain text.
- All external communication assumed over HTTPS/WSS in cloud deployment; for local dev, HTTP/WS is acceptable.
- Basic authorization:
  - Users can only read/write messages in channels they are members of.
  - Users can only access DMs where they are a participant.
- No advanced RBAC in v1; can be extended later.

### 6. Observability
- **Logging**:
  - Structured logs for key events: user login, channel/DM creation, message send failures, connection events.
- **Metrics**:
  - Basic service metrics: request counts, latencies, error rates.
  - WebSocket connection counts, messages sent/received per second.
- **Tracing** (optional but recommended for learning):
  - Trace IDs propagated through HTTP/WebSocket handlers and DB calls.

### 7. Deployability & Operability
- Designed to run via **Docker/docker‑compose** locally:
  - Spring Boot app container.
  - Postgres container.
  - MongoDB container.
  - (Optional) Redis container for presence/cache/pub‑sub.
- Cloud‑friendly design:
  - Stateless app containers that can be scaled horizontally.
  - Externalized configuration (env vars, config files).
- Simple rollback strategy: blue/green or versioned deployment of app container images when deployed to a cloud later.

### 8. Extensibility
- The architecture should make it straightforward to add:
  - File sharing.
  - Message search (via search index or DB indexes).
  - Private channels and advanced permissions.
  - Typing indicators, message reactions, threads.
  - Multi‑workspace support.

The system should be designed as a **modular monolith** (initially) that could evolve into separated services if needed.