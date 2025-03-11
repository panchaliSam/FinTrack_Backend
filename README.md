# FinTrack_Backend
Financial Tracking System

# **FinTrack**

FinTrack is a financial tracking application that allows users to manage their financial transactions, budgets, savings, and financial goals effectively.

## **Table of Contents**
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
  - [User API](#user-api)
  - [Transaction API](#transaction-api)
  - [Report API](#report-api)
  - [Budget API](#budget-api)
  - [Goals API](#goals-api)
  - [Savings API](#savings-api)
  - [Currency API](#currency-api)
- [Running Tests](#running-tests)
- [Environment Variables](#environment-variables)
- [Contributors](#contributors)

---

## **Getting Started**
This guide provides instructions to set up and run the project on your local machine.

---

## **Project Structure**

<img width="410" alt="Screenshot 2025-03-11 at 11 16 28" src="https://github.com/user-attachments/assets/6d769b0f-9f11-4422-a0ed-7f7c5ffbd763" />


---

## **Prerequisites**
Ensure you have the following installed:
- **Java 21 (JDK 21)**
- **MongoDB**
- **IntelliJ IDEA** (Recommended for development)
- **Postman** (For API testing)

---

## **API Documentation**

### **User API**
| Method   | Endpoint                 | Description         |
|----------|--------------------------|---------------------|
| `GET`    | `/api/user`              | Get all users       |
| `GET`    | `/api/user/{id}`         | Get user by ID      |
| `POST`   | `/api/user/sign-up`      | Register a new user |
| `POST`   | `/api/user/sign-in`      | User login          |
| `PUT`    | `/api/user/{id}`         | Update user details |
| `DELETE` | `/api/user/{id}`         | Delete user         |

### **Transaction API**
| Method   | Endpoint                   | Description              |
|----------|----------------------------|--------------------------|
| `GET`    | `/api/transaction`         | Get all transactions     |
| `GET`    | `/api/transaction/{id}`    | Get transaction by ID    |
| `POST`   | `/api/transaction`         | Create a new transaction |
| `PUT`    | `/api/transaction/{id}`    | Update transaction       |
| `DELETE` | `/api/transaction/{id}`    | Delete transaction       |

### **Report API**
| Method   | Endpoint                                                      | Description                     |
|----------|----------------------------------------------------------------|---------------------------------|
| `GET`    | `/api/report/income-vs-expenses?userId={userId}&startDate={startDate}&endDate={endDate}` | Get income vs expenses report |
| `GET`    | `/api/report/total-income`                                     | Get total income               |
| `GET`    | `/api/report/total-expense`                                    | Get total expense              |
| `GET`    | `/api/report/spending-trends?userId={userId}&startDate={startDate}&endDate={endDate}` | Get spending trends |
| `GET`    | `/api/report/filter?userId={userId}&category={category}&tag={tag}&startDate={startDate}&endDate={endDate}` | Filter transactions by category and tag |
| `GET`    | `/api/report/total-income/year?year={year}`                    | Get total income for a year    |
| `GET`    | `/api/report/total-expense/year?year={year}`                    | Get total expense for a year   |
| `GET`    | `/api/report/total-budget-income/year?year={year}`              | Get total budgeted income for a year |
| `GET`    | `/api/report/total-budget-expense/year?year={year}`             | Get total budgeted expense for a year |
| `GET`    | `/api/report/download?userId={userId}&year={year}`              | Generate financial report      |

### **Budget API**
| Method   | Endpoint               | Description       |
|----------|------------------------|-------------------|
| `GET`    | `/api/budget`          | Get all budgets  |
| `GET`    | `/api/budget/{id}`     | Get budget by ID |
| `POST`   | `/api/budget`          | Create a new budget |
| `PUT`    | `/api/budget/{id}`     | Update a budget  |
| `DELETE` | `/api/budget/{id}`     | Delete a budget  |

### **Goals API**
| Method   | Endpoint                | Description      |
|----------|-------------------------|------------------|
| `GET`    | `/api/goals`            | Get all goals   |
| `GET`    | `/api/goals/{id}`       | Get goal by ID  |
| `POST`   | `/api/goals`            | Create a goal   |
| `PUT`    | `/api/goals/{id}`       | Update a goal   |
| `DELETE` | `/api/goals/{id}`       | Delete a goal   |
| `GET`    | `/api/goals/{id}/monthly-progress` | Get monthly progress of a goal |

### **Savings API**
| Method   | Endpoint                  | Description            |
|----------|---------------------------|------------------------|
| `GET`    | `/api/savings`            | Get all savings       |
| `GET`    | `/api/savings/total-savings` | Get total savings amount |
| `PUT`    | `/api/savings/{id}`       | Update savings amount |

### **Currency API**
| Method   | Endpoint                                            | Description           |
|----------|----------------------------------------------------|-----------------------|
| `GET`    | `/api/currency/convert?from={currency1}&to={currency2}&amount={amount}` | Convert currency amount |

---

## **Running Tests**
The project uses **JUnit 5** for unit testing.

### **Running All Tests**
```sh
./mvnw test
