# Quick Start Guide - Storybook Search & Cart

## 5-Minute Setup

### 1. Prerequisites
- Java 21+
- MySQL 8.0+
- Maven (via mvnw)
- Git

### 2. Clone & Setup
```bash
cd e:\SpringBoot\storybook-backend
```

### 3. Database
```bash
# Create database
mysql -u root -p << EOF
create database storybookdb;
use storybookdb;
source src/main/resources/TableScripts.sql;
EOF
```

### 4. Add Sample Data
```sql
-- Add Authors
INSERT INTO authors (name, bio) VALUES 
('John Doe', 'Bestselling author'),
('Jane Smith', 'Mystery writer');

-- Add Categories
INSERT INTO categories (name, description) VALUES 
('Fantasy', 'Fantasy adventures'),
('Mystery', 'Mystery thrillers');

-- Add Storybooks
INSERT INTO storybooks (title, description, author_id, category_id, price, audio_url, cover_image_url) VALUES
('The Great Adventure', 'An epic tale', 1, 1, 9.99, 'https://example.com/audio1', 'https://example.com/image1'),
('Mystery in Mountains', 'Thrilling mystery', 2, 2, 7.99, 'https://example.com/audio2', 'https://example.com/image2'),
('Fantasy Quest', 'Epic fantasy', 1, 1, 11.99, 'https://example.com/audio3', 'https://example.com/image3');
```

### 5. Run Application
```bash
.\mvnw spring-boot:run
```

Application starts at `http://localhost:1234`

---

## Quick Test

### Register & Login
```bash
# Register
curl -X POST http://localhost:1234/users/register \
  -H "Content-Type: application/json" \
  -d '{"name":"John","email":"john@example.com","password":"password123"}'

# Login (save the token)
curl -X POST http://localhost:1234/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password123"}'
```

### Browse & Search
```bash
# Get all
curl http://localhost:1234/api/storybooks

# Search
curl "http://localhost:1234/api/storybooks/search?keyword=fantasy"

# Details
curl http://localhost:1234/api/storybooks/1
```

### Cart Operations
```bash
TOKEN="your_token_here"

# Add to cart
curl -X POST http://localhost:1234/api/storybooks/cart/add \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"storybookId":1}'

# View cart
curl -X GET http://localhost:1234/api/storybooks/cart \
  -H "Authorization: Bearer $TOKEN"

# Remove from cart (replace 1 with cartItemId)
curl -X DELETE http://localhost:1234/api/storybooks/cart/items/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## Project Structure

```
src/main/java/com/company/storybook/
├── controller/
│   ├── UserAuthController       (Existing)
│   ├── AdminController          (Existing)
│   └── StoryBookUserController  (NEW - Search & Cart)
├── service/
│   ├── AuthService              (Existing)
│   ├── AdminService             (Existing)
│   ├── CartService              (NEW - Interface)
│   └── CartServiceImpl           (NEW - Implementation)
├── repository/
│   ├── UserRepository           (Existing)
│   ├── StorybookRepository      (Enhanced - Added search methods)
│   ├── CartRepository           (NEW)
│   └── CartItemRepository       (NEW)
├── entity/
│   ├── User, Role              (Existing)
│   ├── Storybook               (Existing)
│   ├── Author, Category        (Existing)
│   ├── Cart                    (NEW)
│   └── CartItem                (NEW)
├── dto/
│   ├── LoginRequest, RegisterRequest (Existing)
│   ├── StorybookRequest, StorybookResponse (Existing)
│   ├── CartItemDTO             (NEW)
│   ├── CartResponseDTO         (NEW)
│   └── AddToCartRequest        (NEW)
├── exception/
│   ├── GlobalExceptionHandler  (Existing)
│   └── StoryBookException      (Existing)
├── config/
│   ├── JwtAuthFilter           (Existing)
│   └── SecurityConfig          (Enhanced - Added new endpoints)
└── utility/
    ├── JwtUtil                 (Existing)
    └── ErrorInfo               (Existing)

resources/
├── application.properties      (Enhanced - Added MessageSource config)
└── messages.properties         (Enhanced - Added cart messages)
```

---

## Key Endpoints

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| GET | `/api/storybooks` | No | Get all storybooks |
| GET | `/api/storybooks/search?keyword=...` | No | Search storybooks |
| GET | `/api/storybooks/{id}` | No | Get details by ID |
| POST | `/api/storybooks/cart/add` | Yes | Add to cart |
| GET | `/api/storybooks/cart` | Yes | View cart |
| DELETE | `/api/storybooks/cart/items/{id}` | Yes | Remove from cart |

---

## Features

✅ **Search Functionality**
- Search by title, author, category, description
- Case-insensitive
- Returns any matching storybooks

✅ **Cart Management**
- One cart per user
- Single quantity per item
- Prevents duplicate items with proper message
- Shows total items and total price
- Add/remove operations

✅ **Proper Messages**
- All messages externalized to properties file
- No hardcoded strings
- Localized via MessageSource
- Custom error responses

✅ **Security**
- Public browse & search endpoints
- JWT-protected cart operations
- User ID extracted from token
- Role-based access control

✅ **Best Practices**
- Clean layered architecture
- RESTful API design
- Proper exception handling
- Data transfer objects (DTOs)
- Service layer abstraction

---

## Troubleshooting

### Build Issues
```bash
# Clean rebuild
.\mvnw clean install

# Skip tests
.\mvnw clean install -DskipTests
```

### Database Connection
- Check MySQL is running: `mysql -u root -p -e "SELECT 1"`
- Verify credentials in `application.properties`
- Ensure database `storybookdb` exists

### Port Conflict
- Change in `application.properties`: `server.port = 8080`

### Auth Errors
- Token format: `Authorization: Bearer {token}`
- Token expires after 10 hours
- Re-login if token invalid

---

## Common Commands

```bash
# Start app
.\mvnw spring-boot:run

# Build
.\mvnw clean package

# Run tests
.\mvnw test

# Run specific test
.\mvnw test -Dtest=CartServiceTest

# Check dependencies
.\mvnw dependency:tree

# Run in background
.\mvnw spring-boot:run &
```

---

## Documentation Files

- **API_DOCUMENTATION.md** - Complete API reference with examples
- **TESTING_GUIDE.md** - Detailed testing scenarios and curl examples
- **IMPLEMENTATION_SUMMARY.md** - Architecture, design, and implementation details
- **This file** - Quick start guide

---

## Support

### Debug Logging
Add to `application.properties`:
```properties
logging.level.com.company.storybook=DEBUG
logging.level.org.springframework.security=DEBUG
```

### Check Cart Status
```sql
-- View all carts
SELECT u.email, c.id, COUNT(ci.id) as items FROM users u 
LEFT JOIN cart c ON u.id = c.user_id 
LEFT JOIN cart_items ci ON c.id = ci.cart_id 
GROUP BY u.id;
```

---

## Next Steps

1. ✅ Follow Quick Start above
2. ✅ Test all endpoints from Quick Test section
3. 📖 Read API_DOCUMENTATION.md for detailed API info
4. 📋 Review TESTING_GUIDE.md for comprehensive testing
5. 🏗️ Study IMPLEMENTATION_SUMMARY.md for architecture understanding
6. 💻 Extend with checkout, payments, recommendations

---

## Version Info

- **Spring Boot:** 3.x
- **Java:** 21+
- **MySQL:** 8.0+
- **JWT:** JJWT (JSON Web Token)
- **ORM:** Jakarta Persistence (JPA/Hibernate)

---

Happy coding! 🚀
