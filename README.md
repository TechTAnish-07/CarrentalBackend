# 🚗 Car Rental Backend – SAngRaj Rentals

Backend service for a **driver-less car rental platform**, built using **Spring Boot** and **PostgreSQL**.  
This backend handles authentication, bookings, car availability, admin operations, and secure user workflows.

🔗 **Live Frontend:** https://sangrajrentalll.netlify.app/

---

## 🧱 Tech Stack

- **Backend:** Spring Boot  
- **Database:** PostgreSQL  
- **Authentication:** JWT (Token-based authentication)  
- **Email Service:** Brevo (Email verification)  
- **ORM:** Spring Data JPA / Hibernate  
- **Build Tool:** Maven  

---

## ✨ Features

### 🔐 Authentication & Security
- User registration with **email verification**
- Secure login using **JWT**
- Role-based access (USER / ADMIN)
- **Admin is initialized from backend only** (for security reasons)

### 🚗 Car & Booking Management
- Display available cars
- Book cars for a selected date & time interval
- Prevent overlapping bookings
- View active bookings and booking history
- Return car and calculate total rental amount

### 👤 User Features
- Register & login
- Book cars
- View current and past bookings
- Secure APIs using JWT

### 🛠 Admin Features
- Admin account created only from backend
- View all bookings
- View active & completed bookings
- Monitor booking history

---

## 🧠 Key Learnings

- JWT authentication & request filtering
- Handling overlapping time-based bookings
- Real-world deployment challenges
- SMTP worked locally but failed in production
- Migrated to **Brevo** for reliable email verification
- Importance of backend-controlled admin access

---

## 🔐 Security Design

- Admin creation restricted to backend only
- Frontend registration enabled only for users
- JWT validated on every secured request
- Stateless authentication (no server sessions)

---

## 📁 Project Structure
src/main/java/com/sangraj/carrental
├── config # Security, JWT, CORS configuration
├── controller # REST controllers
├── entity # JPA entities
├── repository # JPA repositories
├── service # Business logic
├── dto # Request & response DTOs
└── CarrentalApplication.java


---

## ⚙️ Environment Variables

Create `application.properties` (or `application.yml`):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/carrental
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password

jwt.secret=your_jwt_secret_key
jwt.expiration=86400000

brevo.api.key=your_brevo_api_key
brevo.sender.email=no-reply@example.com

# Clone the repository
git clone https://github.com/your-username/car-rental-backend.git

# Navigate to project
cd car-rental-backend

# Run the application
mvn spring-boot:run

