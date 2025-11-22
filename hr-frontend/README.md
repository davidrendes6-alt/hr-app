# HR Application - Frontend

A single-page React application for employee profile management, absence requests, and coworker feedback with AI enhancement capabilities.

## 🏗️ Architecture Overview

This frontend application is part of a three-service architecture:

### Backend Services

1. **Auth Service** (`http://localhost:8001`)
   - Handles user authentication and authorization
   - JWT token management
   - User validation

2. **HR Service** (`http://localhost:8002`)
   - Employee profile management (CRUD operations)
   - Absence request management
   - Feedback system
   - Permission-based data access

3. **AI Service** (`http://localhost:8003`)
   - Text polishing using HuggingFace models
   - Feedback enhancement
   - Grammar and tone improvement

### Frontend Architecture

- **Framework**: React 19 with TypeScript
- **State Management**: Zustand (for auth state)
- **Routing**: React Router v7
- **HTTP Client**: Axios with service-specific instances
- **Build Tool**: Vite

## 🚀 Getting Started

### Prerequisites

- Node.js 18+ and npm
- Backend services running (auth-service, hr-service, ai-service)

### Installation

```bash
npm install
```

### Running the Application

```bash
npm run dev
```

The application will be available at `http://localhost:5173`

### Building for Production

```bash
npm run build
npm run preview
```

## 📋 Features

### Role-Based Access Control

The application supports two primary roles with specific permissions:

#### 1. **Employee Role**
- ✅ View own complete profile (including sensitive data)
- ✅ Edit own profile information
- ✅ Request absence from work
- ✅ View coworker profiles (non-sensitive data only)
- ✅ Leave feedback on coworker profiles
- ✅ Use AI to polish feedback

#### 2. **Manager Role**
- ✅ All employee permissions
- ✅ View and edit all employee profiles (including sensitive data)
- ✅ Approve/reject absence requests
- ✅ Access manager dashboard

### Key Functionality

#### Profile Management
- **Full Profile View**: Employees see all their data including sensitive information (salary, SSN, contact details)
- **Public Profile View**: Coworkers see limited, non-sensitive information (name, email, department, position)
- **Edit Capabilities**: Profile owners and managers can edit all profile fields
- **Sensitive Data Protection**: SSN is always masked (shows last 4 digits only)

#### Feedback System
- **Leave Feedback**: Any employee can leave feedback on a coworker's profile
- **AI Enhancement**: Optional AI polishing improves grammar, tone, and professionalism
- **Feedback History**: All feedback is displayed with author info and timestamps
- **AI Badge**: Polished feedback is marked with a visual indicator

#### Absence Requests
- **Create Request**: Employees can request time off with date ranges and reasons
- **Manager Review**: Managers see all pending requests in their dashboard
- **Approval/Rejection**: Managers can approve or reject absence requests
- **Status Tracking**: Requests show current status (pending, approved, rejected)

## 🗂️ Project Structure

```
src/
├── api/
│   ├── client.ts          # Axios instances for each service
│   ├── authService.ts     # Auth-related API calls
│   ├── hrService.ts       # HR-related API calls (profiles, absences, feedback)
│   └── aiService.ts       # AI text polishing API calls
├── components/
│   ├── Layout.tsx         # Main layout with navigation
│   └── Navbar.tsx         # Navigation bar
├── hooks/
│   └── useAuth.ts         # Zustand auth state management
├── pages/
│   ├── Login.tsx          # Authentication page
│   ├── Profile.tsx        # Own profile with edit capability
│   ├── CoworkerProfile.tsx # View coworker + leave feedback
│   ├── ManagerProfile.tsx  # Manager dashboard for absence approvals
│   └── AbsenceRequest.tsx  # Form to request absence
├── types/
│   └── index.ts           # TypeScript type definitions
├── App.tsx                # Main app with routing
└── main.tsx              # Entry point
```

## 🔑 API Configuration

The application connects to three separate backend services. Update the base URLs in `src/api/client.ts` if needed:

```typescript
export const authApi = createApiClient("http://localhost:8001");
export const hrApi = createApiClient("http://localhost:8002");
export const aiApi = createApiClient("http://localhost:8003");
```

