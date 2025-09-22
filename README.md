# Notification API

Notification API is a microservices-based system designed to send personalized notifications via multiple channels such as **email** and **push**. It leverages **Spring Boot, Spring Cloud (Eureka, Feign, API Gateway)**, and a modular microservice architecture to ensure scalability, reliability, and maintainability.

The system is composed of multiple services, each responsible for a specific function — from managing user details and preferences to building dynamic notification content and finally delivering the notification.

## Features

* **Microservices Architecture**: Independent, loosely coupled services for scalability.
* **Service Discovery (Eureka)**: Automatic registration and discovery of services.
* **API Gateway**: Centralized routing and entry point for client requests.
* **User Details Service**: Stores and manages user information and notification preferences.
* **Notification Content Building**: Dynamically generates personalized notification messages.
* **Notification Sending**: Supports **email** and **push** notifications with failure handling and retry mechanisms.
* **DTO Module**: Standardized request/response objects across services.

## Project Structure

* **APIGateway** → Manages routing and acts as a single entry point.
* **eureka** → Service discovery server for all microservices.
* **input\_dto** → DTOs shared between services (e.g., NotificationRequest, UserInfo).
* **userDetails** → Handles user data and preferences, the entry point for notification flow.
* **notificationContentBuilding** → Builds notification content (email/push) based on templates and preferences.
* **notificationSend** → Sends notifications and handles delivery failures.

## Data Flow

1. **Client Request** → Goes through the **API Gateway**.
2. **User Details Service** → Validates user info and retrieves preferences.
3. **Notification Content Building Service (via Feign)** → Builds personalized notification content.
4. **Notification Send Service (via Feign)** → Delivers the notification (email/push).

## Installation

To set up the Notification API on your local machine:

1. **Clone the Repository**:

   ```bash
   git clone https://github.com/<your-username>/Notification-API.git
   ```

2. **Navigate to the Project Directory**:

   ```bash
   cd Notification-API
   ```

3. **Build All Modules**:

   Use Maven to build the entire multi-module project:

   ```bash
   mvn clean install
   ```

4. **Run Services**:
   Start services in the following order:

   ```bash
   # 1. Start Eureka Server
   cd eureka
   mvn spring-boot:run

   # 2. Start API Gateway
   cd ../APIGateway
   mvn spring-boot:run

   # 3. Start Supporting Services
   cd ../userDetails
   mvn spring-boot:run

   cd ../notificationContentBuilding
   mvn spring-boot:run

   cd ../notificationSend
   mvn spring-boot:run
   ```

## Usage

Once the services are up and running:

* **Register Users & Preferences**: Use the `userDetails` service to store user info and preferences.
* **Send Notification Request**: Send a POST request via **API Gateway**, which routes to `userDetails`.
* **Content Building**: The `notificationContentBuilding` service generates personalized content (via Feign).
* **Delivery**: The `notificationSend` service delivers the message (Email/Push).
* **Failure Handling**: Failed notifications are logged and can be retried.

### Example Notification Request (JSON)

```json
{
  "userId": "123",
  "notificationType": "EMAIL",
  "subject": "Welcome to Notification API",
  "body": "Hello, your account has been successfully created!"
}
```

## Scalability & Resiliency Enhancements

### 1. Service Discovery with Eureka

Enables dynamic discovery of microservices without hardcoding endpoints.

### 2. API Gateway

Provides a single entry point, request routing, and load balancing.

### 3. Feign Clients

Simplifies inter-service communication by using declarative REST clients.

### 4. Fault Tolerance (Retry & DLQ)

Failed notifications are stored in a **Dead Letter Queue (DLQ)** for retries.

---

For any questions or support, please open an issue in this repository.
