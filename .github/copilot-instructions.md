# Copilot Instructions - Slack Clone Backend

This repository contains a Slack-like chat backend built with Spring Boot. This file provides quick navigation to all AI agent guidance documents and the system design for the Slack clone.

## Documentation Structure

### **Start Here: Project Context**

If you are working on the Slack clone backend, start with the system design documents:

#### [**Slack Clone – Problem & Goals**](./stories-and-plans/system-designs/slack-clone-backend/01-problem-and-goals.md)

**Purpose**: High-level problem statement and scope for the Slack-like chat system.  
**What it covers**:

- Target features (auth, channels, DMs, messaging, presence)
- User journeys
- Goals vs explicit non-goals

#### [**Slack Clone – Requirements & NFRs**](./stories-and-plans/system-designs/slack-clone-backend/02-requirements-and-nfrs.md)

**Purpose**: Functional and non-functional requirements for the backend.  
**What it covers**:

- Auth, channels, DMs, messaging, presence requirements
- Scale assumptions, availability, performance, consistency, security, observability

#### [**Slack Clone – Architecture & Tech Stack**](./stories-and-plans/system-designs/slack-clone-backend/03-architecture-and-tech-stack.md)

**Purpose**: Overall architecture and technology choices.  
**What it covers**:

- Modular-monolith design with WebSockets and REST
- Polyglot persistence (Postgres + Mongo + Redis)
- Multi-module Gradle layout (`app`, `identity-module`, `chat-module`, `shared-module`)

#### [**Slack Clone – Components & Responsibilities**](./stories-and-plans/system-designs/slack-clone-backend/04-components-and-responsibilities.md)

**Purpose**: Clear ownership and boundaries for each component.  
**What it covers**:

- API/WebSocket gateway
- Auth/identity, channels, DMs, messaging, presence
- Security and observability layers

#### [**Slack Clone – Data Model & Schemas**](./stories-and-plans/system-designs/slack-clone-backend/05-data-model-and-schemas.md)

**Purpose**: Database schemas and relationships.  
**What it covers**:

- Postgres tables (users, channels, memberships, dm_conversations, optional presence)
- MongoDB collections (channel_messages, dm_messages) and indexing

#### [**Slack Clone – API Design**](./stories-and-plans/system-designs/slack-clone-backend/06-api-design.md)

**Purpose**: High-level REST and WebSocket API contracts.  
**What it covers**:

- Auth/user, channel, DM, messaging, presence endpoints
- WebSocket endpoints, topics, and typical flows

#### [**Slack Clone – Deployment, Operations & Roadmap**](./stories-and-plans/system-designs/slack-clone-backend/07-deployment-operations-and-roadmap.md)

**Purpose**: How to run, scale, and evolve the system.  
**What it covers**:

- docker-compose layout for local dev
- Cloud-friendly deployment model
- Observability and phased implementation plan

---

### **Coding Standards & Guidelines**

#### [**Core Standards**](./guidelines/core-standards.md) ⭐ READ FIRST

**Purpose**: Universal coding principles applicable to ALL languages and projects  
**What it covers**:

- General principles (KISS, YAGNI, DRY, Boy Scout Rule, SOLID)
- Code smell detection and elimination
- Method/class size guidelines
- Naming conventions and readability requirements
- Cross-functional requirements (error handling, logging, security, performance, i18n)
- Testing best practices

**When to consider**:

- **Before writing ANY code** - these are mandatory principles
- When reviewing code quality
- Resolving design decisions
- Identifying code smells
- Refactoring legacy code

#### [**Java & Spring Boot Standards**](./guidelines/java-spring-boot.md)

**Purpose**: Java and Spring Boot best practices for the **backend service**  
**What it covers**:

- Java language standards (modern Java features, type system, null safety)
- Spring Boot patterns (dependency injection, component annotations)
- Layered architecture (Controller → Service → Repository)
- MongoDB with Spring Data
- DTOs and validation (Bean Validation)
- Exception handling and global error handling
- Transaction management
- Testing (JUnit 5, Mockito, Testcontainers)
- Logging and configuration

**When to use**:

- Working on the Slack clone Spring Boot backend
- Writing controllers, services, repositories, or WebSocket handlers
- Working with Postgres, MongoDB, or Redis
- Implementing REST/WebSocket endpoints
- Writing backend tests

#### [**React & JavaScript Standards**](./guidelines/react-javascript.md)

**Purpose**: React 18.2 and modern JavaScript/TypeScript best practices for any UI work that talks to this backend.  
**What it covers**:

