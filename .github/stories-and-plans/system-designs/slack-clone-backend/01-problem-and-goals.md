# Slack Clone Backend – Problem & Goals

## Problem Statement

Design and build a backend (plus basic UI assumptions and infra) for a Slack‑like chat application with limited but realistic features:

- User registration and login
- Public channels
- 1:1 direct messages
- Real‑time message delivery
- Message history
- User presence status (online/offline)

The primary goal is **learning system design concepts**, not shipping a production product. The system should still be realistic enough that it could later be deployed to the cloud.

## Target Users & Clients

- **End users**: People who create accounts, log in, join channels, send DMs, and chat in real time.
- **Client types (for this design)**:
  - Web client (browser) using REST + WebSockets
  - (Future) Mobile apps can be added later using the same APIs.

## Key User Journeys (v1)

1. **Register & Login**
   - User registers with email/username + password.
   - User logs in and receives a JWT access token.

2. **Channels**
   - User can view list of channels they can access.
   - User can create a channel (e.g., public for v1).
   - User can join/leave channels.
   - User can see message history in a channel.
   - User can send messages to a channel.

3. **Direct Messages (1:1 DMs)**
   - User can open a DM conversation with another user.
   - User can see DM history.
   - User can send messages in a DM.

4. **Real‑Time Messaging**
   - When a user sends a message in a channel or DM, all online participants see it appear in near real time.

5. **Presence**
   - When a user is connected, they appear as **online**.
   - When they disconnect/idle, they appear as **offline** (or away) after some timeout.

## Goals

- **Educational focus**:
  - Practice designing a **modular backend** in Spring Boot (JDK 21).
  - Practice **polyglot persistence** with PostgreSQL (users/channels) and MongoDB (messages).
  - Understand **real‑time communication** with WebSockets.
  - Cover **core NFRs**: availability, performance, scalability (single region, medium scale), security basics, and observability.
  - Design **infra** for local Docker/docker‑compose that is easy to migrate to cloud later.

- **Architecture goals**:
  - Stateless HTTP layer with JWT‑based auth.
  - WebSocket gateway for real‑time messaging, horizontally scalable.
  - Clean separation of domains: auth/user, channels, messaging, presence.
  - Clear API boundaries so a real UI can be built later.

## Non‑Goals (Explicitly Out of Scope for v1)

- File uploads and file sharing.
- Message threading, replies, reactions, emojis, rich formatting.
- Multiple workspaces/organizations; assume a single logical workspace.
- Complex channel permissions (ACLs, private channels, roles) – start with simple public channels and basic membership.
- Search across messages and channels (can be added later, possibly with a search index).
- Multi‑region deployment; assume a single region / single data center.
- Full enterprise‑grade security, compliance (SOC2/GDPR), and SSO.

These non‑goals keep the design focused while leaving room for future extensions if needed for deeper learning.