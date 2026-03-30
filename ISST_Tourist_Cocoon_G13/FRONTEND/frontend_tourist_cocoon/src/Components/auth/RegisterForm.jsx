import { useState } from "react";
import { register } from "../../services/authService";

export default function RegisterForm() {
  const [form, setForm] = useState({
    nombre: "",
    dni: "",
    telefono: "",
    email: "",
    password: ""
  });

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await register({
        ...form,
        nombre: form.nombre.trim(),
        dni: form.dni.trim(),
        telefono: form.telefono.trim(),
        email: form.email.trim()
      });
      alert("Usuario registrado correctamente");
      setForm({
        nombre: "",
        dni: "",
        telefono: "",
        email: "",
        password: ""
      });
    } catch (error) {
      console.error(error);
      alert(error.message || "Error en el registro");
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Registro</h2>

      <input name="nombre" placeholder="Nombre" value={form.nombre} onChange={handleChange} required />
      <input name="dni" placeholder="DNI" value={form.dni} onChange={handleChange} required />
      <input
        name="telefono"
        placeholder="Teléfono"
        value={form.telefono}
        onChange={handleChange}
        required
      />
      <input name="email" placeholder="Email" value={form.email} onChange={handleChange} required />
      <input
        type="password"
        name="password"
        placeholder="Contraseña"
        value={form.password}
        onChange={handleChange}
        required
      />

      <button type="submit">Registrarse</button>
    </form>
  );
}