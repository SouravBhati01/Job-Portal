# 🚀 Job Portal System - Windows Quick Start Guide

## Prerequisites Installation

### 1. ✅ Java 21 (You have this!)
You already installed Java 21. Verify:
```bash
java -version
```
Expected: OpenJDK Runtime Environment Temurin-21.x.x

---

### 2. ⬇️ Install Maven 3.9+ (REQUIRED)

**Download Maven:**
1. Go to: https://maven.apache.org/download.cgi
2. Download: `apache-maven-3.9.x-bin.zip` (Binary zip archive)
3. Extract to: `C:\Program Files\apache-maven-3.9.x`

**Setup Environment Variable (Windows):**
1. Press `Win + Pause/Break` 
2. Click "Advanced system settings" 
3. Click "Environment Variables" button
4. Under "System variables", click "New":
   - Variable name: `MAVEN_HOME`
   - Variable value: `C:\Program Files\apache-maven-3.9.x`
5. Find "Path" variable, click "Edit":
   - Add new entry: `%MAVEN_HOME%\bin`
   - Click OK
6. **IMPORTANT: Restart Command Prompt/PowerShell**

**Verify Maven Installation:**
```bash
mvn -version
```
Expected: Apache Maven 3.9.x (or higher)

---

### 3. ⬇️ Install PostgreSQL 12+ (Database)

**Download PostgreSQL:**
1. Go to: https://www.postgresql.org/download/windows/
2. Download PostgreSQL 15 or 16
3. Install with default settings
4. Remember the password you set for `postgres` user

**Create Database (Open pgAdmin or psql):**
```sql
CREATE DATABASE job_portal_db;
```

**Verify Connection:**
```bash
psql -U postgres -d postgres -c "SELECT 1"
```

---

## 🔧 Quick Setup (Follow in Order)

### Step 1: Navigate to Project Directory
```bash
cd "c:\Users\Sourav Rajput\Downloads\Online Class\job portal system"
```

### Step 2: Run Setup Script (Recommended)
```bash
setup-env.bat
```
This will automatically set environment variables. Follow the prompts.

**OR Manual Environment Setup:**
Open Command Prompt and set variables:
```bash
setx DB_URL "jdbc:postgresql://localhost:5432/job_portal_db"
setx DB_USERNAME "postgres"
setx DB_PASSWORD "postgres"
setx JWT_SECRET "MySecureSecretKeyHere123456789012345"
setx JWT_EXPIRATION_MS "86400000"
```
**Then restart Command Prompt** for variables to take effect.

### Step 3: Build Project
```bash
mvn clean package -DskipTests
```
This will:
- Download all dependencies (~400MB)
- Compile all Java code
- Create an executable JAR file
- Takes 3-10 minutes first time

### Step 4: Run Application
```bash
mvn spring-boot:run
```

Or use the helper script:
```bash
run.bat
```

**Expected Output:**
```
Started JobPortalApplication in X.xxx seconds
```

---

## ✅ Verify It's Running

Open these in your browser:

**API Documentation (Swagger):**
```
http://localhost:8080/api/swagger-ui.html
```

**Health Check:**
```
http://localhost:8080/api/actuator/health
```

**Metrics:**
```
http://localhost:8080/api/actuator/metrics
```

---

## 📋 API Testing via Swagger

