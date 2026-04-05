import { useEffect, useState } from "react";
import { apiGetPerfil, apiUpdatePerfil } from "../../services/apiService";

export default function MiPerfil() {
  const [currentUser, setCurrentUser] = useState(null);
  const [nombre, setNombre] = useState("");
  const [email, setEmail] = useState("");
  const [telefono, setTelefono] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [successMsg, setSuccessMsg] = useState("");
  const [errorMsg, setErrorMsg] = useState("");

  useEffect(() => {
    const cargarPerfil = async () => {
      const rawUser = localStorage.getItem("currentUser");
      let user = null;

      try {
        user = rawUser ? JSON.parse(rawUser) : null;
      } catch {
        user = null;
      }

      if (!user?.id) {
        setErrorMsg("No se encontró sesión de usuario.");
        setLoading(false);
        return;
      }

      setCurrentUser(user);

      try {
        const perfil = await apiGetPerfil(user.id);
        setNombre(perfil.nombre || "");
        setEmail(perfil.email || "");
        setTelefono(perfil.telefono || "");
      } catch (e) {
        setErrorMsg(e.message || "No se pudo cargar tu perfil.");
      } finally {
        setLoading(false);
      }
    };

    cargarPerfil();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!currentUser?.id) return;

    setSaving(true);
    setSuccessMsg("");
    setErrorMsg("");

    try {
      const updated = await apiUpdatePerfil(currentUser.id, {
        nombre: nombre.trim(),
        email: email.trim(),
        telefono: telefono.trim() || null,
      });

      // Actualizar localStorage con los nuevos datos
      const updatedUser = { ...currentUser, nombre: updated.nombre, email: updated.email, telefono: updated.telefono };
      localStorage.setItem("currentUser", JSON.stringify(updatedUser));
      setCurrentUser(updatedUser);
      window.dispatchEvent(new Event("authStateChanged"));

      setSuccessMsg("Datos actualizados correctamente.");
    } catch (e) {
      setErrorMsg(e.message || "No se pudieron actualizar los datos.");
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <section className="card stay-panel stay-panel--estancia">
        <h2>Mi Perfil</h2>
        <p className="stay-intro">Cargando datos del perfil...</p>
      </section>
    );
  }

  return (
    <section className="card stay-panel stay-panel--estancia">
      <h2>Mi Perfil</h2>
      <p className="stay-intro">Modifica tus datos personales</p>

      {successMsg && <p className="auth-message auth-message--success">{successMsg}</p>}
      {errorMsg && <p className="auth-message auth-message--error">{errorMsg}</p>}

      <form onSubmit={handleSubmit} className="auth-form">
        <label>
          Nombre
          <input
            type="text"
            value={nombre}
            onChange={(e) => setNombre(e.target.value)}
            required
          />
        </label>

        <label>
          Correo electrónico
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </label>

        <label>
          Teléfono
          <input
            type="tel"
            value={telefono}
            onChange={(e) => setTelefono(e.target.value)}
            maxLength={20}
          />
        </label>

        <button type="submit" disabled={saving}>
          {saving ? "Guardando..." : "Guardar cambios"}
        </button>
      </form>
    </section>
  );
}
