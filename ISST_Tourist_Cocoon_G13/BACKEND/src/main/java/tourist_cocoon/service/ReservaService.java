package com.touristcocoon.service;

import com.touristcocoon.model.Reserva;
import com.touristcocoon.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.temporal.ChronoUnit;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    // Estos valores serían configurables por el administrador [1, 7]
    private final int MAX_NOCHES_SEGUIDAS = 7; 
    private final int MAX_NOCHES_MES = 15;

    public boolean validarReglasEstancia(Reserva nuevaReserva) {
        long noches = ChronoUnit.DAYS.between(nuevaReserva.getFechaInicio(), nuevaReserva.getFechaFinal());
        
        // 1. Verificar noches consecutivas [1, 8]
        if (noches > MAX_NOCHES_SEGUIDAS) return false;

        // 2. Aquí se añadiría la lógica para consultar en la BD las noches 
        // acumuladas en el mes actual para ese huésped [1, 6]
        
        return true;
    }
}