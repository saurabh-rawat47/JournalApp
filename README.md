
# 🗒️ Journal App

A modular, sentiment-aware journaling platform built for expressive writing, emotional insight, and seamless user experience. It features a visually rich frontend paired with a clean, secure backend architecture — designed to feel intuitive while remaining methodically robust.

---

## 🎨 Frontend Overview

Developed in **Cursor IDE**, the Journal App’s frontend focuses on animation-driven, responsive design with a clear component hierarchy and fluid UX.

### Key Highlights

- ✨ **Framer Motion Animations** — Page transitions, modals, and micro-interactions  
- 🎯 **Tailwind CSS** — Utility-first styling with a custom visual palette  
- 🔐 **JWT Authentication** — Secure and persistent user login flow  
- 📝 **Journal Management** — Create, view, edit, and delete entries with emotion tagging  
- 🔍 **Search & Filter** — Realtime search and sentiment-based filtering  
- 📱 **Responsive Layout** — Optimized across mobile, tablet, and desktop devices  

### Architecture

```
frontend/
├── components/         # Login, Dashboard, Journal Modal
├── contexts/           # Authentication state
├── services/           # API communication via Axios
├── types/              # TypeScript interfaces and props
├── App.tsx             # Routing and global layout
└── index.css           # Global Tailwind styling
```

---

## 🔧 Backend Overview

Built in **IntelliJ IDEA** using **Spring Boot** and **MongoDB Atlas**, the backend is designed around modular service architecture, clear authentication flow, and sentiment-enhanced journal processing.

### Key Highlights

- 🔒 **JWT-Based Auth** — Stateless token handling and secured endpoints  
- 🗃️ **MongoDB Atlas** — NoSQL document storage for users and journals  
- 🧠 **Sentiment Analysis** — Emotional categorization of journal entries  
- 📦 **RESTful APIs** — Organized endpoint structure for frontend integration  
- 🧼 **Clean Codebase** — Service separation, DTO layering, and predictable patterns  

### Architecture

```
backend/
├── config/             # JWT config, CORS policies, security setup
├── controller/         # Auth & journal APIs
├── dto/                # Input/output data models
├── model/              # MongoDB schema definitions
├── repository/         # Spring Data interfaces
├── service/            # Business logic abstraction
├── util/               # Helper utilities (e.g., token, sentiment)
└── application.properties  # Environment config
```
```

