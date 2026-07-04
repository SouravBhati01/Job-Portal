# 📦 Job Portal System - Java Project Manifest

## Project Overview
**Pure Java Backend Application** - Spring Boot 3.2.5 REST API  
**Total Java Files**: 40+ classes  
**Build System**: Maven 3.8+  
**Java Version**: Java 17+  
**Database**: PostgreSQL 12+  

---

## 📂 Complete File Structure

### Root Configuration Files
```
pom.xml                          - Maven project configuration (dependencies, plugins)
docker-compose.yml              - Docker Compose setup (PostgreSQL + App)
Dockerfile                       - Container definition
application.properties           - Spring Boot application configuration
application-dev.properties       - Development profile settings
```

### Shell Scripts
```
run.bat                         - Windows batch script to build and run project
setup-env.bat                   - Windows environment variable setup helper
```

### Documentation Files
```
README.md                       - Complete project documentation
WINDOWS_SETUP.md               - Step-by-step Windows setup guide
SETUP_GUIDE.md                 - General setup instructions
BUG_FIXES.md                   - All bug fixes with before/after code
PROJECT_MANIFEST.md            - This file (Java files list)
```

---

## 🔷 Java Source Files (src/main/java/com/jobportal/)

### 1. Main Application Class
```
JobPortalApplication.java       - Main entry point, starts Spring Boot
```

### 2. Config Package (Security & Spring Configuration)
```
config/
├── SecurityConfig.java         - JWT authentication, authorization, CORS, security headers
├── JpaConfig.java              - Hibernate/JPA configuration
├── OpenApiConfig.java          - Swagger/OpenAPI documentation setup
└── DataInitializer.java        - Initialize test data on startup
```

### 3. Controller Package (REST API Endpoints)
```
controller/
├── AuthController.java         - Login & Registration endpoints
│   └── POST   /auth/login      - Authenticate user
│   └── POST   /auth/register   - Create new user account
│
├── JobController.java          - Job listing endpoints (public)
│   └── GET    /jobs            - List all jobs (with pagination)
│   └── GET    /jobs/{id}       - Get single job details
│   └── POST   /jobs            - Post new job (Recruiter only)
│   └── PUT    /jobs/{id}       - Update job listing (Recruiter only)
│
├── ApplicantController.java    - Job applicant features
│   └── POST   /applicant/apply - Submit job application
│   └── GET    /applicant/applications - My applications (with status)
│   └── GET    /applicant/applications/{id} - Application details
│
├── RecruiterController.java    - Recruiter-specific operations
│   └── GET    /recruiter/jobs/{jobId}/applicants - View applicants
│   └── PATCH  /recruiter/applications/{id}/status - Update status
│   └── PUT    /recruiter/applications/{id}/notes - Add feedback notes
│
├── ResumeController.java       - Resume file management
│   └── POST   /resumes/upload  - Upload resume file
│   └── GET    /resumes/my      - Get my resumes
│   └── GET    /resumes/{id}/download - Download resume
│   └── DELETE /resumes/{id}    - Delete resume
│
└── AdminController.java        - Admin operations (Admin role only)
    └── GET    /admin/reports   - Platform statistics
    └── GET    /admin/users     - List all users
    └── PATCH  /admin/users/{id}/status - Enable/disable user
```

### 4. Service Package (Business Logic)

**Service Interfaces:**
```
service/
├── AuthService.java            - Authentication business logic interface
├── JobService.java             - Job operations interface
├── ApplicationService.java     - Application management interface
├── ResumeService.java          - Resume management interface
├── AdminService.java           - Admin operations interface
└── UserService.java            - User management interface
```

**Service Implementations:**
```
service/impl/
├── AuthServiceImpl.java         - User registration & login logic
│   ├── register()              - Create new user with password hashing
│   ├── login()                 - Validate credentials & generate JWT
│   └── validateToken()         - Check JWT token validity
│
├── JobServiceImpl.java          - Job CRUD operations
│   ├── getAll()                - Retrieve paginated job list
│   ├── getById()               - Get specific job details
│   ├── create()                - Create new job (Recruiter)
│   ├── update()                - Modify job listing
│   └── delete()                - Remove job
│
├── ApplicationServiceImpl.java  - Job application tracking
│   ├── apply()                 - Submit application
│   ├── getMyApplications()     - Retrieve applicant's applications
│   ├── getApplicationsForJob() - Get all applicants for a job
│   ├── updateStatus()          - Change application status
│   └── addNotes()              - Add recruiter feedback
│
├── ResumeServiceImpl.java       - Resume file handling
│   ├── upload()                - Store resume with validation
│   ├── getMyResumes()          - Retrieve user's resumes
│   ├── download()              - Get resume file
│   └── delete()                - Remove resume file
│
├── AdminServiceImpl.java        - Admin functionality
│   ├── getReports()            - Generate platform statistics
│   ├── getAllUsers()           - List all users
│   ├── disableUser()           - Disable account
│   └── enableUser()            - Enable account
│
└── UserServiceImpl.java         - User management
    ├── getUserById()           - Get user details
    ├── updateProfile()         - Update user information
    └── changePassword()        - Update user password
```

