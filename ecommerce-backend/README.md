# E-Commerce Backend API

**Created and designed by:** Swastideepa Dash

## Project Overview
This is a robust, production-ready E-commerce REST API built with Spring Boot. It features secure user authentication, product management, a functional shopping cart, simulated order checkout, and automated email notifications.

## Technologies Used
* **Java 17** & **Spring Boot 3**
* **Spring Security** & **JWT (JSON Web Tokens)** for authentication
* **Spring Data JPA** & **Hibernate** for database interactions
* **MySQL** (Relational Database)
* **ModelMapper** (DTO conversion) & **Spring Mail** (Email receipts)
* **JUnit 5** & **Mockito** (Unit Testing)
* **Swagger UI / OpenAPI** (API Documentation)
* **Docker & Docker Compose** (Containerization)


### Steps to Run

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/Swastii06/ecommerce-backend-api.git](https://github.com/Swastii06/ecommerce-backend-api.git)
   cd ecommerce-backend-api
   ```

2. **Start the MySQL Database using Docker:**
   ```bash
   docker-compose up -d mysqldb
   ```

3. **Build and Run the Application:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Access the API:**
   The server will start on `http://localhost:8080`.


## Entity Relationship (ER) Diagram
*The database architecture representing how entities are related.*

```mermaid
erDiagram
    USER ||--o{ ORDER : places
    USER ||--|| CART : owns
    CART ||--o{ CART_ITEM : contains
    ORDER ||--o{ ORDER_ITEM : includes
    PRODUCT ||--o{ CART_ITEM : "added_to"
    PRODUCT ||--o{ ORDER_ITEM : "purchased_as"

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
        String description
        BigDecimal price
        Integer stock
        String category
        String imageUrl
        Double rating
    }

    CART {
        Long id PK
        Long user_id FK
        BigDecimal totalPrice
    }

    CART_ITEM {
        Long id PK
        Long cart_id FK
        Long product_id FK
        Integer quantity
    }

    ORDER {
        Long id PK
        Long user_id FK
        BigDecimal totalAmount
        LocalDateTime orderDate
        String paymentStatus
        String orderStatus
    }

    ORDER_ITEM {
        Long id PK
        Long order_id FK
        Long product_id FK
        Integer quantity
        BigDecimal price
    }

 ```

## API Endpoints & Methods

| Method | Endpoint | Description | Security |
|--------|----------|-------------|----------|
| **Users** | | | |
| `POST` | `/api/users/register` | Register a new user account | Public |
| `POST` | `/api/users/login` | Authenticate and generate JWT token | Public |
| `PUT` | `/api/users/profile` | Update logged-in user profile | Secured (JWT) |
| `PUT` | `/api/users/change-password` | Change password for logged-in user | Secured (JWT) |
| `GET` | `/api/users/{id}` | Retrieve a user by ID | Secured (Admin) |
| `PUT` | `/api/users/{id}` | Update a user by ID | Secured (Admin) |
| `DELETE`| `/api/users/{id}` | Delete a user by ID | Secured (Admin) |
| **Products** | | | |
| `POST` | `/api/products` | Add a new product to the catalog | Secured (Admin) |
| `GET`  | `/api/products` | Retrieve all products (Supports Pagination & Filters) | Public |
| `PUT` | `/api/products/{id}` | Update an existing product | Secured (Admin) |
| `DELETE`| `/api/products/{id}` | Delete a product from the catalog | Secured (Admin) |
| **Cart** | | | |
| `GET` | `/api/cart` | View the current user's active cart | Secured (JWT) |
| `POST` | `/api/cart/add/{productId}?quantity={qty}` | Add product to active shopping cart | Secured (JWT) |
| `PUT` | `/api/cart/update/{productId}?quantity={qty}`| Update item quantity in cart | Secured (JWT) |
| `DELETE`| `/api/cart/remove/{productId}` | Remove an item entirely from the cart | Secured (JWT) |
| **Orders** | | | |
| `POST` | `/api/orders/checkout` | Process payment and place order | Secured (JWT) |

## API Screenshots

**Screenshot 1: User Registration**
![User Registration](https://github.com/user-attachments/assets/61aae984-ef85-4a39-a178-47f7a77031ee)

**Screenshot 2: JWT Token Generation (Login)**
![Login](https://github.com/user-attachments/assets/0754b4f0-1aea-4e18-8e2d-bb65e214d059)

**Screenshot 3: Adding a Product**
![Add Product](https://github.com/user-attachments/assets/1cc8d55a-1214-465d-88a8-2d517862a765)

**Screenshot 4: Adding to Cart (200 OK)**
![Add to Cart](https://github.com/user-attachments/assets/888eb6ac-9366-4d8b-9835-f9194e5350f1)

**Screenshot 5: Order Checkout (200 OK)**
![Checkout](https://github.com/user-attachments/assets/898cef3e-015f-4f25-a277-9a93ad788506)

**Screenshot 6: Get User by ID (Admin)**
![Get User by ID](https://github.com/user-attachments/assets/46e44932-a291-46fb-a39e-62a4b92fa0f9)

**Screenshot 7: Get All Products**
![Get All Products](https://github.com/user-attachments/assets/2d8f5e1b-d033-46fe-8f8b-86f7571d2718)

**Screenshot 8: Get Product by ID**
![Get Product by ID](https://github.com/user-attachments/assets/58fa0f47-3099-49df-af63-52d0649887f4)

**Screenshot 9: Update Product (Admin)**
![Update Product](https://github.com/user-attachments/assets/3fc801c7-987d-4c4c-ab26-d26093b939be)

**Screenshot 10: Delete Product (Admin)**
![Delete Product](https://github.com/user-attachments/assets/417ce130-2233-46f6-a7d4-f7eec2cd5c0d)

**Screenshot 11: View Cart (Customer)**
![View Cart](https://github.com/user-attachments/assets/1c07a0f6-889d-4d30-9732-0f837739f6d3)

**Screenshot 12: Update Cart Quantity (Customer)**
![Update Cart Quantity](https://github.com/user-attachments/assets/b672bec7-03d9-4fc0-8479-a88988ab2f1e)

**Screenshot 13: Remove Item From Cart (Customer)**
![Remove Item From Cart](https://github.com/user-attachments/assets/91690d5e-e718-48f7-9e54-415dfdc8f327)

**Screenshot 14: Update User (Admin)**
![Update User](https://github.com/user-attachments/assets/6390bfd9-1b88-4f3d-ae0c-0f4032f02979)

**Screenshot 15: Delete User (Admin)**
![Delete User](https://github.com/user-attachments/assets/628b984b-467c-4843-a0bb-d464f31f6b7e)

**Screenshot 16: View Order History (Customer)**
![View Order History](https://github.com/user-attachments/assets/e26c5944-d51f-4ecc-a75f-c7dc0fbaf80c)

**Screenshot 17: View Order by ID (Customer)**
![View Order by ID](https://github.com/user-attachments/assets/e7d6230b-ac27-491f-a1db-b50fc1d3c77d)

**Screenshot 18: Update Order Status (Admin)**
![Update Order Status](https://github.com/user-attachments/assets/efd3814b-6f98-4778-a8ea-be27a555e360)
