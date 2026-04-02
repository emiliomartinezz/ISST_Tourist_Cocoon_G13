import { useState } from "react";
import { apiRealizarCheckIn } from "../../services/apiService";

export default function CheckIn() {
  const [documentoIdentidad, setDocumentoIdentidad] = useState("");
  const [documentoValidado, setDocumentoValidado] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [resultado, setResultado] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setResultado(null);

    const rawUser = localStorage.getItem("currentUser");
    let currentUser = null;

    try {
      currentUser = rawUser ? JSON.parse(rawUser) : null;
    } catch {
      currentUser = null;
    }

    if (!currentUser?.id) {
      setError("No se encontró sesión de usuario.");
      return;
    }

    if (!documentoIdentidad.trim()) {
      setError("Debes introducir tu documento de identidad.");
      return;
    }

    if (!documentoValidado) {
      setError("Debes validar el documento para completar el check-in.");
      return;
    }

    setLoading(true);

    try {
      const response = await apiRealizarCheckIn({
        huespedId: currentUser.id,
        documentoIdentidad: documentoIdentidad.trim(),
        documentoValidado
      });

      setResultado(response);
    } catch (err) {
      setError(err.message || "No se pudo realizar el check-in.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <section>
      <h2>Check-in automatizado</h2>
      <p>
        Valida tu identidad para habilitar el acceso al edificio y a tu cápsula.
      </p>

      {error && <p className="auth-message auth-message--error">{error}</p>}

      {resultado ? (
        <div>
          <p className="auth-message auth-message--success">
            {resultado.mensaje}
          </p>

          <p>
            <strong>Reserva:</strong> {resultado.reservaId}
          </p>
          <p>
            <strong>Cápsula asignada:</strong> {resultado.capsulaId}
          </p>
          <p>
            <strong>Código de acceso:</strong> {resultado.codigoAcceso}
          </p>
          <p>
            <strong>Fecha de check-in:</strong> {resultado.fechaCheckIn}
          </p>
          <p>
            <strong>Acceso válido hasta:</strong> {resultado.accesoValidoHasta}
          </p>
          <p>
            <strong>Datos enviados a autoridades:</strong>{" "}
            {resultado.datosAutoridadEnviados ? "Sí" : "Pendiente"}
          </p>
        </div>
      ) : (
        <form onSubmit={handleSubmit} className="auth-form" noValidate>
          <label>
            Documento de identidad
            <input
              type="text"
              value={documentoIdentidad}
              onChange={(e) => setDocumentoIdentidad(e.target.value)}
              placeholder="DNI / Pasaporte"
            />
          </label>

          <label className="auth-checkbox">
            <input
              type="checkbox"
              checked={documentoValidado}
              onChange={(e) => setDocumentoValidado(e.target.checked)}
            />
            <span>Confirmo que el documento ha sido validado</span>
          </label>

          <button type="submit" disabled={loading}>
            {loading ? "Procesando check-in..." : "Realizar check-in"}
          </button>
        </form>
      )}
    </section>
  );
}