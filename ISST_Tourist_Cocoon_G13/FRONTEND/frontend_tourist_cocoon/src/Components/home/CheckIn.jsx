import { useState } from "react";
import { apiRealizarCheckIn } from "../../services/apiService";

export default function CheckIn() {
  const [documentoIdentidad, setDocumentoIdentidad] = useState("");
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

    setLoading(true);

    try {
      const response = await apiRealizarCheckIn({
        huespedId: currentUser.id,
        documentoIdentidad: documentoIdentidad.trim()
      });
      setResultado(response);
    } catch (err) {
      setError(err.message || "No se pudo realizar el check-in.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Check-in automatizado</h2>
      <p>Valida tu identidad para habilitar el acceso al edificio y a tu cápsula.</p>

      {error && <p>{error}</p>}

      {resultado ? (
        <div>
          <p>{resultado.mensaje}</p>
          <p>Reserva: {resultado.reservaId}</p>
          <p>Cápsula asignada: {resultado.capsulaId}</p>
          <p>Código de acceso: {resultado.codigoAcceso}</p>
          <p>Fecha de check-in: {resultado.fechaCheckIn}</p>
          <p>Acceso válido hasta: {resultado.accesoValidoHasta}</p>
          <p>
            Datos enviados a autoridades:{" "}
            {resultado.datosAutoridadEnviados ? "Sí" : "Pendiente"}
          </p>
        </div>
      ) : (
        <form onSubmit={handleSubmit}>
          <label>Documento de identidad</label>
          <input
            type="text"
            value={documentoIdentidad}
            onChange={(e) => setDocumentoIdentidad(e.target.value)}
            placeholder="DNI / NIE"
          />
          <button type="submit" disabled={loading}>
            {loading ? "Procesando check-in..." : "Realizar check-in"}
          </button>
        </form>
      )}
    </div>
  );
}