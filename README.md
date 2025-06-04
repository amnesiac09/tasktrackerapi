# **Task Tracker API**

A RESTful API for managing projects and tasks with role-based access control, built with Spring Boot.

## **🚀 Features**

- **User Management**: Registration and JWT-based authentication
- **Role-Based Access Control**: ADMIN, MANAGER, and USER roles with different permissions
- **Project Management**: Create, read, update, and delete projects
- **Task Management**: Full CRUD operations with task assignment and status tracking
- **Filtering & Pagination**: Filter tasks by status/priority with paginated results
- **Comprehensive Security**: JWT authentication with method-level security
- **API Documentation**: Interactive Swagger UI documentation

## **🛠️ Tech Stack**

- **Framework**: Spring Boot 3.3.5
- **Security**: Spring Security with JWT
- **Database**: H2 (file-based for persistence)
- **ORM**: Spring Data JPA / Hibernate
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Build Tool**: Maven
- **Java Version**: 17+

## **⚙️ Setup & Installation**

### **Prerequisites**
- Java 17 or higher
- Maven 3.6+

### **Installation Steps**

1. **Clone the repository**
   ```bash
   git clone https://github.com/amnesiac09/tasktrackerapi
   cd tasktrackerapi
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
    - **API Base URL**: `http://localhost:8080/api`
    - **Swagger UI**: `http://localhost:8080/api/swagger-ui.html`
    - **H2 Console**: `http://localhost:8080/api/h2-console`

## **👥 User Roles & Permissions**

| Feature                | ADMIN  | MANAGER          | USER               |
|------------------------|--------|------------------|--------------------|
| View all projects      | ✅      | ✅ (own only)     | ❌                  |
| Create projects        | ✅      | ✅                | ❌                  |
| Update/Delete projects | ✅      | ✅ (own only)     | ❌                  |
| Create tasks           | ✅      | ✅ (own projects) | ❌                  |
| Assign tasks           | ✅      | ✅ (own projects) | ❌                  |
| View task details      | ✅      | ✅ (own projects) | ✅ (assigned only)  |
| Update task status     | ✅      | ✅                | ✅ (assigned only)  |
| View assigned tasks    | ✅      | ✅                | ✅ (own only)       |

## **🔐 Pre-loaded Test Users**

The application comes with pre-loaded test data:

| Email               | Password   | Role    | Description                           |
|---------------------|------------|---------|---------------------------------------|
| `admin@test.com`    | `password` | ADMIN   | Full system access                    |
| `manager1@test.com` | `password` | MANAGER | Owns E-Commerce & Mobile App projects |
| `manager2@test.com` | `password` | MANAGER | Owns Analytics & Marketing projects   |
| `user1@test.com`    | `password` | USER    | Developer assigned to various tasks   |
| `user2@test.com`    | `password` | USER    | Developer assigned to various tasks   |
| `user3@test.com`    | `password` | USER    | Designer assigned to UI tasks         |

## **📋 API Testing Guide**

### **Step 1: Authentication**

#### **Login to get JWT tokens**
**POST** `/auth/login`

**Admin Login:**
```json
{
  "email": "admin@test.com",
  "password": "password"
}
```

**Manager Login:**
```json
{
  "email": "manager1@test.com",
  "password": "password"
}
```

**User Login:**
```json
{
  "email": "user1@test.com",
  "password": "password"
}
```

#### **Set Authorization in Swagger**
1. Click **"Authorize"** button in Swagger UI
2. Enter: `Bearer YOUR_JWT_TOKEN`
3. Click **"Authorize"**

### **Step 2: Project Management Tests (Use Manager1 token)**

#### **Get All Projects**
**GET** `/projects`
- **Expected**: 2 projects (E-Commerce Platform, Mobile App Development)

#### **Get Specific Project**
**GET** `/projects/1`
- **Expected**: E-Commerce Platform details

#### **Create New Project**
**POST** `/projects`
```json
{
  "name": "Test Project",
  "description": "Testing project creation"
}
```
- **Expected**: 201 Created

#### **Update Project**
**PUT** `/projects/1`
```json
{
  "name": "Updated E-Commerce Platform",
  "description": "Updated description"
}
```
- **Expected**: 200 OK

### **Step 3: Task Management Tests (Continue with Manager1 token)**

#### **Get Tasks by Project**
**GET** `/tasks/project/1`
- **Expected**: 5 tasks from E-Commerce Platform

#### **Filter Tasks by Status**
**GET** `/tasks/project/1?status=TODO`
- **Expected**: Only TODO tasks

#### **Filter Tasks by Priority**
**GET** `/tasks/project/1?priority=HIGH`
- **Expected**: Only HIGH priority tasks

#### **Create New Task**
**POST** `/tasks`
```json
{
  "title": "New Test Task",
  "description": "Testing task creation",
  "projectId": 1,
  "assignedUserId": 4,
  "status": "TODO",
  "priority": "MEDIUM"
}
```
- **Expected**: 201 Created