## 🔐 Authentication Flow

1. User logs in via `/login` page
2. Auth service validates credentials and returns JWT token + user info
3. Token is stored in localStorage
4. All subsequent requests include the token in Authorization header
5. 401 responses automatically redirect to login page

## 📊 Data Privacy & Security

### Sensitive vs. Non-Sensitive Data

**Sensitive Data** (only visible to owner and managers):
- Salary
- Phone number
- Address
- Emergency contact
- Bank account
- SSN (always masked)

**Non-Sensitive Data** (visible to all coworkers):
- Name
- Email
- Department
- Position

### Permission System

The backend enforces permissions, but the frontend also implements:
- Conditional rendering based on user role
- Route protection for manager-only pages
- API request filtering based on permissions

## 🎨 Future Improvements

Given more time, the following enhancements would be valuable:

### Frontend
1. **Enhanced UI/UX**
   - More polished styling with a design system (e.g., Material-UI, Tailwind)
   - Loading skeletons instead of simple spinners
   - Toast notifications for actions
   - Dark mode support

2. **Form Validation**
   - Client-side validation with libraries like Zod or Yup
   - Real-time validation feedback
   - Better error messages

3. **Search & Filtering**
   - Search for coworkers by name, department, or position
   - Filter absence requests by date range or status
   - Pagination for large datasets

4. **Real-time Updates**
   - WebSocket integration for live notifications
   - Real-time absence request status updates
   - Live feedback notifications

5. **Testing**
   - Unit tests with Vitest
   - Component tests with React Testing Library
   - E2E tests with Playwright

6. **Accessibility**
   - ARIA labels and roles
   - Keyboard navigation
   - Screen reader support
   - Focus management

### Backend Integration
1. **Error Handling**
   - More granular error types
   - Retry logic for failed requests
   - Offline support with service workers

2. **Caching**
   - React Query or SWR for data caching
   - Optimistic updates
   - Background refetching

3. **File Uploads**
   - Profile pictures
   - Document attachments for absence requests
   - Feedback attachments

### Features
1. **Advanced Feedback**
   - Ratings/stars in addition to text
   - Categories (teamwork, communication, technical skills)
   - Anonymous feedback option

2. **Absence Calendar**
   - Visual calendar view of absences
   - Team availability overview
   - Conflict detection

3. **Notifications**
   - Email/push notifications for absence decisions
   - Feedback notifications
   - Deadline reminders

4. **Analytics Dashboard**
   - Absence trends
   - Department statistics
   - Feedback metrics

5. **Multi-language Support**
   - i18n integration
   - Language selector
   - Localized dates and currencies

## 📝 Notes on Implementation

### Pragmatic Choices Made

1. **Simple State Management**: Used Zustand only for auth state; local component state for everything else. For larger apps, consider React Query or Redux.

2. **Basic Styling**: Minimal CSS to demonstrate functionality. In production, use a UI library or design system.

3. **No Backend Mocking**: Assumes backend services are running. For development without backends, add MSW (Mock Service Worker).

4. **Limited Error Handling**: Basic try-catch blocks. Production apps need comprehensive error boundaries and logging.

5. **No Test Coverage**: Focused on feature implementation. Real projects require thorough testing.

6. **Simplified Permissions**: Role-based (employee/manager) rather than granular permission system. Fine-grained permissions would require a more complex RBAC system.

## 🛠️ Development

### Code Quality

```bash
# Run linter
npm run lint

# Type check
npm run type-check  # (if you add this script: tsc --noEmit)
```

### Environment Variables

For production deployments, use environment variables for API URLs:

```env
VITE_AUTH_API_URL=https://auth.yourapp.com
VITE_HR_API_URL=https://hr.yourapp.com
VITE_AI_API_URL=https://ai.yourapp.com
```

Update `src/api/client.ts` to use:
```typescript
const authApi = createApiClient(import.meta.env.VITE_AUTH_API_URL);
```

## 📄 License

This is a coding assignment project.

---

**Built with ❤️ using React, TypeScript, and Vite**