- Modern JavaScript (ES6+): const/let, arrow functions, destructuring, async/await
- React functional components and hooks
- State management (useState, useEffect, Context API)
- Component design patterns (presentational vs container)
- API integration with Axios
- React Router v6 for routing
- Forms and validation
- Error handling (Error Boundaries)
- Testing (React Testing Library, Jest)
- Performance and accessibility

**When to use**:

- Working on a React SPA (for example: Vite + React + TypeScript) for the Slack clone
- Writing React components and hooks
- Managing client-side routing (React Router) and server state (React Query)
- Integrating with REST and WebSocket APIs exposed by the backend
- Writing frontend tests

---

## Recommended Workflow

### For Backend Development (Java/Spring Boot)

1. **First visit**: Read the Slack clone system design docs (problem, requirements, architecture).
2. **Before coding**: Review [core-standards.md](./guidelines/core-standards.md).
3. **While coding**: Follow [java-spring-boot.md](./guidelines/java-spring-boot.md).
4. **Always**: Run the Gradle test tasks before committing.
5. **Testing**: Write unit tests for services, integration tests for repositories and WebSocket handlers where relevant.

### For Frontend Development (React SPA)

1. **First visit**: Skim the Slack clone API and architecture docs so the UI matches backend contracts.
2. **Before coding**: Review [core-standards.md](./guidelines/core-standards.md) and [react-javascript.md](./guidelines/react-javascript.md).
3. **Stack**: Prefer Vite + React + TypeScript, React Router, React Query, and a small WebSocket client wrapper for real-time messaging.
4. **Always**: Run frontend tests (Jest/React Testing Library) before committing.
5. **Integration**: Manually verify key flows (login, channel list, DM list, basic chat) against the running backend.

### Full Stack Development

1. **Understanding the system**: Read the Slack clone system design docs end-to-end.
2. **Backend changes**: Apply Java/Spring Boot and database guidelines.
3. **Frontend changes**: Apply React/JavaScript/TypeScript guidelines.
4. **Integration**: Run the backend (and later docker-compose) plus the Vite dev server; test end-to-end flows.
5. **Running locally**: Use docker-compose for backend infra (Postgres, Mongo, Redis) and `npm run dev` (or `pnpm`/`yarn` equivalent) for the React SPA.

---

## Quick Reference by Task

| Task                | Guidelines to Follow                                                                                             |
| ------------------- | ---------------------------------------------------------------------------------------------------------------- |
| REST API endpoint   | [Core](./guidelines/core-standards.md) → [Java/Spring Boot](./guidelines/java-spring-boot.md) § Controller Layer |
| Service layer logic | [Core](./guidelines/core-standards.md) → [Java/Spring Boot](./guidelines/java-spring-boot.md) § Service Layer    |
| MongoDB repository  | [Java/Spring Boot](./guidelines/java-spring-boot.md) § Repository Layer                                          |
| Domain entity       | [Java/Spring Boot](./guidelines/java-spring-boot.md) § Domain Models                                             |
| React component     | [Core](./guidelines/core-standards.md) → [React/JavaScript](./guidelines/react-javascript.md) § Component Design |
| API integration     | [React/JavaScript](./guidelines/react-javascript.md) § API Integration                                           |
| Form handling       | [React/JavaScript](./guidelines/react-javascript.md) § Forms and Validation                                      |
| State management    | [React/JavaScript](./guidelines/react-javascript.md) § State Management                                          |
| Error handling      | [Core](./guidelines/core-standards.md) § Error Handling + service-specific guide                                 |
| Testing             | [Core](./guidelines/core-standards.md) § Testing + service-specific guide                                        |

---

## Code Review Checklist

Before submitting code for review:

- [ ] **Core Standards**: Applied SOLID principles, no code smells, small methods/classes
- [ ] **Types**: Backend uses proper Java types, Frontend has PropTypes
- [ ] **Error Handling**: Comprehensive error handling with proper logging
- [ ] **Security**: Input validation, no hardcoded secrets, sanitized data
- [ ] **Testing**: Unit tests for services/utilities, component tests for React
- [ ] **Logging**: Structured logging with appropriate levels
- [ ] **Documentation**: Complex logic has explanatory comments (WHY, not WHAT)
- [ ] **Performance**: No N+1 queries, proper indexing, resource cleanup
- [ ] **Backend**: DTOs for API, transaction management, Bean Validation
- [ ] **Frontend**: Functional components with hooks, proper state management

---

## Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data MongoDB](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/)
- [React Documentation](https://react.dev/)
- [React Router](https://reactrouter.com/)
- [MongoDB Best Practices](https://www.mongodb.com/docs/manual/administration/production-notes/)
