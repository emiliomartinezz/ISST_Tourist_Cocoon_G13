import { useEffect, useMemo, useState } from "react";
import {
  apiGetPerfil,
  apiGoogleOAuthDisconnect,
  apiGoogleOAuthStart,
  apiGoogleOAuthStatus,
  apiUpdatePerfil,
} from "../../services/apiService";

export default function MiPerfil() {
  const [currentUser, setCurrentUser] = useState(null);
  const [nombre, setNombre] = useState("");
  const [email, setEmail] = useState("");
  const [telefono, setTelefono] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [successMsg, setSuccessMsg] = useState("");
  const [errorMsg, setErrorMsg] = useState("");

  const [googleStatus, setGoogleStatus] = useState({ connected: false, calendarId: null, connectedAt: null });
  const [googleLoading, setGoogleLoading] = useState(false);
  const [googleMsg, setGoogleMsg] = useState("");

  const googleConnectedLabel = useMemo(() => {
    if (!googleStatus?.connected) return "No conectado";
    return `Conectado (${googleStatus.calendarId || "primary"})`;
  }, [googleStatus]);

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

        const status = await apiGoogleOAuthStatus(user.id);
        setGoogleStatus(status);
      } catch (e) {
        setErrorMsg(e.message || "No se pudo cargar tu perfil.");
      } finally {
        setLoading(false);
      }
    };

    cargarPerfil();
  }, []);

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const google = params.get("google");
    if (!google) return;

    if (google === "connected") {
      setGoogleMsg("Google Calendar conectado correctamente.");
    } else if (google === "error") {
      setGoogleMsg("No se pudo conectar Google Calendar. Inténtalo de nuevo.");
    }
  }, []);

  const handleConectarGoogle = async () => {
    if (!currentUser?.id) return;
    setGoogleLoading(true);
    setGoogleMsg("");
    setErrorMsg("");

    try {
      const { authUrl } = await apiGoogleOAuthStart(currentUser.id);
      window.location.href = authUrl;
    } catch (e) {
      setGoogleMsg(e.message || "No se pudo iniciar la conexión con Google Calendar.");
      setGoogleLoading(false);
    }
  };

  const handleDesconectarGoogle = async () => {
    if (!currentUser?.id) return;
    setGoogleLoading(true);
    setGoogleMsg("");
    setErrorMsg("");

    try {
      await apiGoogleOAuthDisconnect(currentUser.id);
      const status = await apiGoogleOAuthStatus(currentUser.id);
      setGoogleStatus(status);
      setGoogleMsg("Google Calendar desconectado.");
    } catch (e) {
      setGoogleMsg(e.message || "No se pudo desconectar Google Calendar.");
    } finally {
      setGoogleLoading(false);
    }
  };

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

      <div style={{ marginTop: "1.5rem" }}>
        <h3 style={{ marginBottom: "0.25rem" }}>Google Calendar</h3>
        <p style={{ marginTop: 0, color: "var(--color-text-secondary)", fontSize: "13px" }}>
          Si conectas tu cuenta, tus reservas se sincronizarán automáticamente en tu calendario.
        </p>

        {googleMsg && (
          <p className="auth-message auth-message--success" role="status" aria-live="polite">
            {googleMsg}
          </p>
        )}

        <p style={{ fontSize: "13px", marginBottom: "0.75rem" }}>
          Estado: <strong>{googleConnectedLabel}</strong>
        </p>

        <div style={{ display: "flex", gap: "8px", flexWrap: "wrap" }}>
          {!googleStatus?.connected ? (
            <button type="button" onClick={handleConectarGoogle} disabled={googleLoading}>
              {googleLoading ? "Conectando..." : "Conectar Google Calendar"}
            </button>
          ) : (
            <button type="button" onClick={handleDesconectarGoogle} disabled={googleLoading}>
              {googleLoading ? "Desconectando..." : "Desconectar"}
            </button>
          )}
        </div>
      </div>
    </section>
  );
}
