# API Endpoints Summary - StoryBook Backend

## Updated Controller Review & API Documentation

### 📌 Latest Changes (OTP Registration Implementation)

**New Endpoints Added:**
- ✅ `POST /users/register` - Send OTP for registration (202 ACCEPTED)
- ✅ `POST /users/verify-registration` - Verify OTP and complete registration (201 CREATED)

**Updated in Postman Collection:**
- ✅ Replaced old registration flow with OTP-based registration
- ✅ Added comprehensive test scripts with assertions
- ✅ Auto-saving of tokens to collection variables
- ✅ Full endpoint documentation with descriptions

---

## 🔐 Authentication Endpoints (`/users`)

| # | Endpoint | Method | Status | Description |
|---|----------|--------|--------|-------------|
| 1 | `/users/register` | POST | 202 | **Send OTP** - Validate registration details & send OTP email |
| 2 | `/users/verify-registration` | POST | 201 | **Verify OTP** - Complete registration after OTP verification |
| 3 | `/users/login` | POST | 200 | Login and receive JWT token |
| 4 | `/users/profile` | GET | 200 | Get authenticated user's profile |
| 5 | `/users/change-password` | POST | 200 | Change user password |
| 6 | `/users/change-username` | POST | 200 | Change user's display name |
| 7 | `/users/logout` | POST | 200 | Logout and invalidate token |

**Authentication Required:** Endpoints 4-7 require `Authorization: Bearer <token>` header

---

## 🛡️ Admin Endpoints (`/admin`)

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/admin/login` | POST | ❌ | Admin login |
| `/admin/logout` | POST | ✅ | Admin logout |
| `/admin/storybooks` | POST | ✅ | Create storybook |
| `/admin/storybooks/{id}/price` | PUT | ✅ | Update storybook price |
| `/admin/authors` | POST | ✅ | Create author |
| `/admin/authors/{id}` | PUT/DELETE | ✅ | Update/delete author |
| `/admin/categories` | POST | ✅ | Create category |
| `/admin/categories/{id}` | PUT/DELETE | ✅ | Update/delete category |

---

## 🛒 Shopping Endpoints

### Cart (`/cart`)
| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/cart` | GET | ✅ | Get user's cart |
| `/cart/add` | POST | ✅ | Add storybook to cart |
| `/cart/items/{id}` | DELETE | ✅ | Remove item from cart |

### Library (`/library`)
| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/library` | GET | ✅ | Get user's purchased storybooks |
| `/library/owns/{id}` | GET | ✅ | Check if user owns storybook |
| `/library/stats` | GET | ✅ | Get library statistics |

### Wallet (`/wallet`)
| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/wallet/balance` | GET | ✅ | Get wallet balance |
| `/wallet/checkout` | POST | ✅ | Checkout from cart |

---

## 📚 Storybook Endpoints (`/storybooks`)

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/storybooks` | GET | ❌ | Get all storybooks |
| `/storybooks/search` | GET | ❌ | Search storybooks (query: keyword) |
| `/storybooks/{id}` | GET | ❌ | Get storybook by ID |

---

## 📝 Postman Collection Updates

### New Test Requests Added:
1. **1️⃣ Send OTP for Registration** (with status code assertions)
2. **2️⃣ Verify OTP & Complete Registration** (with status code assertions)
3. **User Login** (saves token automatically)
4. **Get User Profile** (uses saved token)
5. **Change Password** (uses saved token)
6. **Change Username** (uses saved token)
7. **User Logout** (invalidates token)
8. **Admin Login** (saves admin token)
9. **Admin Logout** (invalidates admin token)

### Features:
- ✅ **Automatic Token Saving** - Login endpoints auto-save JWT to `{{userToken}}`
- ✅ **Test Scripts** - Each endpoint has assertions checking response status
- ✅ **Collection Variables** - `baseUrl`, `userToken`, `adminToken` pre-configured
- ✅ **Descriptions** - Each endpoint has detailed documentation
- ✅ **Error Scenarios** - Example requests for testing error cases

---

## 🧪 Testing Workflow

### Step 1: OTP Registration
```bash
POST /users/register
→ 202 ACCEPTED (OTP sent to email)
```

### Step 2: Verify OTP
```bash
POST /users/verify-registration  
→ 201 CREATED (Account created)
```

### Step 3: Login
```bash
POST /users/login
→ 200 OK (Token saved to {{userToken}})
```

### Step 4: Use Protected Endpoints
```bash
GET /users/profile
Authorization: Bearer {{userToken}}
→ 200 OK (User data returned)
```

---

## 🔄 Complete Request/Response Examples

### Example 1: OTP Registration (Step 1)
```
POST http://localhost:1234/users/register
Content-Type: application/json