### 5. Entity Package (JPA/Database Models)

```
entity/
├── BaseEntity.java             - Abstract base class with id, timestamps
│   └── Properties: id, createdAt, updatedAt
│
├── User.java                   - User/Account entity
│   └── Columns: firstName, lastName, email, password, phone, enabled
│   └── Relations: roles (many-to-many), postedJobs (one-to-many), applications (one-to-many)
│
├── Role.java                   - User role entity (APPLICANT, RECRUITER, ADMIN)
│   └── Columns: name (enum), description
│
├── Job.java                    - Job listing entity
│   └── Columns: title, description, location, salary, type, status, remote
│   └── Relations: postedBy (many-to-one), applications (one-to-many)
│
├── JobApplication.java         - Job application entity
│   └── Columns: status, coverLetter, appliedDate, recruiterNotes
│   └── Relations: job (many-to-one), applicant (many-to-one), resume
│
└── Resume.java                 - Resume file entity
    └── Columns: fileName, fileSize, filePath, uploadDate
    └── Relation: uploadedBy (many-to-one)
```

### 6. Repository Package (Database Access Layer)

```
repository/
├── UserRepository.java         - User database queries
│   ├── findByEmail()           - Get user by email
│   ├── existsByEmail()         - Check if email exists
│   ├── countByEnabled()        - Count enabled/disabled users
│   └── findAll(Pageable)       - Paginated user list
│
├── JobRepository.java          - Job database queries
│   ├── findAll(Pageable)       - Paginated job list
│   ├── findByStatus()          - Get jobs by status
│   ├── findByLocation()        - Get jobs in location
│   ├── findByPostedBy()        - Get recruiter's jobs
│   └── searchByKeyword()       - Full-text search
│
├── JobApplicationRepository.java - Application queries
│   ├── findByApplicantId()     - Get applicant's applications
│   ├── findByJobId()           - Get job's applications
│   ├── findByStatus()          - Filter by status
│   └── existsByJobIdAndApplicantId() - Check duplicate apply
│
├── ResumeRepository.java       - Resume file queries
│   ├── findByUploadedById()    - Get user's resumes
│   ├── findById()              - Get specific resume
│   └── deleteById()            - Remove resume
│
└── RoleRepository.java         - Role queries
    └── findByName()            - Get role by name
```

### 7. Security Package (JWT & Authentication)

```
security/
├── JwtTokenProvider.java       - JWT token generation & validation
│   ├── generateToken()         - Create JWT with claims
│   ├── getSubjectFromToken()  - Extract user from token
│   ├── getExpirationDate()    - Get token expiry time
│   └── validateToken()         - Check signature & expiry
│
├── JwtAuthenticationFilter.java - JWT filter for requests
│   ├── doFilterInternal()      - Check Authorization header
│   ├── extractToken()          - Parse Bearer token
│   └── authenticate()          - Validate token & set context
│
└── UserDetailsServiceImpl.java  - Spring Security user details
    └── loadUserByUsername()    - Load user for authentication
```

### 8. Exception Package (Error Handling)

```
exception/
├── GlobalExceptionHandler.java - Centralized exception handling
│   ├── @RestControllerAdvice   - Handles all controller exceptions
│   ├── handleResourceNotFound()     - 404 errors
│   ├── handleBadRequest()           - 400 errors
│   ├── handleUnauthorized()         - 401 errors
│   ├── handleAccessDenied()         - 403 errors
│   └── handleServerError()          - 500 errors
│
├── BadRequestException.java    - Invalid input exception
├── UnauthorizedException.java  - Authentication failed exception
├── ResourceNotFoundException.java - Resource not found exception
├── DuplicateResourceException.java - Duplicate entry exception
└── FileStorageException.java   - File upload error exception
```

