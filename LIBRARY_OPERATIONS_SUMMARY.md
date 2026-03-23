# User Library Operations - Comprehensive Summary

## Overview
The User Library feature enables users to view their purchased storybooks and tracks purchase history. The library is populated automatically when users complete checkout, and prevents re-purchasing already owned books.

---

## 1. CONTROLLER METHODS (REST API Endpoints)

### WalletController
**Location:** [src/main/java/com/company/storybook/controller/WalletController.java](src/main/java/com/company/storybook/controller/WalletController.java)

#### 1.1 Get User Library
- **Method:** `getUserLibrary()`
- **HTTP Endpoint:** `GET /api/wallet/library`
- **Authentication:** Required (JWT Token)
- **Purpose:** Retrieve all purchased storybooks for the authenticated user
- **Implementation Details:**
  - Gets current user ID from security context
  - Queries UserLibraryRepository for all library entries by userId
  - Maps UserLibrary entities to StorybookResponse DTOs
  - Returns list of purchased books with metadata
- **Response Structure:**
  ```json
  {
    "message": "User library retrieved successfully",
    "items": [StorybookResponse objects],
    "total": 5
  }
  ```
- **Lines:** [73-88](src/main/java/com/company/storybook/controller/WalletController.java#L73-L88)

#### 1.2 Checkout (Library Population)
- **Method:** `checkout()`
- **HTTP Endpoint:** `POST /api/wallet/checkout`
- **Authentication:** Required (JWT Token)
- **Purpose:** Process payment and automatically add purchased books to user library
- **Delegates to:** OrderService.checkout()
- **Lines:** [58-67](src/main/java/com/company/storybook/controller/WalletController.java#L58-L67)

---

## 2. SERVICE LAYER METHODS

### OrderServiceImpl (Core Library Management)
**Location:** [src/main/java/com/company/storybook/service/OrderServiceImpl.java](src/main/java/com/company/storybook/service/OrderServiceImpl.java)

#### 2.1 Checkout with Library Addition
- **Method:** `checkout(Long userId)`
- **Interface:** `OrderService.checkout()`
- **Scope:** @Transactional
- **Purpose:** Complete the purchase workflow and add books to user library
- **Key Operations:**
  1. Validates user and cart existence
  2. Checks wallet balance
  3. Creates Order and OrderItem records
  4. **Library Addition Logic:**
     - For each CartItem in the cart:
       - Checks if book already exists in user library using `existsByUserIdAndStorybookId()`
       - If not already owned, creates new UserLibrary entry with:
         - User reference
         - Storybook reference
         - Current timestamp as purchasedAt
       - Saves UserLibrary to database
  5. Deducts wallet balance
  6. Clears user's shopping cart
- **Lines:** [36-103](src/main/java/com/company/storybook/service/OrderServiceImpl.java#L36-L103)
- **Library-Related Code Snippet:**
  ```java
  // Lines 79-85: Add storybook to user library
  if (!userLibraryRepository.existsByUserIdAndStorybookId(userId, cartItem.getStorybook().getId())) {
      UserLibrary userLibrary = new UserLibrary();
      userLibrary.setUser(user);
      userLibrary.setStorybook(cartItem.getStorybook());
      userLibrary.setPurchasedAt(LocalDateTime.now());
      userLibraryRepository.save(userLibrary);
  }
  ```

### CartServiceImpl (Library Validation)
**Location:** [src/main/java/com/company/storybook/service/CartServiceImpl.java](src/main/java/com/company/storybook/service/CartServiceImpl.java)

#### 2.2 Add to Cart with Library Check
- **Method:** `addToCart(Long userId, AddToCartRequest request)`
- **Scope:** @Transactional
- **Purpose:** Prevent users from adding already-purchased books to cart
- **Library Usage:**
  - Calls `userLibraryRepository.existsByUserIdAndStorybookId(userId, storybookId)`
  - If true, throws exception: `cart.item.already.purchased`
  - This prevents duplicate purchases
- **Lines:** [48-89](src/main/java/com/company/storybook/service/CartServiceImpl.java#L48-L89)
- **Library Check Code:**
  ```java
  // Line 59: Check if storybook is already purchased (in user library)
  if (userLibraryRepository.existsByUserIdAndStorybookId(userId, request.getStorybookId())) {
      throw new StoryBookException("cart.item.already.purchased");
  }
  ```

---

## 3. ENTITY MODELS

### UserLibrary Entity
**Location:** [src/main/java/com/company/storybook/entity/UserLibrary.java](src/main/java/com/company/storybook/entity/UserLibrary.java)

- **Database Table:** `user_library`
- **Purpose:** JPA entity representing a user's purchased storybook
- **Fields:**
  - `id` (Long) - Primary key, auto-generated
  - `user` (User) - ManyToOne relationship to User entity
  - `storybook` (Storybook) - ManyToOne relationship to Storybook entity
  - `purchasedAt` (LocalDateTime) - Timestamp of purchase, immutable after creation
- **Relationships:**
  - User (1:N) - One user can have multiple library entries
  - Storybook (1:N) - One storybook can be in multiple users' libraries
- **Fetch Strategy:** LAZY (for both relationships)

---

## 4. REPOSITORY METHODS

### UserLibraryRepository
**Location:** [src/main/java/com/company/storybook/repository/UserLibraryRepository.java](src/main/java/com/company/storybook/repository/UserLibraryRepository.java)

#### 4.1 Find User's Library
- **Method:** `findByUserId(Long userId)`
- **Return Type:** `List<UserLibrary>`
- **Purpose:** Retrieve all book entries in a user's library
- **Usage:** WalletController.getUserLibrary()
- **Implementation:** Spring Data JPA auto-generated query

#### 4.2 Check Book Ownership
- **Method:** `existsByUserIdAndStorybookId(Long userId, Long storybookId)`
- **Return Type:** `boolean`
- **Purpose:** Check if user already owns a specific book
- **Usage:**
  - CartServiceImpl.addToCart() - Prevent duplicate additions
  - OrderServiceImpl.checkout() - Check before adding to library
- **Implementation:** Spring Data JPA auto-generated query

---

## 5. DATA TRANSFER OBJECTS (DTOs)

### StorybookResponse (Library Response DTO)
**Location:** [src/main/java/com/company/storybook/dto/StorybookResponse.java](src/main/java/com/company/storybook/dto/StorybookResponse.java)

- **Fields:**
  - `id` (Long)
  - `title` (String)
  - `description` (String)
  - `authorId` (Long)
  - `authorName` (String)
  - `categoryId` (Long)
  - `categoryName` (String)
  - `price` (BigDecimal)
  - `audioUrl` (String)
  - `sampleAudioUrl` (String)
  - `coverImageUrl` (String)
  - `createdAt` (LocalDateTime)
- **Usage:** 
  - Returned in GET /api/wallet/library endpoint
  - Maps from Storybook entities in user library
  - Contains complete metadata for library display

---

## 6. COMPLETE FLOW DIAGRAM

```
┌─────────────────────────────────────────────────────────────────┐
│ USER LIBRARY WORKFLOW                                           │
└─────────────────────────────────────────────────────────────────┘

1. USER ADDS BOOK TO CART
   │
   └─> CartServiceImpl.addToCart()
       ├─> Verify user & storybook exist
       ├─> [LIBRARY CHECK] ✓ Query: existsByUserIdAndStorybookId()
       │   └─> If book already owned → Throw "cart.item.already.purchased"
       ├─> Verify not already in cart
       └─> Add to cart

2. USER COMPLETES CHECKOUT
   │
   └─> WalletController.checkout()
       └─> OrderServiceImpl.checkout()
           ├─> Verify cart not empty
           ├─> Calculate total amount
           ├─> Check wallet balance
           ├─> Create Order & OrderItems
           ├─> [LIBRARY POPULATION]
           │   └─> For each cart item:
           │       ├─> Query: existsByUserIdAndStorybookId()
           │       ├─> If not already owned:
           │       │   └─> Create UserLibrary entry
           │       │       ├─> Set user
           │       │       ├─> Set storybook
           │       │       ├─> Set purchasedAt timestamp
           │       │       └─> Save to database
           │       └─> (Prevents duplicate entries)
           ├─> Debit wallet
           └─> Clear shopping cart

3. USER VIEWS LIBRARY
   │
   └─> WalletController.getUserLibrary()
       ├─> Get current user ID from security context
       ├─> Query: userLibraryRepository.findByUserId(userId)
       ├─> Map each UserLibrary → StorybookResponse
       └─> Return list with metadata

┌──────────────────────────────────────────────┐
│ LIBRARY → Shows purchased books with full     │
│        metadata (title, author, category)     │
└──────────────────────────────────────────────┘
```

---

## 7. VALIDATION LOGIC

### Duplicate Purchase Prevention
- **Check Point 1:** addToCart() - Validates before adding to cart
  - Query: `existsByUserIdAndStorybookId(userId, storybookId)`
  - Exception: `cart.item.already.purchased`
- **Check Point 2:** checkout() - Double-checks before library entry
  - Query: `existsByUserIdAndStorybookId(userId, storybookId)`
  - Only creates new entry if not already present

### Security
- All library operations require authentication (JWT token)
- User can only access their own library (userId verified from security context)
- Current implementation: Direct user context lookup

---

## 8. RELATED BUSINESS LOGIC

### How Books Enter User Library
1. Books are NOT in library until checkout completes
2. Checkout creates library entries for all cart items
3. Cart is cleared after successful checkout
4. Each book gets a `purchasedAt` timestamp

### How Library Prevents Issues
1. **Prevents Re-purchasing:** Blocks adding already-owned books to cart
2. **Prevents Duplicate Entries:** Checks before inserting in library during checkout
3. **Maintains History:** `purchasedAt` field tracks when book was purchased

---

## 9. DATABASE SCHEMA

### user_library Table
```sql
CREATE TABLE user_library (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    storybook_id BIGINT NOT NULL,
    purchased_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (storybook_id) REFERENCES storybook(id)
)
```

### Relationships
- `user_library.user_id` → `user.id` (ManyToOne)
- `user_library.storybook_id` → `storybook.id` (ManyToOne)

---

## 10. API EXAMPLES

### Get User Library
```bash
GET /api/wallet/library
Authorization: Bearer <JWT_TOKEN>

Response (200 OK):
{
  "message": "User library retrieved successfully",
  "items": [
    {
      "id": 1,
      "title": "Harry Potter",
      "authorName": "J.K. Rowling",
      "categoryName": "Fantasy",
      "price": 9.99,
      "audioUrl": "https://...",
      "sampleAudioUrl": "https://...",
      "coverImageUrl": "https://..."
    },
    {
      "id": 2,
      "title": "The Hobbit",
      "authorName": "J.R.R. Tolkien",
      "categoryName": "Fantasy",
      "price": 7.99,
      ...
    }
  ],
  "total": 2
}
```

### Prevent Re-adding to Cart
```bash
POST /api/cart/add
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "storybookId": 1  // Already in user library
}

Response (400 Bad Request):
{
  "status": 400,
  "message": "This storybook is already in your library. You cannot add it to cart again."
}
```

---

## 11. SUMMARY TABLE

| Component | Location | Method Name | Purpose |
|-----------|----------|------------|---------|
| **Controller** | WalletController.java | getUserLibrary() | REST endpoint to fetch user's library |
| **Controller** | WalletController.java | checkout() | REST endpoint that triggers library population |
| **Service** | OrderServiceImpl.java | checkout() | Core logic for adding books to library |
| **Service** | CartServiceImpl.java | addToCart() | Prevents adding already-owned books |
| **Repository** | UserLibraryRepository.java | findByUserId() | Query: Get all books in library |
| **Repository** | UserLibraryRepository.java | existsByUserIdAndStorybookId() | Query: Check book ownership |
| **Entity** | UserLibrary.java | - | JPA entity for library records |
| **DTO** | StorybookResponse.java | - | Response format for library items |

---

## 12. KEY FEATURES

✅ **Automatic Library Population** - Books added during checkout
✅ **Duplicate Prevention** - Multi-level validation to prevent re-purchasing
✅ **Purchase Tracking** - `purchasedAt` timestamp records when book was purchased
✅ **Security** - JWT authentication required for all library operations
✅ **Data Consistency** - Transactional operations ensure data integrity
✅ **Rich Metadata** - Library items include title, author, category, URLs
✅ **Lazy Loading** - User and Storybook relationships use LAZY fetch strategy

---

## 13. EXTENSION POINTS FOR FUTURE ENHANCEMENTS

1. **Library Statistics**
   - Service method to count total purchased books
   - Average purchase price, total spent

2. **Wishlist Feature**
   - Similar to library but for items user wants to buy
   - `addToWishlist()`, `removeFromWishlist()`, `getWishlist()`

3. **Purchase History**
   - Enhanced library with order references
   - Query by date range, price range, category

4. **Reading Progress**
   - Track which books user has started, completed
   - Bookmarks and progress percentage

5. **Library Export**
   - Export library as PDF, CSV, or JSON

6. **Library Filtering/Search**
   - Filter by author, category, purchase date
   - Full-text search on library items

---

*Last Updated: March 23, 2026*
