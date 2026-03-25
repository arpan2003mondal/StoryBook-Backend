# OTP Registration & Testing Guide

## 📋 Table of Contents
1. [OTP Registration Flow](#otp-registration-flow)
2. [API Endpoints](#api-endpoints)
3. [Testing in Postman](#testing-in-postman)
4. [Error Scenarios](#error-scenarios)
5. [Troubleshooting](#troubleshooting)

---

## 🔄 OTP Registration Flow

### Step-by-Step Process

```
┌─────────────────────────────────────────────────────────────┐
│ STEP 1: Send Registration Details                            │
├─────────────────────────────────────────────────────────────┤
│ POST /users/register                                          │
│ Response: 202 ACCEPTED                                        │
│ Action: OTP sent to email, stored in database (10 min expiry)│
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│ STEP 2: User receives OTP in Email                           │
├─────────────────────────────────────────────────────────────┤
│ Email contains: 6-digit OTP code                             │
│ Validity: 10 minutes from generation                         │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│ STEP 3: Verify OTP & Complete Registration                  │
├─────────────────────────────────────────────────────────────┤
│ POST /users/verify-registration                              │
│ Request: Email, OTP, Registration Details                    │
│ Response: 201 CREATED (User account created)                │
│ Action: Create User, Wallet (1000 RS), Cart                 │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│ STEP 4: User Can Now Login                                  │
├─────────────────────────────────────────────────────────────┤
│ POST /users/login                                            │
│ Request: Email, Password                                     │
│ Response: 200 OK with JWT Token                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔌 API Endpoints

### 1. Send OTP for Registration
```bash
POST /users/register
Content-Type: application/json

Request Body:
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "Password@123",
  "confirmPassword": "Password@123"
}

Response: 202 ACCEPTED
{
  "message": "OTP sent successfully to your email. Please verify within 10 minutes."
}

Validation Rules:
- Name: Must start with uppercase, min 3 characters
- Email: Valid email format
- Password: 8+ chars, uppercase, lowercase, digit, special char (@$!%*?&)
- Confirm Password: Must match password
```

### 2. Verify OTP & Register
```bash
POST /users/verify-registration
Content-Type: application/json

Request Body:
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

Response: 201 CREATED
{
  "message": "User Registration Successful"
}

Success Creates:
- User account (email_verified = true)
- Wallet (1000 RS balance)
- Shopping Cart (empty)
```

### 3. User Login
```bash
POST /users/login
Content-Type: application/json

Request Body:
{
  "email": "john@example.com",
  "password": "Password@123"
}

Response: 200 OK
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Token Valid For: 10 hours
Use in headers: Authorization: Bearer <token>
```

---

## 🧪 Testing in Postman

### Import Collection
1. Open Postman
2. Click **Import** → **Upload Files**
3. Select `Postman_Collection.json`
4. Collection imported with all endpoints

### Testing OTP Registration Flow

#### Test 1: Send OTP
```
Collection → Authentication → 1️⃣ Send OTP for Registration
- Edit email to test with
- Click Send
- Expected: 202 ACCEPTED
- Check email for OTP code (in real scenario)
```

#### Test 2: Verify OTP
```
Collection → Authentication → 2️⃣ Verify OTP & Complete Registration
- Replace OTP value with actual OTP from email
- Click Send
- Expected: 201 CREATED
- Response: "User Registration Successful"
```

#### Test 3: Login
```
Collection → Authentication → User Login
- Use credentials from registration
- Click Send
- Expected: 200 OK
- Response: JWT Token (auto-saved to {{userToken}})
```

#### Test 4: Access Protected Routes
```
Collection → Any authenticated endpoint (e.g., Get User Profile)
- Automatically uses {{userToken}} from login
- Expected: 200 OK with user data
```

---

## ❌ Error Scenarios

### Scenario 1: Invalid Registration Details

**Request:**
```json
{
  "name": "john",  // ❌ Lowercase start (invalid)
  "email": "invalid-email",  // ❌ Invalid format
  "password": "weak",  // ❌ Too short, no special char
  "confirmPassword": "different"  // ❌ Doesn't match
}
```

**Response: 400 Bad Request**
```json
{
  "errors": {
    "name": "Username must start with uppercase (min 3 chars)",
    "email": "Invalid email format",
    "password": "Must be 8+ chars with uppercase, lowercase, digit, special char",
    "confirmPassword": "Password does not match"
  }
}
```

---

### Scenario 2: Email Already Registered

**Request:**
```json
{
  "name": "John Doe",
  "email": "john@storybook.com",  // ❌ Already exists
  "password": "Password@123",
  "confirmPassword": "Password@123"
}
```

**Response: 400 Bad Request**
```
"User already exists"
```

---

### Scenario 3: Invalid OTP

**Request:**
```json
{
  "email": "john@example.com",
  "otp": "000000",  // ❌ Wrong OTP
  "registerRequest": { ... }
}
```

**Response: 400 Bad Request**
```
"Invalid OTP. Please try again. Be careful, you have limited attempts."
```

**Max Attempts:** 5 wrong attempts before OTP invalidation

---

### Scenario 4: OTP Expired

**If waiting >10 minutes before verification:**

**Request:**
```json
{
  "email": "john@example.com",
  "otp": "123456",  // ❌ OTP expired
  "registerRequest": { ... }
}
```

**Response: 400 Bad Request**
```
"OTP has expired. Please request a new one."
```

**Action:** Resend OTP by calling Step 1 again

---

### Scenario 5: OTP Email Mismatch

**Request:**
```json
{
  "email": "john@example.com",  // ❌ OTP was sent to different email
  "otp": "123456",
  "registerRequest": {
    "email": "different@example.com"
  }
}
```

**Response: 400 Bad Request**
```
"Email address does not match. Please enter the correct email."
```

---

## 🔧 Troubleshooting

### Issue 1: "OTP not found" or "No OTP found for this email"
**Cause:** OTP not sent or already expired
**Solution:**
1. Resend OTP by calling Step 1 again
2. Use same email address in verification
3. Verify within 10 minutes

### Issue 2: "Maximum OTP verification attempts exceeded"
**Cause:** Entered wrong OTP more than 5 times
**Solution:**
1. Request new OTP (call Step 1)
2. Be more careful with OTP entry
3. Copy-paste OTP from email to avoid typos

### Issue 3: "Email already exists"
**Cause:** Trying to register with existing email
**Solution:**
1. Use a different email address
2. OR login with that email if already registered

### Issue 4: Email not received
**Check:**
1. Verify email configuration in `application.properties`
2. Ensure Gmail App Password is correct
3. Check spam/junk folder
4. Enable "Less secure apps" if using Gmail (not recommended)

### Issue 5: "Failed to send OTP"
**Cause:** Email service misconfigured
**Check:**
- Mail server settings in `application.properties`
- Gmail SMTP credentials
- Internet connectivity
- Firewall/port 587 access

---

## 📊 Complete Testing Workflow

### For Complete Feature Testing:

```bash
# 1. Send OTP
POST /users/register
{
  "name": "Test User",
  "email": "test@example.com",
  "password": "TestPass@123",
  "confirmPassword": "TestPass@123"
}
→ 202 ACCEPTED

# 2. Get OTP from email (check inbox)

# 3. Verify OTP & Register
POST /users/verify-registration
{
  "email": "test@example.com",
  "otp": "XXXXXX",  // From email
  "registerRequest": { ... }
}
→ 201 CREATED

# 4. Login
POST /users/login
{
  "email": "test@example.com",
  "password": "TestPass@123"
}
→ 200 OK (JWT Token)

# 5. Use token for other operations
GET /users/profile
Authorization: Bearer <token>
→ 200 OK (User profile)

# 6. Additional operations
- Add to cart
- Browse library
- Check wallet balance
- Etc.
```

---

## 🔐 Security Notes

✅ **Implemented Security:**
- OTP expires in 10 minutes
- Max 5 wrong attempts
- Passwords hashed with BCrypt
- JWT tokens (10-hour validity)
- Email verification required
- HTTPS recommended in production

⚠️ **Best Practices:**
- Never hardcode credentials
- Use environment variables for sensitive data
- Implement rate limiting in production
- Add CORS restrictions
- Use HTTPS only
- Monitor failed login attempts

---

## 📝 Notes

- OTP is 6 digits (000000 - 999999)
- Email service requires Gmail App Password (not regular password)
- All messages are configurable in `messages.properties`
- Database auto-creates `otp_verification` table on first run

---

## 🆘 Support

For issues or questions:
1. Check `messages.properties` for error message keys
2. Review application logs for detailed errors
3. Test individual endpoints in isolation
4. Verify database connection and OTP table exists

