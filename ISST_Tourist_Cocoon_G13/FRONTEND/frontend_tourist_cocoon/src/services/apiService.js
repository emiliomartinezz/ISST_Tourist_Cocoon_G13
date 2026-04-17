// Todas las llamadas al backend Spring Boot centralizadas aquí.
// Cambia BASE_URL si el servidor corre en otro puerto/host.
const BASE_URL = "http://localhost:8080/api";
class ApiError extends Error {
  constructor(message, status, validationErrors = null) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.validationErrors = validationErrors;
  }
}

async function parseApiPayload(response) {
  const contentType = response.headers.get("content-type") || "";

  if (contentType.includes("application/json")) {
    return await response.json();
  }

  const text = await response.text();

  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}

function extractApiErrorMessage(payload, fallback = "Se ha producido un error") {
  if (!payload) return fallback;

  if (typeof payload === "string") {
    return payload;
  }

  if (payload.validationErrors && typeof payload.validationErrors === "object") {
    const firstValidationError = Object.values(payload.validationErrors).find(Boolean);
    if (firstValidationError) return firstValidationError;
  }

  if (payload.message) return payload.message;
  if (payload.error) return payload.error;

  return fallback;
}

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
export async function apiLogin(email, password) {
  const response = await fetch(`${BASE_URL}/auth/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ email, password })
  });

  const payload = await parseApiPayload(response);

  if (!response.ok) {
    throw new ApiError(
      extractApiErrorMessage(payload, "No se pudo iniciar sesión"),
      response.status,
      payload?.validationErrors || null
    );
  }

  return payload;
}

export async function apiRegister(data) {
  const response = await fetch(`${BASE_URL}/auth/register`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(data)
  });

  const payload = await parseApiPayload(response);

  if (!response.ok) {
    throw new ApiError(
      extractApiErrorMessage(payload, "No se pudo completar el registro"),
      response.status,
      payload?.validationErrors || null
    );
  }

  return payload;
}

// ── Perfil ────────────────────────────────────────────────────────────────────
export async function apiGetPerfil(userId) {
  const response = await fetch(`${BASE_URL}/auth/perfil/${userId}`, {
    method: "GET",
    headers: { "Content-Type": "application/json" }
  });

  const payload = await parseApiPayload(response);

  if (!response.ok) {
    throw new ApiError(
      extractApiErrorMessage(payload, "No se pudo cargar el perfil"),
      response.status,
      payload?.validationErrors || null
    );
  }

  return payload;
}

export async function apiUpdatePerfil(userId, { nombre, email, telefono }) {
  const response = await fetch(`${BASE_URL}/auth/perfil/${userId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ nombre, email, telefono })
  });

  const payload = await parseApiPayload(response);

  if (!response.ok) {
    throw new ApiError(
      extractApiErrorMessage(payload, "No se pudo actualizar el perfil"),
      response.status,
      payload?.validationErrors || null
    );
  }

  return payload;
}

export async function apiSolicitarAcceso({ huespedId, puerta, capsulaId = null, credencial = "APP" }) {
  const response = await fetch(`${BASE_URL}/accesos/solicitar`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      huespedId,
      puerta,
      capsulaId,
      credencial
    })
  });

  const payload = await parseApiPayload(response);

  if (!response.ok) {
    throw new ApiError(
      extractApiErrorMessage(payload, "No se pudo solicitar el acceso"),
      response.status,
      payload?.validationErrors || null
    );
  }

  return payload;
}
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

export async function apiGetReservaActiva(huespedId) {
  const response = await fetch(`${BASE_URL}/reservas/huesped/${huespedId}/activa`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json"
    }
  });

  if (response.status === 404) {
    return null;
  }

  const payload = await parseApiPayload(response);

  if (!response.ok) {
    throw new ApiError(
      extractApiErrorMessage(payload, "No se pudo cargar la reserva activa"),
      response.status,
      payload?.validationErrors || null
    );
  }

  return payload;
}

