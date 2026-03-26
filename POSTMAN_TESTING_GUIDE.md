# Postman API Testing Guide - StoryBook Backend

## Setup Instructions

### 1. Import the Collection
- Open Postman
- Click **File** → **Import**
- Select the `Postman_Collection.json` file from this project
- The collection will be imported with all endpoints and test data

### 2. Configure Environment Variables
The collection uses the following variables that are automatically managed:
- `baseUrl`: http://localhost:1234 (the server URL)
- `userToken`: Automatically saved after user login
- `adminToken`: Automatically saved after admin login

## Test Data Reference

### Pre-existing Users (From Database)
These users are already in the database with the specified credentials:

#### Admin User
- **Email**: admin@storybook.com
- **Password**: Admin@123
- **Role**: ADMIN

#### Regular Users
1. **User 1**
   - Email: john@storybook.com
   - Password: User@123
   - Role: USER

2. **User 2**
   - Email: jane@storybook.com
   - Password: User@123
   - Role: USER

### Pre-existing Authors
- **ID 1**: J.K. Rowling
- **ID 2**: George R.R. Martin
- **ID 3**: Agatha Christie
- **ID 4**: J.R.R. Tolkien

### Pre-existing Categories
- **ID 1**: Fantasy
- **ID 2**: Mystery
- **ID 3**: Adventure
- **ID 4**: Thriller

### Pre-existing Storybooks
- **ID 1**: The Sorcerer's Stone - J.K. Rowling - $9.99
- **ID 2**: The Chamber of Secrets - J.K. Rowling - $9.99
- **ID 3**: The Prisoner of Azkaban - J.K. Rowling - $9.99
- **ID 4**: A Game of Thrones - George R.R. Martin - $12.99
- **ID 5**: A Clash of Kings - George R.R. Martin - $12.99

## API Testing Workflow

### Step 1: Authentication

#### Register a New User
```
POST /users/register
Body:
{
  "name": "Alice Cooper",
  "email": "alice@example.com",
  "password": "Password@123"
}
```
**Expected Response**: 201 Created with success message

#### User Login
```
POST /users/login
Body:
{
  "email": "john@storybook.com",
  "password": "User@123"
}
```
**Expected Response**: 200 OK with JWT token (automatically saved as `userToken`)

#### Admin Login
```
POST /admin/login
Body:
{
  "email": "admin@storybook.com",
  "password": "Admin@123"
}
```
**Expected Response**: 200 OK with JWT token (automatically saved as `adminToken`)

#### User Logout
```
POST /users/logout
Headers:
- Authorization: Bearer {{userToken}}
```
**Expected Response**: 200 OK with logout success message

---

### Step 2: Browse Storybooks

#### Get All Storybooks
```
GET /storybooks
Headers:
- Authorization: Bearer {{userToken}}
```
**Expected Response**: 200 OK with list of all storybooks

**Sample Response Structure**:
```json
[
  {
    "id": 1,
    "title": "The Sorcerer's Stone",
    "description": "A young boy discovers he is a wizard...",
    "authorName": "J.K. Rowling",
    "categoryName": "Fantasy",
    "price": 9.99,
    "audioUrl": "https://...",
    "sampleAudioUrl": "https://example.com/samples/hp1_sample.mp3",
    "coverImageUrl": "https://..."
  }
]
```

#### Search Storybooks by Keyword
```
GET /storybooks/search?keyword=Harry
Headers:
- Authorization: Bearer {{userToken}}
```
**Supported Search Fields**: Title, Author Name, Category Name

**Available Keywords for Testing**:
- "Harry" - returns Harry Potter series
- "Game" - returns A Game of Thrones
- "Fantasy" - returns all fantasy books
- "J.K." - returns all books by J.K. Rowling

#### Get Specific Storybook by ID
```
GET /storybooks/1
Headers:
- Authorization: Bearer {{userToken}}
```
**Expected Response**: 200 OK with detailed storybook information

---

### Step 3: Shopping Cart Operations

#### Add Item to Cart
```
POST /storybooks/cart/add
Headers:
- Authorization: Bearer {{userToken}}
- Content-Type: application/json

Body:
{
  "storybookId": 1
}
```
**Expected Response**: 201 Created with updated cart

**Test Data**:
- Add Storybook ID 1
- Add Storybook ID 2
- Add Storybook ID 3

