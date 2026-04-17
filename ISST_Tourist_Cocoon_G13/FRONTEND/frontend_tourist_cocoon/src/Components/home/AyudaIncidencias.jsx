import { useEffect, useState } from "react";
import {
  apiCrearIncidencia,
  apiGetIncidenciasAbiertasHuesped,
} from "../../services/apiService";

const CATEGORIAS = ["LIMPIEZA", "MANTENIMIENTO", "SEGURIDAD", "ACCESO"];

export default function AyudaIncidencias() {
  const [currentUser, setCurrentUser] = useState(null);
  const [incidencias, setIncidencias] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const [form, setForm] = useState({
    categoria: "ACCESO",
    descripcion: "",
    fotoUrl: "",
    canalNotificacion: "BACKOFFICE",
  });

  const cargarAbiertas = async (user) => {
    if (!user?.id) {
      setIncidencias([]);
      setLoading(false);
      setError("No se encontró sesión de usuario.");
      return;
    }

    try {
      setError("");
      const lista = await apiGetIncidenciasAbiertasHuesped(user.id);
      setIncidencias(lista || []);
    } catch (e) {
      setIncidencias([]);
      setError(e.message || "No se pudieron cargar tus incidencias.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const raw = localStorage.getItem("currentUser");
    let user = null;
    try {
      user = raw ? JSON.parse(raw) : null;
    } catch {
      user = null;
    }

    setCurrentUser(user);
    cargarAbiertas(user);
  }, []);

  const onChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    if (!currentUser?.id) return;

    setSaving(true);
    setError("");
    setSuccessMessage("");

    try {
      const payload = {
        huespedId: currentUser.id,
        categoria: form.categoria,
        descripcion: form.descripcion,
        fotoUrl: form.fotoUrl || null,
        canalNotificacion: form.canalNotificacion || null,
      };

      const response = await apiCrearIncidencia(payload);
      setSuccessMessage(response.mensaje || "Incidencia recibida. Personal avisado.");

      if (response.telefonoEmergencia) {
        setSuccessMessage(
          `${response.mensaje || "Incidencia registrada."} Teléfono de emergencia: ${response.telefonoEmergencia}`
        );
      }

      setForm((prev) => ({ ...prev, descripcion: "", fotoUrl: "" }));
      await cargarAbiertas(currentUser);
    } catch (e2) {
      setError(e2.message || "No se pudo registrar la incidencia.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <section className="card stay-panel stay-panel--estancia">
      <h2>Ayuda e Incidencias</h2>
      <p className="stay-intro">Reporta incidencias de limpieza, mantenimiento, seguridad o acceso.</p>

      {error && <p className="auth-message auth-message--error">{error}</p>}
      {successMessage && <p className="auth-message auth-message--success">{successMessage}</p>}

      <form onSubmit={onSubmit} className="auth-content" style={{ marginTop: "0.8rem" }}>
        <h3>Reportar incidencia</h3>
        <label>
          Categoría
          <select name="categoria" value={form.categoria} onChange={onChange}>
            {CATEGORIAS.map((c) => (
              <option key={c} value={c}>{c}</option>
            ))}
          </select>
        </label>
        <label>
          Descripción
          <textarea
            name="descripcion"
            rows={4}
            value={form.descripcion}
            onChange={onChange}
            placeholder="Describe el problema con el mayor detalle posible"
            required
          />
        </label>
        <label>
          Foto URL (opcional)
          <input
            type="url"
            name="fotoUrl"
            value={form.fotoUrl}
            onChange={onChange}
            placeholder="https://..."
          />
        </label>
        <label>
          Canal de notificación
          <select name="canalNotificacion" value={form.canalNotificacion} onChange={onChange}>
            <option value="BACKOFFICE">BACKOFFICE</option>
            <option value="APP">APP</option>
            <option value="EMAIL">EMAIL</option>
            <option value="SIN_NOTIFICACION">SIN_NOTIFICACION (test)</option>
          </select>
        </label>

        <button type="submit" disabled={saving}>
          {saving ? "Enviando..." : "Enviar incidencia"}
        </button>
      </form>

      <hr className="stay-divider" />

      <h3 className="stay-subtitle">Incidencias abiertas</h3>
      {loading ? (
        <p className="stay-intro">Cargando incidencias...</p>
      ) : incidencias.length === 0 ? (
        <p className="stay-empty">No tienes incidencias abiertas.</p>
      ) : (
        <div className="reservas-list">
          {incidencias.map((i) => (
            <article key={i.id} className="reserva-card">
              <div className="reserva-card-header">
                <span className="reserva-card-id">Incidencia #{i.id}</span>
                <span className={`reserva-card-estado reserva-card-estado--${String(i.estado || "").toLowerCase()}`}>
                  {i.estado}
                </span>
              </div>
              <div className="reserva-card-body">
                <p><strong>Categoría:</strong> {i.categoria}</p>
                <p><strong>Prioridad:</strong> {i.prioridad}</p>
                <p><strong>Descripción:</strong> {i.descripcion}</p>
                <p><strong>Creada:</strong> {i.fechaCreacion ? new Date(i.fechaCreacion).toLocaleString("es-ES") : "—"}</p>
                {i.capsulaId && <p><strong>Cápsula:</strong> {i.capsulaId}</p>}
              </div>
            </article>
          ))}
        </div>
      )}
    </section>
  );
}
