import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  apiAdminGetCapsulas,
  apiAdminGetReservas,
  apiAdminGetUsuarios,
  apiAdminGetOrdenesLimpieza,
  apiAdminActualizarEstadoCapsula,
  apiAdminCompletarOrdenLimpieza,
  apiAdminGetRegistrosAcceso,
} from "../services/apiService";
import { logout } from "../services/authService";
import "./App.css";

const TABS = {
  CAPSULAS: "capsulas",
  RESERVAS: "reservas",
  USUARIOS: "usuarios",
  LIMPIEZA: "limpieza",
  ACCESOS: "accesos",
};

export default function AdminDashboard() {
  const [activeTab, setActiveTab] = useState(TABS.CAPSULAS);
  const navigate = useNavigate();

  const userName = useMemo(() => {
    try {
      const user = JSON.parse(localStorage.getItem("currentUser"));
      return user?.nombre || "Admin";
    } catch {
      return "Admin";
    }
  }, []);

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  return (
    <main className="dashboard admin-dashboard">
      <header className="portal-header admin-header">
        <div className="portal-header-main">
          <div className="portal-brand">
            <div className="portal-brand-icon admin-brand-icon" aria-hidden="true">TC</div>
            <div className="portal-brand-copy">
              <h1>Tourist Cocoon</h1>
              <span className="admin-badge">Panel de Administración</span>
            </div>
          </div>
          <div className="portal-user-actions">
            <span className="portal-user-name">{userName}</span>
            <button type="button" className="portal-logout" onClick={handleLogout}>Salir</button>
          </div>
        </div>

        <nav className="portal-nav" aria-label="Navegación admin">
          {[
            { key: TABS.CAPSULAS, icon: "◻", label: "Cápsulas" },
            { key: TABS.RESERVAS, icon: "📋", label: "Reservas" },
            { key: TABS.USUARIOS, icon: "👤", label: "Usuarios" },
            { key: TABS.LIMPIEZA, icon: "🧹", label: "Limpieza" },
            { key: TABS.ACCESOS, icon: "🔐", label: "Accesos" },
          ].map((t) => (
            <button
              key={t.key}
              type="button"
              className={activeTab === t.key ? "active" : ""}
              onClick={() => setActiveTab(t.key)}
            >
              <span aria-hidden="true">{t.icon}</span>
              {t.label}
            </button>
          ))}
        </nav>
      </header>

      {activeTab === TABS.CAPSULAS && <CapsulasPanel />}
      {activeTab === TABS.RESERVAS && <ReservasPanel />}
      {activeTab === TABS.USUARIOS && <UsuariosPanel />}
      {activeTab === TABS.LIMPIEZA && <LimpiezaPanel />}
      {activeTab === TABS.ACCESOS && <AccesosPanel />}
    </main>
  );
}

