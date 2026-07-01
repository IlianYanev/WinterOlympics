# ❄️ Winter Olympics Management System

[![Java](https://img.shields.io/badge/Java-17-007396?logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

## 👨‍💻 Author
* **Ilian Yanev**
* **Faculty Number:** F115564

---

## 📋 Description
This system is a comprehensive platform designed for managing Winter Olympic Games. The project enables the administration of sports competitions, real-time results tracking, automated ranking calculations, and in-depth analysis of Olympic statistics.

## 🚀 Key Features
- **Competition Management:** Full CRUD operations for Ski Slalom and Biathlon events.
- **Intelligent Results Logic:**
  - Multi-run support for Ski Slalom with qualification logic (Top 5).
  - Automated penalty calculations for Biathlon.
  - Handling of specific statuses: **DNF** (Did Not Finish) and **DNQ** (Did Not Qualify).
- **Security & Access Control:**
  - Role-based authentication (ADMIN / ATHLETE).
  - Data privacy: Athletes can only access their own profiles and statistics.
- **Olympic Dashboard:**
  - Automated Medal Tally (Gold/Silver/Bronze).
  - Average participant age calculation.
  - Dynamic identification of the youngest and oldest medalists.

---

## 🛠 Technologies Used
| Technology | Description |
| :--- | :--- |
| **Backend** | Java 17, Spring Boot 3.x |
| **Database** | SQL Server |
| **ORM** | Spring Data JPA / Hibernate |
| **Frontend** | Thymeleaf, HTML5, CSS3 |
| **Security** | Spring Security |

---

## 🗄 Database Schema (ER Diagram)

<img width="720" height="1030" alt="WinterOlympicsER" src="https://github.com/user-attachments/assets/cf940f8a-a92b-4b3a-9c22-20a114115a38" />


## ⚙️ Getting Started

1. **Clone the repository:**
   `git clone [https://github.com/your-repo/winter-olympics.git](https://github.com/your-repo/winter-olympics.git)`

2. **Configure the database:**
   Open src/main/resources/application.properties and update the settings to match your SQL Server environment:
   `spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=WinterOlympics`
   `spring.datasource.username=your_user`
   `spring.datasource.password=your_password`

3. **Run the project:**
   - Using IntelliJ: Click Run on WinterOlympicsApplication.java.
   - Using terminal: mvn spring-boot:run.

4. **Access the application:**
   Open http://localhost:8080 in your web browser.
