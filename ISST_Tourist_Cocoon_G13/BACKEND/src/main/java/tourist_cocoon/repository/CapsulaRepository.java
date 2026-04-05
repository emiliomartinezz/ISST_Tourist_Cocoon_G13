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
    @Query("""
        SELECT c FROM Capsula c
        WHERE c.id NOT IN (
            SELECT r.capsula.id FROM Reserva r
            WHERE r.fechaInicio < :fechaFin
              AND r.fechaFinal > :fechaInicio
              AND r.estado NOT IN ('CANCELADA', 'FINALIZADA')
        )
        AND c.estado = 'Disponible'
    """)
    List<Capsula> findDisponiblesBetween(LocalDate fechaInicio, LocalDate fechaFin);
}
