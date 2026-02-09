# Data Model & Schemas – Slack Clone Backend

## Overview

We use **polyglot persistence**:
- **PostgreSQL** for relational, transactional data (users, channels, memberships, DM metadata, optional presence snapshot).
- **MongoDB** for high‑volume, append‑only message data (channel and DM messages).

This section describes the main entities and key indexing strategies.

---

## PostgreSQL Schema (Relational)

### 1. users

Represents registered users.

- `id` (UUID, PK)
- `email` (varchar, unique)
- `username` (varchar, unique or optional, depending on design)
- `display_name` (varchar)
- `password_hash` (varchar)
- `created_at` (timestamp)
- `updated_at` (timestamp)

**Indexes**
- Unique index on `email`.
- Optional index on `username`.

### 2. channels

Represents chat channels.

- `id` (UUID, PK)
- `name` (varchar, unique within workspace)
- `description` (varchar, nullable)
- `created_by` (UUID → users.id)
- `created_at` (timestamp)

**Indexes**
- Unique index on `name`.
- Index on `created_by` if needed for queries like "channels I created".

### 3. channel_members

Mapping between users and channels.

- `id` (UUID, PK) or composite key
- `channel_id` (UUID → channels.id)
- `user_id` (UUID → users.id)
- `joined_at` (timestamp)

**Indexes**
- Composite index on (`channel_id`, `user_id`) and optionally unique.
- Index on `user_id` for quickly listing a user’s channels.

### 4. dm_conversations

Represents a DM conversation between two users.

- `id` (UUID, PK)
- `user1_id` (UUID → users.id)
- `user2_id` (UUID → users.id)
- `created_at` (timestamp)

**Design choice**
- For v1, enforce **one DM conversation per unordered pair** (user1, user2).
  - For example, store smaller UUID as `user1_id`, larger as `user2_id`.

**Indexes**
- Unique composite index on (`user1_id`, `user2_id`).
- Index on `user1_id` and `user2_id` for queries like "all my DM conversations".

### 5. user_presence (optional snapshot)

Ephemeral presence primarily lives in memory/Redis, but we may optionally persist snapshots.

- `user_id` (UUID → users.id, PK)
- `status` (enum: ONLINE, OFFLINE)
- `last_seen_at` (timestamp)

**Indexes**
- PK on `user_id`.
- Index on `status` if querying for all online users (likely low volume; may not be needed).

---

## MongoDB Schema (Messages)

We use separate collections for channel messages and DM messages for clarity and indexing optimization.

### 1. channel_messages Collection

Each document represents a message sent in a channel.

- `_id`: ObjectId (or UUID)
- `channelId`: string/UUID (refers to Postgres channels.id)
- `senderId`: string/UUID (refers to Postgres users.id)
- `content`: string (message body; can be extended to structured content)
- `createdAt`: Date (server‑side timestamp)
- `editedAt`: Date (nullable, for future edit feature)
- `type`: string (e.g., `TEXT`, `SYSTEM`; extensible for future)

**Indexes**
- Compound index on `{ channelId: 1, createdAt: 1 }`.
  - Supports efficient pagination of messages within a channel ordered by time.
- Optional index on `{ senderId: 1, createdAt: -1 }` for auditing or user message history.

### 2. dm_messages Collection

Each document represents a DM message between two users in a specific DM conversation.

- `_id`: ObjectId (or UUID)
- `conversationId`: string/UUID (refers to Postgres dm_conversations.id)
- `senderId`: string/UUID (refers to Postgres users.id)
- `content`: string
- `createdAt`: Date
- `editedAt`: Date (nullable)
- `type`: string

**Indexes**
- Compound index on `{ conversationId: 1, createdAt: 1 }`.
  - Efficient pagination for a conversation’s history.

---

## Relationships & Access Patterns

### Channels

- To render a channel view:
  1. Look up channel metadata in Postgres (`channels`).
  2. Validate that user is a member via `channel_members`.
  3. Query Mongo `channel_messages` by `channelId`, ordered by `createdAt` with pagination.

- To join a channel:
  - Insert record into `channel_members`.

### DMs

- To start a DM:
  1. Check if `dm_conversations` entry exists for the two users (using (user1_id, user2_id) pair).
  2. If not, create it.
  3. Return `conversationId`.

- To view DM messages:
  - Query Mongo `dm_messages` by `conversationId` with pagination.

### Presence

- Primary presence state is kept in memory and Redis.
- Optional Postgres snapshot only for backup/analytics; not used for real‑time decisions.

---

## Consistency Considerations

- **Cross‑store consistency** (Postgres + Mongo):
  - For most operations, we can accept eventual consistency between metadata and messages.
  - Example: channel metadata in Postgres is authoritative; messages reference `channelId` but do not require a distributed transaction.

- **Per‑conversation ordering**:
  - Server assigns `createdAt` timestamp and writes messages to Mongo in the order requests are accepted.
  - Indexes on `(channelId, createdAt)` or `(conversationId, createdAt)` ensure retrieval in that order.

- **Failure scenarios**:
  - If message persistence to Mongo fails after broadcasting over WebSocket, the system should:
    - Either ensure persist‑then‑broadcast semantics (preferred),
    - Or implement compensations (e.g., error messages, retries) to avoid ghost messages.

---

## Future Extensions

- Add **message reactions**, **threads**, and **attachments**:
  - Could extend Mongo documents with nested arrays (reactions, thread IDs), or add new collections.

- Add **search**:
  - Either leverage Mongo’s text search indexes.
  - Or introduce a dedicated search engine (Elasticsearch/OpenSearch) that indexes Mongo docs.

- Support **multi‑workspace**:
  - Add `workspace_id` to users, channels, memberships, DMs.
  - Partition data logically per workspace.