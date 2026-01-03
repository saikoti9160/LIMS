# Laboratory Information Management System (LIMS)

This repository contains the source code for the LIMS project, designed for hospital environments to manage laboratory operations efficiently.

## Project Structure

The project is divided into two main parts:

### 1. Backend (`back-end-lims`)
A microservices-based architecture built with Java and Spring Boot. It handles the core logic, data management, and services for the LIMS.

**Key Services:**
- `auth-service`: Authentication and Authorization.
- `inventory-service`: Manages laboratory inventory.
- `lab-management-service`: Core laboratory operations.
- `lims-core`: Shared core libraries and utilities.
- `master-data-service`: Manages master data.
- `reports-analytics-service`: Reporting and analytics.

**Build Tool:** Maven

### 2. Frontend (`lims-web-app`)
A web-based user interface built with React.js (Create React App). It provides the dashboard and interaction layer for users.

**Build Tool:** Node.js / npm

## Getting Started

### Prerequisites
- Java JDK 17 or higher
- Maven
- Node.js & npm

### Backend Setup
Navigate to each service directory (or the parent if configured as a multi-module project) and run:
```bash
mvn clean install
```

### Frontend Setup
Navigate to the `lims-web-app` directory:
```bash
cd lims-web-app
npm install
npm start
```