### 9. DTO Package (Data Transfer Objects)

**Request DTOs (Input):**
```
dto/request/
├── LoginRequest.java           - { email, password }
├── RegisterRequest.java        - { firstName, lastName, email, password, phone, role }
├── JobRequest.java             - { title, description, location, salary, type, remote }
├── ApplicationRequest.java     - { jobId, coverLetter, resumeId }
└── ApplicationStatusRequest.java - { status, notes }
```

**Response DTOs (Output):**
```
dto/response/
├── AuthResponse.java           - User data + JWT token
├── UserResponse.java           - { id, firstName, lastName, email, phone, roles }
├── JobResponse.java            - { id, title, company, location, salary, type }
├── ApplicationResponse.java    - { id, jobTitle, applicantName, status, appliedDate }
├── ResumeResponse.java         - { id, fileName, uploadDate, fileSize }
├── ApiResponse.java            - { success, message, data, timestamp }
└── AdminReportResponse.java    - { totalUsers, activeJobs, totalApplications, ... }
```

### 10. Enum Package (Constants)

```
enums/
├── RoleName.java               - APPLICANT, RECRUITER, ADMIN
├── JobStatus.java              - DRAFT, ACTIVE, CLOSED, FLAGGED
├── JobType.java                - FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP
├── ApplicationStatus.java      - APPLIED, SHORTLISTED, OFFERED, REJECTED
└── FileType.java               - PDF, DOCX (supported resume formats)
```

### 11. Util Package (Utility Classes)

```
util/
└── FileStorageUtil.java        - Resume file handling with security
    ├── uploadFile()            - Save file securely
    ├── downloadFile()          - Retrieve file
    ├── deleteFile()            - Remove file
    ├── validateFilePath()      - Path traversal protection
    └── validateFileContent()   - Magic number validation (prevent spoofing)
```

---

## 🔄 Data Flow Example: User Registration

```
1. User sends HTTP POST to /auth/register
   ↓
2. AuthController.register() receives request
   ↓
3. AuthController validates input
   ↓
4. AuthService.register() executes
   - Hash password with BCrypt
   - Check for duplicate email
   - Create User entity
   - Generate JWT token
   ↓
5. UserRepository.save() persists to database
   ↓
6. AuthResponse returned with JWT token
   ↓
7. Client stores token for future requests
```

---

## 🔐 Security Architecture

```
Client Request
    ↓
JwtAuthenticationFilter (extracts token from header)
    ↓
JwtTokenProvider (validates signature & expiry)
    ↓
UserDetailsServiceImpl (loads user from database)
    ↓
SecurityContext (stores authentication)
    ↓
@PreAuthorize annotations (method-level access control)
    ↓
Controller Method (processes request)
    ↓
Service Layer (business logic with authority checks)
    ↓
Repository Layer (database query)
    ↓
Response sent to client
```

---

## 📊 Database Tables Automatically Created

```sql
-- Users and Roles
users                   - User account data
user_roles             - Role assignments (many-to-many)
roles                  - Role definitions

-- Job Operations
jobs                   - Job listings
job_applications      - Application records

-- Resume Management
resumes               - Uploaded resume files

-- Audit
hibernate_sequence   - ID generator (optional)
```

---

## 🧪 Test Files (src/test/)

```
ApplicationServiceTest.java    - Application service unit tests
AuthServiceTest.java          - Authentication service tests
JobServiceTest.java           - Job service tests
```

---

## ✨ Features Implemented in Java

### Authentication & Security
- ✅ JWT-based stateless authentication
- ✅ Password hashing with BCrypt (strength 12)
- ✅ Role-based access control (3 roles)
- ✅ Security headers (HSTS, CSP, X-Frame-Options, X-XSS-Protection)
- ✅ CORS configuration (production-ready)
- ✅ Method-level authorization with @PreAuthorize

### Job Management
- ✅ Post job listings (Recruiter)
- ✅ Browse all jobs (public)
- ✅ Search/filter jobs by keyword and location
- ✅ View job details
- ✅ Manage job status (DRAFT, ACTIVE, CLOSED, FLAGGED)

### Application Tracking
- ✅ Apply for jobs (Applicant)
- ✅ View all applicants for a job (Recruiter)
- ✅ Update application status (Recruiter)
- ✅ Add recruiter notes/feedback
- ✅ Prevent duplicate applications
- ✅ Track application timeline

