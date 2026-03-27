import React, { useState } from 'react';

const Register = () => {
    const [formData, setFormData] = useState({
        nombre: '', nif: '', email: '', password: ''
    });

    const handleSubmit = async (e) => {
        e.preventDefault();
        const response = await fetch('http://localhost:8080/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formData)
        });
        if (response.ok) alert("Registro completado con éxito");
    };

    return (
        <form onSubmit={handleSubmit}>
            <h2>Registro Tourist Cocoon</h2>
            <input type="text" placeholder="Nombre completo" onChange={e => setFormData({...formData, nombre: e.target.value})} />
            <input type="text" placeholder="NIF/DNI" onChange={e => setFormData({...formData, nif: e.target.value})} />
            <input type="email" placeholder="Email" onChange={e => setFormData({...formData, email: e.target.value})} />
            <input type="password" placeholder="Contraseña" onChange={e => setFormData({...formData, password: e.target.value})} />
            <button type="submit">Registrarse</button>
        </form>
    );
};

export default Register;