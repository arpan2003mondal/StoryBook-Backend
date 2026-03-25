# Environment Configuration Setup

## Overview
This project uses environment variables to manage sensitive configuration data (database credentials, email passwords, etc.). The `.env` file is used locally for development and is **NEVER committed to version control**.

---

## Setup Instructions

### 1. Copy `.env.example` to `.env`
Create a local `.env` file from the template:
```bash
cp .env.example .env
```

### 2. Update `.env` with Actual Values
Edit the `.env` file and replace placeholders with your actual configuration:

```env
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=storybookdb
DB_USERNAME=root
DB_PASSWORD=your_actual_db_password

# Mail Configuration (Gmail with App Password)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your_gmail_app_password
MAIL_FROM=noreply@storybook.com

# Application Configuration
APP_NAME=StoryBook
APP_PORT=1234

# OTP Configuration
OTP_EXPIRY_MINUTES=10
```

### 3. Restart Your Application
After updating `.env`, restart the Spring Boot application for changes to take effect.

---

## File Descriptions

### `.env`
- **Location**: Root of project
- **Should I commit this?** ❌ **NO** - This file contains sensitive credentials
- **Purpose**: Stores actual values for local development
- **Default behavior**: Automatically loaded by `dotenv-java` library on application startup

### `.env.example`
- **Location**: Root of project
- **Should I commit this?** ✅ **YES** - This file contains only placeholders
- **Purpose**: Template for developers to create their own `.env` file
- **Usage**: New developers copy this file to `.env` and fill in their own values

### `application.properties`
- **Location**: `src/main/resources/`
- **Purpose**: Spring configuration using environment variable references
- **Format**: `${ENV_VAR_NAME:default_value}`
  - Reads from `.env` file or system environment variables
  - Falls back to default value if variable is not set

### `EnvConfig.java`
- **Location**: `src/main/java/com/company/storybook/config/`
- **Purpose**: Loads `.env` file on application startup
- **Provides**: Helper methods to access environment variables with type safety

---

## Environment Variables Reference

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_HOST` | Database host | `localhost` |
| `DB_PORT` | Database port | `3306` |
| `DB_NAME` | Database name | `storybookdb` |
| `DB_USERNAME` | Database username | `root` |
| `DB_PASSWORD` | Database password | `secure_password` |
| `MAIL_HOST` | SMTP server host | `smtp.gmail.com` |
| `MAIL_PORT` | SMTP server port | `587` |
| `MAIL_USERNAME` | Email sender address | `your-email@gmail.com` |
| `MAIL_PASSWORD` | Email app password | `xxxx xxxx xxxx xxxx` |
| `MAIL_FROM` | From email address | `noreply@storybook.com` |
| `APP_NAME` | Application name | `StoryBook` |
| `APP_PORT` | Application port | `1234` |
| `OTP_EXPIRY_MINUTES` | OTP validity duration | `10` |

---

## How It Works

1. **Application Startup**: Java loads `dotenv-java` library which reads `.env` file
2. **Property Resolution**: Spring Boot resolves `${ENV_VAR_NAME}` patterns in `application.properties`
3. **Fallback Logic**: If environment variable is missing, default value is used
4. **Type Safety**: `EnvConfig.java` provides methods for programmatic access

### Property Resolution Hierarchy
```
1. Environment Variable (.env file or system)
2. Default Value (from application.properties)
3. Error (if required and not found)
```

---

## Security Best Practices

✅ **DO:**
- Never commit `.env` file to version control
- Add `.env` to `.gitignore` (already configured)
- Use strong, unique passwords for database and email
- Store `.env.example` in version control (contains only placeholders)
- Rotate credentials regularly
- Use environment-specific `.env` files for staging/production

❌ **DON'T:**
- Hardcode sensitive values in code or properties
- Share `.env` file across team members (each developer has their own)
- Commit actual credentials to Git
- Use weak or default passwords
- Store `.env` in public repositories

---

## Troubleshooting

### `.env` file not being loaded
- Ensure `.env` file exists in project root directory
- Verify file name is exactly `.env` (case-sensitive on Linux/Mac)
- Check that `EnvConfig.java` bean is being created

### Properties showing as `null` or default values
- Verify environment variable is set in `.env` file
- Check for typos in variable names (case-sensitive)
- Ensure application was restarted after `.env` changes
- Check `application.properties` for correct `${VAR_NAME}` syntax

### Database connection fails
```
Error: Access denied for user 'root'@'localhost'
```
- Verify `DB_USERNAME` and `DB_PASSWORD` in `.env` file
- Check MySQL is running
- Confirm database `DB_NAME` exists

### Email sending fails
```
Error: 535 5.7.45 Authentication failed
```
- Verify `MAIL_USERNAME` is a Gmail address
- Verify `MAIL_PASSWORD` is an App Password (not regular password)
- Check Gmail 2FA is enabled
- Ensure app is added to Gmail "Less secure app access" or use App Password

---

## Production Deployment

For production environments:

1. **Set environment variables on server:**
   ```bash
   export DB_PASSWORD="prod_password"
   export MAIL_PASSWORD="prod_app_password"
   # ... other variables
   ```

2. **Or use Docker environment variables:**
   ```dockerfile
   ENV DB_PASSWORD=prod_password
   ENV MAIL_PASSWORD=prod_app_password
   ```

3. **Never commit `.env` to production**
   - Use CI/CD secrets management
   - Use cloud provider's secrets store (AWS Secrets Manager, Azure Key Vault, etc.)
   - Ensure credentials are rotated regularly

---

## Related Files
- `.env` - Local environment variables (gitignored)
- `.env.example` - Template for environment variables
- `application.properties` - Spring configuration with env var references
- `EnvConfig.java` - Environment configuration loader
- `.gitignore` - Git ignore rules (includes `.env`)

---

**Last Updated**: March 26, 2026
**Dependency**: dotenv-java 3.0.0
