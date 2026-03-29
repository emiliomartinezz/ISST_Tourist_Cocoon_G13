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
      await register(form);
      alert("Usuario registrado correctamente");
    } catch (error) {
      console.error(error);
      alert("Error en el registro");
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Registro</h2>

      <input name="nombre" placeholder="Nombre" onChange={handleChange} />
      <input name="dni" placeholder="DNI" onChange={handleChange} />
      <input name="telefono" placeholder="Teléfono" onChange={handleChange} />
      <input name="email" placeholder="Email" onChange={handleChange} />
      <input type="password" name="password" placeholder="Contraseña" onChange={handleChange} />

      <button type="submit">Registrarse</button>
    </form>
  );
}