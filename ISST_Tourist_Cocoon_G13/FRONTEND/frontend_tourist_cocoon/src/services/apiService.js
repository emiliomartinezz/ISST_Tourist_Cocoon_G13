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

export const apiGetReservaActiva = (huespedId) =>
  request("GET", `/reservas/huesped/${huespedId}/activa`);

export async function apiRealizarCheckIn({ huespedId, documentoIdentidad, documentoValidado }) {
  const response = await fetch(`${BASE_URL}/checkin`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      huespedId,
      documentoIdentidad,
      documentoValidado
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