### Resume Management
- ✅ Upload resumes (PDF, DOCX only, 5MB max)
- ✅ Validate file content (magic number validation)
- ✅ Prevent path traversal attacks
- ✅ Download resumes
- ✅ Manage multiple resumes

### Admin Features
- ✅ Generate platform reports
- ✅ Manage users (enable/disable)
- ✅ View user statistics
- ✅ Moderation tools

### Performance & Reliability
- ✅ Database connection pooling (HikariCP)
- ✅ Pagination for large datasets
- ✅ N+1 query optimization
- ✅ Lazy loading of relationships
- ✅ Transaction management
- ✅ Exception handling with proper HTTP status codes

### API Documentation
- ✅ Swagger UI at /swagger-ui.html
- ✅ OpenAPI 3.0 specification
- ✅ Interactive API testing
- ✅ Auto-generated API documentation

---

## 🚀 Build & Deployment Files

### Maven Build
```
pom.xml                 - Defines all dependencies, build configuration
                        - Spring Boot 3.2.5 parent POM
                        - 15+ dependencies included
                        - Plugins: spring-boot-maven, maven-compiler
```

### Docker Deployment
```
Dockerfile              - Container image definition
                        - Multi-stage build (optimized)
                        - Runs on Java 17 slim image
                        - Exposes port 8080

docker-compose.yml      - Orchestrates PostgreSQL + App
                        - PostgreSQL 12 database service
                        - Job Portal app service
                        - Volume for database persistence
                        - Network configuration
```

### Configuration Files
```
application.properties  - Spring Boot configuration
                        - Database connection settings
                        - JWT configuration
                        - Actuator endpoints
                        - Logging configuration
                        - Hibernate settings
                        - File upload limits
```

---

## 📈 Total Code Statistics

| Category | Count |
|----------|-------|
| Controllers | 6 |
| Services | 6 |
| Service Implementations | 6 |
| Entities | 5 |
| Repositories | 5 |
| DTOs | 15+ |
| Enums | 4 |
| Exceptions | 6 |
| Utility Classes | 1 |
| Configuration Classes | 4 |
| Security Components | 3 |
| **Total Java Classes** | **~70** |
| Total Lines of Code | **5000+** |

---

## 🎯 Application Endpoints Summary

```
Authentication
  POST   /auth/register         - Create new account
  POST   /auth/login            - Login and get JWT

Jobs (Public)
  GET    /jobs                  - List all jobs
  GET    /jobs/{id}             - Get job details

Jobs (Recruiter)
  POST   /jobs                  - Post new job
  PUT    /jobs/{id}             - Update job

Applications (Applicant)
  POST   /applicant/apply       - Apply for job
  GET    /applicant/applications - My applications

Applications (Recruiter)
  GET    /recruiter/jobs/{id}/applicants - View applicants
  PATCH  /recruiter/applications/{id}/status - Update status

Resumes
  POST   /resumes/upload        - Upload resume
  GET    /resumes/my            - My resumes
  GET    /resumes/{id}/download - Download resume

Admin
  GET    /admin/reports         - Platform statistics
  GET    /admin/users           - All users

Actuator
  GET    /actuator/health       - Health check
  GET    /actuator/metrics      - Application metrics
```

---

## ✅ All Bugs Fixed (28 Total)

See `BUG_FIXES.md` for detailed information on:
- 3 Critical security vulnerabilities
- 11 High-severity code issues
- 12 Medium-severity problems
- 2 Low-severity improvements

---

## 📦 Dependencies Included

- **Spring Boot 3.2.5** - Web framework
- **Spring Security 6.x** - Authentication/authorization
- **Spring Data JPA** - ORM with Hibernate
- **PostgreSQL 42.x** - Database driver
- **JWT (jjwt 0.11.5)** - Token generation/validation
- **Lombok** - Code generation
- **Springdoc OpenAPI 2.3.0** - Swagger/API docs
- **Jakarta Validation** - Input validation
- **H2 Database** - Development testing

---

## 🎓 Learning Resources

This project demonstrates:
- Spring Boot best practices
- Microservices patterns
- RESTful API design
- JWT authentication
- Spring Security configuration
- JPA/Hibernate ORM
- Transaction management
- Exception handling
- File upload with security
- API documentation with Swagger

---

**Status**: ✅ Production-Ready  
**Java Version**: 17+  
**Framework**: Spring Boot 3.2.5  
**Build Tool**: Maven 3.8+  
**Database**: PostgreSQL 12+  
**Total Files**: 40+ Java classes + 4 configuration files
