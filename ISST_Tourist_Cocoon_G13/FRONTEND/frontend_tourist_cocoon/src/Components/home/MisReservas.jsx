import { useEffect, useState } from "react";
import { apiGetReservasHuesped, apiCancelarReserva } from "../../services/apiService";

export default function MisReservas() {
  const [reservas, setReservas] = useState([]);
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [cancellingId, setCancellingId] = useState(null);
  const [cardMessages, setCardMessages] = useState({});

  const cargarReservas = async (user) => {
    if (!user?.id) {
      setError("No se encontró sesión de usuario.");
      return;
    }

    try {
      setError("");
      const lista = await apiGetReservasHuesped(user.id);
      setReservas(lista || []);
    } catch (e) {
      setError(e.message || "No se pudieron cargar tus reservas.");
    }
  };

  useEffect(() => {
    const inicializar = async () => {
      const rawUser = localStorage.getItem("currentUser");
      let user = null;

      try {
        user = rawUser ? JSON.parse(rawUser) : null;
      } catch {
        user = null;
      }

      setCurrentUser(user);
      await cargarReservas(user);
      setLoading(false);
    };

    inicializar();
  }, []);

  const handleCancelar = async (reservaId) => {
    const confirmar = window.confirm(
      "¿Estás seguro de que deseas cancelar esta reserva? Esta acción no se puede deshacer."
    );
    if (!confirmar) return;

    setCancellingId(reservaId);
    setCardMessages((prev) => ({ ...prev, [reservaId]: null }));

    try {
      await apiCancelarReserva({ reservaId, huespedId: currentUser.id });
      setCardMessages((prev) => ({
        ...prev,
        [reservaId]: { type: "success", text: "Reserva cancelada correctamente." },
      }));
      await cargarReservas(currentUser);
    } catch (e) {
      setCardMessages((prev) => ({
        ...prev,
        [reservaId]: { type: "error", text: e.message || "No se pudo cancelar la reserva." },
      }));
    } finally {
      setCancellingId(null);
    }
  };

  const puedeCancelar = (reserva) => {
    return (
      reserva.estado === "CONFIRMADA" &&
      !reserva.checkInRealizado
    );
  };

  if (loading) {
    return (
      <section className="card stay-panel stay-panel--estancia">
        <h2>Mis Reservas</h2>
        <p className="stay-intro">Cargando tus reservas...</p>
      </section>
    );
  }

  return (
    <section className="card stay-panel stay-panel--estancia">
      <h2>Mis Reservas</h2>
      <p className="stay-intro">Consulta y gestiona todas tus reservas</p>

      {error && <p className="auth-message auth-message--error">{error}</p>}

      {reservas.length === 0 && !error ? (
        <p className="stay-empty">No tienes reservas registradas.</p>
      ) : (
        <div className="reservas-list">
          {reservas.map((reserva) => (
            <article key={reserva.id} className="reserva-card">
              <div className="reserva-card-header">
                <span className="reserva-card-id">Reserva #{reserva.id}</span>
                <span className={`reserva-card-estado reserva-card-estado--${reserva.estado?.toLowerCase()}`}>
                  {reserva.estado}
                </span>
              </div>

              <div className="reserva-card-body">
                <p><strong>Cápsula:</strong> {reserva.capsula?.id}</p>
                <p><strong>Entrada:</strong> {reserva.fechaInicio}</p>
                <p><strong>Salida prevista:</strong> {reserva.fechaFinal}</p>
                {reserva.fechaSalida && (
                  <p><strong>Salida real:</strong> {reserva.fechaSalida}</p>
                )}
                <p><strong>Check-in:</strong> {reserva.checkInRealizado ? "Sí" : "No"}</p>
              </div>

              {puedeCancelar(reserva) && (
                <div className="reserva-card-actions">
                  <button
                    type="button"
                    className="btn-cancelar-reserva"
                    onClick={() => handleCancelar(reserva.id)}
                    disabled={cancellingId === reserva.id}
                  >
                    {cancellingId === reserva.id ? "Cancelando..." : "Cancelar reserva"}
                  </button>
                </div>
              )}

              {cardMessages[reserva.id] && (
                <p className={`auth-message auth-message--${cardMessages[reserva.id].type}`} style={{ marginTop: "0.5rem" }}>
                  {cardMessages[reserva.id].text}
                </p>
              )}
            </article>
          ))}
        </div>
      )}
    </section>
  );
}
