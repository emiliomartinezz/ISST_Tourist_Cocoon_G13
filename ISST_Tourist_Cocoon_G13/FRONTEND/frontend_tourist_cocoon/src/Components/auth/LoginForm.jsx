import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

export default function LoginForm() {
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const { login, loading } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm((prev) => ({
      ...prev,
      [e.target.name]: e.target.value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (!form.email.trim() || !form.password.trim()) {
      setError("Introduce email y contraseña");
      return;
    }

    try {
      await login({
        email: form.email.trim().toLowerCase(),
        password: form.password
      });
      navigate("/home", { replace: true });
    } catch (err) {
      setError(err.message || "Credenciales incorrectas");
    }
  };

  return (
    <form onSubmit={handleSubmit} className="auth-form" noValidate>
      <h2>Login</h2>

      {error && <p className="auth-message auth-message--error">{error}</p>}

      <label>
        Correo electrónico
        <input
          type="email"
          name="email"
          placeholder="correo@ejemplo.com"
          value={form.email}
          onChange={handleChange}
          required
        />
      </label>

      <label>
        Contraseña
        <input
          type="password"
          name="password"
          placeholder="Tu contraseña"
          value={form.password}
          onChange={handleChange}
          required
        />
      </label>

      <button type="submit" disabled={loading}>
        {loading ? "Entrando..." : "Entrar"}
      </button>
    </form>
  );
}