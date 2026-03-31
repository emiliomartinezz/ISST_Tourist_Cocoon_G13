// Todas las llamadas al backend Spring Boot centralizadas aquí.
// Cambia BASE_URL si el servidor corre en otro puerto/host.
const BASE_URL = "http://localhost:8080/api";

async function request(method, path, body) {
  const res = await fetch(`${BASE_URL}${path}`, {
    method,
    headers: { "Content-Type": "application/json" },
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  const text = await res.text();
  if (!res.ok) {
    // El backend devuelve strings de error en el body
    throw new Error(text || `Error ${res.status}`);
  }
  return text ? JSON.parse(text) : null;
}

// ── Auth ──────────────────────────────────────────────────────────────────────
export const apiLogin = (email, password) =>
  request("POST", "/auth/login", { email, password });

export const apiRegister = (data) =>
  request("POST", "/auth/register", data);

// ── Cápsulas ──────────────────────────────────────────────────────────────────
export const apiGetCapsulas = () =>
  request("GET", "/capsulas");

export const apiGetCapsulasDisponibles = (fechaInicio, fechaFin) =>
  request("GET", `/capsulas/disponibles?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`);

// ── Reservas ──────────────────────────────────────────────────────────────────
export const apiCrearReserva = ({ huespedId, capsulaId, fechaInicio, fechaFinal }) =>
  request("POST", "/reservas", { huespedId, capsulaId, fechaInicio, fechaFinal });

export const apiGetReservasHuesped = (huespedId) =>
  request("GET", `/reservas/huesped/${huespedId}`);

export const apiGetReservaActiva = (huespedId) =>
  request("GET", `/reservas/huesped/${huespedId}/activa`);
