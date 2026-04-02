import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./App.css";

import LoginForm from "../Components/auth/LoginForm";
import RegisterForm from "../Components/auth/RegisterForm";

export default function Authentication() {
  const location = useLocation();
  const navigate = useNavigate();

  const initialView = location.pathname === "/register" ? "register" : "login";
  const [view, setView] = useState(initialView);

  useEffect(() => {
    setView(location.pathname === "/register" ? "register" : "login");
  }, [location.pathname]);

  const goTo = (nextView) => {
    setView(nextView);
    navigate(nextView === "login" ? "/login" : "/register", { replace: true });
  };

  return (
    <main className="app-shell">
      <section className="auth-card">
        <header className="auth-header">
          <h1>Tourist Cocoon</h1>
          <p>Accede o crea tu cuenta</p>
        </header>

        <nav className="auth-switch" aria-label="Cambiar entre login y registro">
          <button
            type="button"
            onClick={() => goTo("login")}
            className={view === "login" ? "is-active" : ""}
          >
            Login
          </button>

          <button
            type="button"
            onClick={() => goTo("register")}
            className={view === "register" ? "is-active" : ""}
          >
            Registro
          </button>
        </nav>

        <div className="auth-content">
          {view === "login" ? <LoginForm /> : <RegisterForm />}
        </div>
      </section>
    </main>
  );
}