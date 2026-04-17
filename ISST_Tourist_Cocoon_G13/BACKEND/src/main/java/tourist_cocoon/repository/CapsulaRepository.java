package tourist_cocoon.repository;

import tourist_cocoon.model.Capsula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CapsulaRepository extends JpaRepository<Capsula, String> {

    /**
     * Devuelve las cápsulas que NO tienen ninguna reserva que se solape
     * con el rango de fechas dado. Útil para la pantalla de "Nueva Reserva".
     */
    @Query(value = """
        SELECT c.*
        FROM capsulas c
        WHERE c.id NOT IN (
            SELECT r.capsula_id
            FROM reservas r
            WHERE r.fecha_inicio < :fechaFin
              AND r.fecha_final > :fechaInicio
              AND UPPER(r.estado) NOT IN ('CANCELADA', 'FINALIZADA')
        )
        AND UPPER(c.estado) = 'DISPONIBLE'
    """, nativeQuery = true)
    List<Capsula> findDisponiblesBetween(LocalDate fechaInicio, LocalDate fechaFin);
}
