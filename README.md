# Comissio - Commission Management System

## 📖 Description

**Comissio** is a robust backend system designed to automate and manage the calculation of employee commissions based on the services they perform. The system aims to replace manual processes, reduce inconsistencies, provide transparent financial control, and generate detailed reports, optimizing commission management.

This project is built using Java with Spring Boot and follows best development practices to ensure a secure, scalable, and easily maintainable application.

---

## ✨ Key Features

* **Authentication and Authorization:** Secure system with JWT-based authentication and role-based access control using Spring Security.
* **User Management:** User registration and login with different access levels (ADMIN, MANAGER, etc.).
* **Employee Management:** Registration and maintenance of employee information.
* **Service Type Management:** Definition of offered services that can generate commissions.
* **Commission Configuration:** Allows defining commission rules, including standard percentages per service type and specific percentages per employee.
* **Registration of Performed Services:** Logging of services performed by each employee.
* **Automatic Commission Calculation:** Automatic calculation of commissions based on configured rules.
* **Payment Control:** Tracking of pending and paid commissions.
* **Report Generation:** Issuance of detailed commission reports by employee, period, among other filters.
* **API Documentation:** API documented with Swagger (Springdoc OpenAPI) for easy integration and testing.
* **Database Migrations:** Database schema versioning management using Flyway.

---

## 🛠️ Tech Stack

* **Backend:**
    * Java 21
    * Spring Boot 3.4.0
        * Spring Web (RESTful APIs)
        * Spring Data JPA (Data Persistence)
        * Spring Security (Authentication and Authorization)
    * Hibernate Validator (Data Validation)
* **Database:**
    * PostgreSQL
    * Flyway (Schema Versioning)
* **Authentication:**
    * JSON Web Tokens (JWT) - via `com.auth0:java-jwt`
* **API Documentation:**
    * Springdoc OpenAPI (Swagger)
* **Build & Dependency Management:**
    * Apache Maven
* **Utilities:**
    * Lombok

---

## ⚙️ Prerequisites

Before you begin, ensure you have the following installed:

* JDK 21 or higher
* Apache Maven 3.6.x or higher
* Docker and Docker Compose (recommended for the PostgreSQL database) or a locally installed PostgreSQL instance.
* Your preferred IDE (IntelliJ IDEA, VS Code, Eclipse, etc.)

---

## 🚀 Getting Started

Follow the steps below to set up and run the project locally:

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/srmatheusmaciel/comissio.git
    cd comissio
    ```

2.  **Database Setup (PostgreSQL):**
    * **Using Docker (Recommended):**
      Create a `docker-compose.yml` file in the project root with the following content:
        ```yaml
        version: '3.8'
        services:
          postgres-comissio:
            image: postgres:15 # Or your preferred version
            container_name: comissio_db_container
            environment:
              POSTGRES_USER: postgres
              POSTGRES_PASSWORD: postgres
              POSTGRES_DB: comissio_db
            ports:
              - "5433:5432" # Maps host port 5433 to container port 5432
            volumes:
              - comissio_postgres_data:/var/lib/postgresql/data

        volumes:
          comissio_postgres_data:
        ```
      Then run:
        ```bash
        docker-compose up -d
        ```
    * **Local Instance:** If you have a local PostgreSQL instance running, ensure it's accessible at `jdbc:postgresql://localhost:5433/`, create a database named `comissio_db`, and configure the user `postgres` with the password `postgres` (or adjust the settings in `application.properties`).

3.  **Environment Variables:**
    The project uses an environment variable for the JWT secret. You can set it in your system or in your IDE's run configuration:
    * `JWT_SECRET`: The secret for signing JWT tokens. If not set, it will use the default value `my-secret-key` (defined in `application.properties` as a fallback).
      Example (Linux/macOS): `export JWT_SECRET="your-super-secret-key-here"`

4.  **Application Configuration:**
    Check the `src/main/resources/application.properties` file for database, Flyway, Swagger, and JWT secret configurations:
    ```properties
    # Database
    spring.datasource.url=jdbc:postgresql://localhost:5433/comissio_db
    spring.datasource.username=postgres
    spring.datasource.password=postgres
    spring.datasource.driver-class-name=org.postgresql.Driver
    spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
    spring.jpa.hibernate.ddl-auto=validate # Flyway will handle schema creation/updates

    # Flyway
    spring.flyway.enabled=true
    spring.flyway.locations=classpath:db/migration

    # Swagger / Springdoc
    springdoc.api-docs.path=/v3/api-docs
    springdoc.swagger-ui.path=/swagger-ui.html

    # JWT Secret (with fallback)
    api.security.token.secret=${JWT_SECRET:my-secret-key}
    ```

5.  **Build and Run the Project:**
    * **Via Maven Wrapper (recommended):**
        ```bash
        ./mvnw spring-boot:run
        ```
    * **Or by compiling and running the JAR:**
        ```bash
        ./mvnw clean package
        java -jar target/comissio-0.0.1-SNAPSHOT.jar
        ```

The application will be available at `http://localhost:8080` (or the configured port).

---

## 📄 API Documentation (Swagger)

After starting the application, the API documentation generated by Swagger UI will be accessible at:
`http://localhost:8080/swagger-ui.html`

The OpenAPI specification (JSON) can be found at:
`http://localhost:8080/v3/api-docs`

---

## 🗄️ Database Migrations (Flyway)

Database schema versioning and evolution are managed by Flyway. The SQL migration scripts are located in:
`src/main/resources/db/migration`

Flyway will automatically apply pending migrations when the application starts.

---

## 🔐 Security

* API security is implemented with Spring Security.
* Authentication is based on JWT (JSON Web Tokens).
* Protected endpoints require a valid JWT in the `Authorization` header (Ex: `Authorization: Bearer <your_jwt_token>`).

---

## 🏗️ Project Structure (Overview)

The project structure follows a modular organization to facilitate maintenance and development:

* `src/main/java/com/matheusmaciel/comissio`
    * `core/domain`: Contains the core domain logic.
        * `dto`: Data Transfer Objects.
        * `model`: JPA entities representing the data model.
            * `access`: Entities related to access and users.
            * `register`: Main business entities (commissions, services, etc.).
        * `repository`: Spring Data JPA interfaces for data access.
        * `service`: Service classes containing business logic.
    * `infra`: Infrastructure components.
        * `config`: Application configurations (Security, Swagger, etc.).
            * `security`: Specific configurations for Spring Security and JWT.
        * `controller`: REST controllers that expose the API.
        * `exception`: Custom exception classes and global handlers.
* `src/main/resources`
    * `application.properties`: Main configuration file.
    * `db/migration`: SQL scripts for Flyway.

---

## 🤝 Contribution


1.  Fork the project.
2.  Create a Branch for your feature (`git checkout -b feature/MyFeature`).
3.  Commit your changes (`git commit -m 'Add some MyFeature'`).
4.  Push to the Branch (`git push origin feature/MyFeature`).
5.  Open a Pull Request.]

---

## 📜 License

MIT License