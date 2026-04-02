import { useEffect, useState } from "react";
import { apiGetReservaActiva, apiSolicitarAcceso } from "../../services/apiService";

export default function MiEstancia() {
  const [reservaActiva, setReservaActiva] = useState(null);
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [accessLoading, setAccessLoading] = useState(false);
  const [accessMessage, setAccessMessage] = useState("");
  const [accessResult, setAccessResult] = useState("");

  useEffect(() => {
    const cargarReservaActiva = async () => {
      const rawUser = localStorage.getItem("currentUser");
      let user = null;

      try {
        user = rawUser ? JSON.parse(rawUser) : null;
      } catch {
        user = null;
      }

      setCurrentUser(user);

      if (!user?.id) {
        setError("No se encontró sesión de usuario.");
        setLoading(false);
        return;
      }

      try {
        const activa = await apiGetReservaActiva(user.id);
        setReservaActiva(activa);
      } catch (e) {
        setError(e.message || "No se pudo cargar tu reserva activa.");
      } finally {
        setLoading(false);
      }
    };

    cargarReservaActiva();
  }, []);

  const solicitarAcceso = async (puerta) => {
    if (!currentUser?.id || !reservaActiva) {
      return;
    }

    setAccessLoading(true);
    setAccessMessage("");
    setAccessResult("");

    try {
      const response = await apiSolicitarAcceso({
        huespedId: currentUser.id,
        puerta,
        capsulaId: puerta === "CAPSULA" ? reservaActiva.capsula.id : null,
        credencial: "APP",
      });

      setAccessMessage(response.mensaje);
      setAccessResult(response.resultado);
    } catch (e) {
      setAccessMessage(e.message || "No se pudo solicitar el acceso.");
      setAccessResult("DENEGADO");
    } finally {
      setAccessLoading(false);
    }
  };

  if (loading) {
    return (
      <section>
        <h2>Tu reserva actual</h2>
        <p>Cargando datos de tu estancia...</p>
      </section>
    );
  }

  return (
    <section>
      <h2>Tu reserva actual</h2>

      {error && <p className="auth-message auth-message--error">{error}</p>}

      {reservaActiva ? (
        <>
          <p>
            <strong>Reserva:</strong> {reservaActiva.id}
          </p>
          <p>
            <strong>Entrada:</strong> {reservaActiva.fechaInicio}
          </p>
          <p>
            <strong>Salida:</strong> {reservaActiva.fechaFinal}
          </p>

          <hr />

          <p>
            <strong>Cápsula ID:</strong> {reservaActiva.capsula.id}
          </p>
          <p>
            <strong>Planta:</strong> {reservaActiva.capsula.planta}
          </p>
          <p>
            <strong>Estado ocupación:</strong> {reservaActiva.capsula.estado}
          </p>
          <p>
            <strong>Estado reserva:</strong> {reservaActiva.estado}
          </p>

          <hr />

          <h3>Acceso</h3>
          <p>
            <strong>Credencial activa:</strong> APP
          </p>

          <div className="access-actions">
            <button
              type="button"
              onClick={() => solicitarAcceso("EDIFICIO")}
              disabled={accessLoading}
            >
              {accessLoading ? "Procesando..." : "Abrir edificio"}
            </button>

            <button
              type="button"
              onClick={() => solicitarAcceso("CAPSULA")}
              disabled={accessLoading}
            >
              {accessLoading ? "Procesando..." : "Abrir cápsula"}
            </button>
          </div>

          {accessMessage && (
            <p
              className={
                accessResult === "EXITO"
                  ? "auth-message auth-message--success"
                  : "auth-message auth-message--error"
              }
              style={{ marginTop: "1rem" }}
            >
              {accessMessage}
            </p>
          )}
        </>
      ) : (
        <p>No tienes una reserva activa.</p>
      )}
    </section>
  );
}