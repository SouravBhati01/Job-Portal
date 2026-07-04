# 🎯 Job Portal System - Quick Command Reference

## Essential Commands (Copy & Paste)

### 1️⃣ Navigate to Project
```bash
cd "c:\Users\Sourav Rajput\Downloads\Online Class\job portal system"
```

---

### 2️⃣ Setup Environment Variables (One-Time)

**Option A: Run setup script (Easiest)**
```bash
setup-env.bat
```

**Option B: Manual setup**
```bash
setx DB_URL "jdbc:postgresql://localhost:5432/job_portal_db"
setx DB_USERNAME "postgres"
setx DB_PASSWORD "postgres"
setx JWT_SECRET "MySecureSecretKey123456789012345"
setx JWT_EXPIRATION_MS "86400000"
```
Then **restart your Command Prompt**

---

### 3️⃣ Database Setup (PostgreSQL)

**Login to PostgreSQL:**
```bash
psql -U postgres
```

**Create database:**
```sql
CREATE DATABASE job_portal_db;
\q
```

---

### 4️⃣ Build & Run Project

**Option A: Using helper script (Easiest)**
```bash
run.bat
```

**Option B: Manual commands**
```bash
# Clean and build
mvn clean package -DskipTests

# Run application
mvn spring-boot:run
```

---

### 5️⃣ Verify Application is Running

**Test API (in browser or curl):**
```bash
# Check if API is responding
curl http://localhost:8080/api/actuator/health

# Expected response:
# {"status":"UP"}
```

**Access Swagger UI (in browser):**
```
http://localhost:8080/api/swagger-ui.html
```

---

## 🧪 Testing API Endpoints

### Test Registration
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "Password123!",
    "phone": "9876543210",
    "role": "APPLICANT"
  }'
```

### Test Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "Password123!"
  }'
```

### Get All Jobs (Public, No Auth Needed)
```bash
curl http://localhost:8080/api/jobs?page=0&size=10
```

---

## 🔧 Troubleshooting Commands

### Check Java Installation
```bash
java -version
```

### Check Maven Installation
```bash
mvn -version
```

### Check PostgreSQL Connection
```bash
psql -U postgres -c "SELECT 1"
```

### View Maven Dependencies
```bash
mvn dependency:tree
```

### Clean Maven Cache
```bash
mvn clean
```

### Compile Code Only (No Run)
```bash
mvn compile
```

### Run Tests
```bash
mvn test
```

### Check if Port 8080 is in Use
```bash
netstat -ano | findstr :8080
```

### Kill Process Using Port 8080
```bash
taskkill /PID <PID_NUMBER> /F
```

---

## 📊 Application URLs

| URL | Purpose |
|-----|---------|
| `http://localhost:8080/api/` | API Base URL |
| `http://localhost:8080/api/swagger-ui.html` | API Documentation |
| `http://localhost:8080/api/actuator/health` | Health Check |
| `http://localhost:8080/api/actuator/metrics` | Application Metrics |

---

## 🔑 Environment Variables Check

**Verify variables are set:**
```bash
echo %DB_URL%
echo %DB_USERNAME%
echo %DB_PASSWORD%
echo %JWT_SECRET%
echo %JWT_EXPIRATION_MS%
```

**If empty, run setup again:**
```bash
setup-env.bat
```

---

## 📝 Log Output

### Expected Startup Logs
```
INFO: Started JobPortalApplication in X.XXX seconds
INFO: Apache Catalina started on port 8080
INFO: Started Tomcat in X milliseconds
INFO: Tomcat initialized with port(s): 8080
```

### Common Error Messages

**"No qualifying bean of type"**
- Solution: Restart Command Prompt (environment variables not loaded)

**"Connection refused"**
- Solution: Check PostgreSQL is running: `net start PostgreSQL14`

**"Port 8080 already in use"**
- Solution: Kill process using port: `taskkill /PID <PID> /F`

---

## 🚀 Development Workflow

### First Time Setup
```bash
# 1. Setup environment
setup-env.bat

# 2. Restart Command Prompt

# 3. Navigate to project
cd "c:\Users\Sourav Rajput\Downloads\Online Class\job portal system"

# 4. Build project
mvn clean package -DskipTests

# 5. Run
mvn spring-boot:run
```

### Daily Development
```bash
# Simply run
cd "c:\Users\Sourav Rajput\Downloads\Online Class\job portal system"
run.bat
```

### After Code Changes
```bash
# Rebuild and restart
mvn clean compile
mvn spring-boot:run
```

---

## 📱 Using Swagger to Test API

1. Open: `http://localhost:8080/api/swagger-ui.html`
2. Expand endpoint (e.g., "AuthController")
3. Click "Try it out"
4. Fill in parameters
5. Click "Execute"
6. View response

---

## 🎓 Project Structure Quick View

```
Java Files          → src/main/java/com/jobportal/
Configuration       → src/main/resources/
Database Scripts    → Follow WINDOWS_SETUP.md
API Docs           → http://localhost:8080/api/swagger-ui.html
Full Docs          → README.md, JAVA_PROJECT_MANIFEST.md
```

---

## ✅ Checklist Before Running

- [ ] Java 21 installed (`java -version`)
- [ ] Maven installed (`mvn -version`)
- [ ] PostgreSQL running (`psql -U postgres`)
- [ ] Database created (`CREATE DATABASE job_portal_db`)
- [ ] Environment variables set (run `setup-env.bat`)
- [ ] Restarted Command Prompt after setting variables

---

## 🚨 Emergency Fixes

### Application won't start
```bash
# 1. Clean build
mvn clean

# 2. Restart Command Prompt

# 3. Check environment variables
echo %JWT_SECRET%
echo %DB_URL%

# 4. Check database
psql -U postgres -d job_portal_db -c "SELECT 1"
```

### Maven downloads stuck
```bash
# Clear Maven cache
rmdir /s /q %USERPROFILE%\.m2\repository

# Re-download
mvn clean install
```

### Port conflict
```bash
# Find process using 8080
netstat -ano | findstr :8080

# Kill it
taskkill /PID <PID> /F

# Or change port in application.properties:
# server.port=8081
```

---

## 📞 Getting Help

1. **Check WINDOWS_SETUP.md** for detailed step-by-step guide
2. **Check JAVA_PROJECT_MANIFEST.md** for file structure
3. **Check BUG_FIXES.md** for known issues fixed
4. **Check README.md** for complete documentation
5. **Check Swagger** at `/swagger-ui.html` for API reference

---

## 💾 Useful File Locations

```
pom.xml                           → Maven configuration
src/main/java/...                 → Java source code
src/main/resources/
  └── application.properties      → Application settings
run.bat                           → Start script
setup-env.bat                     → Setup environment
WINDOWS_SETUP.md                  → Windows setup guide
JAVA_PROJECT_MANIFEST.md          → File structure reference
README.md                         → Full documentation
BUG_FIXES.md                      → Bug fixes applied
```

---

## 🎯 Next Steps After Starting

1. **Test Registration**: Use Swagger to register a user
2. **Test Login**: Login with created user and get JWT token
3. **Copy JWT Token**: From login response
4. **Test Protected Endpoint**: Paste token in Authorization header
5. **Create Job**: Post a job as Recruiter
6. **Browse Jobs**: View all jobs
7. **Apply for Job**: Submit application as Applicant

---

**Status**: Ready to Go! 🚀  
**Framework**: Spring Boot 3.2.5  
**Java**: 17+  
**Build**: Maven 3.8+
