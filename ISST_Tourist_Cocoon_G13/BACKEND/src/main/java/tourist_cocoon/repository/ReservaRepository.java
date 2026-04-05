package tourist_cocoon.repository;

import tourist_cocoon.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    // Permite buscar reservas por huésped para verificar los límites de estancia legal
    List<Reserva> findByHuespedId(Long huespedId);

    @Query("""
        SELECT r FROM Reserva r
        WHERE r.huesped.id = :huespedId
          AND r.fechaInicio < :fechaFin
          AND r.fechaFinal > :fechaInicio
          AND r.estado NOT IN ('CANCELADA', 'FINALIZADA')
    """)
    List<Reserva> findReservasActivasSolapadas(Long huespedId, LocalDate fechaInicio, LocalDate fechaFin);
}