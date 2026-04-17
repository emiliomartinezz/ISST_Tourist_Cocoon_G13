package tourist_cocoon.repository;

import tourist_cocoon.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    // Permite buscar reservas por huésped para verificar los límites de estancia legal
    List<Reserva> findByHuespedId(Long huespedId);

    @Query(value = """
        SELECT r.*
        FROM reservas r
        WHERE r.huesped_id = :huespedId
          AND r.fecha_inicio < :fechaFin
          AND r.fecha_final > :fechaInicio
          AND UPPER(r.estado) NOT IN ('CANCELADA', 'FINALIZADA')
    """, nativeQuery = true)
    List<Reserva> findReservasActivasSolapadas(Long huespedId, LocalDate fechaInicio, LocalDate fechaFin);
}