import { useState } from "react";
import data from "../mocks/dashboardData.json";
import MiEstancia from "../Components/home/MiEstancia";
import NuevaReserva from "../Components/home/NuevaReserva";
import CheckIn from "../Components/home/CheckIn";
import "./App.css";

const TABS = {
  ESTANCIA: "mi_estancia",
  NUEVA_RESERVA: "nueva_reserva",
  CHECKIN: "checkin"
};

export default function HomeDashboard() {
  const [activeTab, setActiveTab] = useState(TABS.ESTANCIA);
  const { usuario } = data;

  const renderContent = () => {
    if (activeTab === TABS.ESTANCIA) return <MiEstancia />;
    if (activeTab === TABS.NUEVA_RESERVA) return <NuevaReserva />;
    return <CheckIn />;
  };

  return (
    <main className="dashboard">
      <header className="portal-header">
        <div className="portal-header-main">
          <div className="portal-brand">
            <div className="portal-brand-icon" aria-hidden="true">TC</div>
            <div className="portal-brand-copy">
              <h1>Tourist Cocoon</h1>
              <span>Portal del Huésped</span>
            </div>
          </div>

          <div className="portal-user-actions">
            <span className="portal-user-name">{usuario.nombre}</span>
            <button type="button" className="portal-logout">Salir</button>
          </div>
        </div>

        <nav className="portal-nav" aria-label="Navegación principal">
          <button
            type="button"
            className={activeTab === TABS.ESTANCIA ? "active" : ""}
            onClick={() => setActiveTab(TABS.ESTANCIA)}
          >
            <span aria-hidden="true">⌂</span>
            Mi Estancia
          </button>
          <button
            type="button"
            className={activeTab === TABS.NUEVA_RESERVA ? "active" : ""}
            onClick={() => setActiveTab(TABS.NUEVA_RESERVA)}
          >
            <span aria-hidden="true">☐</span>
            Nueva Reserva
          </button>
          <button
            type="button"
            className={activeTab === TABS.CHECKIN ? "active" : ""}
            onClick={() => setActiveTab(TABS.CHECKIN)}
          >
            <span aria-hidden="true">⇥</span>
            Check-in
          </button>
        </nav>
      </header>

      {renderContent()}
    </main>
  );
}