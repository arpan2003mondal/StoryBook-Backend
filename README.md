# 📚 StoryBook Backend API

A comprehensive Spring Boot backend application for an audio storybook platform with shopping cart, wallet management, and user library features.

## 🌟 Features

### User Management
- ✅ User registration and authentication
- ✅ JWT-based token authentication
- ✅ Role-based access control (USER, ADMIN)
- ✅ User logout with token blacklisting
- ✅ Secure password hashing with BCrypt

### Storybook Catalog
- ✅ Browse all available storybooks
- ✅ Advanced search by title, author, or category
- ✅ Detailed storybook information with author and category details

### Shopping Cart
- ✅ Add storybooks to cart
- ✅ View cart contents
- ✅ Remove items from cart
- ✅ Prevent duplicate items in cart
- ✅ Prevent re-purchasing already owned books

### Wallet & Checkout
- ✅ Check wallet balance
- ✅ Process checkout and create orders
- ✅ Automatic wallet deduction for purchases
- ✅ Add purchased books to user library

### User Library
- ✅ View purchased storybooks
- ✅ Track purchase history
- ✅ Prevent re-adding purchased books to cart

### Admin Panel
- ✅ Admin login with role verification
- ✅ Add new storybooks to catalog
- ✅ Manage author and category information
- ✅ Admin logout

### Security
- ✅ JWT Token authentication
- ✅ Token blacklisting on logout
- ✅ CORS enabled
- ✅ Request validation

## 🛠️ Technology Stack

| Layer | Technology |
|-------|-----------|
| **Backend Framework** | Spring Boot 4.0.3 |
| **Language** | Java 21 |
| **Database** | MySQL 8.0+ |
| **Authentication** | JWT (JSON Web Tokens) |
| **ORM** | Spring Data JPA / Hibernate |
| **API** | RESTful API |
| **Build Tool** | Maven |
| **Validation** | Jakarta Bean Validation |
| **Security** | Spring Security, BCrypt |

## 📁 Project Structure

```
storybook-backend/
├── src/
│   ├── main/
│   │   ├── java/com/company/storybook/
│   │   │   ├── controller/           # REST Controllers
│   │   │   │   ├── UserAuthController.java
│   │   │   │   ├── AdminController.java
│   │   │   │   ├── StoryBookUserController.java
│   │   │   │   └── WalletController.java
│   │   │   ├── service/              # Business Logic
│   │   │   │   ├── UserAuthService.java
│   │   │   │   ├── AdminService.java
│   │   │   │   ├── CartService.java
│   │   │   │   ├── OrderService.java
│   │   │   │   ├── WalletService.java
│   │   │   │   └── TokenBlacklistService.java
│   │   │   ├── repository/           # Data Access Layer
│   │   │   ├── entity/               # JPA Entities
│   │   │   ├── dto/                  # Data Transfer Objects
│   │   │   ├── exception/            # Custom Exceptions
│   │   │   ├── config/               # Configuration Classes
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── JwtAuthFilter.java
│   │   │   └── utility/              # Utility Classes
│   │   │       └── JwtUtil.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── messages.properties
│   │       └── TableScripts.sql
│   └── test/
└── pom.xml
```

## 📋 Entity Diagram

```
User
├── Cart (1:1)
│   └── CartItem (1:N)
│       └── Storybook
├── Wallet (1:1)
├── Order (1:N)
│   └── OrderItem (1:N)
│       └── Storybook
└── UserLibrary (1:N)
    └── Storybook

Storybook
├── Author (N:1)
├── Category (N:1)
├── CartItem (1:N)
└── UserLibrary (1:N)
```

## 🚀 Getting Started

### Prerequisites
- Java 21 or higher
- MySQL 8.0 or higher
- Maven 3.6+
- Git
- Postman (for API testing)

### Installation

#### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/storybook-backend.git
cd storybook-backend
```

#### 2. Configure Database
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/storybookdb
spring.datasource.username=root
spring.datasource.password=your_password
```

#### 3. Create Database and Tables
```bash
mysql -u root -p < src/main/resources/TableScripts.sql
```

#### 4. Build the Project
```bash
./mvnw clean install
```

#### 5. Run the Application
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:1234`

## 📚 API Documentation

### Base URL
```
http://localhost:1234
```

### Authentication Endpoints

#### Register User
```http
POST /users/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "Password@123"
}
```

#### User Login
```http
POST /users/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "Password@123"
}
Response: JWT Token
```

#### User Logout
```http
POST /users/logout
Authorization: Bearer <token>
```

#### Admin Login
```http
POST /admin/login
Content-Type: application/json

{
  "email": "admin@storybook.com",
  "password": "Admin@123"
}
```

#### Admin Logout
```http
POST /admin/logout
Authorization: Bearer <adminToken>
```

### Storybook Endpoints

#### Get All Storybooks
```http
GET /api/storybooks
Authorization: Bearer <userToken>
```

#### Search Storybooks
```http
GET /api/storybooks/search?keyword=Harry
Authorization: Bearer <userToken>
```

#### Get Storybook by ID
```http
GET /api/storybooks/{id}
Authorization: Bearer <userToken>
```

#### Add Storybook (Admin Only)
```http
POST /admin/storybooks
Authorization: Bearer <adminToken>
Content-Type: application/json

{
  "title": "The Hobbit",
  "description": "A fantasy adventure",
  "authorId": 4,
  "categoryId": 1,
  "price": 8.99,
  "audioUrl": "https://example.com/audio",
  "coverImageUrl": "https://example.com/image"
}
```

### Cart Endpoints

#### Add Item to Cart
```http
POST /api/storybooks/cart/add
Authorization: Bearer <userToken>
Content-Type: application/json

