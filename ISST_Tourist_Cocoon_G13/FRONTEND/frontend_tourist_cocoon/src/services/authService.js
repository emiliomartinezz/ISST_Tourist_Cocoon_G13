import { apiLogin, apiRegister } from "./apiService";

const SESSION_KEY   = "isLoggedIn";
const CURRENT_USER  = "currentUser";
const AUTH_EVENT    = "authStateChanged";

function emitAuthChange() {
  window.dispatchEvent(new Event(AUTH_EVENT));
}

/**
 * Login contra el backend real.
 * Si el servidor no está disponible cae al mock de localStorage como fallback.
 */
export async function login({ email, password }) {
  if (!email || !password) throw new Error("Email y contraseña son obligatorios");

  let user;
  try {
    user = await apiLogin(email, password);
  } catch (err) {
    // Fallback: mock local (útil cuando el backend no arrancó todavía)
    user = loginMock(email, password);
  }

  localStorage.setItem(SESSION_KEY, "true");
  localStorage.setItem(CURRENT_USER, JSON.stringify(user));
  emitAuthChange();
  return user;
}

/**
 * Registro contra el backend real.
 */
export async function register(data) {
  if (!data?.email || !data?.password) throw new Error("Email y contraseña son obligatorios");
  return await apiRegister(data);
}

export async function logout() {
  localStorage.removeItem(SESSION_KEY);
  localStorage.removeItem(CURRENT_USER);
  emitAuthChange();
}

export function getCurrentUser() {
  const raw = localStorage.getItem(CURRENT_USER);
  if (!raw) return null;
  try { return JSON.parse(raw); } catch { return null; }
}

// ── Mock fallback ─────────────────────────────────────────────────────────────
const SEED_USERS = [
  { id: 1, nombre: "Usuario Demo", email: "demo@cocoon.com", password: "123456", rol: "HUESPED" }
];

function loginMock(email, password) {
  const user = SEED_USERS.find(
    u => u.email.toLowerCase() === email.toLowerCase() && u.password === password
  );
  if (!user) throw new Error("Credenciales incorrectas (backend no disponible)");
  const { password: _p, ...safe } = user;
  return safe;
}
