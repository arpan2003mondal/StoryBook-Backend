# Review and Rating API Documentation

## Overview
Review and Rating API with 3 core endpoints: submit review, fetch reviews, and get average rating. **All endpoints require user authentication.**

## Base URL
```
http://localhost:8080
```

---

## Authentication Requirement

All review endpoints require authentication via JWT Bearer token:
```
Authorization: Bearer <your_jwt_token>
```

To obtain a token, use the **User Login** endpoint first, then use the returned token for all review operations.

---

## API Endpoints

### 1. Submit Review
Submit a new review for a storybook. **Requires authentication.**

**Endpoint:** `POST /storybooks/reviews/add`

**Authentication**: Required (Bearer token)

**Important**: User must have purchased the storybook (storybook must be in their library) to submit a review.

**Request Body:**
```json
{
  "userId": 1,
  "storyBookId": 1,
  "rating": 5,
  "reviewText": "This storybook is absolutely amazing!"
}
```

**Request Parameters:**
- `userId` (Long, required): ID of the user submitting the review
- `storyBookId` (Long, required): ID of the storybook being reviewed
- `rating` (Integer, required): Rating on a scale of 1-5
- `reviewText` (String, optional): Review text/commentary

**Success Response (200 OK):**
```json
{
  "reviewId": 1,
  "userName": "John Doe",
  "rating": 5,
  "reviewText": "This storybook is absolutely amazing!",
  "createdAt": "2024-03-26T10:30:00"
}
```

**Error Response (400 Bad Request):**
```json
{
  "error": "Rating must be between 1 and 5"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/storybooks/reviews/add \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "storyBookId": 1,
    "rating": 5,
    "reviewText": "Excellent book!"
  }'
```

---

### 2. Fetch Reviews
Retrieve all reviews for a specific storybook. **Requires authentication.**

**Endpoint:** `POST /storybooks/reviews/fetch`

**Authentication**: Required (Bearer token)

**Request Body:**
```json
{
  "storyBookId": 1
}
```

**Request Parameters:**
- `storyBookId` (Long, required): ID of the storybook to fetch reviews for

**Success Response (200 OK):**
```json
[
  {
    "reviewId": 1,
    "userName": "John Doe",
    "rating": 5,
    "reviewText": "This storybook is absolutely amazing!",
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

**Error Response (404 Not Found):**
```json
{
  "error": "Storybook not found with ID: 999"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/storybooks/reviews/fetch \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "storyBookId": 1
  }'
```

---

### 3. Get Average Rating
Retrieve average rating and total review count for a storybook. **Requires authentication.**

**Endpoint:** `GET /storybooks/reviews/rating/{storyBookId}`

**Authentication**: Required (Bearer token)

**Path Parameters:**
- `storyBookId` (Long, required): ID of the storybook

**Success Response (200 OK):**
```json
{
  "storyBookId": 1,
  "averageRating": 4.5,
  "totalReviews": 2
}
```

**Error Response (404 Not Found):**
```json
{
  "error": "Storybook not found with ID: 999"
}
```

**cURL Example:**
```bash
curl -X GET http://localhost:8080/storybooks/reviews/rating/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Database Schema

```sql
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    storybook_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    review_text LONGTEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_reviews_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_storybook_id FOREIGN KEY (storybook_id) REFERENCES storybooks(id) ON DELETE CASCADE,
    
    INDEX idx_storybook_id (storybook_id),
    INDEX idx_user_id (user_id)
);
```

---

## Business Rules

1. **Rating Validation**: Ratings must be between 1 and 5 (inclusive)
2. **User Verification**: User must exist in the database
3. **Storybook Verification**: Storybook must exist in the database
4. **Cascade Delete**: If a user or storybook is deleted, all related reviews are automatically deleted
5. **Average Rating**: Calculated from all reviews for a storybook. Returns 0.0 if no reviews exist

---

## Implementation Location

### Backend Components:
- **Entity**: `src/main/java/com/company/storybook/entity/Review.java`
- **Repository**: `src/main/java/com/company/storybook/repository/ReviewRepository.java`
- **Service Interface**: `src/main/java/com/company/storybook/service/ReviewService.java`
- **Service Implementation**: `src/main/java/com/company/storybook/service/ReviewServiceImpl.java`
- **Controller**: `src/main/java/com/company/storybook/controller/StoryBookUserController.java`

### DTOs:
- **ReviewSubmitRequest**: `src/main/java/com/company/storybook/dto/ReviewSubmitRequest.java`
- **FetchReviewsRequest**: `src/main/java/com/company/storybook/dto/FetchReviewsRequest.java`
- **ReviewResponse**: `src/main/java/com/company/storybook/dto/ReviewResponse.java`
- **AverageRatingResponse**: `src/main/java/com/company/storybook/dto/AverageRatingResponse.java`

### Database:
- **Migration Script**: `src/main/resources/REVIEWS_MIGRATION.sql`
- **Main Script**: `src/main/resources/TableScripts.sql`

---

## HTTP Status Codes

- **200 OK**: Request successful
- **400 Bad Request**: Invalid request data (e.g., invalid rating)
- **404 Not Found**: Resource not found (e.g., user or storybook)
- **500 Internal Server Error**: Server error

---

## Notes

- Reviews are indexed on `storybook_id` and `user_id` for query optimization
- All reviews cascade delete when a user or storybook is deleted
- Average rating is calculated in real-time from the database
- Created timestamp is automatically set to current time