#### View User's Cart
```
GET /storybooks/cart
Headers:
- Authorization: Bearer {{userToken}}
```
**Expected Response**: 200 OK with cart contents

**Sample Response Structure**:
```json
{
  "cartId": 1,
  "userId": 2,
  "items": [
    {
      "cartItemId": 1,
      "storybookId": 1,
      "title": "The Sorcerer's Stone",
      "price": 9.99,
      "audioUrl": "https://archive.org/download/hp1_sorcerers_2018/...",
      "sampleAudioUrl": "https://example.com/samples/hp1_sample.mp3",
      "coverImageUrl": "https://images.unsplash.com/...",
      "authorName": "J.K. Rowling",
      "categoryName": "Fantasy"
    }
  ],
  "totalValue": 29.97
}
```

#### Remove Item from Cart
```
DELETE /storybooks/cart/items/1
Headers:
- Authorization: Bearer {{userToken}}
```
**Expected Response**: 200 OK with updated cart

**Note**: Replace `1` with the actual `cartItemId` from your cart

---

### Step 4: Wallet & Checkout

#### Check Wallet Balance
```
GET /api/wallet/balance
Headers:
- Authorization: Bearer {{userToken}}
```
**Expected Response**: 200 OK with balance information

**Sample Response**:
```json
{
  "userId": 2,
  "balance": 100.00
}
```

#### Checkout (Create Order from Cart)
```
POST /api/wallet/checkout
Headers:
- Authorization: Bearer {{userToken}}
```
**Expected Response**: 201 Created with order details

**Sample Response Structure**:
```json
{
  "message": "Checkout successful",
  "order": {
    "orderId": 1,
    "userId": 2,
    "totalAmount": 29.97,
    "status": "CREATED",
    "createdAt": "2026-03-14T10:30:00",
    "items": [
      {
        "storybookId": 1,
        "title": "The Sorcerer's Stone",
        "price": 9.99
      }
    ]
  }
}
```

**Important**: Make sure cart has items before checkout

#### View User's Library (Purchased Storybooks)
```
GET /api/wallet/library
Headers:
- Authorization: Bearer {{userToken}}
```
**Expected Response**: 200 OK with purchased items list

**Sample Response**:
```json
{
  "message": "User library retrieved successfully",
  "items": [
    {
      "id": 1,
      "title": "The Sorcerer's Stone",
      "authorName": "J.K. Rowling",
      "price": 9.99,
      "audioUrl": "https://archive.org/download/hp1_sorcerers_2018/...",
      "sampleAudioUrl": "https://example.com/samples/hp1_sample.mp3",
      "coverImageUrl": "https://images.unsplash.com/...",
      "categoryName": "Fantasy"
    }
  ],
  "total": 1
}
```

---

### Step 5: Admin Operations

#### Add New Storybook (Admin Only)
```
POST /admin/storybooks
Headers:
- Authorization: Bearer {{adminToken}}
- Content-Type: application/json

Body:
{
  "title": "The Hobbit",
  "description": "A fantasy adventure about a small hobbit's unexpected journey",
  "authorId": 4,
  "categoryId": 1,
  "price": 8.99,
  "audioUrl": "https://archive.org/download/hobbit_librivox/hobbit_64kb.m4b",
  "sampleAudioUrl": "https://example.com/samples/hobbit_sample.mp3",
  "coverImageUrl": "https://images.unsplash.com/photo-1507842217343-583f20270319?w=400&h=600&fit=crop"
}
```
**Expected Response**: 201 Created with the new storybook details

**Test Data Examples**:

Example 1 - Mystery Book:
```json
{
  "title": "Murder on the Orient Express",
  "description": "A classic detective mystery featuring Hercule Poirot",
  "authorId": 3,
  "categoryId": 2,
  "price": 7.99,
  "audioUrl": "https://archive.org/download/murder_orient_express/mystery.m4b",
  "sampleAudioUrl": "https://example.com/samples/murder_orient_express_sample.mp3",
  "coverImageUrl": "https://images.unsplash.com/photo-1507842217343-583f20270319?w=400&h=600&fit=crop"
}
```

