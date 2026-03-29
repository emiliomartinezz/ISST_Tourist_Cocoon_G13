export default function TarjetaCapsula( props ) {
  const estadoClass =
    props.estadoOcupacion === "LIBRE" ? "estado-libre" : "estado-ocupada";

  return (
    <article className="tarjeta-capsula">
      <h3>{props.id}</h3>
      <p>
        <strong>Planta:</strong> {props.planta}
      </p>
      <p className={estadoClass}>
        <strong>Estado:</strong> {props.estadoOcupacion}
      </p>
      <button disabled={props.estadoOcupacion !== "LIBRE"}>
        {props.estadoOcupacion === "LIBRE" ? "Reservar" : "No disponible"}
      </button>
    </article>
  );
}