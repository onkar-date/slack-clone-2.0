# API Design – Slack Clone Backend

This document outlines the main external APIs (HTTP/REST + WebSockets) for the Slack‑like backend. It is **high‑level**, meant for system design and not a full OpenAPI spec.

---

## 1. Authentication & Users

### POST /api/auth/register
- **Purpose**: Register a new user.
- **Auth**: Public.
- **Request (JSON)**
  - `email`: string
  - `password`: string
  - `displayName`: string
- **Response (201)**
  - `id`: string (userId)
  - `email`: string
  - `displayName`: string
  - `createdAt`: ISO timestamp

### POST /api/auth/login
- **Purpose**: Authenticate user and issue JWT.
- **Auth**: Public.
- **Request (JSON)**
  - `email`: string
  - `password`: string
- **Response (200)**
  - `accessToken`: string (JWT)
  - `tokenType`: string (e.g., "Bearer")
  - `expiresIn`: number (seconds)
  - `user`: basic user object

### GET /api/users/me
- **Purpose**: Get current user profile.
- **Auth**: JWT required.
- **Response (200)**
  - `id`, `email`, `displayName`, `createdAt`.

---

## 2. Channels

### POST /api/channels
- **Purpose**: Create a new channel.
- **Auth**: JWT required.
- **Request (JSON)**
  - `name`: string
  - `description`: string (optional)
- **Response (201)**
  - `id`, `name`, `description`, `createdBy`, `createdAt`.

### GET /api/channels
- **Purpose**: List channels current user can see.
- **Auth**: JWT required.
- **Query Params**
  - Optional pagination (`page`, `size`).
- **Response (200)**
  - Array of channel summaries:
    - `id`, `name`, `description`, `memberCount`, `createdAt`.

### POST /api/channels/{channelId}/join
- **Purpose**: Join a channel.
- **Auth**: JWT required.
- **Response (200/204)**
  - Success or no content.

### POST /api/channels/{channelId}/leave
- **Purpose**: Leave a channel.
- **Auth**: JWT required.
- **Response (200/204)**
  - Success or no content.

---

## 3. Direct Messages (DMs)

### POST /api/dm/conversations
- **Purpose**: Create or fetch a DM conversation between current user and another.
- **Auth**: JWT required.
- **Request (JSON)**
  - `peerUserId`: string
- **Response (200/201)**
  - `id`: conversationId
  - `participants`: array of user summaries
  - `createdAt`: timestamp

### GET /api/dm/conversations
- **Purpose**: List DM conversations for current user.
- **Auth**: JWT required.
- **Response (200)**
  - Array of conversations (id, participants, lastMessage summary, updatedAt).

### GET /api/dm/conversations/{conversationId}
- **Purpose**: Get details of a specific DM conversation.
- **Auth**: JWT required.
- **Response (200)**
  - `id`, `participants`, metadata.

---

## 4. Message History (Channels & DMs)

### GET /api/channels/{channelId}/messages
- **Purpose**: Get paginated message history for a channel.
- **Auth**: JWT required; user must be a member.
- **Query Params**
  - `page`: int (optional)
  - `size`: int (optional)
  - Or cursor‑based: `before`, `after` timestamps or message IDs.
- **Response (200)**
  - `items`: array of messages
    - `id`, `senderId`, `content`, `createdAt`, `type`.
  - `pageInfo` or `nextCursor` for pagination.

### GET /api/dm/conversations/{conversationId}/messages
- Same pattern as channel messages, but for DMs.

### POST /api/channels/{channelId}/messages (Optional)
- **Purpose**: Send a message via REST instead of WebSocket.
- **Auth**: JWT required.
- **Request (JSON)**
  - `content`: string
- **Response (201)**
  - The created message object.

### POST /api/dm/conversations/{conversationId}/messages (Optional)
- Same pattern for DMs.

---

## 5. Presence

### GET /api/presence/{userId}
- **Purpose**: Get presence status of a single user.
- **Auth**: JWT required.
- **Response (200)**
  - `userId`: string
  - `status`: `ONLINE` | `OFFLINE`
  - `lastSeenAt`: timestamp

### POST /api/presence/bulk
- **Purpose**: Get presence for a list of user IDs.
- **Auth**: JWT required.
- **Request (JSON)**
  - `userIds`: array of strings
- **Response (200)**
  - Array of presence objects.

---

## 6. WebSocket API

### Endpoint
- **URL**: `/ws` (upgraded from HTTP to WebSocket or STOMP over WebSocket).
- **Auth**: JWT provided during handshake (e.g., as header or query param) and validated by server.

### Connection Flow
1. Client logs in and gets JWT.
2. Client opens WebSocket connection to `/ws` with token.
3. On connect, server marks user as `ONLINE` and subscribes them to default topics.

### Channels (Topics / Destinations)

If using STOMP style semantics:

- **Subscribe to channel messages**
  - Destination: `/topic/channel.{channelId}`
  - Server sends messages with payload:
    - `id`, `channelId`, `senderId`, `content`, `createdAt`, `type`.

- **Subscribe to DM messages**
  - Destination: `/queue/dm.{conversationId}` or `/user/queue/dm.{conversationId}`.

- **Presence updates**
  - Destination: `/topic/presence` or per‑channel/user scopes as needed.
  - Payload: `userId`, `status`, `timestamp`.

### Sending Messages over WebSocket

- **Send channel message**
  - Client sends to `/app/channels.{channelId}.send` (application destination).
  - Payload: `{ content: string }`.
  - Server:
    - Authenticates, validates membership.
    - Persists message to Mongo.
    - Broadcasts to `/topic/channel.{channelId}`.

- **Send DM message**
  - Client sends to `/app/dm.{conversationId}.send`.
  - Similar flow: validate conversation, persist, broadcast.

### Error Handling

- On auth failure: close connection with appropriate code and message.
- On invalid destination or permission error: send error frame/message to client.

---

## 7. Versioning & Extensibility

- REST endpoints are prefixed with `/api` and can adopt `/api/v1` if versioning is needed.
- WebSocket destinations use a namespace convention (`/app/...`, `/topic/...`, `/queue/...`) that can be extended to support:
  - Typing indicators.
  - Message edits/deletes.
  - Reactions and threads.

This API design intentionally keeps things simple but realistic, enabling a small UI to be built while illustrating standard system design practices.