Example 2 - Adventure Book:
```json
{
  "title": "Treasure Island",
  "description": "An adventure classic about pirates and buried treasure",
  "authorId": 2,
  "categoryId": 3,
  "price": 6.99,
  "audioUrl": "https://archive.org/download/treasure_island/adventure.m4b",
  "sampleAudioUrl": "https://example.com/samples/treasure_island_sample.mp3",
  "coverImageUrl": "https://images.unsplash.com/photo-1507842217343-583f20270319?w=400&h=600&fit=crop"
}
```

---

## Reviews & Ratings Endpoints

### Overview
The Review and Rating API allows **authenticated users** to submit reviews and ratings for storybooks, view all reviews for a storybook, and see the average rating.

**Note:** All review endpoints require authentication (Bearer token).

### Available Endpoints

#### 1. **Submit Review**
- **Endpoint**: `POST /storybooks/reviews/add`
- **Authentication**: Required (Bearer {{userToken}})
- **Description**: Submit a new review for a storybook
- **Request Body**:
```json
{
  "userId": 2,
  "storyBookId": 1,
  "rating": 5,
  "reviewText": "This is an amazing storybook! Highly recommended."
}
```
- **Parameters**:
  - `userId`: ID of the user submitting the review (must exist)
  - `storyBookId`: ID of the storybook being reviewed (must exist)
  - `rating`: Rating on a scale of 1-5 (required)
  - `reviewText`: Review commentary (optional)
- **Success Response** (200 OK):
```json
{
  "reviewId": 1,
  "userName": "John Doe",
  "rating": 5,
  "reviewText": "This is an amazing storybook! Highly recommended.",
  "createdAt": "2024-03-26T10:30:00"
}
```
- **Error Cases**:
  - Rating not between 1-5: Returns 400 Bad Request
  - User not found: Returns 404 Not Found
  - Storybook not found: Returns 404 Not Found
  - Storybook not in user library: Returns 400 Bad Request (user must purchase storybook first)
  - No authentication token: Returns 401 Unauthorized

#### 2. **Fetch Reviews for Storybook**
- **Endpoint**: `POST /storybooks/reviews/fetch`
- **Authentication**: Required (Bearer {{userToken}})
- **Description**: Get all reviews submitted for a specific storybook
- **Request Body**:
```json
{
  "storyBookId": 1
}
```
- **Parameters**:
  - `storyBookId`: ID of the storybook to fetch reviews for
- **Success Response** (200 OK):
```json
[
  {
    "reviewId": 1,
    "userName": "John Doe",
    "rating": 5,
    "reviewText": "This is an amazing storybook!",
    "createdAt": "2024-03-26T10:30:00"
  },
  {
    "reviewId": 2,
    "userName": "Jane Smith",
    "rating": 4,
    "reviewText": "Very good story, enjoyed it thoroughly.",
    "createdAt": "2024-03-26T11:15:00"
  }
]
```
- **Error Cases**:
  - Storybook not found: Returns 404 Not Found
  - No authentication token: Returns 401 Unauthorized

#### 3. **Get Average Rating**
- **Endpoint**: `GET /storybooks/reviews/rating/{storyBookId}`
- **Authentication**: Required (Bearer {{userToken}})
- **Description**: Get the average rating and total review count for a storybook
- **Path Parameter**:
  - `storyBookId`: ID of the storybook (e.g., 1)
- **Success Response** (200 OK):
```json
{
  "storyBookId": 1,
  "averageRating": 4.5,
  "totalReviews": 2
}
```
- **Response Fields**:
  - `storyBookId`: The ID of the storybook
  - `averageRating`: Average of all ratings (0.0 if no reviews exist)
  - `totalReviews`: Total number of reviews
- **Error Cases**:
  - Storybook not found: Returns 404 Not Found
  - No authentication token: Returns 401 Unauthorized

### Test Cases

### Test Case 1: Submit Review (Requires Purchase First)
1. **First, login as a regular user**:
   - Use: `User Login` endpoint
   - Get the {{userToken}}
2. **Add storybook to cart**:
   - Use: `Add Item to Cart` endpoint
   - Add storybook ID 1 or another ID
3. **Checkout to purchase the storybook**:
   - Use: `Checkout` endpoint
   - Storybook will be added to user library
4. **Then submit review**:
   - Use endpoint: `POST /storybooks/reviews/add`
   - Add Authorization header: `Bearer {{userToken}}`
   - Use userId: 2, storyBookId: 1 (must match the purchased storybook)
   - Set rating: 5
   - Add review text: "This is an amazing storybook!"
   - Expected: 200 OK with review details

