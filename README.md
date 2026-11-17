
Payment Service is a microservice of the BookNPlay Sports Turf Booking Platform. It is responsible for handling payment-related operations, including initiating payments, processing payment requests, and managing payment status. It maintains payment records and integrates with the Booking Service to update booking details based on payment outcomes. The service also integrates with the Notification Service to trigger payment-related email notifications.

Technology Stack: Java 21, Spring Boot, Spring Data JPA, Spring Security, MySQL, Maven, and Eureka Client.

Configuration: Service Name: payment-service | Port: 8085 | Database: payment_db | Eureka Server: http://localhost:8761

The service is part of the BookNPlay microservices architecture and communicates with other services through REST APIs.
 
