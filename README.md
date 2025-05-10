# keycloak_demo
Authentication SPI in Keycloak that accepts authentication via a custom token sent in the X-Konneqt-Token header.

## Goal

### Keycloak
- Implement an authenticator in Keycloak that handles the `X-Konneqt-Token` header.  
  The header contains an email address used to fetch the user from the test realm.  
  If the user does not exist, the authenticator creates a new user with the provided email address.  
  The first and last names are derived from the email's local part.

### Backend
- Implement the `/api/login` and `/api/user/info` endpoints.  
  - The `/api/login` endpoint passes the `X-Konneqt-Token` header to the Keycloak authenticator and returns a JWT `access_token`.  
  - The `/api/user/info` endpoint retrieves user information from the JWT `access_token`.

### Frontend
- Implement the `/login` and `/user-info` pages.
  - The `/login` page includes an email address field and a login button.
    When the login button is clicked, the `/api/login` endpoint is called with the email address in the `X-Konneqt-Token` header.  
    Once the JWT `access_token` is returned, the `/user-info` page is opened.  
  - The `/user-info` page calls the `/api/user/info` endpoint, passing the `Authorization` header with the JWT `access_token`.  
    The user information retrieved is used to populate the form.

### Docker
- Implement a Docker Compose stack with the Frontend, Backend, Keycloak, and PostgreSQL services.

## Prerequisites

- JDK 21+
- Docker 27+
- Gradle 8.8+
- Git

## Installation

```sh
# Clone the keycloak_demo repository
git clone https://github.com/pintode/keycloak_demo

# Access keycloak_demo directory
cd keycloak_demo

# Generate ./backend/build/libs/backend-1.0.0.jar
gradle -p ./backend build

# Generate ./keycloak/build/libs/keycloak-1.0.0.jar
gradle -p ./keycloak build

# Build all images
docker compose build
```

## Running the Docker Compose stack

```sh
# Start the Docker Compose stack
docker compose up

# Stop the Docker Compose stack
docker compose down
```

## Services URLs

- Frontend: [http://localhost:3000](http://localhost:3000)
- Backend: [http://localhost:8081/api](http://localhost:8081/api)
- Keycloak: [http://localhost:8080](http://localhost:8080) (user: admin, password: admin_password)
- PostgreSQL: [jdbc:postgresql://postgres:5432/keycloak_db?user=keycloak_db_user&password=keycloak_db_password](jdbc:postgresql://postgres:5432/keycloak_db?user=keycloak_db_user&password=keycloak_db_password)

Note: Keycloak and PostgreSQL ports were exposed for demonstration purposes only.

## Base Docker images:
- Frontend: node:18
- Backend: [ghcr.io/graalvm/jdk-community:24](ghcr.io/graalvm/jdk-community:24)
- Keycloak: [quay.io/keycloak/keycloak:26.2](quay.io/keycloak/keycloak:26.2)
- PostgreSQL: postgres:17

## Keycloak Documentation Reference Links
- [https://www.keycloak.org/docs/latest/server_development/index.html#implementing-an-authenticator](https://www.keycloak.org/docs/latest/server_development/index.html#implementing-an-authenticator)
- [https://www.keycloak.org/docs/latest/server_development/index.html#implementing-an-authenticatorfactory](https://www.keycloak.org/docs/latest/server_development/index.html#implementing-an-authenticatorfactory)