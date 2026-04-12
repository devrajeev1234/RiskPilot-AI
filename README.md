# RiskPilot AI — Enterprise Risk Assessment Platform

An intelligent loan risk assessment platform powered by Reinforcement Learning, featuring real-time decision making, role-based analytics, and an AI-driven conversational assistant.

## 🎯 Overview

RiskPilot AI leverages a trained RL (Reinforcement Learning) agent to evaluate loan applications with precision and transparency. The platform provides:

- **Automated Risk Assessment**: RL agent evaluates applications and recommends lending decisions
- **Multi-Role Support**: Borrower, Loan Officer, and Admin portals with tailored dashboards
- **Rule-Based Chatbot**: Intelligent assistant for loan eligibility, risk, and improvement guidance
- **Admin Review & Audit**: Comprehensive tracking, review queues, and compliance logging
- **Real-Time Analytics**: Portfolio monitoring and decision analytics for administrators

## 🏗️ Architecture

### Tech Stack

- **Backend**: Java 17 + Spring Boot 3.2 + Spring Data JPA + Spring Security
- **Frontend**: React 18 + CSS3 with theme support (light/dark)
- **Database**: MySQL 8.0+
- **ML Service**: FastAPI + Python (Reinforcement Learning agent)
- **Containerization**: Docker + Docker Compose

### Project Structure

```
.
├── backend/              # Spring Boot REST API
│   ├── src/
│   │   ├── main/java/com/loanguard/
│   │   │   ├── config/           # DataInitializer, WebConfig
│   │   │   ├── controller/       # REST endpoints
│   │   │   ├── service/          # Business logic
│   │   │   ├── model/            # JPA entities
│   │   │   ├── repository/       # Data access layer
│   │   │   ├── security/         # Auth & security
│   │   │   └── exception/        # Error handling
│   │   └── resources/
│   │       ├── application.yml   # Configuration
│   │       └── db/migration/     # Flyway migrations
│   ├── pom.xml
│   └── Dockerfile
├── frontend/             # React SPA
│   ├── src/
│   │   ├── components/   # Reusable UI components
│   │   ├── pages/        # Route pages
│   │   ├── context/      # React contexts (auth, theme)
│   │   ├── services/     # API client
│   │   └── styles/       # Global CSS & design tokens
│   ├── public/
│   ├── package.json
│   └── Dockerfile
├── ml-service/           # FastAPI RL Agent
│   ├── main.py          # FastAPI app
│   ├── rl_agent.py      # RL agent implementation
│   ├── environment.py    # Loan environment simulator
│   ├── train_agent.py    # Training script
│   ├── requirements.txt
│   └── Dockerfile
├── docker-compose.yml    # Orchestrate all services
├── schema.sql           # Database schema
└── README.md           # This file
```

## 🚀 Quick Start

### Prerequisites

- Docker & Docker Compose
- Node.js 18+ (for frontend development)
- Java 17+ (for backend development)
- Python 3.11+ (for ML service)

### Using Docker Compose

```bash
# Navigate to project root
cd LoanGaurd-new

# Build and start all services
docker compose up -d --build

# Services will be available at:
# Frontend: http://localhost:3000
# Backend API: http://localhost:8080/api
# ML Service: http://localhost:8000
# MySQL: localhost:3306
```

### Local Development

#### Backend

```bash
cd backend
mvn clean package
mvn spring-boot:run
# API available at http://localhost:8080/api
```

#### Frontend

```bash
cd frontend
npm install
# Add Google Client ID to .env.local
echo "REACT_APP_GOOGLE_CLIENT_ID=your_client_id_here" > .env.local
npm start
# App available at http://localhost:3000
```

#### ML Service

```bash
cd ml-service
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
pip install -r requirements.txt
python train_agent.py  # Train the RL agent first
python main.py         # Start FastAPI server
# Service available at http://localhost:8000
```

## 🔐 Authentication

RiskPilot AI uses **Google Authentication only** (OAuth 2.0).

### Setup Google OAuth

