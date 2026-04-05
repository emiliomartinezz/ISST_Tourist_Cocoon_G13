import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import data from "../mocks/dashboardData.json";
import MiEstancia from "../Components/home/MiEstancia";
import NuevaReserva from "../Components/home/NuevaReserva";
import CheckIn from "../Components/home/CheckIn";
import MiPerfil from "../Components/home/MiPerfil";
import MisReservas from "../Components/home/MisReservas";
import { logout } from "../services/authService";
import "./App.css";

const TABS = {
  ESTANCIA: "mi_estancia",
  NUEVA_RESERVA: "nueva_reserva",
  MIS_RESERVAS: "mis_reservas",
  CHECKIN: "checkin",
  PERFIL: "mi_perfil"
};

export default function HomeDashboard() {
  const [activeTab, setActiveTab] = useState(TABS.ESTANCIA);
  const navigate = useNavigate();

  const userName = useMemo(() => {
    const userRaw = localStorage.getItem("currentUser");
    if (!userRaw) return "Huesped";

    try {
      const user = JSON.parse(userRaw);
      return user?.nombre || "Huesped";
    } catch {
      return "Huesped";
    }
  }, []);

  const handleLogout = async () => {
    await logout();
    navigate("/login", { replace: true });
  };

  const renderContent = () => {
    if (activeTab === TABS.ESTANCIA) return <MiEstancia />;
    if (activeTab === TABS.NUEVA_RESERVA) return <NuevaReserva />;
    if (activeTab === TABS.MIS_RESERVAS) return <MisReservas />;
    if (activeTab === TABS.PERFIL) return <MiPerfil />;
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
            <span className="portal-user-name">{userName}</span>
            <button type="button" className="portal-logout" onClick={handleLogout}>Salir</button>
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
          <button
            type="button"
            className={activeTab === TABS.MIS_RESERVAS ? "active" : ""}
            onClick={() => setActiveTab(TABS.MIS_RESERVAS)}
          >
            <span aria-hidden="true">☰</span>
            Mis Reservas
          </button>
          <button
            type="button"
            className={activeTab === TABS.PERFIL ? "active" : ""}
            onClick={() => setActiveTab(TABS.PERFIL)}
          >
            <span aria-hidden="true">👤</span>
            Mi Perfil
          </button>
        </nav>
      </header>

      {renderContent()}
    </main>
  );
}