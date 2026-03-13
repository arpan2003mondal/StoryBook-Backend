# Storybook Homepage Search & Cart API - Complete Implementation Guide

## Overview
This implementation provides a complete search and shopping cart functionality for the Storybook application. Users can browse audiobooks, search by title/author/genre, view details, and manage a shopping cart.

---

## Database Schema

### New Tables Created
1. **cart** - Stores user's cart
   - id (PK, AUTO_INCREMENT)
   - user_id (FK, UNIQUE) - One cart per user
   - created_at
   - updated_at

2. **cart_items** - Stores items in cart
   - id (PK, AUTO_INCREMENT)
   - cart_id (FK)
   - storybook_id (FK)
   - quantity (DEFAULT 1)

---

## API Endpoints

### 1. Browse & Search Storybooks (Public Access)

#### Get All Storybooks
```
GET /api/storybooks
Content-Type: application/json

Response (200 OK):
[
  {
    "id": 1,
    "title": "The Great Adventure",
    "description": "An epic tale of adventure",
    "authorId": 1,
    "authorName": "John Doe",
    "categoryId": 1,
    "categoryName": "Fantasy",
    "price": 9.99,
    "audioUrl": "https://example.com/audio/1",
    "coverImageUrl": "https://example.com/image/1",
    "createdAt": "2026-03-13T22:30:00"
  },
  ...
]
```

#### Get Storybook Details by ID
```
GET /api/storybooks/{id}
Content-Type: application/json

Example: GET /api/storybooks/1

Response (200 OK):
{
  "id": 1,
  "title": "The Great Adventure",
  "description": "An epic tale of adventure",
  "authorId": 1,
  "authorName": "John Doe",
  "categoryId": 1,
  "categoryName": "Fantasy",
  "price": 9.99,
  "audioUrl": "https://example.com/audio/1",
  "coverImageUrl": "https://example.com/image/1",
  "createdAt": "2026-03-13T22:30:00"
}

Error Response (404):
{
  "statusCode": 400,
  "message": "Storybook not found.",
  "timestamp": "2026-03-13T22:35:00"
}
```

#### Search Storybooks
```
GET /api/storybooks/search?keyword={keyword}
Content-Type: application/json

Examples:
- GET /api/storybooks/search?keyword=fantasy
- GET /api/storybooks/search?keyword=John%20Doe
- GET /api/storybooks/search?keyword=adventure

Response (200 OK):
[
  {
    "id": 1,
    "title": "The Great Adventure",
    ...
  },
  {
    "id": 3,
    "title": "Adventure Begins",
    ...
  }
]

Notes:
- Search is case-insensitive
- Searches across: title, description, author name, category name
- Returns empty array if no matches found
```

---

### 2. Cart Operations (Requires Authentication)

#### Add Item to Cart
```
POST /api/storybooks/cart/add
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

Request:
{
  "storybookId": 1
}

Response (201 CREATED):
{
  "message": "Item successfully added to cart.",
  "cart": {
    "cartId": 5,
    "cartItems": [
      {
        "id": 1,
        "storybookId": 1,
        "title": "The Great Adventure",
        "description": "An epic tale of adventure",
        "authorName": "John Doe",
        "categoryName": "Fantasy",
        "price": 9.99,
        "coverImageUrl": "https://example.com/image/1",
        "quantity": 1
      }
    ],
    "totalItems": 1,
    "totalPrice": 9.99
  }
}

Error Cases:

1. Item Already in Cart (400):
{
  "statusCode": 400,
  "message": "This storybook is already in your cart.",
  "timestamp": "2026-03-13T22:35:00"
}

2. Storybook Not Found (400):
{
  "statusCode": 400,
  "message": "Storybook not found.",
  "timestamp": "2026-03-13T22:35:00"
}

3. Not Authenticated (401):
{
  "statusCode": 400,
  "message": "User is not authenticated. Please login first.",
  "timestamp": "2026-03-13T22:35:00"
}
```

#### Get User's Cart
```
GET /api/storybooks/cart
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

Response (200 OK):
{
  "cartId": 5,
  "cartItems": [
    {
      "id": 1,
      "storybookId": 1,
      "title": "The Great Adventure",
      "description": "An epic tale of adventure",
      "authorName": "John Doe",
      "categoryName": "Fantasy",
      "price": 9.99,
      "coverImageUrl": "https://example.com/image/1",
      "quantity": 1
    },
    {
      "id": 2,
      "storybookId": 2,
      "title": "Mystery in the Mountains",
      "description": "A thrilling mystery",
      "authorName": "Jane Smith",
      "categoryName": "Mystery",
      "price": 7.99,
      "coverImageUrl": "https://example.com/image/2",
      "quantity": 1
    }
  ],
  "totalItems": 2,
  "totalPrice": 17.98
}

Error Response (400):
{
  "statusCode": 400,
  "message": "Cart not found.",
  "timestamp": "2026-03-13T22:35:00"
}
```

