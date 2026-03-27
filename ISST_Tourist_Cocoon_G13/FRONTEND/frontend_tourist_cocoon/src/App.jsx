import { useState } from "react";
import "./App.css";

import LoginForm from "./LoginForm";
import RegisterForm from "./RegisterForm";

function App() {
  const [view, setView] = useState("login"); // "login" | "register"

  return (
    <main style={{ minHeight: "100vh", display: "grid", placeItems: "center" }}>
      <section
        style={{
          width: "min(520px, 92vw)",
          padding: 24,
          borderRadius: 12,
          border: "1px solid rgba(255,255,255,0.12)",
          background: "rgba(255,255,255,0.04)",
        }}
      >
        <header style={{ marginBottom: 16 }}>
          <h1 style={{ margin: 0 }}>Tourist Cocoon</h1>
          <p style={{ margin: "8px 0 0", opacity: 0.8 }}>
            Accede o crea tu cuenta
          </p>
        </header>

        <nav style={{ display: "flex", gap: 8, marginBottom: 16 }}>
          <button
            type="button"
            onClick={() => setView("login")}
            style={{
              flex: 1,
              padding: "10px 12px",
              borderRadius: 10,
              border: "1px solid rgba(255,255,255,0.12)",
              background: view === "login" ? "#646cff" : "transparent",
              color: "white",
              cursor: "pointer",
            }}
          >
            Login
          </button>

          <button
            type="button"
            onClick={() => setView("register")}
            style={{
              flex: 1,
              padding: "10px 12px",
              borderRadius: 10,
              border: "1px solid rgba(255,255,255,0.12)",
              background: view === "register" ? "#646cff" : "transparent",
              color: "white",
              cursor: "pointer",
            }}
          >
            Registro
          </button>
        </nav>

        <div
          style={{
            padding: 16,
            borderRadius: 12,
            border: "1px solid rgba(255,255,255,0.12)",
          }}
        >
          {view === "login" ? <LoginForm /> : <RegisterForm />}
        </div>
      </section>
    </main>
  );
}

export default App;