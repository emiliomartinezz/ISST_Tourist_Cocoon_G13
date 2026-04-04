import { useState } from "react";
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

const LETTERS = "TRWAGMYFPDXBNJZSQVHLCKE";

function normalizeDocumento(value) {
  return (value || "").replace(/[\s-]/g, "").toUpperCase();
}

function isValidDni(doc) {
  if (!/^\d{8}[A-Z]$/.test(doc)) return false;
  const number = parseInt(doc.slice(0, 8), 10);
  return doc[8] === LETTERS[number % 23];
}

function isValidNie(doc) {
  if (!/^[XYZ]\d{7}[A-Z]$/.test(doc)) return false;

  const prefixMap = { X: "0", Y: "1", Z: "2" };
  const number = parseInt(prefixMap[doc[0]] + doc.slice(1, 8), 10);
  return doc[8] === LETTERS[number % 23];
}

function isValidDocumento(doc) {
  return isValidDni(doc) || isValidNie(doc);
}

export default function RegisterForm() {
  const [form, setForm] = useState(initialForm);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const { register, loading } = useAuth();

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");

    const nifNormalizado = normalizeDocumento(form.nif);

    if (!isValidDocumento(nifNormalizado)) {
      setError("El DNI/NIE no es válido.");
      return;
    }

    try {
      await register({
        ...form,
        nombre: form.nombre.trim(),
        nif: nifNormalizado,
        telefono: form.telefono.trim(),
        email: form.email.trim().toLowerCase()
      });

      setSuccess("Usuario registrado correctamente");
      setForm({
        nombre: "",
        nif: "",
        telefono: "",
        email: "",
        password: "",
        confirmPassword: "",
        aceptaPoliticaPrivacidad: false
      });
    } catch (error) {
      console.error(error);
      setError(error.message || "Error en el registro");
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