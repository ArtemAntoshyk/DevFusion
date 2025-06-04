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
Description: Registers a job seeker or a company and logs them in automatically.

Responses:
- 200: User successfully registered and authenticated
- 400: Invalid registration data
- 409: User already exists

POST /api/auth/login - User login
Description: Logs in a user by verifying email and password.

Responses:
- 200: Login successful
- 401: Invalid login credentials
```

### User Management
```
GET /api/v1/user/me - Get current user profile
Description: Returns information about the currently authenticated user based on their JWT token

Responses:
- 200: Successfully retrieved user profile
  Content: UserDataResponseDTO
- 401: Unauthorized - Invalid or missing token
- 404: User not found
```

### Company Management
```
GET /api/v1/companies - Get all companies with basic information
Description: Returns a list of all companies with their basic information

Responses:
- 200: Successfully retrieved companies
  Content: List<CompanyBaseResponseDTO>

GET /api/v1/companies/with-posts - Get all companies with their job posts
Description: Returns a list of all companies including their job posts

Responses:
- 200: Successfully retrieved companies with posts
  Content: List<CompanyWithPostsResponseDTO>

GET /api/v1/companies/{id} - Get company by ID with all information
Description: Returns detailed information about a specific company

Responses:
- 200: Successfully retrieved company
  Content: CompanyAllInfoResponseDTO
- 404: Company not found

GET /api/v1/companies/{id}/with-posts - Get company by ID with its job posts
Description: Returns detailed information about a specific company including its job posts

Responses:
- 200: Successfully retrieved company with posts
  Content: CompanyWithPostsResponseDTO
- 404: Company not found

GET /api/v1/companies/search - Search and filter companies with pagination
Description: Search and filter companies with pagination support

Parameters:
- page: Page number (0-based)
- size: Page size (default: 10)
- search: Search term for company name
- businessStream: Business stream filter

Responses:
- 200: Successfully retrieved filtered companies
  Content: PaginatedCompanyResponseDTO

DELETE /api/v1/companies/{id} - Delete a company
Description: Deletes a company by its ID

Responses:
- 204: Company successfully deleted
- 404: Company not found
```

### Company Profile
```
GET /api/v1/companies/me - Get authenticated company profile
Description: Retrieves the complete profile of the currently authenticated company

Authentication: Required (Bearer token)

Responses:
- 200: Successfully retrieved company profile
  Content: CompanyProfileResponseDTO
- 401: Unauthorized - Invalid or missing token
- 403: Forbidden - User is not a company
- 404: Company profile not found

PUT /api/v1/companies/me - Update company profile
Description: Updates the profile of the currently authenticated company

Authentication: Required (Bearer token)

Request Body:
{
    "name": "New Company Name",
    "businessStreamName": "IT Services",
    "companyLogo": "https://example.com/logo.png",
    "companyDescription": "Updated company description",
    "companyWebsiteUrl": "https://company.com",
    "establishmentDate": "2020-01-01",
    "companyImages": [
        "https://example.com/image1.jpg",
        "https://example.com/image2.jpg"
    ],
    "email": "new.email@company.com",
    "contactNumber": "+380501234567"
}

Field Validations:
- name: 2-100 characters
- businessStreamName: 2-100 characters
- companyLogo: max 500 characters, valid URL
- companyDescription: max 2000 characters
- companyWebsiteUrl: max 500 characters, valid URL format
- establishmentDate: valid date in YYYY-MM-DD format
- companyImages: array of valid image URLs
- email: valid email format, max 100 characters
- contactNumber: international format (e.g., +380501234567), 10-15 digits

Responses:
- 200: Company profile updated successfully
- 400: Invalid request data
- 401: Unauthorized - Invalid or missing token
- 403: Forbidden - User is not a company
- 404: Company profile not found
- 409: Email already exists
```

### Company Statistics
```
GET /api/v1/statistics/companies/top-with-vacancies - Get top companies with their vacancies
Description: Returns a list of top companies along with their most recent job posts. Companies are ranked based on their total number of vacancies.

Authentication: Required (Bearer token)

Responses:
- 200: Successfully retrieved top companies
  Content: List<CompanyWithVacanciesDTO>
- 401: Unauthorized - Invalid or missing token
```

### Job Posts
```
GET /api/v1/job-posts - Get list of job posts
Description: Returns a paginated and filtered list of job posts. Supports comprehensive filtering and sorting options.

Filtering Options:
- searchQuery: Search in title and description
- location: Filter by job location (e.g., 'London', 'Remote')
- companyId: Filter by specific company
- jobType: Filter by job type (FULL_TIME, PART_TIME, CONTRACT, FREELANCE, INTERNSHIP)
- gradation: Filter by experience level (JUNIOR, MIDDLE, SENIOR, LEAD)
- isActive: Filter by vacancy status (true/false)

Sorting Options:
- createdDateTime (default)
- title
- location
- company.name

Pagination:
- Default page size: 6 items
- Page numbering starts from 0

Example Requests:
1. Basic: GET /api/v1/job-posts
2. With filters: GET /api/v1/job-posts?searchQuery=java&location=London&jobType=FULL_TIME
3. With sorting: GET /api/v1/job-posts?sortBy=createdDateTime&sortDirection=DESC
4. With pagination: GET /api/v1/job-posts?page=0&size=10

Responses:
- 200: Successfully retrieved list of job posts
  Content: Page<JobPostResponseDTO>
- 400: Invalid request parameters

GET /api/v1/job-posts/{id} - Get job post details
Description: Returns detailed information about a specific job post by its ID

Responses:
- 200: Successfully retrieved job post details
  Content: JobPostDetailedResponseDTO
- 404: Job post not found

GET /api/v1/job-posts/search - Search job posts
Description: Search job posts with filters and save search history for authenticated users

Responses:
- 200: Successfully retrieved filtered job posts
  Content: Page<JobPostResponseDTO>
```

## Security Implementation

The project implements JWT-based authentication with the following features:
- Token-based authentication
- Role-based access control
- Secure password handling
- API endpoint protection

### Authentication Flow
1. User registers or logs in
2. Server validates credentials and returns JWT token
3. Client includes token in Authorization header for subsequent requests
4. Server validates token and grants access to protected resources

### Error Responses
All endpoints return standardized error responses in the following format:
```json
{
    "success": false,
    "message": "Error description",
    "data": null
}
```

### Common HTTP Status Codes
- 200: Success
- 400: Bad Request (validation errors)
- 401: Unauthorized (invalid/missing token)
- 403: Forbidden (insufficient permissions)
- 404: Not Found
- 409: Conflict (e.g., email already exists)

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