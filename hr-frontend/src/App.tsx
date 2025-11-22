import { useEffect } from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { useAuth } from "./hooks/useAuth";
import Layout from "./components/Layout";
import Login from "./pages/Login";
import Profile from "./pages/Profile";
import CoworkerProfile from "./pages/CoworkerProfile";
import ManagerProfile from "./pages/ManagerProfile";
import AbsenceRequest from "./pages/AbsenceRequest";
import EmployeeList from "./components/EmployeeList";

const PrivateRoute = ({ children }: { children: React.ReactNode }) => {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" />;
};

function App() {
  const { initializeAuth } = useAuth();

  useEffect(() => {
    initializeAuth();
  }, [initializeAuth]);

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/" element={<PrivateRoute><Layout /></PrivateRoute>}>
          <Route index element={<Navigate to="/profile" />} />
          <Route path="profile" element={<Profile />} />
          <Route path="employees" element={<EmployeeList />} />
          <Route path="coworker/:id" element={<CoworkerProfile />} />
          <Route path="manager" element={<ManagerProfile />} />
          <Route path="absence-request" element={<AbsenceRequest />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
