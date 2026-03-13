# E-Commerce Backend API

**Created and designed by:** Swastideepa Dash

## 📖 Project Overview
This is a robust, production-ready E-commerce REST API built with Spring Boot. It features secure user authentication, product management, a functional shopping cart, simulated order checkout, and automated email notifications.

## 💻 Technologies Used
* **Java 17** & **Spring Boot 3**
* **Spring Security** & **JWT (JSON Web Tokens)** for authentication
* **Spring Data JPA** & **Hibernate** for database interactions
* **MySQL** (Relational Database)
* **ModelMapper** (DTO conversion) & **Spring Mail** (Email receipts)
* **JUnit 5** & **Mockito** (Unit Testing)
* **Swagger UI / OpenAPI** (API Documentation)
* **Docker & Docker Compose** (Containerization)



## 📊 Entity Relationship (ER) Diagram
*The database architecture representing how entities are related.*

```mermaid
erDiagram
    USER ||--o{ ORDER : places
    USER ||--|| CART : owns
    CART ||--o{ CART_ITEM : contains
    ORDER ||--o{ ORDER_ITEM : includes
    PRODUCT ||--o{ CART_ITEM : "added as"
    PRODUCT ||--o{ ORDER_ITEM : "bought as"

    USER {
        Long id PK
        String name
        String email
        String password
        String role
    }
    PRODUCT {
        Long id PK
        String name
        BigDecimal price
        int stock
        String category
    }
    CART {
        Long id PK
        BigDecimal totalPrice
        Long user_id FK
    }
    ORDER {
        Long id PK
        BigDecimal totalAmount
        String paymentStatus
        String orderStatus
        Long user_id FK
    }