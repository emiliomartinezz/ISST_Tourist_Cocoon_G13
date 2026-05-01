import { useState } from "react";
import { apiRealizarCheckIn } from "../../services/apiService";
import { ClipboardCheck, CreditCard, CheckCircle2, ShieldCheck } from "lucide-react";

export default function CheckIn() {
  const [documentoIdentidad, setDocumentoIdentidad] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [resultado, setResultado] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setResultado(null);

    const rawUser = localStorage.getItem("currentUser");
    let currentUser = null;

    try {
      currentUser = rawUser ? JSON.parse(rawUser) : null;
    } catch {
      currentUser = null;
    }

    if (!currentUser?.id) {
      setError("No se encontró sesión de usuario.");
      return;
    }

    if (!documentoIdentidad.trim()) {
      setError("Debes introducir tu documento de identidad.");
      return;
    }

    setLoading(true);

    try {
      const response = await apiRealizarCheckIn({
        huespedId: currentUser.id,
        documentoIdentidad: documentoIdentidad.trim()
      });
      setResultado(response);
    } catch (err) {
      setError(err.message || "No se pudo realizar el check-in.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="stay-panel card" style={{ maxWidth: 560, margin: "0 auto" }}>

      {/* Header */}
      <div style={{ display: "flex", alignItems: "center", gap: 12, marginBottom: 8 }}>
        <div style={{
          width: 44, height: 44, borderRadius: 13, flexShrink: 0,
          background: "linear-gradient(140deg, var(--forest-500) 0%, var(--forest-900) 100%)",
          display: "flex", alignItems: "center", justifyContent: "center",
          boxShadow: "0 4px 14px rgba(26,52,37,0.28)"
        }}>
          <ClipboardCheck size={22} color="rgba(236,253,245,0.95)" strokeWidth={1.8} />
        </div>
        <div>
          <h2 style={{ margin: 0, fontFamily: "var(--font-family-heading)", color: "var(--forest-900)", fontSize: "var(--text-2xl)" }}>
            Check-in automático
          </h2>
          <p className="stay-intro" style={{ marginTop: 2 }}>
            Valida tu identidad para acceder al edificio y a tu cápsula
          </p>
        </div>
      </div>

      <hr className="stay-divider" style={{ margin: "14px 0" }} />

      {error && (
        <p className="auth-message auth-message--error" style={{ marginBottom: 16 }}>{error}</p>
      )}

      {resultado ? (
        <div className="reserva-result reserva-result-ok" style={{ marginTop: 4 }}>
          <div style={{ display: "flex", alignItems: "center", gap: 10, marginBottom: 14 }}>
            <CheckCircle2 size={26} style={{ color: "var(--forest-500)", flexShrink: 0 }} />
            <h2 style={{ margin: 0, fontSize: "1.3rem", color: "var(--forest-900)", fontFamily: "var(--font-family-heading)" }}>
              ¡Check-in completado!
            </h2>
          </div>

          {resultado.mensaje && (
            <p style={{ margin: "0 0 12px", color: "#335443", fontSize: "var(--text-sm)" }}>{resultado.mensaje}</p>
          )}

          <div className="reserva-result-data">
            {resultado.reservaId    && <p><strong>Reserva:</strong> #{resultado.reservaId}</p>}
            {resultado.capsulaId    && <p><strong>Cápsula asignada:</strong> {resultado.capsulaId}</p>}
            {resultado.fechaCheckIn && <p><strong>Check-in:</strong> {resultado.fechaCheckIn}</p>}
            {resultado.accesoValidoHasta && (
              <p><strong>Acceso válido hasta:</strong> {resultado.accesoValidoHasta}</p>
            )}
            {resultado.datosAutoridadEnviados !== undefined && (
              <p>
                <strong>Datos enviados a autoridades:</strong>{" "}
                {resultado.datosAutoridadEnviados ? "✓ Enviados" : "Pendiente"}
              </p>
            )}
          </div>

          {resultado.codigoAcceso && (
            <div className="access-code-box" style={{ marginTop: 16 }}>
              <p style={{ margin: "0 0 10px", fontWeight: 700, color: "var(--forest-800)", fontSize: "var(--text-sm)" }}>
                Código de acceso
              </p>
              <span className="access-code-value">{resultado.codigoAcceso}</span>
              <p className="access-code-hint">Usa este código en el panel de entrada</p>
            </div>
          )}
        </div>
      ) : (
        <form onSubmit={handleSubmit}>
          {/* Info box */}
          <div style={{
            display: "flex", gap: 10, alignItems: "flex-start",
            padding: "12px 14px", borderRadius: 12, marginBottom: 20,
            background: "rgba(207,232,199,0.35)",
            border: "1px solid rgba(79,127,102,0.22)"
          }}>
            <ShieldCheck size={18} style={{ color: "var(--forest-600)", flexShrink: 0, marginTop: 1 }} />
            <p style={{ margin: 0, fontSize: "var(--text-sm)", color: "#2d4a3a", lineHeight: 1.55 }}>
              Tu documento se verificará de forma segura. Asegúrate de que coincide
              exactamente con el de la reserva.
            </p>
          </div>

          {/* DNI field */}
          <div className="auth-form">
            <label htmlFor="dni-input" style={{ color: "var(--forest-900)", fontWeight: 700, fontSize: "var(--text-sm)" }}>
              Documento de identidad
            </label>

            <div style={{ position: "relative" }}>
              <div style={{
                position: "absolute", left: 13, top: "50%", transform: "translateY(-50%)",
                display: "flex", alignItems: "center", gap: 6,
                color: "var(--forest-500)", pointerEvents: "none"
              }}>
                <CreditCard size={18} strokeWidth={1.8} />
                <span style={{
                  width: 1, height: 18, background: "rgba(79,127,102,0.28)", display: "block"
                }} />
              </div>
              <input
                id="dni-input"
                type="text"
                value={documentoIdentidad}
                onChange={(e) => setDocumentoIdentidad(e.target.value.toUpperCase())}
                placeholder="Ej: 12345678A"
                autoComplete="off"
                maxLength={20}
                style={{
                  width: "100%",
                  paddingLeft: 46,
                  border: "1.5px solid rgba(47,90,70,0.28)",
                  borderRadius: 13,
                  padding: "14px 14px 14px 46px",
                  background: "rgba(255,255,255,0.92)",
                  color: "var(--ink)",
                  fontSize: "var(--text-base)",
                  fontFamily: "var(--font-family-sans)",
                  fontWeight: 600,
                  letterSpacing: "0.08em",
                  outline: "none",
                  transition: "border-color 180ms ease, box-shadow 180ms ease",
                  boxSizing: "border-box"
                }}
                onFocus={e => {
                  e.target.style.borderColor = "var(--forest-500)";
                  e.target.style.boxShadow = "var(--shadow-focus)";
                  e.target.style.background = "#fff";
                }}
                onBlur={e => {
                  e.target.style.borderColor = "rgba(47,90,70,0.28)";
                  e.target.style.boxShadow = "none";
                  e.target.style.background = "rgba(255,255,255,0.92)";
                }}
              />
            </div>

            <p style={{ margin: "5px 0 0", fontSize: "var(--text-xs)", color: "var(--muted-foreground)" }}>
              DNI, NIE o pasaporte tal como aparece en el documento original
            </p>
          </div>

          <button
            type="submit"
            className="reserva-confirmar"
            disabled={loading}
            style={{ marginTop: 20, width: "100%", fontSize: "var(--text-base)" }}
          >
            {loading ? "Procesando..." : "Realizar check-in"}
          </button>
        </form>
      )}
    </div>
  );
}
