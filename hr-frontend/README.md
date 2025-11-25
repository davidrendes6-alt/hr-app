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
- ✅ View all employee profiles with complete sensitive data
- ✅ Edit any employee profile (all fields including salary, contact info)
- ✅ Browse employee directory
- ✅ Approve/reject absence requests
- ✅ Access manager dashboard with pending requests

### Key Functionality

#### Profile Management
- **Full Profile View**: Employees see all their data including sensitive information (salary, SSN, contact details)
- **Public Profile View**: Regular employees see limited, non-sensitive information of coworkers (name, email, department, position)
- **Manager Profile View**: Managers see complete employee profiles with all sensitive data (salary, phone, address, emergency contact, SSN)
- **Edit Capabilities**: 
  - Employees can edit their own profile
  - Managers can edit any employee's profile with full access to all fields
- **Sensitive Data Protection**: SSN is always masked (shows last 4 digits only), even for managers
- **Employee Directory**: Searchable grid of all employees with filtering by name, email, department, or position

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
│   └── hrService.ts       # HR-related API calls (profiles, absences, feedback with AI polishing)
├── components/
│   ├── Layout.tsx         # Main layout with navigation
│   ├── Navbar.tsx         # Modern navigation bar with icons and user info
│   └── EmployeeList.tsx   # Employee directory with search functionality
├── hooks/
│   └── useAuth.ts         # Zustand auth state management
├── pages/
│   ├── Login.tsx          # Authentication page
│   ├── Profile.tsx        # Own profile with edit capability
│   ├── CoworkerProfile.tsx # View/edit employee profiles (manager) + leave feedback
│   ├── ManagerProfile.tsx  # Manager dashboard for absence approvals
│   └── AbsenceRequest.tsx  # Form to request absence
├── types/
│   └── index.ts           # TypeScript type definitions
├── App.css                # Global styles and component styling
├── index.css              # Base styles and CSS reset
├── App.tsx                # Main app with routing
└── main.tsx              # Entry point
```

## 🔑 API Configuration

The application connects to two backend services. Update the base URLs in `src/api/client.ts` if needed:

```typescript
export const authApi = createApiClient("http://localhost:8001");
export const hrApi = createApiClient("http://localhost:8002");
```

The HR service internally communicates with the AI service when feedback polishing is requested, so the frontend doesn't need direct AI service access.

## 🔐 Authentication Flow

1. User logs in via `/login` page
2. Auth service validates credentials and returns JWT token + user info
3. Token is stored in localStorage
4. All subsequent requests include the token in Authorization header
5. 401 responses automatically redirect to login page

## 🎨 UI/UX Features

### Modern Design
- **Gradient Theme**: Purple gradient navigation bar with smooth animations
- **Card-Based Layout**: Clean white cards with shadows on gradient background
- **Interactive Elements**: Hover effects, smooth transitions, and micro-interactions
- **Responsive Design**: Fully responsive layout that works on mobile, tablet, and desktop

### Navigation
- **Sticky Navbar**: Stays visible while scrolling for easy navigation
- **Icon Navigation**: Each link has intuitive emoji icons (👤 Profile, 👥 Employees, 📅 Absence, ⚡ Dashboard)
- **Active State**: Current page is highlighted with underline and background
- **User Info Display**: Shows avatar circle with initials, name, and role
- **Role-Based Menu**: Manager dashboard link only visible to managers

### Profile Pages
- **Sectioned Layout**: Information organized into logical sections (Basic, Contact, Compensation, Sensitive)
- **Edit Mode Toggle**: Seamless switch between view and edit modes
- **Inline Editing**: Fields convert to inputs in edit mode
- **Save/Cancel Actions**: Clear action buttons with loading states

### Employee Directory
- **Grid Layout**: Responsive grid of employee cards
- **Search Functionality**: Real-time filtering by name, email, department, or position
- **Card Hover Effects**: Cards lift and highlight on hover
- **Quick Navigation**: Click any employee card to view their profile

### Feedback System
- **Collapsible Form**: Feedback form expands when needed, stays compact otherwise
- **AI Enhancement Toggle**: Checkbox to enable AI polishing
- **Visual AI Badge**: Gradient badge marks AI-enhanced feedback
- **Feedback Cards**: Individual cards for each feedback item with hover effects

### Forms & Inputs
- **Focus States**: Input fields highlight with purple border on focus
- **Button Variants**: Color-coded buttons (purple for primary, green for save, red for reject)
- **Loading States**: Buttons show loading text and are disabled during operations
- **Error/Success Messages**: Colored message boxes for feedback

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

The backend enforces permissions, and the frontend implements:
- Conditional rendering based on user role
- Different views for employees vs managers when viewing profiles
- Route protection for manager-only pages
- Edit capabilities restricted to profile owners and managers
- API request filtering based on permissions

## 🎨 Future Improvements

Given more time, the following enhancements would be valuable:

### Frontend
1. **Enhanced UI/UX**
   - Consistent design system with reusable components
   - Loading skeletons instead of simple spinners
   - Toast notifications for actions (success/error)
   - Animated page transitions
   - Modal dialogs for confirmations

2. **Form Validation**
   - Client-side validation with libraries like Zod or Yup
   - Real-time validation feedback
   - Better error messages with field-level errors

3. **Advanced Search & Filtering**
   - Multi-criteria filtering for employees
   - Sort options (by name, department, hire date)
   - Pagination for large datasets
   - Export employee list to CSV

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

### Current Implementation Highlights

1. **Modern UI**: Custom CSS with gradient themes, card layouts, smooth animations, and hover effects throughout the application.

2. **Enhanced Navigation**: Sticky navbar with emoji icons, active state indicators, user profile display with avatar and role badge.

3. **Manager Capabilities**: Full CRUD operations on employee profiles with edit mode toggle, similar to own profile editing.

4. **Employee Directory**: Searchable grid layout with real-time filtering and interactive employee cards.

5. **Role-Based Views**: Dynamic content rendering based on user role - managers see sensitive data, employees see public data only.

6. **Feedback System**: Complete feedback workflow with AI enhancement option, visual badges, and organized display.

### Pragmatic Choices Made

1. **Simple State Management**: Used Zustand only for auth state; local component state for everything else. For larger apps, consider React Query or Redux.

2. **Custom CSS**: Hand-written CSS for full control and demonstration. Production apps might benefit from Tailwind CSS or a component library.

3. **No Backend Mocking**: Assumes backend services are running. For development without backends, add MSW (Mock Service Worker).

4. **Basic Error Handling**: Try-catch blocks with user-friendly messages. Production apps need comprehensive error boundaries and logging services.

5. **No Test Coverage**: Focused on feature implementation and UI. Real projects require thorough unit, integration, and E2E testing.

6. **Simplified Permissions**: Two-role system (employee/manager). Enterprise apps might need fine-grained RBAC with multiple roles and permissions.

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
```

Update `src/api/client.ts` to use:
```typescript
const authApi = createApiClient(import.meta.env.VITE_AUTH_API_URL);
const hrApi = createApiClient(import.meta.env.VITE_HR_API_URL);
```

## 📄 License

This is a coding assignment project.

---

**Built with ❤️ using React, TypeScript, and Vite**