#### Test Case 2: Submit Review for Non-Purchased Storybook
1. Login and get {{userToken}}
2. Try to submit review for a storybook NOT in library
3. Expected: 400 Bad Request with message "You can only review storybooks in your library."
1. Login first and get {{userToken}}
2. Use endpoint: `POST /storybooks/reviews/add`
3. Add Authorization header: `Bearer {{userToken}}`
4. Set rating: 6 (invalid)
5. Expected: 400 Bad Request with error message

#### Test Case 3: Fetch Reviews (Requires Login)
1. Login first and get {{userToken}}
2. Use endpoint: `POST /storybooks/reviews/fetch`
3. Add Authorization header: `Bearer {{userToken}}`
4. Use storyBookId: 1
5. Expected: 200 OK with array of reviews

#### Test Case 4: Get Average Rating (Requires Login)
1. Login first and get {{userToken}}
2. Use endpoint: `GET /storybooks/reviews/rating/1`
3. Add Authorization header: `Bearer {{userToken}}`
4. Expected: 200 OK with average rating and total reviews
5. If no reviews: averageRating should be 0.0

#### Test Case 5: Access Review Without Authentication
1. Try to submit review WITHOUT Authorization header
2. Expected: 401 Unauthorized - "User not authenticated"

---

## Complete Test Scenario

Here's a recommended order to test all endpoints:

1. **Admin Login** - Get admin token
2. **Add New Storybook** - Create a new book
3. **User Registration** - Create a new user account
4. **User Login** - Get user token (REQUIRED for review endpoints)
5. **Get All Storybooks** - View available books
6. **Search Storybooks** - Find specific books
7. **Get Storybook by ID** - Get details of one book
8. **Add to Cart** - Add items (ID 1, 2, 3)
9. **View Cart** - See cart contents
10. **Check Wallet Balance** - Check funds
11. **Checkout** - Create an order
12. **View Library** - See purchased books
13. **Submit Review** - Add review for purchased storybook (requires userToken)
14. **Fetch Reviews** - View all reviews for storybook (requires userToken)
15. **Get Average Rating** - Check storybook rating (requires userToken)
16. **Remove from Cart** - Test cart management
17. **User Logout** - End session

---

## Common Issues & Solutions

### Issue: "Invalid token" error
**Solution**: 
- Ensure you're using the correct token (userToken for regular users, adminToken for admin)
- Re-login to refresh the token
- Check the Authorization header format: `Bearer <token>`

### Issue: "User not authenticated" error
**Solution**:
- Add the Authorization header to the request
- Make sure the token is valid and not expired
- Re-login if needed

### Issue: "Storybook not found" error
**Solution**:
- Use valid storybook IDs (1-5 are pre-existing)
- Check if the ID exists by calling Get All Storybooks first

### Issue: "Rating must be between 1 and 5" error
**Solution**:
- Ensure rating is an integer between 1 and 5
- Check that the rating value is provided

### Issue: "401 Unauthorized" when submitting/fetching reviews
**Solution**:
- Review endpoints require authentication (Bearer token)
- First login with **User Login** endpoint to get {{userToken}}
- Add Authorization header: `Bearer {{userToken}}`
- All review endpoints in Postman collection already have this configured
- If manually testing, include the Authorization header with a valid JWT token

### Issue: "You can only review storybooks in your library" error
**Solution**:
- You can only submit reviews for storybooks you have purchased
- First, add the storybook to your cart and complete checkout  
- The storybook will be added to your library
- Then you can submit a review for it

### Issue: "User not found" when submitting review
**Solution**:
- The userId in the review request must exist in the database
- Use one of the pre-existing user IDs (2 or 3) or create a new user first

### Issue: "Checkout failed" error
**Solution**:
- Ensure cart has items
- Check wallet balance is sufficient
- Cart items use correct storybookId

### Issue: "Insufficient funds" error
**Solution**:
- Check wallet balance
- The test users may have limited funds
- Create a new user or modify wallet balance in database

---

## Additional Notes

- All timestamps are in ISO 8601 format
- Prices are in USD with 2 decimal places
- JWT tokens expire after the session ends
- Passwords are hashed using BCrypt in the database
- The application runs on port 1234
- Rating scale is 1-5, where 1 is poorest and 5 is excellent
- Average rating is calculated from all reviews for a storybook

