import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import HomeDashboard from "./Views/Home";
import RegisterForm from "./Components/auth/RegisterForm";
import Authentication from "./Views/Authentication";


function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Redirección por defecto */}
        <Route path="/" element={<Navigate to="/home" replace />} />

        {/* Vistas principales */}
        <Route path="/home" element={<HomeDashboard />} />
        <Route path="/login" element={<Authentication />} />
        <Route path="/register" element={<RegisterForm />} />

        {/* Fallback para rutas no existentes */}
        <Route path="*" element={<Navigate to="/home" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;