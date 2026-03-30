import { useEffect, useState } from "react";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import HomeDashboard from "./Views/Home";
import Authentication from "./Views/Authentication";
import "./Views/App.css";

function getAuthState() {
  return localStorage.getItem("isLoggedIn") === "true";
}

function ProtectedRoute({ children, isLoggedIn }) {
  return isLoggedIn ? children : <Navigate to="/login" replace />;
}

function PublicOnlyRoute({ children, isLoggedIn }) {
  return isLoggedIn ? <Navigate to="/home" replace /> : children;
}

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(getAuthState);

  useEffect(() => {
    const syncAuthState = () => setIsLoggedIn(getAuthState());

    window.addEventListener("storage", syncAuthState);
    window.addEventListener("authStateChanged", syncAuthState);

    return () => {
      window.removeEventListener("storage", syncAuthState);
      window.removeEventListener("authStateChanged", syncAuthState);
    };
  }, []);

  return (
    <BrowserRouter>
      <Routes>
        {/* Entrada principal: si no logueado -> login, si logueado -> home */}
        <Route
          path="/"
          element={
            isLoggedIn ? (
              <Navigate to="/home" replace />
            ) : (
              <Navigate to="/login" replace />
            )
          }
        />

        {/* Públicas solo si NO está logueado */}
        <Route
          path="/login"
          element={
            <PublicOnlyRoute isLoggedIn={isLoggedIn}>
              <Authentication />
            </PublicOnlyRoute>
          }
        />
        <Route
          path="/register"
          element={
            <PublicOnlyRoute isLoggedIn={isLoggedIn}>
              <Authentication />
            </PublicOnlyRoute>
          }
        />

        {/* Protegidas */}
        <Route
          path="/home"
          element={
            <ProtectedRoute isLoggedIn={isLoggedIn}>
              <HomeDashboard />
            </ProtectedRoute>
          }
        />

        {/* Cualquier otra ruta */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;