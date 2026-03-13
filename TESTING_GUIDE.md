# Storybook API - Testing Guide & Examples

## Setup Instructions

### 1. Start the Application
```powershell
cd e:\SpringBoot\storybook-backend
.\mvnw spring-boot:run
```

Application will start on `http://localhost:1234`

### 2. Database Setup
Ensure MySQL is running and database is created:
```sql
create database storybookdb;
use storybookdb;
-- Run TableScripts.sql for all tables
```

### 3. Seed Test Data
Before testing, you need to add some storybooks via Admin API or directly in database.

---

## Test Scenarios

### Scenario 1: User Registration and Login

#### Step 1: Register a New User
```bash
curl -X POST http://localhost:1234/users/register \
  -H "Content-Type: application/json" \
  -d {
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123"
  }
```

**Expected Response (201 CREATED):**
```
User Registration Successfull
```

#### Step 2: Login with Credentials
```bash
curl -X POST http://localhost:1234/users/login \
  -H "Content-Type: application/json" \
  -d {
    "email": "john@example.com",
    "password": "password123"
  }
```

**Expected Response (200 OK):**
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE2MjM1MjU1MDB9...
```

**Save this token for subsequent requests!**

---

### Scenario 2: Browse & Search Storybooks (No Auth Required)

#### Get All Storybooks
```bash
curl -X GET http://localhost:1234/api/storybooks \
  -H "Content-Type: application/json"
```

#### Get Specific Storybook Details
```bash
curl -X GET http://localhost:1234/api/storybooks/1 \
  -H "Content-Type: application/json"
```

#### Search Storybooks by Keyword
```bash
# Search by title or description
curl -X GET "http://localhost:1234/api/storybooks/search?keyword=adventure" \
  -H "Content-Type: application/json"

# Search by author name
curl -X GET "http://localhost:1234/api/storybooks/search?keyword=John%20Doe" \
  -H "Content-Type: application/json"

# Search by category
curl -X GET "http://localhost:1234/api/storybooks/search?keyword=Fantasy" \
  -H "Content-Type: application/json"
```

---

### Scenario 3: Cart Operations (Auth Required)

#### Step 1: Add First Item to Cart
```bash
TOKEN="your_jwt_token_here"

curl -X POST http://localhost:1234/api/storybooks/cart/add \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d {
    "storybookId": 1
  }
```

**Expected Response (201 CREATED):**
```json
{
  "message": "Item successfully added to cart.",
  "cart": {
    "cartId": 1,
    "cartItems": [
      {
        "id": 1,
        "storybookId": 1,
        "title": "The Great Adventure",
        "description": "An epic tale",
        "authorName": "John Doe",
        "categoryName": "Fantasy",
        "price": 9.99,
        "coverImageUrl": "https://...",
        "quantity": 1
      }
    ],
    "totalItems": 1,
    "totalPrice": 9.99
  }
}
```

#### Step 2: Add Another Item to Cart
```bash
curl -X POST http://localhost:1234/api/storybooks/cart/add \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d {
    "storybookId": 2
  }
```

**Expected Response (201 CREATED):**
```json
{
  "message": "Item successfully added to cart.",
  "cart": {
    "cartId": 1,
    "cartItems": [
      {...},
      {
        "id": 2,
        "storybookId": 2,
        "title": "Mystery in Mountains",
        "description": "A thrilling mystery",
        "authorName": "Jane Smith",
        "categoryName": "Mystery",
        "price": 7.99,
        "coverImageUrl": "https://...",
        "quantity": 1
      }
    ],
    "totalItems": 2,
    "totalPrice": 17.98
  }
}
```

#### Step 3: Try Adding Duplicate Item (Should Fail)
```bash
curl -X POST http://localhost:1234/api/storybooks/cart/add \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d {
    "storybookId": 1
  }
```

**Expected Response (400 BAD REQUEST):**
```json
{
  "statusCode": 400,
  "message": "This storybook is already in your cart.",
  "timestamp": "2026-03-13T23:05:00"
}
```

#### Step 4: View Cart
```bash
curl -X GET http://localhost:1234/api/storybooks/cart \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

**Expected Response (200 OK):**
```json
{
  "cartId": 1,
  "cartItems": [
    {
      "id": 1,
      "storybookId": 1,
      "title": "The Great Adventure",
      "description": "An epic tale",
      "authorName": "John Doe",
      "categoryName": "Fantasy",
      "price": 9.99,
      "coverImageUrl": "https://...",
      "quantity": 1
    },
    {
      "id": 2,
      "storybookId": 2,
      "title": "Mystery in Mountains",
      "description": "A thrilling mystery",
      "authorName": "Jane Smith",
      "categoryName": "Mystery",
      "price": 7.99,
      "coverImageUrl": "https://...",
      "quantity": 1
    }
  ],
  "totalItems": 2,
  "totalPrice": 17.98
}
```

#### Step 5: Remove Item from Cart
```bash
# Remove the first item (cartItemId = 1)
curl -X DELETE http://localhost:1234/api/storybooks/cart/items/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

**Expected Response (200 OK):**
```json
{
  "message": "Item successfully removed from cart.",
  "cart": {
    "cartId": 1,
    "cartItems": [
      {
        "id": 2,
        "storybookId": 2,
        "title": "Mystery in Mountains",
        "description": "A thrilling mystery",
        "authorName": "Jane Smith",
        "categoryName": "Mystery",
        "price": 7.99,
        "coverImageUrl": "https://...",
        "quantity": 1
      }
    ],
    "totalItems": 1,
    "totalPrice": 7.99
  }
}
```

---

### Scenario 4: Authentication Error Handling

#### Try Cart Operation Without Token
```bash
curl -X POST http://localhost:1234/api/storybooks/cart/add \
  -H "Content-Type: application/json" \
  -d {
    "storybookId": 1
  }