REQUEST:
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "Password@123",
  "confirmPassword": "Password@123"
}

RESPONSE: 202 ACCEPTED
"OTP sent successfully to your email. Please verify within 10 minutes."
```

### Example 2: Verify OTP (Step 2)
```
POST http://localhost:1234/users/verify-registration
Content-Type: application/json

REQUEST:
{
  "email": "john@example.com",
  "otp": "123456",
  "registerRequest": {
    "name": "John Doe",
    "email": "john@example.com",
    "password": "Password@123",
    "confirmPassword": "Password@123"
  }
}

RESPONSE: 201 CREATED
"User Registration Successful"

CREATES:
- User account (emailVerified = true)
- Wallet (1000 RS balance)
- Shopping Cart (empty)
```

### Example 3: Login
```
POST http://localhost:1234/users/login
Content-Type: application/json

REQUEST:
{
  "email": "john@example.com",
  "password": "Password@123"
}

RESPONSE: 200 OK
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE2MjM1MjU1MDB9...
```

---

## ✅ Postman Collection Changes Summary

| Change | Status | Details |
|--------|--------|---------|
| Added OTP registration endpoints | ✅ | 2 new endpoints with full test scripts |
| Updated authentication flow | ✅ | OTP-based registration now primary flow |
| Added token auto-save | ✅ | `{{userToken}}` and `{{adminToken}}` auto-populated |
| Added response assertions | ✅ | Test scripts verify status codes and responses |
| Added descriptions | ✅ | Each endpoint has detailed documentation |
| Added error scenarios | ✅ | Example requests for testing failure cases |
| Updated base URL | ✅ | Uses `{{baseUrl}}` variable for flexibility |

---

## 📊 Error Response Examples

### Invalid Email Format
```json
{
  "error": "Invalid email format. Please enter a valid email address."
}
```

### Password Mismatch
```json
{
  "error": "Confirm password does not match password"
}
```

### OTP Expired
```json
{
  "error": "OTP has expired. Please request a new one."
}
```

### Max OTP Attempts
```json
{
  "error": "Maximum OTP verification attempts exceeded. Please request a new OTP."
}
```

---

## 🔑 API Key/Token Usage

**Format:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Example Header:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE2MjM1MjU1MDB9...
```

**Token Validity:** 10 hours from issue time

---

## 📱 Postman Usage Instructions

1. **Import Collection:**
   - Open Postman
   - Click Import → Select `Postman_Collection.json`
   - Collection loads with all endpoints

2. **Configure Variables:**
   - Default `baseUrl`: `http://localhost:1234`
   - Change if using different host/port

3. **Test OTP Flow:**
   - Run "1️⃣ Send OTP" → Get OTP from email
   - Run "2️⃣ Verify OTP" → Replace with actual OTP
   - Run "User Login" → Token auto-saved
   - Run other endpoints → Token used automatically

4. **Monitor Responses:**
   - Check "Tests" tab for assertions
   - View response in "Body" tab
   - Check "Headers" for status codes

---

## 🎯 Complete Testing Checklist

- [ ] Send OTP with valid details (202)
- [ ] Send OTP with invalid email (400)
- [ ] Send OTP with weak password (400)
- [ ] Verify OTP with correct code (201)
- [ ] Verify OTP with wrong code (400)
- [ ] Verify OTP after expiry (400)
- [ ] Login with correct credentials (200)
- [ ] Login with wrong password (400)
- [ ] Access protected endpoint with token (200)
- [ ] Access protected endpoint without token (401)
- [ ] Logout and invalidate token (200)
- [ ] Try to use token after logout (401)
- [ ] Admin login (200)
- [ ] Admin create storybook (201)
- [ ] Admin logout (200)