#### **Assign Task to User**
**PUT** `/tasks/3/assign/5`
- **Expected**: Task assigned successfully

### **Step 4: User Permission Tests (Switch to User1 token)**

#### **Get My Assigned Tasks**
**GET** `/tasks/my-tasks`
- **Expected**: Tasks assigned to user1@test.com

#### **Update Task Status**
**PUT** `/tasks/1/status?status=DONE`
- **Expected**: 200 OK (User can update their assigned task status)

#### **Try to Create Project (Should Fail)**
**POST** `/projects`
```json
{
  "name": "Unauthorized Project",
  "description": "This should fail"
}
```
- **Expected**: 403 Forbidden

### **Step 5: Admin Permission Tests (Switch to Admin token)**

#### **View All Projects**
**GET** `/projects`
- **Expected**: All 4 projects visible

#### **View Any User's Tasks**
**GET** `/tasks/user/4`
- **Expected**: Tasks assigned to any user

#### **Delete Project**
**DELETE** `/projects/4`
- **Expected**: 204 No Content

### **Step 6: Pagination & Sorting Tests**

#### **Paginated Results**
**GET** `/tasks/project/1?page=0&size=2`
- **Expected**: First 2 tasks with pagination metadata

#### **Sorted Results**
**GET** `/tasks/project/1?sort=title,asc`
- **Expected**: Tasks sorted by title A-Z

#### **Multiple Filters**
**GET** `/tasks/project/1?status=TODO&priority=HIGH`
- **Expected**: HIGH priority TODO tasks only

### **Step 7: Error Handling Tests**

#### **Invalid Login**
**POST** `/auth/login`
```json
{
  "email": "invalid@test.com",
  "password": "wrongpassword"
}
```
- **Expected**: 401 Unauthorized

#### **Resource Not Found**
**GET** `/projects/999`
- **Expected**: 404 Not Found

#### **Validation Error**
**POST** `/tasks`
```json
{
  "title": "",
  "description": "Missing required fields"
}
```
- **Expected**: 400 Bad Request with validation errors

## **🗄️ Database Schema**

### **Users Table**
- `id` (Primary Key)
- `email` (Unique)
- `password` (Encrypted)
- `role` (ADMIN/MANAGER/USER)

### **Projects Table**
- `id` (Primary Key)
- `name`
- `description`
- `owner_id` (Foreign Key to Users)
- `created_at`
- `updated_at`

### **Tasks Table**
- `id` (Primary Key)
- `title`
- `description`
- `status` (TODO/IN_PROGRESS/DONE)
- `priority` (LOW/MEDIUM/HIGH)
- `project_id` (Foreign Key to Projects)
- `assigned_user_id` (Foreign Key to Users)
- `created_at`
- `updated_at`

## **📚 API Endpoints**

### **Authentication**
- `POST /auth/register` - Register new user
- `POST /auth/login` - User login

### **Projects**
- `GET /projects` - Get all projects (role-based)
- `POST /projects` - Create project (MANAGER/ADMIN only)
- `GET /projects/{id}` - Get project by ID
- `PUT /projects/{id}` - Update project
- `DELETE /projects/{id}` - Delete project

### **Tasks**
- `POST /tasks` - Create task
- `GET /tasks/{id}` - Get task by ID
- `PUT /tasks/{id}` - Update task
- `DELETE /tasks/{id}` - Delete task
- `PUT /tasks/{id}/assign/{userId}` - Assign task to user
- `PUT /tasks/{id}/status` - Update task status
- `GET /tasks/project/{projectId}` - Get tasks by project
- `GET /tasks/user/{userId}` - Get tasks by assigned user
- `GET /tasks/my-tasks` - Get current user's assigned tasks

### **Query Parameters**
- `status` - Filter by task status (TODO/IN_PROGRESS/DONE)
- `priority` - Filter by priority (LOW/MEDIUM/HIGH)
- `page` - Page number (default: 0)
- `size` - Page size (default: 10, max: 100)
- `sort` - Sort by field,direction (e.g., `title,asc`)

## **🔧 Configuration**

Key configuration in `application.properties`:

```properties
# Server
server.port=8080
server.servlet.context-path=/api

# Database (H2 file-based)
spring.datasource.url=jdbc:h2:file:./data/tasktracker

# JWT
jwt.secret=TaskTrackerSecretKey...
jwt.expiration=86400000

# Swagger
springdoc.swagger-ui.path=/swagger-ui.html
```

## **🏗️ Project Structure**

```
src/main/java/naneishvili/aleksandre/tasktrackerapi/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/            # Data Transfer Objects
├── entity/         # JPA entities
├── enums/          # Enums (Role, TaskStatus, Priority)
├── exception/      # Custom exceptions
├── mapper/         # Entity-DTO mappers
├── repository/     # JPA repositories
├── security/       # Security configuration
└── service/        # Business logic services
```
