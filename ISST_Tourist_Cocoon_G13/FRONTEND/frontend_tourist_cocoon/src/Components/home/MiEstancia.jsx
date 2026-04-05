import { useEffect, useState } from "react";
import {
  apiGetReservaActiva,
  apiSolicitarAcceso,
  apiCheckoutReserva,
} from "../../services/apiService";

export default function MiEstancia() {
  const [reservaActiva, setReservaActiva] = useState(null);
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [accessLoading, setAccessLoading] = useState(false);
  const [accessMessage, setAccessMessage] = useState("");
  const [accessResult, setAccessResult] = useState("");

  const [checkoutLoading, setCheckoutLoading] = useState(false);
  const [checkoutMessage, setCheckoutMessage] = useState("");
  const [checkoutError, setCheckoutError] = useState("");

  const cargarReservaActiva = async (user) => {
    if (!user?.id) {
      setError("No se encontró sesión de usuario.");
      setReservaActiva(null);
      return;
    }

    try {
      setError("");
      const activa = await apiGetReservaActiva(user.id);
      setReservaActiva(activa);
    } catch (e) {
      if (e.status === 404) {
        setReservaActiva(null);
        setError("");
      } else {
        setReservaActiva(null);
        setError(e.message || "No se pudo cargar tu reserva activa.");
      }
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
      await cargarReservaActiva(user);
      setLoading(false);
    };

    inicializar();
  }, []);

  const solicitarAcceso = async (puerta) => {
    if (!currentUser?.id || !reservaActiva) return;

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

  const realizarCheckOut = async () => {
    if (!currentUser?.id || !reservaActiva?.id) return;

    const confirmar = window.confirm(
      "¿Confirmas el check-out? Perderás el acceso al edificio y a la cápsula."
    );

    if (!confirmar) return;

    setCheckoutLoading(true);
    setCheckoutMessage("");
    setCheckoutError("");
    setAccessMessage("");
    setAccessResult("");

    try {
      const hoy = new Date().toISOString().split("T")[0];

      const response = await apiCheckoutReserva({
        reservaId: reservaActiva.id,
        huespedId: currentUser.id,
        fechaSalida: hoy,
      });

      setCheckoutMessage("¡Gracias por su visita! Esperamos verle de nuevo pronto.");

      setReservaActiva(null);
    } catch (e) {
      setCheckoutError(e.message || "No se pudo realizar el check-out.");
    } finally {
      setCheckoutLoading(false);
    }
  };

  if (loading) {
    return (
      <section className="card stay-panel stay-panel--estancia">
        <h2>Tu reserva actual</h2>
        <p className="stay-intro">Cargando datos de tu estancia...</p>
      </section>
    );
  }

  return (
    <section className="card stay-panel stay-panel--estancia">
      <h2>Tu reserva actual</h2>

      {error && <p className="auth-message auth-message--error">{error}</p>}
      {checkoutMessage && (
        <p className="auth-message auth-message--success">{checkoutMessage}</p>
      )}
      {checkoutError && (
        <p className="auth-message auth-message--error">{checkoutError}</p>
      )}

      {reservaActiva ? (
        <>
          <p className="stay-kv-item">
            <strong>Reserva:</strong> {reservaActiva.id}
          </p>
          <p className="stay-kv-item">
            <strong>Entrada:</strong> {reservaActiva.fechaInicio}
          </p>
          <p className="stay-kv-item">
            <strong>Salida:</strong> {reservaActiva.fechaFinal}
          </p>

          <hr className="stay-divider" />

          <p className="stay-kv-item">
            <strong>Cápsula ID:</strong> {reservaActiva.capsula.id}
          </p>
          <p className="stay-kv-item">
            <strong>Planta:</strong> {reservaActiva.capsula.planta}
          </p>
          <p className="stay-kv-item">
            <strong>Estado ocupación:</strong> {reservaActiva.capsula.estado}
          </p>
          <p className="stay-kv-item">
            <strong>Estado reserva:</strong> {reservaActiva.estado}
          </p>

          {reservaActiva.checkInRealizado && reservaActiva.codigoAcceso && (
            <>
              <hr className="stay-divider" />
              <h3 className="stay-subtitle">Tu código de acceso</h3>
              <div className="access-code-box">
                <span className="access-code-value">{reservaActiva.codigoAcceso}</span>
                <p className="access-code-hint">
                  Usa este código para abrir la puerta del edificio y tu cápsula <strong>{reservaActiva.capsula.id}</strong>
                </p>
                {reservaActiva.accesoValidoHasta && (
                  <p className="access-code-expiry">
                    Válido hasta: {new Date(reservaActiva.accesoValidoHasta).toLocaleString("es-ES")}
                  </p>
                )}
              </div>
            </>
          )}

          <hr className="stay-divider" />

          <h3 className="stay-subtitle">Acceso</h3>
          <p className="stay-kv-item">
            <strong>Credencial activa:</strong> APP
          </p>

          <div className="access-actions">
            <button
              type="button"
              onClick={() => solicitarAcceso("EDIFICIO")}
              disabled={accessLoading || checkoutLoading}
            >
              {accessLoading ? "Procesando..." : "Abrir edificio"}
            </button>

            <button
              type="button"
              onClick={() => solicitarAcceso("CAPSULA")}
              disabled={accessLoading || checkoutLoading}
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
              style={{ marginTop: "0.9rem" }}
            >
              {accessMessage}
            </p>
          )}

          <hr className="stay-divider" />

          <h3 className="stay-subtitle">Salida</h3>
          <p className="stay-intro">
            Al realizar el check-out se revocará tu acceso y la cápsula quedará
            pendiente de limpieza.
          </p>

          <button
            type="button"
            onClick={realizarCheckOut}
            disabled={checkoutLoading || accessLoading}
          >
            {checkoutLoading ? "Procesando check-out..." : "Realizar check-out"}
          </button>
        </>
      ) : !error ? (
        <p className="stay-empty">No tienes una reserva activa.</p>
      ) : null}
    </section>
  );
}