export async function apiRealizarCheckIn({ huespedId, documentoIdentidad }) {
  const response = await fetch(`${BASE_URL}/checkin`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      huespedId,
      documentoIdentidad
    })
  });

  const payload = await parseApiPayload(response);

  if (!response.ok) {
    throw new ApiError(
      extractApiErrorMessage(payload, "No se pudo realizar el check-in"),
      response.status,
      payload?.validationErrors || null
    );
  }

  return payload;
}



export async function apiCheckoutReserva({ reservaId, huespedId, fechaSalida }) {
  const response = await fetch(`${BASE_URL}/reservas/${reservaId}/checkout`, {
    method: "PATCH",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      huespedId,
      fechaSalida
    })
  });

  const payload = await parseApiPayload(response);

  if (!response.ok) {
    throw new ApiError(
      extractApiErrorMessage(payload, "No se pudo realizar el check-out"),
      response.status,
      payload?.validationErrors || null
    );
  }

  return payload;
}

export async function apiCancelarReserva({ reservaId, huespedId }) {
  const response = await fetch(`${BASE_URL}/reservas/${reservaId}/cancelar`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ huespedId })
  });

  const payload = await parseApiPayload(response);

  if (!response.ok) {
    throw new ApiError(
      extractApiErrorMessage(payload, "No se pudo cancelar la reserva"),
      response.status,
      payload?.validationErrors || null
    );
  }

  return payload;
}

// ── Admin ─────────────────────────────────────────────────────────────────────
export const apiAdminGetReservas = () => request("GET", "/admin/reservas");
export const apiAdminGetUsuarios = () => request("GET", "/admin/usuarios");
export const apiAdminGetCapsulas = () => request("GET", "/admin/capsulas");
export const apiAdminGetOrdenesLimpieza = () => request("GET", "/admin/ordenes-limpieza");
export const apiAdminGetRegistrosAcceso = (filtros = {}) => {
  const params = new URLSearchParams();

  const normalizeDateTimeLocal = (value) => {
    if (!value) return "";
    const trimmed = value.trim();
    if (!trimmed) return "";

    // datetime-local usually comes as yyyy-MM-ddTHH:mm; backend LocalDateTime parses reliably with seconds.
    return /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/.test(trimmed)
      ? `${trimmed}:00`
      : trimmed;
  };

  const normalizeText = (value) => {
    if (typeof value !== "string") return value;
    const trimmed = value.trim();
    return trimmed || "";
  };

  const desde = normalizeDateTimeLocal(filtros.desde);
  const hasta = normalizeDateTimeLocal(filtros.hasta);
  const capsulaId = normalizeText(filtros.capsulaId);
  const huesped = normalizeText(filtros.huesped);
  const resultado = normalizeText(filtros.resultado);

  if (desde) params.append("desde", desde);
  if (hasta) params.append("hasta", hasta);
  if (capsulaId) params.append("capsulaId", capsulaId);
  if (huesped) params.append("huesped", huesped);
  if (resultado) params.append("resultado", resultado);

  const query = params.toString();
  return request("GET", `/admin/accesos${query ? `?${query}` : ""}`);
};

export const apiAdminActualizarEstadoCapsula = (id, estado) =>
  request("PATCH", `/admin/capsulas/${id}/estado`, { estado });

export const apiAdminCompletarOrdenLimpieza = (id) =>
  request("PATCH", `/admin/ordenes-limpieza/${id}/completar`);

export async function apiAdminExportarRegistrosAccesoCSV(filtros = {}) {
  const params = new URLSearchParams();

  if (filtros.desde) params.append("desde", filtros.desde);
  if (filtros.hasta) params.append("hasta", filtros.hasta);
  if (filtros.capsulaId) params.append("capsulaId", filtros.capsulaId);
  if (filtros.huesped) params.append("huesped", filtros.huesped);
  if (filtros.resultado) params.append("resultado", filtros.resultado);

  const query = params.toString();
  const response = await fetch(
    `${BASE_URL}/admin/accesos/export/csv${query ? `?${query}` : ""}`,
    { method: "GET" }
  );

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `Error ${response.status}`);
  }

  return await response.blob();
}