#### Remove Item from Cart
```
DELETE /api/storybooks/cart/items/{cartItemId}
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

Example: DELETE /api/storybooks/cart/items/1

Response (200 OK):
{
  "message": "Item successfully removed from cart.",
  "cart": {
    "cartId": 5,
    "cartItems": [
      {
        "id": 2,
        "storybookId": 2,
        "title": "Mystery in the Mountains",
        "description": "A thrilling mystery",
        "authorName": "Jane Smith",
        "categoryName": "Mystery",
        "price": 7.99,
        "coverImageUrl": "https://example.com/image/2",
        "quantity": 1
      }
    ],
    "totalItems": 1,
    "totalPrice": 7.99
  }
}

Error Cases:

1. Cart Item Not Found (400):
{
  "statusCode": 400,
  "message": "Cart item not found.",
  "timestamp": "2026-03-13T22:35:00"
}

2. Unauthorized Operation (400):
{
  "statusCode": 400,
  "message": "You are not authorized to perform this operation.",
  "timestamp": "2026-03-13T22:35:00"
}
```

---

## Application Flow

### User Journey

```
1. User Registration/Login
   POST /users/register  →  User Account Created
   POST /users/login     →  JWT Token Received

2. Browse Storybooks
   GET /api/storybooks   →  View all available audiobooks
   GET /api/storybooks/search?keyword=... → Search by title/author/genre

3. View Details
   GET /api/storybooks/{id}  →  See complete story details

4. Add to Cart
   POST /api/storybooks/cart/add
   {storybookId: 1}
   ↓
   Check if duplicate? → YES: Error message
                      → NO: Add to cart

5. Manage Cart
   GET /api/storybooks/cart    → View cart contents
   DELETE /api/storybooks/cart/items/{cartItemId}  → Remove item

6. Continue Shopping or Checkout
   [Repeat step 2-5 or proceed to payment]
```

---

## Key Business Rules

1. **Single Quantity per Item**: Each storybook in cart has quantity = 1
2. **No Duplicate Items**: Cannot add same storybook twice to cart
3. **Cart Persistence**: Cart created first time user adds item and persists across sessions
4. **Automatic Cart Creation**: Cart automatically created when first item is added
5. **User-Specific Carts**: Each user has their own cart

---

## Messages (Externalized in messages.properties)

```properties
# Cart Messages
cart.item.added.success=Item successfully added to cart.
cart.item.removed.success=Item successfully removed from cart.
cart.item.already.exists=This storybook is already in your cart.
cart.not.found=Cart not found.
cart.item.not.found=Cart item not found.

# User Messages
user.not.authenticated=User is not authenticated. Please login first.
user.not.found=User not found.

# Storybook Messages
storybook.not.found=Storybook not found.

# Error Messages
unauthorized.operation=You are not authorized to perform this operation.
general.error=An unexpected error occurred.
```

---

## Authentication

All cart operations require JWT Bearer token in Authorization header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Token obtained from:
```
POST /users/login
{
  "email": "user@example.com",
  "password": "password123"
}
```

---

## File Structure

```
src/main/java/com/company/storybook/
├── entity/
│   ├── Cart.java                 (NEW)
│   └── CartItem.java             (NEW)
├── dto/
│   ├── CartItemDTO.java          (NEW)
│   ├── CartResponseDTO.java      (NEW)
│   └── AddToCartRequest.java     (NEW)
├── repository/
│   ├── CartRepository.java       (NEW)
│   ├── CartItemRepository.java   (NEW)
│   └── StorybookRepository.java  (UPDATED)
├── service/
│   ├── CartService.java          (NEW)
│   └── CartServiceImpl.java       (NEW)
├── controller/
│   └── StoryBookUserController.java  (NEW)
└── config/
    └── SecurityConfig.java       (UPDATED)

src/main/resources/
├── application.properties        (UPDATED)
└── messages.properties          (UPDATED)
```

---

## Testing Checklist

### 1. Search Functionality
- [ ] Search all storybooks without keyword
- [ ] Search by title
- [ ] Search by author name
- [ ] Search by category name
- [ ] Case-insensitive search
- [ ] Search with no results

### 2. Add to Cart
- [ ] Add single item to cart
- [ ] Add multiple different items
- [ ] Try adding duplicate item (should fail with message)
- [ ] Verify total price calculation
- [ ] Verify total items count

### 3. View Cart
- [ ] View empty cart
- [ ] View cart with items
- [ ] Verify cart structure and calculations

### 4. Remove from Cart
- [ ] Remove single item
- [ ] Remove all items
- [ ] Verify updated cart totals

### 5. Authentication
- [ ] Public endpoints work without token
- [ ] Cart operations fail without token
- [ ] Cart operations work with valid token

---

## Dependencies
- Spring Boot 3.x
- Spring Security with JWT
- Jakarta Persistence (JPA)
- MySQL
- Lombok

---

## Future Enhancements
1. Wishlist functionality
2. Order history
3. Ratings and reviews
4. Payment integration
5. Order status tracking
6. Notification system
