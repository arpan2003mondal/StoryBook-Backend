# Storybook Homepage Search & Cart - Implementation Summary

## Executive Summary

A complete search and shopping cart system has been implemented for the Storybook audiobook platform. Users can now browse audiobooks, search by title/author/genre, view details, and manage a shopping cart with proper inventory management and message handling.

---

## Architecture Overview

### Layered Architecture

```
┌─────────────────────────────────┐
│  Controller Layer               │
│  StoryBookUserController        │  ← REST API endpoints
│  UserAuthController (Existing)  │
│  AdminController (Existing)     │
└────────────────┬────────────────┘
                 │
┌────────────────▼──────────────────────┐
│  Service Layer                        │
│  CartService (Interface)              │  ← Business Logic
│  CartServiceImpl                       │
│  AuthService (Existing)               │
│  AdminService (Existing)              │
└────────────────┬──────────────────────┘
                 │
┌────────────────▼──────────────────────┐
│  Repository Layer (Data Access)       │
│  CartRepository                       │
│  CartItemRepository                   │
│  StorybookRepository (Enhanced)       │
│  UserRepository (Existing)            │
│  AuthorRepository (Existing)          │
│  CategoryRepository (Existing)        │
└────────────────┬──────────────────────┘
                 │
┌────────────────▼──────────────────────┐
│  Entity Layer (JPA/ORM)               │
│  Cart                                 │  ← Database Models
│  CartItem                             │
│  Storybook (Existing)                 │
│  User (Existing)                      │
│  Author (Existing)                    │
│  Category (Existing)                  │
└────────────────┬──────────────────────┘
                 │
        ┌────────▼────────┐
        │    MySQL DB     │
        │  "storybookdb"  │
        └─────────────────┘
```

---

## Database Schema Changes

### New Tables

#### 1. cart Table
```sql
CREATE TABLE cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE,  -- One-to-One relationship with User
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

**Relationships:**
- One-to-One with User (unique user_id)
- One-to-Many with CartItem (cascade delete orphans)

#### 2. cart_items Table
```sql
CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT,
    storybook_id BIGINT,
    quantity INT DEFAULT 1,
    FOREIGN KEY (cart_id) REFERENCES cart(id),
    FOREIGN KEY (storybook_id) REFERENCES storybooks(id)
);
```

**Relationships:**
- Many-to-One with Cart
- Many-to-One with Storybook

---

## Class Hierarchy & Design

### Entity Classes

#### Cart Entity
```
@Entity @Table("cart")
┌─────────────────────────────┐
│ Cart                        │
├─────────────────────────────┤
│ - id: Long (PK)             │
│ - user: User (FK, Unique)   │
│ - cartItems: List<CartItem> │  Cascade: DELETE orphans
│ - createdAt: LocalDateTime  │
│ - updatedAt: LocalDateTime  │
└─────────────────────────────┘
     ▲
     │ OneToOne
     │ ManyToOne (Items)
     ├──────────────────────────────┐
     │                              │
  [User]                      [CartItem]
```

#### CartItem Entity
```
@Entity @Table("cart_items")
┌─────────────────────────────────┐
│ CartItem                        │
├─────────────────────────────────┤
│ - id: Long (PK)                 │
│ - cart: Cart (FK)               │
│ - storybook: Storybook (FK)     │
│ - quantity: Integer = 1         │
└─────────────────────────────────┘
     ▲
     │ ManyToOne
     ├──────────────────────────────┐
     │                              │
  [Cart]                    [Storybook]
