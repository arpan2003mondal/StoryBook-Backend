# Wishlist API Documentation

## Overview
The Wishlist API provides functionality for users to save their favorite storybooks for later purchase or reference. Users can add storybooks to their wishlist, view their wishlist, and remove items when needed.

## Features
- **Add to Wishlist**: Add storybooks to your personal wishlist
- **View Wishlist**: Retrieve all items in your wishlist with detailed information
- **Remove from Wishlist**: Remove storybooks from your wishlist

## Authentication
All wishlist endpoints require Bearer token authentication. Users must be logged in to access wishlist operations.

**Header Format:**
```
Authorization: Bearer <your_jwt_token>
```

## API Endpoints

### 1. Add to Wishlist
Add a storybook to the user's wishlist.

**Endpoint:** `POST /wishlist/add`

**Authentication:** Required (Bearer token)

**Request Body:**
```json
{
  "storyBookId": 1
}
```

**Success Response:** `201 CREATED`
```json
{
  "message": "Storybook added to wishlist successfully."
}
```

**Error Responses:**
- `400 BAD REQUEST` - Storybook ID is invalid or storybook not found
- `400 BAD REQUEST` - This storybook is already in your wishlist
- `400 BAD REQUEST` - User not found

**Example cURL:**
```bash
curl -X POST http://localhost:1234/wishlist/add \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"storyBookId": 1}'
```

---

### 2. Get User Wishlist
Retrieve all items in the user's wishlist.

**Endpoint:** `GET /wishlist`

**Authentication:** Required (Bearer token)

**Success Response:** `200 OK`
```json
{
  "wishlistItems": [
    {
      "wishlistId": 1,
      "storybookId": 1,
      "title": "Harry Potter and the Philosopher's Stone",
      "description": "The first book in the Harry Potter series",
      "authorName": "J.K. Rowling",
      "categoryName": "Fantasy",
      "price": 9.99,
      "audioUrl": "https://example.com/audio/harry1.m4b",
      "sampleAudioUrl": "https://example.com/sample/harry1_sample.mp3",
      "coverImageUrl": "https://example.com/cover/harry1.jpg",
      "addedAt": "2026-03-27T10:30:00"
    },
    {
      "wishlistId": 2,
      "storybookId": 2,
      "title": "The Lord of the Rings",
      "description": "A legendary fantasy adventure",
      "authorName": "J.R.R. Tolkien",
      "categoryName": "Fantasy",
      "price": 14.99,
      "audioUrl": "https://example.com/audio/lotr.m4b",
      "sampleAudioUrl": "https://example.com/sample/lotr_sample.mp3",
      "coverImageUrl": "https://example.com/cover/lotr.jpg",
      "addedAt": "2026-03-27T11:15:00"
    }
  ],
  "totalItems": 2
}
```

**Error Responses:**
- `400 BAD REQUEST` - User not found
- `401 UNAUTHORIZED` - Invalid or missing authentication token

**Response Fields:**
- `wishlistItems[]` - Array of wishlist items
  - `wishlistId` - Unique wishlist item ID
  - `storybookId` - ID of the storybook
  - `title` - Storybook title
  - `description` - Storybook description
  - `authorName` - Author name
  - `categoryName` - Category/Genre name
  - `price` - Storybook price
  - `audioUrl` - Full audio file URL
  - `sampleAudioUrl` - Sample audio file URL
  - `coverImageUrl` - Cover image URL
  - `addedAt` - Timestamp when added to wishlist
- `totalItems` - Total number of items in wishlist

**Example cURL:**
```bash
curl -X GET http://localhost:1234/wishlist \
  -H "Authorization: Bearer <token>"
```

---

### 3. Remove from Wishlist
Remove a storybook from the user's wishlist.

**Endpoint:** `POST /wishlist/remove`

**Authentication:** Required (Bearer token)

**Request Body:**
```json
{
  "storyBookId": 1
}
```

**Success Response:** `200 OK`
```json
{
  "message": "Storybook removed from wishlist successfully."
}
```

**Error Responses:**
- `400 BAD REQUEST` - This storybook is not in your wishlist
- `400 BAD REQUEST` - User not found
- `401 UNAUTHORIZED` - Invalid or missing authentication token

**Example cURL:**
```bash
curl -X POST http://localhost:1234/wishlist/remove \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"storyBookId": 1}'
```

---

## Error Handling

### Common Error Messages

| Error | HTTP Code | Description |
|-------|-----------|-------------|
| User not found | 400 | The authenticated user does not exist |
| Storybook not found | 400 | The requested storybook does not exist |
| Already in wishlist | 400 | The storybook is already in user's wishlist |
| Item not in wishlist | 400 | Trying to remove an item that's not in wishlist |
| Unauthorized | 401 | Missing or invalid authentication token |

---

## Usage Examples

### Complete Wishlist Workflow

**1. Add a storybook to wishlist:**
```bash
curl -X POST http://localhost:1234/wishlist/add \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{"storyBookId": 5}'
```

**2. View wishlist:**
```bash
curl -X GET http://localhost:1234/wishlist \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**3. Remove item from wishlist:**
```bash
curl -X POST http://localhost:1234/wishlist/remove \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{"storyBookId": 5}'
```

---

## Business Rules

1. **Authentication Required**: All wishlist operations require valid JWT authentication
2. **Unique Items**: Each storybook can only appear once in a user's wishlist
3. **Duplicate Prevention**: Adding an already-wishlisted item returns an error
4. **User Isolation**: Users can only access their own wishlist
5. **Item Information**: Wishlist items include complete storybook details for reference

---

## Database Schema

### Wishlist Table
```sql
CREATE TABLE wishlist (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  storybook_id BIGINT NOT NULL,
  added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (storybook_id) REFERENCES storybooks(id) ON DELETE CASCADE,
  UNIQUE KEY unique_user_storybook (user_id, storybook_id)
);
```

---

## Integration Notes

### With Shopping Cart
- Wishlisted items can be added to cart before checkout
- Wishlist and cart are separate systems for user flexibility

### With User Library
- Wishlisted items that are already purchased (in library) can still be viewed
- Users can wishlist items they've already purchased for future reference

---

## Related Documentation
- [API Endpoints Summary](API_ENDPOINTS_SUMMARY.md)
- [Authentication Documentation](https://docs.storybook.api/auth)
- [Postman Collection Guide](POSTMAN_TESTING_GUIDE.md)

---

## Testing

The Postman collection includes the following wishlist test cases:
- Add to Wishlist (single and multiple items)
- Get User Wishlist
- Remove from Wishlist (success and error cases)

See [POSTMAN_TESTING_GUIDE.md](POSTMAN_TESTING_GUIDE.md) for detailed testing instructions.
