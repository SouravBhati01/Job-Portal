# Job Portal System - Setup & Run Guide

## Prerequisites

Before running the application, ensure you have the following installed:

### 1. **Java 17 or higher**
Download from: https://www.oracle.com/java/technologies/downloads/#java17

Verify installation:
```bash
java -version
```

### 2. **Maven 3.8+**
Download from: https://maven.apache.org/download.cgi

Verify installation:
```bash
mvn -version
```

### 3. **PostgreSQL 12+**
Download from: https://www.postgresql.org/download/

Verify installation:
```bash
psql --version
```

---

## Database Setup

### Create Database and User

1. **Start PostgreSQL service** (if not running):
   ```bash
   # Windows
   psql -U postgres
   
   # macOS/Linux
   sudo su - postgres
   psql
   ```

2. **Create database and user:**
   ```sql
   CREATE DATABASE job_portal_db;
   
   CREATE USER job_portal_user WITH PASSWORD 'JobPortal@2024!';
   
   GRANT ALL PRIVILEGES ON DATABASE job_portal_db TO job_portal_user;
   
   ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO job_portal_user;
   ```

3. **Exit PostgreSQL:**
   ```sql
   \q
   ```

---

## Generate JWT Secret

Before running the application, generate a strong JWT secret:

```bash
# macOS/Linux
openssl rand -base64 32

# Windows (using Git Bash or WSL)
openssl rand -base64 32

# Or use an online tool: https://randomkeygen.com/
```

Copy the generated secret (without any additional characters).

---

## Environment Variables Setup

### Option 1: Create `.env` file in project root

Create a file named `.env` in the project root directory:

```env
# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/job_portal_db
DB_USERNAME=job_portal_user
DB_PASSWORD=JobPortal@2024!

# JWT Configuration (Replace with your generated secret)
JWT_SECRET=YOUR_GENERATED_SECRET_HERE
JWT_EXPIRATION_MS=86400000

# File Storage
UPLOAD_DIR=uploads/resumes

# Spring Profiles
SPRING_PROFILES_ACTIVE=dev
```

### Option 2: Set Environment Variables (Windows)

```bash
set DB_URL=jdbc:postgresql://localhost:5432/job_portal_db
set DB_USERNAME=job_portal_user
set DB_PASSWORD=JobPortal@2024!
set JWT_SECRET=YOUR_GENERATED_SECRET_HERE
set JWT_EXPIRATION_MS=86400000
set UPLOAD_DIR=uploads/resumes
```

### Option 3: Set Environment Variables (macOS/Linux)

```bash
export DB_URL=jdbc:postgresql://localhost:5432/job_portal_db
export DB_USERNAME=job_portal_user
export DB_PASSWORD=JobPortal@2024!
export JWT_SECRET=YOUR_GENERATED_SECRET_HERE
export JWT_EXPIRATION_MS=86400000
export UPLOAD_DIR=uploads/resumes
```

---

## Running the Application

### Method 1: Using Maven (Recommended)

```bash
cd "job portal system"

# Build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

### Method 2: Using Built JAR

```bash
cd "job portal system"

# Build the project
mvn clean package

# Run the JAR file
java -jar target/job-portal-system-1.0.0.jar
```

### Method 3: Using IDE (IntelliJ IDEA or Eclipse)

1. Open the project in your IDE
2. Right-click on `JobPortalApplication.java`
3. Select "Run 'JobPortalApplication.main()'"

---

## Application URLs

Once running, the application will be available at:

- **API Base URL**: `http://localhost:8080/api`
- **Swagger/OpenAPI**: `http://localhost:8080/api/swagger-ui.html`
- **Health Check**: `http://localhost:8080/api/actuator/health`

---

## Testing the API

### 1. Register a New User

**Endpoint**: `POST /api/auth/register`

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "SecurePassword123!",
  "phone": "1234567890",
  "role": "APPLICANT"
}
```

**Response**:
```json
{
  "success": true,
  "message": "Registration successful. Welcome!",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "userId": 1,
    "email": "john@example.com",
    "fullName": "John Doe",
    "roles": ["ROLE_APPLICANT"]
  }
}
```

### 2. Login

**Endpoint**: `POST /api/auth/login`

```json
{
  "email": "john@example.com",
  "password": "SecurePassword123!"
}
```

### 3. Browse Jobs (No Authentication Required)

**Endpoint**: `GET /api/jobs`

Query Parameters:
- `keyword`: Search keyword
- `location`: Job location
- `jobType`: FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP
- `remote`: true/false
- `salaryMin`: Minimum salary
- `page`: Page number (0-indexed)
- `size`: Page size (default: 10)

### 4. Use Bearer Token for Protected Endpoints

Add the JWT token to the `Authorization` header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## Troubleshooting

### Issue: Database Connection Error

**Solution**:
1. Verify PostgreSQL is running
2. Check environment variables are set correctly
3. Verify database and user exist
4. Test connection: `psql -U job_portal_user -d job_portal_db`

### Issue: PORT 8080 Already in Use

**Solution**:
```bash
# Change port in application.properties or via environment variable
export SERVER_PORT=8081
mvn spring-boot:run
```

### Issue: JWT Secret Not Set

**Error**: "JWT secret must be configured"

**Solution**:
1. Generate a secret using `openssl rand -base64 32`
2. Set `JWT_SECRET` environment variable
3. Restart the application

### Issue: File Upload Fails

**Solution**:
1. Ensure `uploads/resumes` directory exists or is writable
2. Check `UPLOAD_DIR` environment variable is set correctly
3. Verify file size is less than 5MB
4. Verify file type is PDF or Word document

---

## CORS Configuration

The application is configured for **local development** only. For production:

Edit `src/main/java/com/jobportal/config/SecurityConfig.java`:

```java
// Change from localhost to your production domain
cfg.setAllowedOrigins(List.of(
    "https://yourdomain.com",
    "https://app.yourdomain.com"
));
```

---

## Production Deployment Checklist

- [ ] Generate a strong JWT secret (min 32 bytes)
- [ ] Update database credentials
- [ ] Change `spring.jpa.hibernate.ddl-auto` to `validate` (not `update`)
- [ ] Configure CORS with production domain
- [ ] Set up HTTPS/SSL certificate
- [ ] Enable rate limiting
- [ ] Configure logging levels
- [ ] Set up database backups
- [ ] Monitor application logs
- [ ] Configure firewall rules

---

## API Documentation

Detailed API documentation is available at:
`http://localhost:8080/api/swagger-ui.html`

---

## Support

For issues or questions:
1. Check the Swagger documentation
2. Review application logs in `logs/` directory
3. Check database connectivity
4. Verify all environment variables are set

---

**Last Updated**: 2026-07-03
**Version**: 1.0.0
