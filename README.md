
# Travel Planning Application

## Overview
The Travel Planning Application is a web-based tool designed to help users plan their trips efficiently. The application allows users to create travel itineraries, manage bookings, and discover attractions based on their preferences. Built with a robust backend using Spring Boot and PostgreSQL, the application offers a seamless user experience with features like JWT authentication and file uploads.

## Features
- User registration and authentication using JWT
- Create, update, and delete travel itineraries
- Browse and manage travel bookings
- Upload and manage travel-related documents (e.g., tickets, reservations)
- Discover popular attractions and activities based on user preferences
- Responsive user interface

## Technologies Used
- **Backend:** Java, Spring Boot
- **Database:** PostgreSQL
- **Web Server:** Apache Tomcat
- **Containerization:** Docker
- **Authentication:** JWT (JSON Web Token)
- **File Management:** Handling file uploads
- **Frontend:** (Specify if you're using a particular frontend technology)

## Installation
To get started with the Travel Planning Application, follow these steps:

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/travel-planning-app.git
   cd travel-planning-app
   ```

2. **Set up the PostgreSQL database:**
   - Create a database and user for the application.
   - Update the `application.properties` file with your database credentials.

3. **Build the application:**
   ```bash
   ./mvnw clean install
   ```

4. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Access the application:**
   Open your web browser and go to `http://localhost:8080`.

## Usage
- Register a new account or log in to an existing one.
- Start creating your travel itineraries by adding destinations, dates, and activities.
- Upload relevant travel documents for easy access during your trip.

## API Endpoints
- **POST /api/auth/register** - Register a new user
- **POST /api/auth/login** - Authenticate user and obtain JWT
- **GET /api/itineraries** - Retrieve all itineraries for the logged-in user
- **POST /api/itineraries** - Create a new itinerary
- **PUT /api/itineraries/{id}** - Update an existing itinerary
- **DELETE /api/itineraries/{id}** - Delete an itinerary
