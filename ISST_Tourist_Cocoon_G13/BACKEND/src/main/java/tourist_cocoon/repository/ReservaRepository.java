package tourist_cocoon.repository;

import tourist_cocoon.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    // Permite buscar reservas por huésped para verificar los límites de estancia legal
    List<Reserva> findByHuespedId(Long huespedId);
}