import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import data from "../mocks/dashboardData.json";
import MiEstancia from "../Components/home/MiEstancia";
import NuevaReserva from "../Components/home/NuevaReserva";
import CheckIn from "../Components/home/CheckIn";
import MiPerfil from "../Components/home/MiPerfil";
import MisReservas from "../Components/home/MisReservas";
import AyudaIncidencias from "../Components/home/AyudaIncidencias";
import { logout } from "../services/authService";
import { Home, CalendarPlus, ClipboardCheck, BookOpen, TriangleAlert, UserRound, LogOut, Leaf } from "lucide-react";
import "./App.css";

const TABS = {
  ESTANCIA: "mi_estancia",
  NUEVA_RESERVA: "nueva_reserva",
  MIS_RESERVAS: "mis_reservas",
  INCIDENCIAS: "incidencias",
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
    if (activeTab === TABS.INCIDENCIAS) return <AyudaIncidencias />;
    if (activeTab === TABS.PERFIL) return <MiPerfil />;
    return <CheckIn />;
  };

  return (
    <main className="dashboard">
      {/* 21st.dev Aurora Background — 3 animated blobs, nature green palette */}
      <div className="aurora-layer" aria-hidden="true">
        <div className="aw1" />
        <div className="aw2" />
        <div className="aw3" />
      </div>

      <header className="portal-header">
        <div className="portal-header-main">
          <div className="portal-brand">
            <div className="portal-brand-icon" aria-hidden="true"><Leaf size={20} strokeWidth={1.8} /></div>
            <div className="portal-brand-copy">
              <h1>Tourist Cocoon</h1>
              <span>Portal del Huésped</span>
            </div>
          </div>

          <div className="portal-user-actions">
            <span className="portal-user-name" data-initial={userName?.[0]?.toUpperCase() ?? "H"}>{userName}</span>
            <button type="button" className="portal-logout" onClick={handleLogout}>
              <LogOut size={15} aria-hidden="true" /> Salir
            </button>
          </div>
        </div>

        <nav className="portal-nav" aria-label="Navegación principal">
          <button type="button" className={activeTab === TABS.ESTANCIA ? "active" : ""} onClick={() => setActiveTab(TABS.ESTANCIA)}>
            <Home size={16} aria-hidden="true" /> Mi Estancia
          </button>
          <button type="button" className={activeTab === TABS.NUEVA_RESERVA ? "active" : ""} onClick={() => setActiveTab(TABS.NUEVA_RESERVA)}>
            <CalendarPlus size={16} aria-hidden="true" /> Nueva Reserva
          </button>
          <button type="button" className={activeTab === TABS.CHECKIN ? "active" : ""} onClick={() => setActiveTab(TABS.CHECKIN)}>
            <ClipboardCheck size={16} aria-hidden="true" /> Check-in
          </button>
          <button type="button" className={activeTab === TABS.MIS_RESERVAS ? "active" : ""} onClick={() => setActiveTab(TABS.MIS_RESERVAS)}>
            <BookOpen size={16} aria-hidden="true" /> Mis Reservas
          </button>
          <button type="button" className={activeTab === TABS.INCIDENCIAS ? "active" : ""} onClick={() => setActiveTab(TABS.INCIDENCIAS)}>
            <TriangleAlert size={16} aria-hidden="true" /> Incidencias
          </button>
          <button type="button" className={activeTab === TABS.PERFIL ? "active" : ""} onClick={() => setActiveTab(TABS.PERFIL)}>
            <UserRound size={16} aria-hidden="true" /> Mi Perfil
          </button>
        </nav>
      </header>

      {renderContent()}
    </main>
  );
}