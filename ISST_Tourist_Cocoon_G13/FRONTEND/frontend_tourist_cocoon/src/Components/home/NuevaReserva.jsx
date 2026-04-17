import { useEffect, useMemo, useState } from "react";
import {
  apiCrearReserva,
  apiGetCapsulas,
  apiGetCapsulasDisponibles,
} from "../../services/apiService";

export default function NuevaReserva() {
  const [checkInDate, setCheckInDate] = useState("");
  const [checkOutDate, setCheckOutDate] = useState("");
  const [selectedCapsula, setSelectedCapsula] = useState("");
  const [capsulasLibres, setCapsulasLibres] = useState([]);
  const [isLoadingCapsulas, setIsLoadingCapsulas] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [reservaCreada, setReservaCreada] = useState(null);
  const [feedbackError, setFeedbackError] = useState("");

  const precioNoche = 15;

  useEffect(() => {
    const cargarCapsulas = async () => {
      setFeedbackError("");
      setIsLoadingCapsulas(true);

      try {
        let capsulas;
        if (checkInDate && checkOutDate) {
          capsulas = await apiGetCapsulasDisponibles(checkInDate, checkOutDate);
        } else {
          capsulas = await apiGetCapsulas();
        }

        const disponibles = (capsulas || []).filter(
          (capsula) => String(capsula.estado || "").toUpperCase() === "DISPONIBLE"
        );
        setCapsulasLibres(disponibles);
      } catch (error) {
        setCapsulasLibres([]);
        setFeedbackError(error.message || "No se pudieron cargar las cápsulas disponibles.");
      } finally {
        setIsLoadingCapsulas(false);
      }
    };

    cargarCapsulas();
  }, [checkInDate, checkOutDate]);

  useEffect(() => {
    if (!selectedCapsula) return;

    const sigueDisponible = capsulasLibres.some((capsula) => capsula.id === selectedCapsula);
    if (!sigueDisponible) {
      setSelectedCapsula("");
    }
  }, [capsulasLibres, selectedCapsula]);

  const noches = useMemo(() => {
    if (!checkInDate || !checkOutDate) return 0;
    const start = new Date(checkInDate);
    const end = new Date(checkOutDate);
    const diffMs = end.getTime() - start.getTime();
    if (diffMs <= 0) return 0;
    return Math.ceil(diffMs / (1000 * 60 * 60 * 24));
  }, [checkInDate, checkOutDate]);

  const precioTotal = noches * precioNoche;
  const puedeConfirmar = checkInDate && checkOutDate && selectedCapsula && noches > 0;

  const resetReserva = () => {
    setReservaCreada(null);
    setFeedbackError("");
    setSelectedCapsula("");
  };

  const handleConfirmarReserva = async () => {
    const raw = localStorage.getItem("currentUser");
    let user = null;

    try {
      user = raw ? JSON.parse(raw) : null;
    } catch {
      user = null;
    }

    if (!user?.id) {
      setFeedbackError("No se ha encontrado tu sesión. Vuelve a iniciar sesión.");
      return;
    }

    setIsSaving(true);
    setFeedbackError("");

    try {
      const nuevaReserva = await apiCrearReserva({
        huespedId: user.id,
        capsulaId: selectedCapsula,
        fechaInicio: checkInDate,
        fechaFinal: checkOutDate,
      });

      setReservaCreada(nuevaReserva);
    } catch (error) {
      setReservaCreada(null);
      setFeedbackError(error.message || "No se pudo completar la reserva.");
    } finally {
      setIsSaving(false);
    }
  };

  if (reservaCreada) {
    return (
      <section className="reserva-page">
        <article className="reserva-result reserva-result-ok" role="status" aria-live="polite">
          <h2>Reserva confirmada</h2>
          <p>Tu reserva se ha guardado correctamente en el sistema.</p>
          <div className="reserva-result-data">
            <p><strong>ID reserva:</strong> {reservaCreada.id}</p>
            <p><strong>Cápsula:</strong> {reservaCreada.capsula?.id || selectedCapsula}</p>
            <p><strong>Entrada:</strong> {reservaCreada.fechaInicio}</p>
            <p><strong>Salida:</strong> {reservaCreada.fechaFinal}</p>
            <p><strong>Estado:</strong> {reservaCreada.estado}</p>
          </div>
          <button type="button" className="reserva-confirmar" onClick={resetReserva}>
            Crear otra reserva
          </button>
        </article>
      </section>
    );
  }

  return (
    <section className="reserva-page">
      <header className="reserva-headline">
        <h2>Nueva Reserva</h2>
        <p>Selecciona tus fechas y cápsula preferida</p>
      </header>

      <div className="reserva-layout">
        <article className="reserva-panel">
          <h3>Detalles de la Reserva</h3>
          <p className="reserva-subtitle">Precio: 15 €/noche · Máximo 7 noches consecutivas</p>

          {feedbackError && (
            <p className="reserva-error" role="alert">{feedbackError}</p>
          )}

          <div className="reserva-dates">
            <label>
              Fecha de Check-in
              <input type="date" value={checkInDate} onChange={(e) => setCheckInDate(e.target.value)} />
            </label>
            <label>
              Fecha de Check-out
              <input type="date" value={checkOutDate} onChange={(e) => setCheckOutDate(e.target.value)} />
            </label>
          </div>

          <h4>Selecciona una Cápsula</h4>
          <div className="capsulas-picker">
            {isLoadingCapsulas && <p className="capsulas-loading">Cargando cápsulas...</p>}

            {!isLoadingCapsulas && capsulasLibres.length === 0 && (
              <p className="capsulas-empty">
                No hay cápsulas disponibles para las fechas seleccionadas.
              </p>
            )}

            {capsulasLibres.map((capsula) => (
              <button
                key={capsula.id}
                type="button"
                className={selectedCapsula === capsula.id ? "capsula-option selected" : "capsula-option"}
                onClick={() => setSelectedCapsula(capsula.id)}
              >
                <strong>{capsula.id}</strong>
                <span>Planta {capsula.planta}</span>
              </button>
            ))}
          </div>
          <p className="capsulas-meta">{capsulasLibres.length} cápsulas disponibles</p>
        </article>

        <aside className="reserva-summary-column">
          <article className="resumen-panel">
            <h3>Resumen</h3>
            {puedeConfirmar ? (
              <div className="resumen-data">
                <p>Cápsula: {selectedCapsula}</p>
                <p>Noches: {noches}</p>
                <p>Total: {precioTotal} €</p>
              </div>
            ) : (
              <p className="resumen-empty">Selecciona las fechas para ver el precio</p>
            )}
          </article>

          <button
            type="button"
            className="reserva-confirmar"
            disabled={!puedeConfirmar || isSaving}
            onClick={handleConfirmarReserva}
          >
            {isSaving ? "Confirmando..." : "Confirmar Reserva"}
          </button>

          {!feedbackError && !puedeConfirmar && (
            <p className="reserva-help">Completa fechas y selecciona una cápsula para reservar.</p>
          )}

          {feedbackError && (
            <button type="button" className="reserva-retry" onClick={() => setFeedbackError("")}>
              Cerrar error
            </button>
          )}

          <article className="reserva-info">
            <ul>
              <li>Check-in: 15:00</li>
              <li>Check-out: 11:00</li>
              <li>Código de acceso enviado 24h antes</li>
            </ul>
          </article>

          <article className="reserva-info">
            <ul>
              <li>Si una reserva falla, no se guarda en base de datos</li>
              <li>Las cápsulas mostradas se consultan en tiempo real</li>
            </ul>
          </article>
        </aside>
      </div>

      <article className="calendar-info">
        <p>
          <strong>Integración Google Calendar:</strong> Tu reserva se sincronizará automáticamente con Google
          Calendar y recibirás notificaciones antes del check-in.
        </p>
      </article>
    </section>
  );
}