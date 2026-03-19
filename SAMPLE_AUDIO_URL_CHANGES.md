# Sample Audio URL Feature Implementation

## Overview
Added a new `sampleAudioUrl` field to the Storybook entity to store preview audio URLs that attract users before they purchase the full audiobook.

## Changes Made

### 1. **Entity Layer** 
**File:** `src/main/java/com/company/storybook/entity/Storybook.java`
- Added new column: `sampleAudioUrl` (VARCHAR 500)
- Database field name: `sample_audio_url`
- Annotation: `@Column(name = "sample_audio_url", length = 500)`

```java
@Column(name = "sample_audio_url", length = 500)
private String sampleAudioUrl;
```

### 2. **DTO Layer - Request**
**File:** `src/main/java/com/company/storybook/dto/StorybookRequest.java`
- Added optional field: `sampleAudioUrl`
- Used when creating/updating storybooks from API requests

```java
private String sampleAudioUrl;
```

### 3. **DTO Layer - Response**
**File:** `src/main/java/com/company/storybook/dto/StorybookResponse.java`
- Added field: `sampleAudioUrl`
- Returned in REST API responses when fetching storybook details

```java
private String sampleAudioUrl;
```

### 4. **DTO Layer - Cart**
**File:** `src/main/java/com/company/storybook/dto/CartItemDTO.java`
- Added field: `sampleAudioUrl`
- Included in cart items to show preview audio in user's shopping cart
- Also included `audioUrl` field for consistency

```java
private String audioUrl;
private String sampleAudioUrl;
```

### 5. **Service Layer - Admin Service**
**File:** `src/main/java/com/company/storybook/service/AdminServiceImpl.java`

#### addStorybook() method:
- Updated to set `sampleAudioUrl` when creating new storybooks
```java
storybook.setSampleAudioUrl(request.getSampleAudioUrl());
```

#### toResponse() method:
- Updated to include `sampleAudioUrl` in response DTOs
```java
response.setSampleAudioUrl(storybook.getSampleAudioUrl());
```

### 6. **Service Layer - Cart Service**
**File:** `src/main/java/com/company/storybook/service/CartServiceImpl.java`

#### mapStorybookToResponse() method:
- Updated to include `sampleAudioUrl` when mapping Storybook entities
```java
response.setSampleAudioUrl(storybook.getSampleAudioUrl());
```

#### mapCartItemToDTO() method:
- Updated to include both `audioUrl` and `sampleAudioUrl` in cart items
```java
dto.setAudioUrl(cartItem.getStorybook().getAudioUrl());
dto.setSampleAudioUrl(cartItem.getStorybook().getSampleAudioUrl());
```

### 7. **Controller Layer - Wallet Controller**
**File:** `src/main/java/com/company/storybook/controller/WalletController.java`

#### mapStorybookToResponse() method:
- Updated helper method to include `sampleAudioUrl` in user library responses
```java
response.setSampleAudioUrl(storybook.getSampleAudioUrl());
```

### 8. **Database Schema**
**File:** `src/main/resources/TableScripts.sql`

#### Updated storybooks table:
```sql
CREATE TABLE storybooks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    author_id BIGINT,
    category_id BIGINT,
    price DECIMAL(10,2),
    audio_url VARCHAR(500),
    sample_audio_url VARCHAR(500),
    cover_image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (author_id) REFERENCES authors(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
```

#### Updated dummy data:
- All storybook INSERT statements now include `sample_audio_url` column
- Sample URLs follow pattern: `https://example.com/samples/{storybook_id}_sample.mp3`
- Example: `'https://example.com/samples/hp1_sample.mp3'`

## Usage

### Adding a new Storybook with Sample Audio
**POST** `/api/storybooks`
```json
{
    "title": "Book Title",
    "description": "Book Description",
    "authorId": 1,
    "categoryId": 1,
    "price": 9.99,
    "audioUrl": "https://example.com/audio/book.m4b",
    "sampleAudioUrl": "https://example.com/samples/book_sample.mp3",
    "coverImageUrl": "https://example.com/images/cover.jpg"
}
```

### Response Structure
All StorybookResponse and CartItemDTO now include:
```json
{
    "id": 1,
    "title": "The Sorcerer's Stone",
    "description": "...",
    "price": 9.99,
    "audioUrl": "https://archive.org/download/hp1_sorcerers_2018/...",
    "sampleAudioUrl": "https://example.com/samples/hp1_sample.mp3",
    "coverImageUrl": "https://images.unsplash.com/...",
    "authorId": 1,
    "authorName": "J.K. Rowling",
    "categoryId": 1,
    "categoryName": "Fantasy",
    "createdAt": "2024-01-01T10:00:00"
}
```

## Benefits
1. **User Experience**: Users can preview sample audio before purchasing
2. **Conversion**: Preview audio increases purchase motivation
3. **Cart Display**: Sample audio visible in shopping cart
4. **Consistency**: Available in all API responses (browse, cart, user library)

## Database Migration
To apply these changes to existing databases:
```sql
ALTER TABLE storybooks 
ADD COLUMN sample_audio_url VARCHAR(500) AFTER audio_url;
```

## Notes
- `sampleAudioUrl` is **optional** when creating storybooks (can be null)
- Recommended URL structure: `https://example.com/samples/{bookId}_sample.mp3`
- CDN recommended for optimal performance
- Sample audio file should be much shorter than full audio (e.g., first 2-3 minutes)
