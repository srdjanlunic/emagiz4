# eMagiz4 — Vulnerability Management System

A full-stack web application for tracking, assessing and managing software
vulnerabilities (CVEs) across an organization's registered systems. It lets
security teams register systems, monitor known CVEs, escalate high-risk
findings and keep owners informed through notifications and dashboards.

Built as a Module 4 project at the University of Twente.

## About

**Vulnerability Management System** — a Vue 3 + Java (Jakarta EE) application
for registering IT systems, tracking their CVEs, escalating high-severity
vulnerabilities and notifying the responsible owners, with role-based access
and analytics dashboards.

## Features

- **Authentication & authorization** — user registration and login with
  BCrypt-hashed passwords and role-based access.
- **System registration** — register and manage IT systems/assets, their
  owners and departments.
- **CVE tracking** — browse a list of CVEs, view details, and see severity,
  status and suggested fixes per system.
- **Escalations** — escalate high-risk vulnerabilities through an escalation
  pool workflow.
- **Notifications** — in-app notifications to keep system owners informed.
- **Admin panel** — user management, department management and system-owner
  administration.
- **Dashboards** — Chart.js-based analytics for an at-a-glance overview.

## Tech stack

**Frontend**
- Vue 3 (`<script setup>` SFCs)
- Vite
- Pinia (state management)
- Vue Router
- Chart.js / vue-chartjs
- Material Design Icons

**Backend**
- Java 11, packaged as a WAR
- Jakarta EE Servlet + Jersey (JAX-RS REST API)
- HikariCP connection pooling
- Jackson (JSON)
- jBCrypt (password hashing)
- Maven (build), Flyway (migrations)

**Database**
- PostgreSQL

## Project structure

```
.
├── src/
│   ├── main/java/        # Java backend (config, service, REST resources, DAO)
│   ├── components/       # Vue components
│   ├── views/            # Vue pages (auth, cve, systems, escalations, admin…)
│   ├── stores/           # Pinia stores (auth, cves, systems, escalations…)
│   ├── router/           # Vue Router config
│   └── layouts/          # Layout components
├── scripts/              # SQL scripts (schema.sql, seed.sql, clear.sql)
├── docs/                 # Feature/design documentation
├── public/               # Static assets
├── pom.xml               # Maven config (backend)
├── package.json          # npm config (frontend)
└── vite.config.js        # Vite config (dev server + /api proxy)
```

## Prerequisites

- Node.js 18+ and npm
- Java 11 (JDK)
- Maven 3.9+
- PostgreSQL 13+

## Getting started

### 1. Database

Create a PostgreSQL database and apply the schema and seed data:

```bash
psql -d your_database -f scripts/schema.sql
psql -d your_database -f scripts/seed.sql
```

Configure the connection by adding a `database.properties` file on the
backend classpath (e.g. `src/main/resources/database.properties`):

```properties
db.url=jdbc:postgresql://localhost:5432/your_database
db.username=your_user
db.password=your_password
```

> The application reads credentials from `database.properties`. Keep this
> file out of version control (it is not committed) and never hardcode real
> credentials in source.

### 2. Backend

The backend runs on Jetty via the Maven plugin (port `8088`), or can be
deployed as a WAR to Tomcat under `/vulnerability-management-system`.

```bash
mvn clean package        # build the WAR
mvn jetty:run            # run locally on http://localhost:8088
```

### 3. Frontend

```bash
npm install
npm run dev              # Vite dev server (default http://localhost:5173)
```

The Vite dev server proxies `/api` to the backend at
`http://localhost:8080/vulnerability-management-system` (see
[vite.config.js](vite.config.js)). Adjust the proxy target if your backend
runs elsewhere.

### Build for production

```bash
npm run build            # outputs to dist/
```

## Documentation

Additional design and feature docs live in [docs/](docs/), including the
authentication system, CVE severity/status handling, escalation pool,
database configuration and the vulnerability service.

## Authors

Developed by the eMagiz4 team as a Module 4 university project.

## License

This project was created for educational purposes as part of university
coursework. No specific license is applied.
