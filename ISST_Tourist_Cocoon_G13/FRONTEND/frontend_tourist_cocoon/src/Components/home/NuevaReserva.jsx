import { useMemo, useState } from "react";
import data from "../../mocks/dashboardData.json";

export default function NuevaReserva() {
  const { capsulasDisponibles } = data;
  const [checkInDate, setCheckInDate] = useState("");
  const [checkOutDate, setCheckOutDate] = useState("");
  const [selectedCapsula, setSelectedCapsula] = useState("");

  const precioNoche = 15;
  const capsulasLibres = useMemo(
    () => capsulasDisponibles.filter((capsula) => capsula.estadoOcupacion === "LIBRE"),
    [capsulasDisponibles]
  );

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

  return (
    <section className="reserva-page">
      <header className="reserva-headline">
        <h2>Nueva Reserva</h2>
        <p>Selecciona tus fechas y cápsula preferida</p>
      </header>

      <div className="reserva-layout">
        <article className="reserva-panel">
          <h3>Detalles de la Reserva</h3>
          <p className="reserva-subtitle">Precio: 15 €/noche · Máximo 14 noches consecutivas</p>

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
            {capsulasLibres.map((capsula) => (
              <button
                key={capsula.id}
                type="button"
                className={selectedCapsula === capsula.id ? "capsula-option selected" : "capsula-option"}
                onClick={() => setSelectedCapsula(capsula.id)}
              >
                <strong>{capsula.id.replace("CAP-", "")}</strong>
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

          <button type="button" className="reserva-confirmar" disabled={!puedeConfirmar}>
            Confirmar Reserva
          </button>

          <article className="reserva-info">
            <ul>
              <li>Check-in: 15:00</li>
              <li>Check-out: 11:00</li>
              <li>Código de acceso enviado 24h antes</li>
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