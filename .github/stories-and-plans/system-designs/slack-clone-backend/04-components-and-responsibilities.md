# Components & Responsibilities – Slack Clone Backend

## Overview

The backend is a modular monolith with clear internal components. This file lists the main components, their responsibilities, and their interactions.

## 1. API Gateway & WebSocket Gateway (Spring Boot App / `app` module)

**Responsibilities**
- Expose REST APIs under `/api/...`.
- Terminate WebSocket connections (e.g., `/ws`).
- Authenticate incoming requests (JWT) and propagate user identity to internal services.
- Route HTTP/WebSocket traffic to appropriate domain services within the app.

**Interfaces**
- External:
  - REST: JSON over HTTP.
  - WebSocket: STOMP or custom protocol over WSS.
- Internal:
  - Method calls to domain service classes.

## 2. Auth & User Service (`identity-module`)

**Responsibilities**
- User registration and login.
- Password hashing and validation.
- JWT token generation and validation (in collaboration with security layer).
- Managing basic user profile (display name, email, created timestamp).

**Data**
- PostgreSQL `users` table.

**Interactions**
- REST endpoints: `/api/auth/register`, `/api/auth/login`, `/api/users/me`, etc.
- Called by security filters to validate JWT and load user details.

## 3. Channel Service (`chat-module`)

**Responsibilities**
- Create, retrieve, update basic channel metadata (name, description, createdBy, timestamps).
- Manage memberships (which users are part of which channels).
- Provide channel listings and membership views for a given user.

**Data**
- PostgreSQL tables:
  - `channels`
  - `channel_members` (user‑to‑channel mapping)

**Interactions**
- REST endpoints: `/api/channels`, `/api/channels/{id}`, `/api/channels/{id}/members`, etc.
- Used by Messaging Service to validate sending permissions (user must be a member).

## 4. Direct Message (DM) Service (`chat-module`)

**Responsibilities**
- Manage DM conversations between two users.
- Ensure uniqueness of a DM conversation per pair of users (or clearly defined behavior if multiple threads allowed).
- Provide list of DM conversations for a user.

**Data**
- PostgreSQL tables:
  - `dm_conversations` (with participants user1_id, user2_id or a separate participants table)

**Interactions**
- REST endpoints: `/api/dm/conversations`, `/api/dm/conversations/{id}`.
- Used by Messaging Service to validate DM messages.

## 5. Messaging Service (`chat-module`)

**Responsibilities**
- Accept messages from REST or WebSocket:
  - Channel messages.
  - DM messages.
- Validate sender permissions (member of channel or DM participant).
- Persist messages to MongoDB.
- Guarantee per‑conversation ordering (based on created timestamp / sequence).
- Publish events for real‑time delivery to WebSocket clients (and across instances via Redis).

**Data**
- MongoDB collections:
  - `channel_messages`
  - `dm_messages`

**Interactions**
- REST endpoints:
  - `/api/channels/{id}/messages` (GET history, POST new message).
  - `/api/dm/conversations/{id}/messages`.
- WebSocket endpoints/topics:
  - For example: `/topic/channel.{channelId}` for channel updates.
  - `/user/queue/dm.{conversationId}` or similar for DM updates.
- Redis pub/sub for cross‑instance message fan‑out.

## 6. Presence Service (`chat-module`)

**Responsibilities**
- Track online/offline status of users based on WebSocket connections and heartbeats.
- Maintain in‑memory presence map per instance.
- Sync presence state across instances via Redis (optional initial phase; can start single‑node).
- Expose presence information via API and via WebSocket events.

**Data**
- Primary state: in‑memory + Redis (fast, ephemeral).
- Optional snapshot: PostgreSQL `user_presence` table for historical or backup purposes.

**Interactions**
- WebSocket lifecycle events (onConnect, onDisconnect).
- REST endpoints: `/api/presence/{userId}`, `/api/presence/bulk`.
- WebSocket topics for real‑time presence updates.

## 7. Persistence Layer (Repositories – spread across `identity-module`, `chat-module`, `shared-module`)

**Responsibilities**
- Abstract DB access for all services.
- Use Spring Data JPA for PostgreSQL entities (users, channels, memberships, DMs, optional presence snapshot).
- Use Spring Data MongoDB for message documents.

**Interactions**
- Called synchronously by domain services.
- Exposes repository interfaces like `UserRepository`, `ChannelRepository`, `MessageRepository`, etc.

## 8. Security Layer (JWT & Access Control – `identity-module`)

**Responsibilities**
- Authenticate REST & WebSocket connections via JWT.
- Provide Spring Security filters/configuration.
- Map JWT claims to authenticated principals.
- Enforce authorization rules (e.g., user can only access channels/DMs they are allowed to).

**Interactions**
- Intercepts and guards all `/api/**` endpoints.
- Intercepts WebSocket handshake to validate tokens.
- Delegates to Auth & User Service for user lookups.

## 9. Observability (Logging, Metrics, Tracing – `shared-module`)

**Responsibilities**
- Centralized logging configuration (structured logs).
- Metrics instrumentation via Micrometer (HTTP metrics, DB metrics, WebSocket metrics).
- Optional tracing integration (OpenTelemetry) for distributed tracing.

**Interactions**
- Cross‑cutting: used by all services/components.

## 10. External Integrations (Future)

Not in v1, but potential future components:
- Notification service (email/push for mentions, missed messages).
- Search service for full‑text search across messages.
- File storage integration (S3 or similar) for attachments.

These can be designed later on top of the core architecture defined here.