import { useState } from "react";
import "./App.css";

import LoginForm from "../Components/auth/LoginForm";
import RegisterForm from "../Components/auth/RegisterForm";

export default function Authentication() {
      const [view, setView] = useState("login"); // "login" | "register"
    
      return (
        <main className="app-shell">
          <section className="auth-card">
            <header className="auth-header">
              <h1>Tourist Cocoon</h1>
              <p>
                Accede o crea tu cuenta
              </p>
            </header>
    
            <nav className="auth-switch" aria-label="Cambiar entre login y registro">
              <button
                type="button"
                onClick={() => setView("login")}
                className={view === "login" ? "is-active" : ""}
              >
                Login
              </button>
    
              <button
                type="button"
                onClick={() => setView("register")}
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