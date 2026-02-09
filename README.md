# Slack Clone Backend

A Slack-like chat application backend built with Spring Boot, following a modular monolith architecture.

## Architecture

The application is structured as a multi-module Gradle project:

- **app**: Main Spring Boot application module with REST controllers and WebSocket configuration
- **identity-module**: Authentication, user management, and JWT security
- **chat-module**: Channels, DMs, messaging, and presence management
- **shared-module**: Common utilities, exceptions, and cross-cutting concerns

## Tech Stack

- **Java 21** - Latest LTS version
- **Spring Boot 3.2.2** - Main framework
- **PostgreSQL** - Relational data (users, channels, memberships, DM metadata)
- **MongoDB** - Message storage (channel and DM messages)
- **Redis** - Caching and pub/sub for real-time features
- **JWT** - Stateless authentication
- **WebSocket (STOMP)** - Real-time messaging
- **Lombok** - Reducing boilerplate code
- **MapStruct** - DTO mapping
- **Swagger/OpenAPI** - API documentation

## Prerequisites

- JDK 21
- Docker and Docker Compose (for databases)
- Gradle 8.x (wrapper included)

## Getting Started

### 1. Start Infrastructure Services

Start PostgreSQL, MongoDB, and Redis using Docker Compose:

```bash
docker-compose up -d
```

This will start:
- PostgreSQL on port 5432
- MongoDB on port 27017
- Redis on port 6379

### 2. Build the Project

```bash
./gradlew clean build
```

### 3. Run the Application

```bash
./gradlew :app:bootRun
```

The application will start on `http://localhost:8080`

### 4. Access API Documentation

Once the application is running, access Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

## Configuration

Application configuration is located in `app/src/main/resources/application.properties`.

Key configurations:

| Property | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | 8080 | Application port |
| `POSTGRES_URL` | jdbc:postgresql://localhost:5432/slackclone | PostgreSQL connection |
| `MONGODB_URI` | mongodb://localhost:27017/slackclone | MongoDB connection |
| `REDIS_HOST` | localhost | Redis host |
| `JWT_SECRET` | (base64 encoded) | Secret for JWT signing |
| `JWT_EXPIRATION` | 3600000 | Token expiration (1 hour) |

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token
- `GET /api/auth/me` - Get current user profile

### Channels

- `POST /api/channels` - Create channel
- `GET /api/channels` - List all channels
- `GET /api/channels/{id}` - Get channel details
- `POST /api/channels/{id}/join` - Join channel
- `POST /api/channels/{id}/leave` - Leave channel
- `GET /api/channels/{id}/messages` - Get channel messages
- `POST /api/channels/{id}/messages` - Send message to channel

### Direct Messages

- `POST /api/dm/conversations` - Create/get DM conversation
- `GET /api/dm/conversations` - List user's DM conversations
- `GET /api/dm/conversations/{id}` - Get DM conversation
- `GET /api/dm/conversations/{id}/messages` - Get DM messages
- `POST /api/dm/conversations/{id}/messages` - Send DM message

### WebSocket

Connect to WebSocket at: `ws://localhost:8080/ws`

Topics:
- `/topic/channel.{channelId}` - Subscribe to channel messages
- `/queue/dm.{conversationId}` - Subscribe to DM messages
- `/topic/presence` - Subscribe to presence updates

## Project Structure

```
Slack2.0/
├── app/                          # Main application module
│   ├── src/main/java/
│   │   └── com/slack/clone/
│   │       ├── SlackCloneApplication.java
│   │       ├── controller/       # REST controllers
│   │       ├── config/           # Configuration classes
│   │       └── exception/        # Global exception handler
│   └── src/main/resources/
│       └── application.properties
├── identity-module/              # Auth & User management
│   └── src/main/java/
│       └── com/slack/clone/identity/
│           ├── entity/           # User entity
│           ├── repository/       # User repository
│           ├── service/          # Auth service
│           ├── security/         # JWT & Security config
│           ├── dto/              # DTOs
│           └── mapper/           # MapStruct mappers
├── chat-module/                  # Chat domain
│   └── src/main/java/
│       └── com/slack/clone/chat/
│           ├── entity/           # Channel, ChannelMember, DmConversation
│           ├── document/         # MongoDB documents
│           ├── repository/       # JPA & Mongo repositories
│           ├── service/          # Business logic
│           ├── dto/              # DTOs
│           └── mapper/           # MapStruct mappers
├── shared-module/                # Shared utilities
│   └── src/main/java/
│       └── com/slack/clone/shared/
│           ├── exception/        # Custom exceptions
│           ├── dto/              # Common DTOs
│           └── util/             # Utilities
├── docker-compose.yml            # Infrastructure services
├── Dockerfile                    # Application container
├── build.gradle                  # Root Gradle config
└── settings.gradle               # Module configuration
```

## Testing

Run all tests:

```bash
./gradlew test
```

Run tests for specific module:

```bash
./gradlew :identity-module:test
./gradlew :chat-module:test
```

## Building Docker Image

Build the application Docker image:

```bash
docker build -t slack-clone-backend .
```

Run with Docker Compose (uncomment app service in docker-compose.yml):

```bash
docker-compose up
```

## Development Guidelines

- Follow [Core Standards](.github/guidelines/core-standards.md)
- Follow [Java & Spring Boot Standards](.github/guidelines/java-spring-boot.md)
- Keep methods small and focused
- Use constructor injection for dependencies
- Write unit tests for services
- Use DTOs for API responses (never expose entities)

## Future Enhancements

- [ ] User presence tracking
- [ ] Message reactions and threading
- [ ] File uploads
- [ ] Full-text search
- [ ] Private channels
- [ ] Message editing and deletion
- [ ] Typing indicators
- [ ] Multi-workspace support

## License

MIT License
