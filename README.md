# 🧾 Employee Management System

This is a **Java-based Employee Management System** built using **IntelliJ IDEA** and **MySQL**. The system allows employees and admins to manage records such as employee name, ID, department, and attendance.

---

## 🚀 Features

- 🧑 Add new employees
- ✅ Mark attendance
- 📋 View employee records
- ❌ Delete records
- 🖥️ User-friendly GUI using `JFrame`
- 🗃️ Database integration with **MySQL**

---

## 💻 Tech Stack

| Tool           | Purpose                        |
|----------------|--------------------------------|
| Java           | Backend logic                  |
| Swing (JFrame) | GUI interface                  |
| MySQL          | Database storage               |
| IntelliJ IDEA  | IDE used for development       |
| JDBC           | Java-MySQL connectivity        |

---

## 🏁 Getting Started

### 🔧 Requirements:
- Java 8+
- IntelliJ IDEA
- MySQL Server

### ⚙️ Setup Steps:
1. Clone the repo or download the source files.
2. Open the project in **IntelliJ IDEA**.
3. Create a MySQL database named: `employees`
4. Update your database credentials in the Java file:
   ```java
   String DB_URL = "jdbc:mysql://localhost:3306/employees";
   String DB_USER = "root";
   String DB_PASSWORD = "Kc@#1234"; // replace with your MySQL password
