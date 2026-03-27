import React, { useState } from 'react';

const AvailabilitySelector = () => {
    const [dates, setDates] = useState({ start: '', end: '' });

    const handleReserve = () => {
        // Aquí llamarías a tu API del backend
        console.log("Reservando desde:", dates.start, "hasta:", dates.end);
        alert("Reserva enviada. Sincronizando con Google Calendar..."); [9]
    };

    return (
        <div className="availability-container">
            <h3>Selecciona tu Estancia</h3>
            {/* El SDD sugiere que esto debe ser Mobile-First y táctil [9, 11] */}
            <input type="date" onChange={e => setDates({...dates, start: e.target.value})} />
            <input type="date" onChange={e => setDates({...dates, end: e.target.value})} />
            <button onClick={handleReserve}>Verificar Disponibilidad</button>
        </div>
    );
};