1. Create a Google OAuth 2.0 project at [Google Cloud Console](https://console.cloud.google.com/)
2. Create a Web Application credential and get your **Client ID**
3. Add redirect URIs:
   - `http://localhost:3000/auth` (development)
   - `https://yourdomain.com/auth` (production)
4. Set `REACT_APP_GOOGLE_CLIENT_ID` in `frontend/.env.local`

## 📋 Environment Variables

### Frontend (`.env.local`)

```
REACT_APP_GOOGLE_CLIENT_ID=your_google_client_id
REACT_APP_API_BASE=http://localhost:8080/api
```

### Backend (`application.yml`)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/loanguard
    username: root
    password: root_password
  jpa:
    hibernate:
      ddl-auto: validate
```

### ML Service (environment)

Set via Docker or in `main.py`:

```
AGENT_PATH=saved_model/rl_agent.json
HIGH_CONFIDENCE_THRESHOLD=0.75
```

## 🔌 API Endpoints

### Authentication

- `POST /api/auth/social` — Google sign-in
- `POST /api/auth/logout` — Sign out

### Loan Management

- `POST /api/loans/create` — Submit new application
- `GET /api/loans/{id}` — Get application details
- `GET /api/loans/history` — User's loan history
- `POST /api/loans/{id}/review` — Officer review

### Chat

- `POST /api/chat/message` — Send message to assistant
- `GET /api/chat/session/{sessionId}` — Retrieve chat history

### Admin

- `GET /api/admin/analytics` — Portfolio analytics
- `GET /api/admin/review-queue` — Pending applications
- `GET /api/admin/audit-log` — Audit trail

For full API docs, run backend and visit `http://localhost:8080/swagger-ui.html`

## 🧠 RL Agent Training

The ML service includes a trained RL agent that learns optimal lending decisions through simulation.

### Training the Agent

```bash
cd ml-service
python train_agent.py --episodes 100000 --report-every 10000
```

This:
1. Simulates 100,000+ loan scenarios
2. Learns Q-values for different borrower profiles
3. Saves trained model to `saved_model/rl_agent.json`

### Agent Decision Logic

- **Input**: Borrower's income, debt, loan amount, employment history
- **Output**: Recommendation (REJECT, APPROVE 8%, APPROVE 12%, APPROVE 16%, REVIEW)
- **Confidence**: Trust score determining if admin review is needed

## 🎨 UI Features

### Role-Based Portals

- **Borrower**: Apply for loans, track status, view rates
- **Loan Officer**: Review applications, manage queue, audit trails
- **Admin**: System analytics, user management, compliance reports

### Responsive Design

- Light/Dark theme toggle
- Mobile-optimized layouts
- Real-time status updates

### Chatbot Assistant

Rule-based conversational AI that helps users with:

- Loan eligibility questions
- Risk score explanations
- Interest rate guidance
- Application improvement tips

## 📊 Database Schema

Key tables:

- `users` — User accounts with roles
- `loan_applications` — Loan requests and outcomes
- `chat_messages` — Conversation history
- `audit_logs` — System activity tracking
- `notifications` — User alerts

## 🔍 Admin Features

- **Analytics Dashboard**: Approval rates, default patterns, risk distribution
- **Review Queue**: Pending applications from the RL agent
- **Audit Log**: Complete record of all system actions
- **User Management**: Create/edit accounts and roles

## 🧪 Testing

### Frontend

```bash
cd frontend
npm test
```

### Backend

```bash
cd backend
mvn test
```

### ML Service

```bash
cd ml-service
pytest
```

## 📦 Deployment

### Docker Hub

```bash
# Build images
docker build -t yourusername/riskpilot-ai:latest backend/
docker build -t yourusername/riskpilot-ai-frontend:latest frontend/
docker build -t yourusername/riskpilot-ai-ml:latest ml-service/

# Push
docker push yourusername/riskpilot-ai:latest
docker push yourusername/riskpilot-ai-frontend:latest
docker push yourusername/riskpilot-ai-ml:latest
```

### Cloud Deployment

Recommended platforms:
- **Azure**: Container Apps, App Service, Functions
- **AWS**: ECS, Elastic Beanstalk, Lambda
- **GCP**: Cloud Run, App Engine

## 🐛 Troubleshooting

### Backend won't connect to database

```bash
# Check MySQL is running
docker ps | grep mysql

# Verify credentials in application.yml
# Default: user=root, password=root
```

### Frontend can't reach backend API

```bash
# Set correct API base URL
echo "REACT_APP_API_BASE=http://localhost:8080/api" > frontend/.env.local
```

### ML agent not found

```bash
# Train the agent first
cd ml-service
python train_agent.py
```

## 📝 License

Proprietary — All rights reserved

## 🤝 Support

For issues or questions, contact the development team or check the [project board](https://github.com/yourusername/riskpilot-ai/issues).

---

**Version**: 3.0.0  
**Last Updated**: April 2026  
**Status**: Production Ready
