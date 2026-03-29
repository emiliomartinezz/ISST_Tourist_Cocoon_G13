import data from "../../mocks/dashboardData.json";

export default function MiEstancia() {
  const { reservaActiva } = data;

  return (
    <section className="card">
      <h2>Tu reserva actual</h2>

      {reservaActiva ? (
        <div className="grid">
          <p><strong>Reserva:</strong> {reservaActiva.idReserva}</p>
          <p><strong>Entrada:</strong> {reservaActiva.fechaEntrada}</p>
          <p><strong>Salida:</strong> {reservaActiva.fechaSalida}</p>
          <hr />
          <p><strong>Cápsula ID:</strong> {reservaActiva.capsula.id}</p>
          <p><strong>Planta:</strong> {reservaActiva.capsula.planta}</p>
          <p>
            <strong>Estado ocupación:</strong> {reservaActiva.capsula.estadoOcupacion}
          </p>
        </div>
      ) : (
        <p>No tienes una reserva activa.</p>
      )}
    </section>
  );
}