# 📁 Full Stack System Design App — Folder Structure

This is the proposed directory layout for your full-stack web app with system design focus:

```
weekends-full-stack-project/
│
├── 00-business-requirements/           # Use cases, personas, MVP scope
│   └── user-stories.md
│
├── 01-system-design/                   # Architecture diagrams and decisions
│   ├── high-level-architecture.md
│   ├── low-level-design.md
│   └── sequence-diagrams/
│
├── 02-ui-ux-design/                    # Figma/penpot files or PNGs
│   ├── wireframes/
│   └── user-flow.md
│
├── 03-frontend/                        # Angular or React app
│   └── src/
│
├── 04-backend/                         # Spring Boot or Node.js
│   ├── src/
│   └── docs/
│
├── 05-database/                        # ER diagrams, schema.sql or models
│
├── 06-authentication/                 # JWT/session, role-based access
│
├── 07-devops/                          # Docker, GitHub Actions
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── ci-cd-pipeline.yaml
│
├── 08-deployment/                     # AWS/Vercel deployment docs
│   ├── frontend.md
│   └── backend.md
│
├── assets/                            # Screenshots, design files
└── README.md
```

> Follow this structure to ensure your full-stack project remains clean, scalable, and ready for both interviews and production.

# 🌐 Domain-Specific Full Stack System Design Projects

These projects are built around **real-world industries** where documentation, workflows, and business logic are rich and publicly available.

---

## 1. 🏥 **Healthcare Appointment & EMR System**

### Domain: Healthcare / Telemedicine
**Objective:** Allow patients to book appointments, doctors to manage slots, and admin to monitor records.

- Patient registration, appointment booking
- Doctor schedule management
- Electronic Medical Records (EMR)
- Prescription generation
- Role-based access (Admin, Doctor, Patient)
- Reports dashboard

> ✅ Great for exploring HIPAA compliance structure, database normalization, role-based access, JWT security, etc.

---

## 2. 🛫 **Airline Cargo Management System**

### Domain: Logistics / Aviation
**Objective:** Manage cargo booking, tracking, space allocation, and pricing rules.

- Cargo booking and confirmation
- Route planning & leg management
- Weight & volume capacity logic
- Queuing & priority management
- eQuote vs Instant Pricing Rule engine

> ✅ Ideal for complex business rule processing, rule engine integration, and HLD modeling.

---

## 3. 🏫 **Online Learning Platform (LMS)**

### Domain: EdTech
**Objective:** Allow instructors to post courses and students to enroll, learn, and track progress.

- Course creation & enrollment
- Video content upload & preview
- Quiz/test module
- Certificates on completion
- Admin dashboard (course stats, user metrics)

> ✅ Good for modular design, microservices, media streaming, and RBAC (role-based access control).

---

## 4. 🏦 **Loan Approval & Credit Score Portal**

### Domain: FinTech / Lending
**Objective:** Users apply for loans, backend checks eligibility and shows personalized offers.

- Loan application form
- Document upload (KYC/ID proof)
- Credit score validation (mock external API)
- EMI calculator & offer generation
- Status tracking (e.g., approved, pending)

> ✅ Teaches external API integration, secure file storage, financial logic, and notification workflows.

---

> ✍️ You can easily find PDFs, ERDs, and documentation for these domains online from open-source projects or case studies. Start with healthcare or airline cargo—they have well-defined flows.
