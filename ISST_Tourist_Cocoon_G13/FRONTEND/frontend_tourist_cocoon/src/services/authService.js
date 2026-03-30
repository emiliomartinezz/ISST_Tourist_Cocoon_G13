import seedUsers from "../mocks/users.json";

const USERS_STORAGE_KEY = "mockUsers";
const SESSION_STORAGE_KEY = "isLoggedIn";
const CURRENT_USER_STORAGE_KEY = "currentUser";
const AUTH_EVENT = "authStateChanged";

function normalizeEmail(email = "") {
  return email.trim().toLowerCase();
}

function removePassword(user) {
  const { password, ...safeUser } = user;
  return safeUser;
}

function ensureUsersInitialized() {
  const storedUsers = localStorage.getItem(USERS_STORAGE_KEY);
  if (!storedUsers) {
    localStorage.setItem(USERS_STORAGE_KEY, JSON.stringify(seedUsers));
  }
}

function getUsers() {
  ensureUsersInitialized();
  const users = localStorage.getItem(USERS_STORAGE_KEY);
  return users ? JSON.parse(users) : [];
}

function saveUsers(users) {
  localStorage.setItem(USERS_STORAGE_KEY, JSON.stringify(users));
}

function validateAuthPayload(data) {
  if (!data?.email || !data?.password) {
    throw new Error("Email y contraseña son obligatorios");
  }
}

function emitAuthChange() {
  window.dispatchEvent(new Event(AUTH_EVENT));
}

export async function register(data) {
  validateAuthPayload(data);

  const users = getUsers();
  const email = normalizeEmail(data.email);
  const alreadyExists = users.some((user) => normalizeEmail(user.email) === email);

  if (alreadyExists) {
    throw new Error("Ya existe un usuario con ese email");
  }

  const newUser = {
    id: `u-${Date.now()}`,
    nombre: data.nombre?.trim() || "",
    dni: data.dni?.trim() || "",
    telefono: data.telefono?.trim() || "",
    email,
    password: data.password
  };

  users.push(newUser);
  saveUsers(users);

  return removePassword(newUser);
}

export async function login(data) {
  validateAuthPayload(data);

  const users = getUsers();
  const email = normalizeEmail(data.email);
  const user = users.find(
    (item) => normalizeEmail(item.email) === email && item.password === data.password
  );

  if (!user) {
    throw new Error("Credenciales incorrectas");
  }

  localStorage.setItem(SESSION_STORAGE_KEY, "true");
  localStorage.setItem(CURRENT_USER_STORAGE_KEY, JSON.stringify(removePassword(user)));
  emitAuthChange();

  return removePassword(user);
}

export async function logout() {
  localStorage.removeItem(SESSION_STORAGE_KEY);
  localStorage.removeItem(CURRENT_USER_STORAGE_KEY);
  emitAuthChange();
}