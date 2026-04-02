import { createContext, useContext, useEffect, useMemo, useState } from "react";
import * as authService from "../services/authService";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => authService.getCurrentUser());
  const [isAuthenticated, setIsAuthenticated] = useState(() => authService.isAuthenticated());
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const syncAuth = () => {
      setUser(authService.getCurrentUser());
      setIsAuthenticated(authService.isAuthenticated());
    };

    window.addEventListener("storage", syncAuth);
    window.addEventListener("authStateChanged", syncAuth);

    return () => {
      window.removeEventListener("storage", syncAuth);
      window.removeEventListener("authStateChanged", syncAuth);
    };
  }, []);

  const value = useMemo(
    () => ({
      user,
      isAuthenticated,
      loading,
      async login(credentials) {
        setLoading(true);
        try {
          const loggedUser = await authService.login(credentials);
          setUser(loggedUser);
          setIsAuthenticated(true);
          return loggedUser;
        } finally {
          setLoading(false);
        }
      },
      async register(data) {
        setLoading(true);
        try {
          return await authService.register(data);
        } finally {
          setLoading(false);
        }
      },
      logout() {
        authService.logout();
        setUser(null);
        setIsAuthenticated(false);
      }
    }),
    [user, isAuthenticated, loading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth debe usarse dentro de AuthProvider");
  }
  return ctx;
}