```

### DTO Classes (Data Transfer Objects)

#### AddToCartRequest
```json
{
  "storybookId": Long
}
```

#### CartItemDTO
```json
{
  "id": Long,
  "storybookId": Long,
  "title": String,
  "description": String,
  "authorName": String,
  "categoryName": String,
  "price": BigDecimal,
  "coverImageUrl": String,
  "quantity": Integer
}
```

#### CartResponseDTO
```json
{
  "cartId": Long,
  "cartItems": [CartItemDTO],
  "totalItems": Integer,
  "totalPrice": BigDecimal
}
```

---

## Service Layer Implementation

### CartService Interface

Defines 6 core operations:

```java
public interface CartService {
    List<StorybookResponse> getAllStorybooks();
    StorybookResponse getStorybookById(Long id) throws StoryBookException;
    List<StorybookResponse> searchStorybooks(String keyword);
    CartResponseDTO addToCart(Long userId, AddToCartRequest request) throws StoryBookException;
    CartResponseDTO removeFromCart(Long userId, Long cartItemId) throws StoryBookException;
    CartResponseDTO getCart(Long userId) throws StoryBookException;
}
```

### CartServiceImpl Implementation

#### Key Methods:

1. **searchStorybooks(String keyword)**
   - Case-insensitive search
   - Searches: title, description, author name, category name
   - Returns empty list if no matches

2. **addToCart(Long userId, AddToCartRequest request)**
   - Validates user exists
   - Validates storybook exists
   - Creates cart if doesn't exist
   - **Prevents duplicate items** (throws "cart.item.already.exists")
   - Sets quantity = 1
   - Returns updated cart

3. **removeFromCart(Long userId, Long cartItemId)**
   - Validates ownership (cart item belongs to user's cart)
   - Deletes cart item
   - Returns updated cart
   - Updates cart's `updated_at` timestamp

4. **Helper Methods:**
   - buildCartResponse(Cart) - Constructs DTO with totals
   - mapStorybookToResponse(Storybook) - Entity to DTO mapping
   - mapCartItemToDTO(CartItem) - Entity to DTO mapping

---

## Repository Layer

### StorybookRepository (Enhanced)
```java
public interface StorybookRepository extends JpaRepository<Storybook, Long> {
    List<Storybook> searchByTitleOrDescription(String keyword);
    List<Storybook> searchByAuthorName(String authorName);
    List<Storybook> searchByCategory(String categoryName);
    List<Storybook> searchStorybooks(String keyword);  // Combined search
}
```

### CartRepository
```java
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
    Optional<Cart> findByUserId(Long userId);
}
```

### CartItemRepository
```java
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndStorybookId(Long cartId, Long storybookId);
}
```

---

## REST Controller Implementation

### StoryBookUserController

```
Route                               Method      Auth    Description
─────────────────────────────────────────────────────────────────────
/api/storybooks                    GET         No      Get all storybooks
/api/storybooks/search            GET         No      Search with keyword
/api/storybooks/{id}              GET         No      Get details by ID
/api/storybooks/cart/add          POST        Yes     Add to cart
/api/storybooks/cart              GET         Yes     View user's cart
/api/storybooks/cart/items/{id}   DELETE      Yes     Remove from cart
```

**Key Features:**
- User ID extracted from JWT token via email lookup
- All responses include localized messages
- Proper HTTP status codes (201 for create, 200 for success, 400 for errors)
- Exception handling via GlobalExceptionHandler

---

## Security Configuration

### Updated SecurityConfig

```
Endpoint Pattern              Access Level      Notes
─────────────────────────────────────────────────────
/users/login                 PUBLIC            User login
/users/register              PUBLIC            User registration
/admin/login                 PUBLIC            Admin login
/api/storybooks              PUBLIC            Browse all
/api/storybooks/search       PUBLIC            Search
/api/storybooks/{id}         PUBLIC (GET)      View details
/api/storybooks/cart/**      AUTHENTICATED    Cart operations
/admin/**                    ADMIN ONLY       Admin endpoints
```

---

## Message Management

### Externalized Messages (messages.properties)

```properties
# Cart Messages
cart.item.added.success=Item successfully added to cart.
cart.item.removed.success=Item successfully removed from cart.
cart.item.already.exists=This storybook is already in your cart. ← NEW
cart.not.found=Cart not found. ← NEW
cart.item.not.found=Cart item not found. ← NEW

# User Messages
user.not.authenticated=User is not authenticated. Please login first. ← NEW
user.not.found=User not found.

# Business
storybook.not.found=Storybook not found. ← NEW
unauthorized.operation=You are not authorized to perform this operation. ← NEW

# Error
general.error=An unexpected error occurred.
```

**Benefit:** All messages are localized via MessageSource - no hardcoded strings in code.

---

## Request/Response Flow Examples

### Flow 1: Add to Cart (Success)
```
Client Request:
┌──────────────────────────────────┐
│ POST /api/storybooks/cart/add    │
│ Auth: Bearer {token}             │
│ Body: {storybookId: 1}           │
└────────────┬─────────────────────┘
             │
             ▼
┌──────────────────────────────────┐
│ StoryBookUserController          │
│ - Extract userId from token      │
│ - Call cartService.addToCart()   │
└────────────┬─────────────────────┘
             │
             ▼
┌──────────────────────────────────────────────┐
│ CartServiceImpl.addToCart()                   │
│ 1. Find user (throws if not found)           │
│ 2. Find storybook (throws if not found)      │
│ 3. Get or create cart                        │
│ 4. Check duplicate (throws if exists) ◄─────┼─ NEW: Prevents duplicates
│ 5. Create CartItem with qty=1                │
│ 6. Save items                                │
│ 7. Return CartResponseDTO with totals        │
└────────────┬─────────────────────────────────┘
             │
             ▼
┌──────────────────────────────────┐
│ Response (201 CREATED)           │
│ {message, cart}                  │
└──────────────────────────────────┘
```

### Flow 2: Add to Cart (Error - Duplicate)
```
Client Request: Add storybookId=1 (already in cart)
             │
             ▼
┌──────────────────────────────────────────────┐
│ CartServiceImpl.addToCart()                   │
│ - ... (steps 1-3 same as above)              │
│ - Check duplicate: FOUND ◄────────────────── Already exists
│ - Throw StoryBookException("cart.item.already.exists")
└────────────┬─────────────────────────────────┘
             │
             ▼
┌──────────────────────────────────┐
│ GlobalExceptionHandler           │
│ - getMessage("cart.item.already.exists")
│ - Return localized message       │
└────────────┬─────────────────────┘
             │
             ▼
┌──────────────────────────────────┐
│ Response (400 BAD REQUEST)       │
│ {statusCode, message, timestamp} │
└──────────────────────────────────┘
```

---

## Data Flow Diagrams

### User Adding Item to Cart

```
Database View:

Before Add:
users [id=1, email='john@example.com']
cart [id=1, user_id=1] (or null)
cart_items []

Client sends:
POST /api/storybooks/cart/add
{storybookId: 1}

After Add:
users [id=1, email='john@example.com']
cart [id=1, user_id=1, created_at=now, updated_at=now]
cart_items [
  {id=1, cart_id=1, storybook_id=1, quantity=1}
]

CartResponseDTO:
{
  cartId: 1,
  cartItems: [{storybookId:1, title:'...', price:9.99, qty:1}],
  totalItems: 1,
  totalPrice: 9.99
}
```

### Search Flow

```
Database Query:

SELECT s FROM Storybook s WHERE
  LOWER(s.title) LIKE '%adventure%' OR
  LOWER(s.description) LIKE '%adventure%' OR
  LOWER(s.author.name) LIKE '%adventure%' OR
  LOWER(s.category.name) LIKE '%adventure%'

Results:
- Storybook(id=1, title='The Great Adventure', author='John Doe')
- Storybook(id=3, title='Adventure Begins', author='Jane Smith')
- Storybook(id=5, category='Adventure Stories')

Mapped to:
List<StorybookResponse> [3 items with full details]
```

---

## Business Logic Rules

### Single Quantity Constraint
- **Why:** Simplify cart management, follow spec requirements
- **Implementation:** CartItem.quantity always = 1
- **Validation:** Prevent duplicate entries in cart

### Automatic Cart Creation
- **When:** First item is added to cart
- **How:** `cartRepository.findByUserId(userId).orElseGet(...)`
- **Benefit:** Clean user experience, no manual cart creation

### Duplicate Prevention
- **Check:** `cartItemRepository.findByCartIdAndStorybookId(...)`
- **Action:** Throw exception with localized message
- **UX:** User receives clear message to remove before re-adding

### Cart Totals Calculation
```java
totalItems = cartItems.size()
totalPrice = cartItems.stream()
  .map(item -> item.price * item.quantity)
  .sum()
```

---

## Entity Relationships

```
User (One-to-One) Cart (One-to-Many) CartItem
  │                                      │
  │  ▲1─────────1▲                       │
  │              │                       │
  └──────────────┘                    (Many)
                                        │
                                        │ (Many-to-One)
                                        ▼
                                   Storybook
                                        │
                                        ├─ Author (Many-to-One)
                                        └─ Category (Many-to-One)
```

---

## Testing Considerations

### Unit Testing
- CartService methods with mocked repositories
- Exception handling for all error cases
- Message source lookups

### Integration Testing
- End-to-end cart operations with real database
- Search functionality with various keywords
- Authorization with JWT tokens

### Load Testing
- Multiple users adding items simultaneously
- Search performance with large dataset
- Cart calculation accuracy

---

## Performance Optimizations

### Current
- Lazy loading for relationships (reduces JSON payload)
- Index on user_id in cart table (unique constraint)
- Index on cart_id, storybook_id in cart_items

### Potential Future
- Cache frequently searched keywords
- Paginate search results
- Denormalize cart totals for faster calculation

---

## Error Handling Strategy

```
Exception Thrown
     │
     ▼
GlobalExceptionHandler.handleStoryBookException()
     │
     ├─ Error Code (message key)
     │
     ├─ MessageSource.getMessage()
     │     │
     │     ▼
     │   Localized Message
     │
     ▼
ErrorInfo {statusCode, message, timestamp}
     │
     ▼
HTTP Response (400/401/500)
```

---

## Summary of Implementation

### What Was Built
1. **Search System:** Complex JPQL queries for multi-field search
2. **Cart Management:** Full CRUD operations with business logic
3. **Message System:** Externalized, localized messages
4. **Security:** Role-based access with JWT
5. **API Layer:** RESTful endpoints following conventions

### Files Created/Modified
- **2 New Entities:** Cart, CartItem
- **3 New DTOs:** CartItemDTO, CartResponseDTO, AddToCartRequest
- **2 New Repositories:** CartRepository, CartItemRepository
- **1 Enhanced Repository:** StorybookRepository (4 new search methods)
- **1 Service Interface:** CartService
- **1 Service Implementation:** CartServiceImpl (250+ lines)
- **1 REST Controller:** StoryBookUserController (80+ lines)
- **2 Configuration Files:** SecurityConfig (updated), application.properties (updated)
- **1 Message File:** messages.properties (updated with 8+ new messages)

### Total Lines of Code
- **Java Code:** ~1000+ lines (entities, services, controllers, repositories)
- **Configuration:** ~50 lines
- **Messages:** ~30 lines
- **Documentation:** API Docs and Testing Guide

---

## Next Steps (Future Enhancements)

1. **Checkout System:** Order creation from cart
2. **Wishlist:** Save items for later
3. **Reviews:** User ratings and comments
4. **Recommendations:** Suggested items based on purchase history
5. **Analytics:** Popular searches, best sellers
6. **Notifications:** Email confirmations, order updates
7. **Mobile Support:** Optimize for mobile clients
8. **Pagination:** Limit search results
9. **Filters:** Filter by price range, author, category
10. **Caching:** Redis for frequently accessed data

---

## Maintenance Notes

- **Database:** Auto DDL enabled (hibernate.ddl-auto = update)
- **Logging:** Enable DEBUG for Spring Security to troubleshoot auth issues
- **Messages:** Add new messages to messages.properties, never hardcode
- **Tokens:** JWT expiration is 10 hours; consider environment variables for production

---

## Conclusion

A production-ready search and cart system has been successfully implemented with proper layering, security, error handling, and message management. The system is scalable, maintainable, and follows Spring Boot best practices.