{
  "storybookId": 1
}
```

#### Get User's Cart
```http
GET /api/storybooks/cart
Authorization: Bearer <userToken>
```

#### Remove Item from Cart
```http
DELETE /api/storybooks/cart/items/{cartItemId}
Authorization: Bearer <userToken>
```

### Wallet & Order Endpoints

#### Get Wallet Balance
```http
GET /api/wallet/balance
Authorization: Bearer <userToken>
```

#### Checkout
```http
POST /api/wallet/checkout
Authorization: Bearer <userToken>
```

#### Get User Library
```http
GET /api/wallet/library
Authorization: Bearer <userToken>
```

## 📊 Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('USER','ADMIN') DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Cart Table
```sql
CREATE TABLE cart (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### CartItems Table
```sql
CREATE TABLE cart_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cart_id BIGINT NOT NULL,
    storybook_id BIGINT NOT NULL,
    quantity INT DEFAULT 1,
    FOREIGN KEY (cart_id) REFERENCES cart(id),
    FOREIGN KEY (storybook_id) REFERENCES storybooks(id)
);
```

### Orders Table
```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2),
    status ENUM('CREATED','PENDING','PAID','FAILED'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Storybooks Table
```sql
CREATE TABLE storybooks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    author_id BIGINT,
    category_id BIGINT,
    price DECIMAL(10,2),
    audio_url VARCHAR(500),
    cover_image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES authors(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
```

## 🧪 Testing with Postman

A complete Postman collection with all endpoints and test data is included:

### Import Collection
1. Open Postman
2. Click **File** → **Import**
3. Select `Postman_Collection.json`
4. All endpoints will be available with automatic token management

### Test Data Available
- **Users**: john@storybook.com, jane@storybook.com, admin@storybook.com
- **Authors**: J.K. Rowling, George R.R. Martin, Agatha Christie, J.R.R. Tolkien
- **Categories**: Fantasy, Mystery, Adventure, Thriller
- **Storybooks**: 5+ pre-loaded books

See [POSTMAN_TESTING_GUIDE.md](POSTMAN_TESTING_GUIDE.md) for detailed testing instructions.

## ⚙️ Configuration

### Application Properties
```properties
# Server
server.port=1234

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/storybookdb
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.generate-ddl=true

# Messages
spring.messages.basename=messages
spring.messages.encoding=UTF-8
```

### Messages Configuration
All user-facing messages are externalized in `src/main/resources/messages.properties`:
- Success messages
- Error messages
- Validation messages
- Business logic messages

## 🔐 Security Features

### JWT Authentication
- Token generation on login
- Token validation on each request
- Token blacklisting on logout
- 24-hour token expiration (configurable)

### Password Security
- BCrypt hashing
- Minimum 6 characters required
- Special character validation

### Authorization
- Role-based access control (RBAC)
- Admin-only endpoints protected
- User-specific resource access validation

### Input Validation
- Email format validation
- Required field validation
- Price validation (must be > 0)
- Request body validation

## 🐛 Error Handling

All errors return consistent responses:
```json
{
  "timestamp": "2026-03-14T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid input provided"
}
```

Common error codes:
- `400` - Bad Request (validation error)
- `401` - Unauthorized (missing/invalid token)
- `403` - Forbidden (insufficient permissions)
- `404` - Not Found (resource not found)
- `409` - Conflict (duplicate item, already purchased)
- `500` - Internal Server Error

## 📝 API Response Format

### Success Response
```json
{
  "message": "Operation successful",
  "data": { ... }
}
```

### Error Response
```json
{
  "timestamp": "2026-03-14T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error description"
}
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📋 Development Guidelines

### Code Style
- Follow Spring Boot conventions
- Use meaningful variable names
- Add JavaDoc comments for public methods
- Keep methods focused and concise

### Testing
- Write unit tests for services
- Write integration tests for controllers
- Aim for 80%+ code coverage

### Documentation
- Update README for new features
- Document API changes
- Add inline comments for complex logic

## 🔄 Workflow

### Add Storybook Workflow
1. Admin authenticates
2. Provides storybook details (title, price, author, category)
3. System validates all fields
4. Storybook is added to database
5. Available for users to browse and purchase

### Purchase Workflow
1. User browses storybooks
2. Adds items to cart
3. Views cart contents
4. Initiates checkout
5. Wallet balance is deducted
6. Books added to user library
7. Order is created

## 🚦 Current Status

- ✅ User Authentication & Authorization
- ✅ Storybook Management
- ✅ Shopping Cart Functionality
- ✅ Wallet Management
- ✅ Order Management
- ✅ User Library
- ✅ Admin Panel
- ⏳ Payment Gateway Integration
- ⏳ Reviews & Ratings
- ⏳ Wishlist Feature
- ⏳ Email Notifications

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- **Development Team** - StoryBook Backend Implementation

## 📧 Contact & Support

For issues, questions, or suggestions, please:
- Open an issue on GitHub
- Email: support@storybook.com
- Check existing documentation in [docs/](docs/) folder

## 📚 Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT Introduction](https://jwt.io/introduction)
- [MySQL Documentation](https://dev.mysql.com/doc/)

## 🎯 Roadmap

### Version 1.1
- [ ] Payment gateway integration
- [ ] Email notifications
- [ ] User profile customization

### Version 1.2
- [ ] Reviews and ratings system
- [ ] Wishlist feature
- [ ] Recommendation engine

### Version 2.0
- [ ] Mobile app support
- [ ] Advanced analytics
- [ ] Multi-currency support

---

**Last Updated**: March 14, 2026
