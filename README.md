# Apex Quiz — Enterprise Role-Based Assessment Platform

An enterprise-grade, role-based quiz and assessment platform built with **Spring Boot 3**, **Spring Security 6**, **Spring Data JPA & JDBC**, and **Thymeleaf** with a modern **Light Glassmorphic UI**.

---

## 1. Problem Understanding & Requirements Analysis

### Problem Statement
Traditional online examination and evaluation tools either lack granular role segregation or force all users into rigid, monolithic workflows. Academic and enterprise assessment workflows demand a three-way separation of concerns:
1. **Administrators**: Control identity lifecycle, approve or reject assessment content, and monitor institutional throughput.
2. **Creators**: Author assessments, define strict question banks, options, timers, and submit assessments for moderation.
3. **Participants**: Experience an isolated, distraction-free examination hall with automated timers and instant, auditable score transcripts.

### Key Requirements Matrix
| Requirement ID | Module | Description | Target Actor |
| :--- | :--- | :--- | :--- |
| **REQ-01** | Authentication & RBAC | Secure BCrypt password hashing, session cookies, and URL-level role guards | All |
| **REQ-02** | Catalog Moderation | Multi-state assessment lifecycle (`PENDING` -> `APPROVED` / `REJECTED`) | Admin |
| **REQ-03** | User Directory | Administrative roster control and account deletion | Admin |
| **REQ-04** | Authoring Studio | Quiz authoring with customizable timer and multi-choice question builder | Creator |
| **REQ-05** | Timed Exam Room | Browser countdown timer with automatic JavaScript auto-submission | Participant |
| **REQ-06** | Score & Analytics | Instant grading engine with tabular performance history | Participant |

---

## 2. System Architecture & Design Diagrams

### High-Level Architecture (MVC + Layered Architecture)

```
       +-----------------------------------------------------------+
       |               Client (Browser / Thymeleaf UI)             |
       |  - Glassmorphic UI  - Responsive Tables  - Auto Timer JS  |
       +-----------------------------+-----------------------------+
                                     | HTTP / HTTPS
                                     v
       +-----------------------------------------------------------+
       |                  Spring Security Filter Chain             |
       |  - AuthenticationProvider  - UserDetailsService (BCrypt)   |
       +-----------------------------+-----------------------------+
                                     |
                                     v
       +-----------------------------------------------------------+
       |             Controller Layer (Spring MVC)                 |
       |  HomeController | AuthController | AdminController        |
       |  CreatorController | ParticipantController                |
       +--------------+-----------------------------+--------------+
                      |                             |
                      v                             v
       +------------------------------+  +-------------------------+
       |  Service Layer (Business)    |  | Async Worker ThreadPool |
       |  - UserService (@Transactional) |  | - QuizAnalyticsService   |
       |  - QuizService (@Transactional) |  |   (CompletableFuture)   |
       +--------------+---------------+  +-------------------------+
                      |
                      v
       +-----------------------------------------------------------+
       |                 Data Access Layer (Persistence)           |
       |  - Spring Data JPA (Hibernate ORM)                        |
       |  - Spring JDBC (JdbcTemplate for native aggregations)     |
       +-----------------------------+-----------------------------+
                                     |
                                     v
       +-----------------------------------------------------------+
       |             Database Layer (MySQL / H2)                   |
       |  Tables: users, quizzes, questions, results               |
       +-----------------------------------------------------------+
```

### Entity-Relationship (ER) Diagram

```
 +--------------------+       1       * +--------------------+
 |       USERS        |----------------<|      QUIZZES       |
 +--------------------+                 +--------------------+
 | PK  id             |                 | PK  id             |
 |     name           |                 |     title          |
 |     email (UQ)     |                 |     duration       |
 |     password       |                 |     status         |
 |     role           |                 | FK  creator_id     |
 +--------------------+                 +--------------------+
       |          ^                                | 1
       | 1        | 1                              |
       |          |                                |
       |          | *                              v *
       |      +--------------------+    +--------------------+
       |      |      RESULTS       |    |     QUESTIONS      |
       |      +--------------------+    +--------------------+
       |      | PK  id             |    | PK  id             |
       +----->| FK  participant_id |    |     question_text  |
              | FK  quiz_id        |    |     option_a       |
              |     score          |    |     option_b       |
              |     total_questions|    |     option_c       |
              |     taken_at       |    |     option_d       |
              +--------------------+    |     correct_option |
                                        | FK  quiz_id        |
                                        +--------------------+
```

---

## 3. Core Java Concepts & Rubric Alignment

- **Object-Oriented Programming (OOP)**:
  - Strict encapsulation with Lombok getters/setters and immutable builders.
  - Polymorphic role handling through enum `Role` and Spring Security granted authorities.
- **Java Collections Framework**:
  - `List<Question>`, `List<Quiz>`, and Java 8 Streams (`filter`, `map`, `count`) used for grading and audit filtering.
- **Exception Handling**:
  - Centralized `@ControllerAdvice` (`GlobalExceptionHandler`) handling `ResourceNotFoundException`, `IllegalArgumentException`, and generic fallbacks.
- **Multithreading & Concurrency**:
  - Dedicated `ThreadPoolTaskExecutor` running `@Async` background analytics tasks (`QuizAnalyticsService`) using `CompletableFuture`.
- **Database Integration (JDBC & JPA)**:
  - Spring Data JPA repositories with custom derived queries (`findByStatus`, `findByCreator`).
  - Native `JdbcTemplate` DAO for high-performance aggregate reporting and transaction handling via `@Transactional`.
- **Servlets & Session Management**:
  - `HttpSession` tracking of active exam attempts to ensure single-session validity.

---

## 4. Default Seed Credentials

Upon startup, `DataInitializer` seeds the following accounts:
- **Admin**: `admin@quizplatform.local` / `Admin@123`
- **Creator**: `creator@quizplatform.local` / `Creator@123`
- **Participant**: `participant@quizplatform.local` / `Participant@123`

---

## 5. Running the Application Locally

```bash
cd demo
# Build and run with Maven Wrapper
./mvnw clean spring-boot:run
```
Access the application at `http://localhost:8080`.
