import { useEffect, useState } from "react";
import { apiGetReservaActiva } from "../../services/apiService";

export default function MiEstancia() {
  const [reservaActiva, setReservaActiva] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const cargarReservaActiva = async () => {
      const rawUser = localStorage.getItem("currentUser");
      let currentUser = null;

      try {
        currentUser = rawUser ? JSON.parse(rawUser) : null;
      } catch {
        currentUser = null;
      }

      if (!currentUser?.id) {
        setError("No se encontró sesión de usuario.");
        setLoading(false);
        return;
      }

      try {
        const activa = await apiGetReservaActiva(currentUser.id);
        setReservaActiva(activa);
      } catch (e) {
        setError(e.message || "No se pudo cargar tu reserva activa.");
      } finally {
        setLoading(false);
      }
    };

    cargarReservaActiva();
  }, []);

  if (loading) {
    return (
      <section className="card">
        <h2>Tu reserva actual</h2>
        <p>Cargando datos de tu estancia...</p>
      </section>
    );
  }

  return (
    <section className="card">
      <h2>Tu reserva actual</h2>

      {error && <p className="reserva-error" role="alert">{error}</p>}

      {reservaActiva ? (
        <div className="grid">
          <p><strong>Reserva:</strong> {reservaActiva.id}</p>
          <p><strong>Entrada:</strong> {reservaActiva.fechaInicio}</p>
          <p><strong>Salida:</strong> {reservaActiva.fechaFinal}</p>
          <hr />
          <p><strong>Cápsula ID:</strong> {reservaActiva.capsula.id}</p>
          <p><strong>Planta:</strong> {reservaActiva.capsula.planta}</p>
          <p>
            <strong>Estado ocupación:</strong> {reservaActiva.capsula.estado}
          </p>
          <p><strong>Estado reserva:</strong> {reservaActiva.estado}</p>
        </div>
      ) : (
        <p>No tienes una reserva activa.</p>
      )}
    </section>
  );
}