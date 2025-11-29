# AI Summarizer

[![Java](https://img.shields.io/badge/Java-%23ED8B00.svg?logo=openjdk&logoColor=white)](www.java.com)
[![React](https://img.shields.io/badge/React-%2320232a.svg?logo=react&logoColor=%2361DAFB)](https://react.dev/learn)
[![Build with Gradle](https://img.shields.io/badge/build-gradle-brightgreen?logo=gradle)](https://gradle.org/)

## Overview

**AI Summarizer** full-stack application for document and text summarization. Users can upload various file types (such as PDF, DOCX, TXT, PPTX) or paste text, and receive concise or comprehensive summaries.

- **Backend:** Java (Spring Boot), REST API
- **Frontend:** TypeScript (React, Vite, TailwindCSS)
- **Features:** Authentication, user dashboard, history tracking, API usage logs

---

## Table of Contents

- [Features](#features)
- [Demo](#demo)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API](#api)
- [Contributing](#contributing)
- [License](#license)

---

## Features

- ✨ Upload text or various document types for summarization
- 🤖 Choose between brief or detailed summaries
- 📜 View and manage your summarization history
- 👤 Secure authentication and profile management
- 📊 Admin dashboard with API usage analytics
- 🌐 Fast, modern UI

---

## Demo

> _Include screenshots/gif here if available_

---

## Tech Stack

| Layer     | Technology                                  |
|-----------|---------------------------------------------|
| Backend   | Java, Spring Boot, Gradle                   |
| Frontend  | React, TypeScript, Vite, TailwindCSS        |
| Database  | (You can specify: PostgreSQL/MySQL/Other)   |
| Auth      | Spring Security (JWT or Session-based)      |

---

## Project Structure

    ai-summarizer/
    ├── ai-summarizer-backend/         # Java Spring Boot backend
    │   └── src/main/...
    ├── ai-summarizer-frontend-ts/     # TypeScript React frontend
    │   └── src/...
    └── README.md

---

## Getting Started

### Prerequisites

- Java 21
- Node.js (v16+ recommended)
- Yarn or npm

### Clone the repository

```sh
git clone https://github.com/atadurdyyewserdar/ai-summarizer.git
cd ai-summarizer
```

### Backend Setup

```sh
cd ai-summarizer-backend
./gradlew build
./gradlew bootRun
```
_Backend runs on: **http://localhost:8080**_

You may need to configure `application.yml` or `application.properties` for database and AI service endpoints.

### Frontend Setup

```sh
cd ai-summarizer-frontend-ts
npm install         # or: yarn install
npm run dev         # or: yarn dev
```
_Frontend runs on: **http://localhost:5173**_

---

## Contributing

1. Fork the repository
2. Create your feature branch:
    ```sh
    git checkout -b my-new-feature
    ```
3. Commit your changes:
    ```sh
    git commit -am "Add some feature"
    ```
4. Push to the branch:
    ```sh
    git push origin my-new-feature
    ```
5. Open a pull request

---

## License

This project is licensed under the [MIT License](LICENSE).

---

## Acknowledgements

- Built with [Spring Boot](https://spring.io/projects/spring-boot) and [React](https://react.dev/)
- Frontend bootstrapped with [Vite](https://vitejs.dev/)
- UI powered by [Tailwind CSS](https://tailwindcss.com/)

---

**Questions or suggestions?**  
Open an issue or contact [@atadurdyyewserdar](https://github.com/atadurdyyewserdar) on GitHub.