```

**Expected Response (403 FORBIDDEN):**
Access denied - Missing authentication token

#### Try with Invalid Token
```bash
curl -X POST http://localhost:1234/api/storybooks/cart/add \
  -H "Authorization: Bearer invalid_token" \
  -H "Content-Type: application/json" \
  -d {
    "storybookId": 1
  }
```

**Expected Response (403 FORBIDDEN):**
Invalid token - Access denied

---

## Postman Collection Import

### Using Postman
1. Download or create a new Postman Collection
2. Add the following environment variables:
   - `base_url`: http://localhost:1234
   - `token`: (paste JWT token after login)
   - `storybook_id`: 1

3. Import these request templates:

**Request 1: User Registration**
```
POST /users/register
Body (JSON):
{
  "name": "Test User",
  "email": "test@example.com",
  "password": "password123"
}
```

**Request 2: User Login**
```
POST /users/login
Body (JSON):
{
  "email": "test@example.com",
  "password": "password123"
}
```
*Save the response token to Postman variable: `{{token}}`*

**Request 3: Get All Storybooks**
```
GET /api/storybooks
No Auth Required
```

**Request 4: Get Storybook Details**
```
GET /api/storybooks/{{storybook_id}}
No Auth Required
```

**Request 5: Search Storybooks**
```
GET /api/storybooks/search?keyword=fantasy
No Auth Required
```

**Request 6: Add to Cart**
```
POST /api/storybooks/cart/add
Auth: Bearer {{token}}
Body (JSON):
{
  "storybookId": 1
}
```

**Request 7: Get Cart**
```
GET /api/storybooks/cart
Auth: Bearer {{token}}
```

**Request 8: Remove from Cart**
```
DELETE /api/storybooks/cart/items/1
Auth: Bearer {{token}}
```

---

## Error Scenarios Testing

### Test 1: Storybook Not Found
```bash
curl -X GET http://localhost:1234/api/storybooks/99999 \
  -H "Content-Type: application/json"
```

Expected: 400 - "Storybook not found."

### Test 2: Invalid Credentials
```bash
curl -X POST http://localhost:1234/users/login \
  -H "Content-Type: application/json" \
  -d {
    "email": "nonexistent@example.com",
    "password": "wrongpassword"
  }
```

Expected: 400 - "User not found."

### Test 3: Duplicate Email Registration
```bash
curl -X POST http://localhost:1234/users/register \
  -H "Content-Type: application/json" \
  -d {
    "name": "Another User",
    "email": "john@example.com",
    "password": "password123"
  }
```

Expected: 400 - "User already exists"

---

## Performance Testing

### Test Load with Multiple Users
```bash
# Register 10 users in sequence
for i in {1..10}; do
  curl -X POST http://localhost:1234/users/register \
    -H "Content-Type: application/json" \
    -d "{
      \"name\": \"User $i\",
      \"email\": \"user$i@example.com\",
      \"password\": \"password123\"
    }"
done
```

### Test Search Performance
```bash
# Search with various keywords
time curl -X GET "http://localhost:1234/api/storybooks/search?keyword=adventure" \
  -H "Content-Type: application/json"
```

---

## Browser Testing

### Using Browser Dev Tools (F12)

```javascript
// Get all storybooks
fetch('http://localhost:1234/api/storybooks')
  .then(res => res.json())
  .then(data => console.log(data));

// Search storybooks
fetch('http://localhost:1234/api/storybooks/search?keyword=fantasy')
  .then(res => res.json())
  .then(data => console.log(data));

// Add to cart (requires token)
const token = 'your_jwt_token_here';
fetch('http://localhost:1234/api/storybooks/cart/add', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({ storybookId: 1 })
})
  .then(res => res.json())
  .then(data => console.log(data));

// Get cart
fetch('http://localhost:1234/api/storybooks/cart', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
})
  .then(res => res.json())
  .then(data => console.log(data));
```

---

## Troubleshooting

### Cart is empty when fetching
- Verify user is logged in with correct JWT token
- Check if cart was created (automatically created on first add)
- Verify user ID extraction from token works correctly

### Search returns no results
- Check if storybooks are added to database
- Verify search is case-insensitive (should work for "fantasy" and "FANTASY")
- Check if author names and categories are correctly linked

### Duplicate item error when adding to cart
- This is expected behavior - ensures single quantity per item
- User must remove item first before adding a different quantity
- Cart shows items with quantity = 1 as per requirements

### Authentication errors
- Verify token is included in Authorization header with "Bearer " prefix
- Check if token is not expired (10-hour expiration)
- Ensure token is from the correct user

---

## Performance Benchmarks

Expected Response Times (on local machine):
- GET all storybooks: ~50-100ms
- Search storybooks: ~50-150ms (depending on keyword)
- Add to cart: ~200-300ms
- Get cart: ~100-200ms
- Remove from cart: ~150-250ms

---

## Notes

- All timestamps are stored in UTC
- Prices are stored as DECIMAL(10,2) for precision
- JWT tokens expire after 10 hours
- Cart is automatically created on first item addition
- Each storybook appears only once in cart with quantity 1
