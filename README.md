
# VolunteerConnect

## Overview

The **Volunteer Matching Platform** is a web-based system that connects volunteers with opportunities that match their skills and interests. Built with a robust backend using Spring Boot, PostgreSQL, and following a monolithic architecture, the platform provides a secure, scalable environment for managing volunteer activities. The platform aims to make volunteering easier by streamlining the process of finding opportunities, signing up, and tracking contributions, thus fostering a culture of community service.

## Features

- **User Registration and Authentication**: Users can register as volunteers or organizations. Authentication is managed using JWT for secure access to protected resources.
  
- **Opportunity Management**: Organizations can create, update, and manage volunteer opportunities, while volunteers can browse, search, and apply for opportunities that suit their skills and schedule.
  
- **Role-based Access Control**: Different user roles, such as "Volunteer" and "Organization", are supported, ensuring users only access features relevant to them.
  
- **Email Verification**: Users must verify their email before they can access the platform to ensure authenticity.
  
- **Password Management**: The platform includes password recovery, password change, and secure storage of user credentials.
  
- **Profile Management**: Users can update their profiles with personal details and preferences, including uploading profile pictures.
  
- **Volunteer Tracking**: Volunteers can view their contribution history, including hours spent and opportunities they've participated in.
  
- **File Uploads**: Organizations can upload documents, such as event guidelines or consent forms, related to volunteer opportunities.
  
- **Soft Delete for Users**: When an admin user deletes a volunteer, it is a soft delete, changing the user status to inactive rather than removing their data from the system.

- **Search and Filtering**: Volunteers can search for opportunities by location, skill requirement, or cause.
  
## Tools and Technologies

- **Java 17**
- **Spring Boot**
- **Spring Security** (JWT)
- **PostgreSQL**
- **Tomcat**
- **Docker**
- **JUnit, MockMvc** (for TDD)
- **Lombok** (for simplifying Java code)
- **Maven** (for dependency management)
- **AWS** (for S3 upload)

## Development Approach

The project follows the **Test Driven Development (TDD)** methodology, ensuring that each feature is first tested using unit and integration tests before the actual implementation. Docker is used to containerize the application for consistent deployment across environments, with separate containers for the database and the backend application.

The backend will be structured in compliance with the **MVC** architecture, ensuring separation of concerns between the models, controllers, and services.

## User Stories

1. **As a volunteer**, I want to easily find and apply for volunteer opportunities that match my interests and availability.
2. **As an organization**, I want to post volunteer opportunities, manage applications, and track volunteer participation.
3. **As an admin**, I want to manage users, opportunities, and ensure the integrity of the platform.

## Entity-Relationship Diagram (ERD)
![Untitled-6](https://github.com/user-attachments/assets/a13b8347-31e5-4573-8920-6f070f80fafd)


| Request Type | URL                                         | Functionality                              | Access       | 
|--------------|---------------------------------------------|--------------------------------------------|--------------|
| POST         | /auth/users/login                           | User login                                 | Public       |
| POST         | /auth/users/register                        | User registration                          | Public       |
| POST         | /auth/users/verify                          | Verify user account                        | Public       |
| POST         | /auth/users/request-password-reset          | Request password reset                     | Public       |
| POST         | /auth/users/reset-password                  | Reset password with token                  | Public       |
| POST         | /auth/users/change-password                 | Change password                            | Authenticated |
| POST         | /auth/users/deactivate                      | Deactivate user                            | Admin        |
| GET          | /auth/users                                 | Get all users                              | Admin        |
| POST         | /api/organizations                          | Create a new organization                  | Public       |
| GET          | /api/organizations/approved                 | Get all approved organizations             | Public       |
| PUT          | /api/organizations/{id}/approve             | Approve an organization                    | Admin        |
| PUT          | /api/organizations/{id}/reject              | Reject an organization                     | Admin        |
| GET          | /api/organizations                          | Get all organizations                      | Public       |
| GET          | /api/organizations/{id}                     | Get an organization by ID                  | Public       |
| PUT          | /api/organizations/{id}                     | Update an organization                     | Public       |
| DELETE       | /api/organizations/{id}                     | Delete an organization                     | Admin        |
| GET          | /api/organizations/search                   | Search organizations by name               | Public       |
| GET          | /api/opportunities                          | Get all volunteer opportunities            | Public       |
| GET          | /api/opportunities/{id}                     | Get a volunteer opportunity by ID          | Public       |
| PUT          | /api/opportunities/{id}                     | Update a volunteer opportunity             | Authenticated |
| DELETE       | /api/opportunities/{id}                     | Delete a volunteer opportunity             | Authenticated |
| GET          | /api/opportunities/search                   | Search volunteer opportunities by title    | Public       |
| PUT          | /api/opportunities/{id}/archive             | Archive a volunteer opportunity            | Authenticated |
| POST         | /api/opportunities                          | Create a new volunteer opportunity         | Authenticated |
| POST         | /api/opportunities/consumes/multipart       | Create a new volunteer opportunity with files | Authenticated |
| GET          | /api/opportunities/current-org              | Get opportunities for the current organization | Authenticated |
| GET          | /api/applications                           | Get all applications                       | Admin        |
| GET          | /api/applications/{id}                      | Get an application by ID                   | Authenticated |
| POST         | /api/applications                           | Create a new application                   | Authenticated |
| PUT          | /api/applications/{id}                      | Update an application                      | Authenticated |
| DELETE       | /api/applications/{id}                      | Delete an application                      | Admin        |
| GET          | /api/applications/user/{userId}             | Get applications by user                   | Authenticated |
| GET          | /api/applications/opportunity/{opportunityId} | Get applications by opportunity           | Public       |
| PUT          | /api/applications/{id}/approve              | Approve an application                     | Admin        |
| PUT          | /api/applications/{id}/reject               | Reject an application                      | Admin        |
| GET          | /api/follows                                | Get all follow records                     | Authenticated |
| GET          | /api/follows/{id}                           | Get a follow record by ID                  | Authenticated |
| POST         | /api/follows                                | Create a new follow                        | Authenticated |
| DELETE       | /api/follows/{id}                           | Delete a follow                            | Authenticated |
| GET          | /api/follows/follower/{followerId}          | Get follows by follower                    | Authenticated |
| GET          | /api/follows/organization/{organizationId}  | Get follows by organization                | Authenticated |


## Installation Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/FatemaMarhoon/java-capstone-project.git
   ```
2. Navigate to the project directory:
   ```bash
   cd java-capstone-project
   ```
3. Build the project using Maven:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```


## Running Spring Boot Application and PostgreSQL Database Using Docker Compose

Now we have our docker compose setup for this application. So first create a jar build for this application using following command,

Navigate to application root folder and execute,

```
 mvn package -Dmaven.test.skip=true
```

Or 

```
 mvn package -DskipTests=true
```
Now there should be a newly created jar file with all the necessary files to run this application on  **build/libs**  folder.

Then create the build with docker compose to build docker image using built jar file.

```
 docker-compose build
```

Then use following command to run whole setup using docker compose.

```
 docker-compose up
```

Then It will capture the docker-compose.yml and start running using the instructions given on that file.

