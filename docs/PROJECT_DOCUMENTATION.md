# DevFusion2025Backend Project Documentation

## Project Overview
DevFusion2025Backend is a comprehensive job search and recruitment platform backend built with Spring Boot. The system facilitates job posting, job searching, and candidate management with support for both job seekers and companies.

## Project Structure

```
src/main/java/devtitans/antoshchuk/devfusion2025backend/
├── api/            # API-related components
├── common/         # Common utilities and shared components
├── config/         # Configuration classes
├── controllers/    # REST controllers
├── core/          # Core business logic
├── dto/           # Data Transfer Objects
├── exceptions/     # Custom exceptions
├── models/        # Domain models/entities
├── repositories/  # Data access layer
├── security/      # Security configuration and components
├── service/       # Service layer components
├── services/      # Additional services
├── util/          # Utility classes
└── DevFusion2025BackendApplication.java  # Main application class
```

## Domain Model

### User Management
1. **UserAccount**
   - Core user entity for both job seekers and companies
   - Properties:
     - id: Integer (PK)
     - email: String
     - password: String (encrypted)
     - contactNumber: String
     - userImage: String
     - active: boolean
     - emailNotificationActive: boolean
     - registrationDate: Date
   - Relationships:
     - OneToOne with Seeker
     - OneToOne with Company
     - ManyToOne with UserType

2. **UserType**
   - Defines user roles (e.g., SEEKER, COMPANY)
   - Properties:
     - id: Integer (PK)
     - name: String
   - Relationships:
     - OneToMany with UserAccount

### Job Seeker Related
1. **Seeker**
   - Represents a job seeker profile
   - Properties:
     - id: Integer (PK)
     - firstName: String
     - lastName: String
     - dateOfBirth: Date
     - currentMonthlySalary: double
   - Relationships:
     - OneToOne with UserAccount
     - OneToMany with EducationDetail
     - OneToMany with ExperienceDetail
     - OneToMany with SeekerSkillSet
     - OneToMany with JobPostActivity

2. **EducationDetail**
   - Educational background of seekers
   - Properties:
     - id: Integer (PK)
     - major: String
     - instituteUniversityName: String
     - startDate: Date
     - completionDate: Date
     - cgpa: int
   - Relationships:
     - ManyToOne with Seeker
     - ManyToOne with CertificateDegree

3. **ExperienceDetail**
   - Work experience of seekers
   - Properties:
     - id: Integer (PK)
     - isCurrentJob: boolean
     - startDate: Date
     - endDate: Date
     - jobTitle: String
     - companyName: String
     - jobLocationCity: String
     - jobLocationCountry: String
     - description: String
   - Relationships:
     - ManyToOne with Seeker

### Company Related
1. **Company**
   - Represents an employer organization
   - Properties:
     - id: Integer (PK)
     - name: String
     - logo: String
     - businessStreamName: String
     - companyDescription: String
   - Relationships:
     - OneToOne with UserAccount
     - OneToMany with JobPost
     - OneToMany with CompanyImage

2. **CompanyImage**
   - Stores company gallery images
   - Properties:
     - id: Integer (PK)
     - companyImage: String
   - Relationships:
     - ManyToOne with Company

### Job Related
1. **JobPost**
   - Represents a job listing
   - Properties:
     - id: Integer (PK)
     - title: String
     - titleEn: String
     - jobDescription: String
     - jobDescriptionEn: String
     - jobLocation: String
     - isCompanyNameHidden: boolean
     - createdDateTime: Date
     - isActive: boolean
     - salary: String
     - language: String
   - Relationships:
     - ManyToOne with Company
     - ManyToOne with JobType
     - ManyToOne with JobGradation
     - OneToMany with JobPostActivity

2. **JobType**
   - Defines types of employment (e.g., Full-time, Part-time)
   - Properties:
     - id: Integer (PK)
     - name: String
   - Relationships:
     - OneToMany with JobPost

3. **JobGradation**
   - Defines job levels (e.g., Junior, Middle, Senior)
   - Properties:
     - id: Integer (PK)
     - name: String
   - Relationships:
     - OneToMany with JobPost

### Skills Management
1. **Skill**
   - Represents professional skills
   - Properties:
     - id: Integer (PK)
     - name: String
   - Relationships:
     - OneToMany with SeekerSkillSet
     - OneToMany with JobPostSkill

2. **SeekerSkillSet**
   - Links skills to job seekers
   - Relationships:
     - ManyToOne with Seeker
     - ManyToOne with Skill

3. **JobPostSkill**
   - Links skills to job posts
   - Relationships:
     - ManyToOne with Skill

## API Endpoints

### Authentication
```
POST /api/auth/register - Register new user (seeker or company)
POST /api/auth/login - User login
```

### User Management
```
GET /api/user/me - Get current user data
```

### Company Management
```
GET /api/v1/companies - Get all companies (basic info)
GET /api/v1/companies/with-posts - Get all companies with their job posts
GET /api/v1/companies/{id} - Get detailed company information
GET /api/v1/companies/{id}/with-posts - Get company with its job posts
GET /api/v1/companies/search - Search companies with filters
DELETE /api/v1/companies/{id} - Delete a company
```

### Job Posts
```
GET /api/jobs/all - Get all job posts (paginated)
GET /api/jobs/{id} - Get detailed job post information
```

## Security Implementation

The project implements JWT-based authentication with the following features:
- Token-based authentication
- Role-based access control
- Secure password handling
- API endpoint protection

## Build and Deployment

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL database

### Building the Project
```bash
./mvnw clean install
```

### Running the Application
```bash
./mvnw spring-boot:run
```

### Environment Configuration
Key configuration properties:
- Database connection
- JWT secret and expiration
- File upload settings
- Email notification settings

## Best Practices
1. All sensitive data is encrypted
2. Implements proper error handling
3. Uses DTO pattern for API responses
4. Implements proper validation
5. Follows REST API best practices
6. Uses proper database indexing
7. Implements proper logging

## Error Handling
The application implements global exception handling with proper error responses for:
- Authentication errors
- Validation errors
- Resource not found errors
- Business logic errors
- System errors

---

Note: This documentation is a living document and should be updated as the project evolves. For the most up-to-date information, please refer to the source code and comments within individual files. 