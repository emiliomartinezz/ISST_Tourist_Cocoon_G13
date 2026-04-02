import { apiLogin, apiRegister } from "./apiService";

const CURRENT_USER_KEY = "currentUser";
const AUTH_EVENT = "authStateChanged";

function emitAuthChange() {
  window.dispatchEvent(new Event(AUTH_EVENT));
}

export async function login({ email, password }) {
  if (!email?.trim() || !password?.trim()) {
    throw new Error("Email y contraseña son obligatorios");
  }

  const user = await apiLogin(email.trim().toLowerCase(), password);

  localStorage.setItem(CURRENT_USER_KEY, JSON.stringify(user));
  emitAuthChange();
  return user;
}

export async function register(data) {
  const payload = {
    nombre: data.nombre?.trim(),
    nif: data.nif?.trim().toUpperCase(),
    telefono: data.telefono?.trim(),
    email: data.email?.trim().toLowerCase(),
    password: data.password,
    aceptaPoliticaPrivacidad: !!data.aceptaPoliticaPrivacidad
  };

  if (!payload.nombre || !payload.nif || !payload.email || !payload.password) {
    throw new Error("Completa todos los campos obligatorios");
  }

  if (!payload.aceptaPoliticaPrivacidad) {
    throw new Error("Debes aceptar la política de privacidad");
  }

  return await apiRegister(payload);
}

export function logout() {
  localStorage.removeItem(CURRENT_USER_KEY);
  emitAuthChange();
}

export function getCurrentUser() {
  const raw = localStorage.getItem(CURRENT_USER_KEY);
  if (!raw) return null;

  try {
    return JSON.parse(raw);
  } catch {
    return null;
  }
}

export function isAuthenticated() {
  return !!getCurrentUser();
}