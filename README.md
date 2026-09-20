# 🛡️ Password Health Analytics

An intelligent, privacy-first Password Security Analyzer and Global Health Dashboard built with **Spring Boot 3**, **MS SQL Server**, and **React**.

---

## 🌟 Key Features

- **Real-Time Password Analysis**: Evaluates password length, character sets, and complexity using **Shannon Entropy** ($E = L \times \log_2(R)$).
- **Crack Time Estimation**: Calculates approximate time needed for brute-force attacks.
- **Pattern & Weakness Detection**: Identifies repetitive sequences, sequential numbers, common dictionary words, and embedded dates using regex logic.
- **k-Anonymity Leak Checking**: Securely queries the **Have I Been Pwned (HIBP)** API using SHA-1 prefixing (first 5 characters) without exposing full user credentials.
- **Privacy-By-Design Storage**: Never stores actual passwords. Only saves anonymous metadata (length, entropy score, category, breach status) in **MS SQL Server**.
- **Analytics Dashboard API**: Aggregates enterprise-wide password health metrics and category distributions (`WEAK`, `MODERATE`, `STRONG`, `CRITICAL_BREACH`).

---

## 🏗️ Tech Stack

* **Backend**: Java 21+, Spring Boot 3, Spring Data JPA, Hibernate, Lombok
* **Database**: Microsoft SQL Server
* **Frontend**: React, Vite, Tailwind CSS, Recharts, Lucide Icons
* **External APIs**: Have I Been Pwned (Pwned Passwords API v3)

---

## 🚀 Getting Started

### Prerequisites

* Java Development Kit (JDK 21 or later)
* Node.js & npm
* Microsoft SQL Server
* IDE (IntelliJ IDEA or VS Code)

---