/* ── Cápsulas ───────────────────────────────────────────────────────────────── */
function CapsulasPanel() {
  const [capsulas, setCapsulas] = useState([]);
  const [loading, setLoading] = useState(true);

  const cargar = () => {
    setLoading(true);
    apiAdminGetCapsulas()
      .then(setCapsulas)
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(cargar, []);

  const cambiarEstado = async (id, nuevoEstado) => {
    try {
      await apiAdminActualizarEstadoCapsula(id, nuevoEstado);
      cargar();
    } catch (e) {
      alert("Error: " + e.message);
    }
  };

  if (loading) return <p className="admin-loading">Cargando cápsulas…</p>;

  const porPlanta = capsulas.reduce((acc, c) => {
    (acc[c.planta] = acc[c.planta] || []).push(c);
    return acc;
  }, {});

  return (
    <section className="admin-section">
      <h2>Estado de las Cápsulas</h2>
      {Object.entries(porPlanta)
        .sort(([a], [b]) => a - b)
        .map(([planta, caps]) => (
          <div key={planta} className="admin-planta-group">
            <h3>Planta {planta}</h3>
            <div className="admin-capsulas-grid">
              {caps.map((c) => (
                <div key={c.id} className={`admin-capsula-card estado-${c.estado.toLowerCase()}`}>
                  <strong>{c.id}</strong>
                  <span className="admin-capsula-estado">{c.estado}</span>
                  <div className="admin-capsula-actions">
                    {c.estado !== "Disponible" && (
                      <button onClick={() => cambiarEstado(c.id, "Disponible")}>Disponible</button>
                    )}
                    {c.estado !== "Ocupada" && (
                      <button onClick={() => cambiarEstado(c.id, "Ocupada")}>Ocupada</button>
                    )}
                    {c.estado !== "Sucia" && (
                      <button onClick={() => cambiarEstado(c.id, "Sucia")}>Sucia</button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
    </section>
  );
}

/* ── Reservas ───────────────────────────────────────────────────────────────── */
function ReservasPanel() {
  const [reservas, setReservas] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiAdminGetReservas()
      .then(setReservas)
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p className="admin-loading">Cargando reservas…</p>;

  return (
    <section className="admin-section">
      <h2>Todas las Reservas ({reservas.length})</h2>
      {reservas.length === 0 ? (
        <p className="admin-empty">No hay reservas registradas.</p>
      ) : (
        <div className="admin-table-wrapper">
          <table className="admin-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Huésped</th>
                <th>Cápsula</th>
                <th>Inicio</th>
                <th>Fin</th>
                <th>Estado</th>
                <th>Check-in</th>
              </tr>
            </thead>
            <tbody>
              {reservas.map((r) => (
                <tr key={r.id}>
                  <td>{r.id}</td>
                  <td>{r.huesped?.nombre || "—"}</td>
                  <td>{r.capsula?.id || "—"}</td>
                  <td>{r.fechaInicio}</td>
                  <td>{r.fechaFinal}</td>
                  <td><span className={`admin-estado-badge estado-${r.estado?.toLowerCase()}`}>{r.estado}</span></td>
                  <td>{r.checkInRealizado ? "✅" : "❌"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}

/* ── Usuarios ───────────────────────────────────────────────────────────────── */
function UsuariosPanel() {
  const [usuarios, setUsuarios] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiAdminGetUsuarios()
      .then(setUsuarios)
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p className="admin-loading">Cargando usuarios…</p>;

  return (
    <section className="admin-section">
      <h2>Usuarios Registrados ({usuarios.length})</h2>
      {usuarios.length === 0 ? (
        <p className="admin-empty">No hay usuarios registrados.</p>
      ) : (
        <div className="admin-table-wrapper">
          <table className="admin-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Email</th>
                <th>NIF</th>
                <th>Teléfono</th>
                <th>Rol</th>
              </tr>
            </thead>
            <tbody>
              {usuarios.map((u) => (
                <tr key={u.id}>
                  <td>{u.id}</td>
                  <td>{u.nombre}</td>
                  <td>{u.email}</td>
                  <td>{u.nif}</td>
                  <td>{u.telefono || "—"}</td>
                  <td><span className={`admin-rol-badge rol-${u.rol?.toLowerCase()}`}>{u.rol}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}

/* ── Limpieza ───────────────────────────────────────────────────────────────── */
function LimpiezaPanel() {
  const [ordenes, setOrdenes] = useState([]);
  const [loading, setLoading] = useState(true);

  const cargar = () => {
    setLoading(true);
    apiAdminGetOrdenesLimpieza()
      .then(setOrdenes)
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(cargar, []);

  const completar = async (id) => {
    try {
      await apiAdminCompletarOrdenLimpieza(id);
      cargar();
    } catch (e) {
      alert("Error: " + e.message);
    }
  };

  if (loading) return <p className="admin-loading">Cargando órdenes…</p>;

  const pendientes = ordenes.filter((o) => o.estado === "PENDIENTE");
  const completadas = ordenes.filter((o) => o.estado === "COMPLETADA");

  return (
    <section className="admin-section">
      <h2>Órdenes de Limpieza</h2>

      <h3>Pendientes ({pendientes.length})</h3>
      {pendientes.length === 0 ? (
        <p className="admin-empty">No hay órdenes pendientes.</p>
      ) : (
        <div className="admin-ordenes-grid">
          {pendientes.map((o) => (
            <div key={o.id} className="admin-orden-card pendiente">
              <div className="admin-orden-header">
                <strong>Cápsula {o.capsula?.id}</strong>
                <span className="admin-estado-badge estado-pendiente">PENDIENTE</span>
              </div>
              <p className="admin-orden-msg">{o.mensaje}</p>
              <p className="admin-orden-fecha">{new Date(o.fechaCreacion).toLocaleString("es-ES")}</p>
              <button className="admin-orden-completar" onClick={() => completar(o.id)}>
                Marcar completada
              </button>
            </div>
          ))}
        </div>
      )}

      {completadas.length > 0 && (
        <>
          <h3>Completadas ({completadas.length})</h3>
          <div className="admin-ordenes-grid">
            {completadas.map((o) => (
              <div key={o.id} className="admin-orden-card completada">
                <div className="admin-orden-header">
                  <strong>Cápsula {o.capsula?.id}</strong>
                  <span className="admin-estado-badge estado-completada">COMPLETADA</span>
                </div>
                <p className="admin-orden-msg">{o.mensaje}</p>
                <p className="admin-orden-fecha">{new Date(o.fechaCreacion).toLocaleString("es-ES")}</p>
              </div>
            ))}
          </div>
        </>
      )}
    </section>
  );
}

function AccesosPanel() {
  const [registros, setRegistros] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [filtros, setFiltros] = useState({
    desde: "",
    hasta: "",
    capsulaId: "",
    huesped: "",
    resultado: "",
  });

  const [filtrosAplicados, setFiltrosAplicados] = useState({
    desde: "",
    hasta: "",
    capsulaId: "",
    huesped: "",
    resultado: "",
  });

  const hayFiltrosActivos = Object.values(filtrosAplicados).some((v) => v && v.trim() !== "");

  const cargar = (criterios = filtrosAplicados) => {
    setLoading(true);
    setError("");
    apiAdminGetRegistrosAcceso(criterios)
      .then(setRegistros)
      .catch((e) => {
        console.error(e);
        setRegistros([]);
        setError(e?.message || "No se pudieron cargar los accesos");
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    cargar({
      desde: "",
      hasta: "",
      capsulaId: "",
      huesped: "",
      resultado: "",
    });
  }, []);

  const onChange = (e) => {
    const { name, value } = e.target;
    setFiltros((prev) => ({ ...prev, [name]: value }));
  };

  const buscar = (e) => {
    e.preventDefault();
    setFiltrosAplicados(filtros);
    cargar(filtros);
  };

  const limpiar = () => {
    const vacios = {
      desde: "",
      hasta: "",
      capsulaId: "",
      huesped: "",
      resultado: "",
    };
    setFiltros(vacios);
    setFiltrosAplicados(vacios);
    cargar(vacios);
  };

  if (loading) return <p>Cargando accesos…</p>;

  return (
    <>
      <h2>Registros de Acceso ({registros.length})</h2>

      {error && <p className="admin-empty">{error}</p>}

      <form onSubmit={buscar} style={{ marginBottom: "1rem" }}>
        <div style={{ display: "grid", gridTemplateColumns: "repeat(5, minmax(0, 1fr))", gap: "0.75rem" }}>
          <div>
            <label>Desde</label>
            <input
              type="datetime-local"
              name="desde"
              value={filtros.desde}
              onChange={onChange}
            />
          </div>

          <div>
            <label>Hasta</label>
            <input
              type="datetime-local"
              name="hasta"
              value={filtros.hasta}
              onChange={onChange}
            />
          </div>

          <div>
            <label>ID de cápsula</label>
            <input
              type="text"
              name="capsulaId"
              placeholder="Ej. 102"
              value={filtros.capsulaId}
              onChange={onChange}
            />
          </div>

          <div>
            <label>Huésped (NIF o nombre)</label>
            <input
              type="text"
              name="huesped"
              placeholder="Ej. 12345678A o Juan"
              value={filtros.huesped}
              onChange={onChange}
            />
          </div>

          <div>
            <label>Estado</label>
            <select
              name="resultado"
              value={filtros.resultado}
              onChange={onChange}
            >
              <option value="">Todos</option>
              <option value="EXITO">Éxito</option>
              <option value="DENEGADO">Denegado</option>
            </select>
          </div>
        </div>

        <div style={{ display: "flex", gap: "0.75rem", marginTop: "1rem" }}>
          <button type="submit">Buscar</button>
          <button type="button" onClick={limpiar}>Limpiar filtros</button>
        </div>
      </form>

      {!error && registros.length === 0 ? (
        <p>
          {hayFiltrosActivos
            ? "No se encontraron registros de acceso para los criterios seleccionados."
            : "No hay registros de acceso."}
        </p>
      ) : !error ? (
        <table>
          <thead>
            <tr>
              <th>Fecha y hora</th>
              <th>Huésped</th>
              <th>NIF</th>
              <th>Email</th>
              <th>Puerta</th>
              <th>Objetivo</th>
              <th>Credencial</th>
              <th>Resultado</th>
              <th>Motivo</th>
              <th>Reserva</th>
            </tr>
          </thead>
          <tbody>
            {registros.map((r) => (
              <tr key={r.id}>
                <td>{new Date(r.fechaHora).toLocaleString("es-ES")}</td>
                <td>{r.huespedNombre || "—"}</td>
                <td>{r.huespedNif || "—"}</td>
                <td>{r.huespedEmail || "—"}</td>
                <td>{r.puerta}</td>
                <td>{r.objetivo || "—"}</td>
                <td>{r.credencial}</td>
                <td>{r.resultado}</td>
                <td>{r.motivo || "—"}</td>
                <td>{r.reservaId ?? "—"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : null}
    </>
  );
}