1. Go to: `http://localhost:8080/api/swagger-ui.html`
2. Expand "**AuthController**"
3. Click "POST /api/auth/register"
4. Click "Try it out"
5. Enter sample data:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "Password123!",
  "phone": "9876543210",
  "role": "APPLICANT"
}
```
6. Click "Execute"
7. You should see a 200 response with a JWT token

---

## 🐛 Troubleshooting

### "mvn: The term 'mvn' is not recognized"
**Solution:** Maven is not in PATH. Reinstall Maven and restart Command Prompt.

### "Connection refused" or "Unable to connect to database"
**Solution:** 
1. Check PostgreSQL is running
2. Verify database exists: `psql -U postgres -d job_portal_db -c "SELECT 1"`
3. Check credentials in application.properties

### "JWT_SECRET is required"
**Solution:** Set environment variable:
```bash
setx JWT_SECRET "your-secret-key-here"
```
Then restart Command Prompt.

### Port 8080 already in use
**Solution:** Either:
- Kill the process using port 8080
- Change port in `application.properties`: `server.port=8081`

### Compilation Error: "cannot find symbol"
**Solution:** 
```bash
mvn clean compile
```
Make sure all dependencies downloaded correctly.

---

## 📚 Project Structure

```
job-portal-system/
├── src/main/java/com/jobportal/
│   ├── JobPortalApplication.java      (Main entry point)
│   ├── config/                         (Spring configuration)
│   │   ├── SecurityConfig.java        (Security settings)
│   │   ├── JpaConfig.java             (Database config)
│   │   └── OpenApiConfig.java         (Swagger/API docs)
│   ├── controller/                     (REST API endpoints)
│   │   ├── AuthController.java        (Login/Register)
│   │   ├── JobController.java         (Job operations)
│   │   ├── ApplicantController.java   (Applicant features)
│   │   └── RecruiterController.java   (Recruiter features)
│   ├── service/                        (Business logic)
│   │   └── impl/                      (Implementations)
│   ├── entity/                         (Database models)
│   │   ├── User.java
│   │   ├── Job.java
│   │   ├── JobApplication.java
│   │   └── Resume.java
│   ├── repository/                     (Database access)
│   ├── security/                       (JWT & Auth)
│   └── exception/                      (Error handling)
├── src/main/resources/
│   └── application.properties          (Configuration)
├── pom.xml                             (Maven configuration)
└── README.md                           (Full documentation)
```

---

## 🔑 Key Environment Variables

| Variable | Value | Example |
|----------|-------|---------|
| DB_URL | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/job_portal_db` |
| DB_USERNAME | Database user | `postgres` |
| DB_PASSWORD | Database password | `yourpassword` |
| JWT_SECRET | Secret key (32+ bytes) | `generated-secret-key-here` |
| JWT_EXPIRATION_MS | Token expiry (ms) | `86400000` (24h) |
| UPLOAD_DIR | Resume storage | `uploads/resumes` |

---

## 📱 Database Setup (If Not Using pgAdmin)

Open Command Prompt and run:

```bash
psql -U postgres
```

Then execute:
```sql
CREATE DATABASE job_portal_db;
CREATE USER job_portal_user WITH PASSWORD 'JobPortal@2024!';
GRANT ALL PRIVILEGES ON DATABASE job_portal_db TO job_portal_user;
```

---

## 🎯 Next Steps After Running

1. **Test Authentication:**
   - Register a new user via Swagger
   - Login with those credentials
   - Copy the JWT token from response

2. **Test Job Operations:**
   - Create a job (as Recruiter)
   - View all jobs (public endpoint)
   - Apply for job (as Applicant)

3. **Test Admin Features:**
   - View reports endpoint
   - Manage users

---

## 💻 Running Without Helper Scripts

If you prefer manual commands:

```bash
# Navigate to project
cd "c:\Users\Sourav Rajput\Downloads\Online Class\job portal system"

# Clean build (remove old files)
mvn clean

# Install dependencies
mvn install

# Compile code
mvn compile

# Package as JAR
mvn package -DskipTests

# Run application
mvn spring-boot:run
```

---

## 🚀 Production Deployment

When ready for production:
1. Change `ddl-auto` to `validate` in application.properties
2. Use strong JWT_SECRET (32+ characters, random)
3. Setup HTTPS/SSL certificate
4. Configure firewall
5. Setup monitoring and logging
6. Use environment-specific properties files
7. Enable rate limiting
8. Setup database backups

---

## 📞 Support

**API Documentation**: Available at `/swagger-ui.html` when app is running

**Full Documentation**: See `README.md` in project root

**Bug Fixes Applied**: See `BUG_FIXES.md` for all fixes made

---

**Status**: ✅ Production-Ready Java Project  
**Version**: 1.0.0  
**Framework**: Spring Boot 3.2.5  
**Java Version**: 17+

Happy coding! 🎉
