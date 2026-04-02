import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

const initialForm = {
  nombre: "",
  nif: "",
  telefono: "",
  email: "",
  password: "",
  confirmPassword: "",
  aceptaPoliticaPrivacidad: false
};

export default function RegisterForm() {
  const [form, setForm] = useState(initialForm);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const { register, login, loading } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value
    }));
  };

  const validateForm = () => {
    if (!form.nombre.trim()) return "El nombre es obligatorio";
    if (!form.nif.trim()) return "El NIF/DNI es obligatorio";
    if (!form.email.trim()) return "El email es obligatorio";
    if (!form.password) return "La contraseña es obligatoria";
    if (form.password.length < 6) return "La contraseña debe tener al menos 6 caracteres";
    if (form.password !== form.confirmPassword) return "Las contraseñas no coinciden";
    if (!form.aceptaPoliticaPrivacidad) return "Debes aceptar la política de privacidad";
    return "";
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");

    const validationError = validateForm();
    if (validationError) {
      setError(validationError);
      return;
    }

    try {
      await register(form);
      setSuccess("Cuenta creada correctamente. Iniciando sesión...");

      await login({
        email: form.email,
        password: form.password
      });

      navigate("/home", { replace: true });
    } catch (err) {
      setError(err.message || "No se pudo completar el registro");
    }
  };

  return (
    <form onSubmit={handleSubmit} className="auth-form" noValidate>
      <h2>Registro</h2>

      {error && <p className="auth-message auth-message--error">{error}</p>}
      {success && <p className="auth-message auth-message--success">{success}</p>}

      <label>
        Nombre completo
        <input
          name="nombre"
          placeholder="Nombre y apellidos"
          value={form.nombre}
          onChange={handleChange}
          required
        />
      </label>

      <label>
        NIF / DNI / Pasaporte
        <input
          name="nif"
          placeholder="Documento de identidad"
          value={form.nif}
          onChange={handleChange}
          required
        />
      </label>

      <label>
        Teléfono
        <input
          name="telefono"
          placeholder="Teléfono"
          value={form.telefono}
          onChange={handleChange}
        />
      </label>

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
          placeholder="Mínimo 6 caracteres"
          value={form.password}
          onChange={handleChange}
          required
        />
      </label>

      <label>
        Confirmar contraseña
        <input
          type="password"
          name="confirmPassword"
          placeholder="Repite la contraseña"
          value={form.confirmPassword}
          onChange={handleChange}
          required
        />
      </label>

      <label className="auth-checkbox">
        <input
          type="checkbox"
          name="aceptaPoliticaPrivacidad"
          checked={form.aceptaPoliticaPrivacidad}
          onChange={handleChange}
        />
        <span>
          He leído y acepto la política de privacidad
        </span>
      </label>

      <button type="submit" disabled={loading}>
        {loading ? "Creando cuenta..." : "Registrarse"}
      </button>
    </form>
  );
}