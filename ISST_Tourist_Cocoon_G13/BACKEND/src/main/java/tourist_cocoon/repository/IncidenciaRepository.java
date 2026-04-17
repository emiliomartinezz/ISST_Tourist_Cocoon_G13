package tourist_cocoon.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tourist_cocoon.model.Incidencia;
import tourist_cocoon.model.enums.EstadoIncidencia;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {

	List<Incidencia> findAllByOrderByFechaCreacionDesc();

	List<Incidencia> findByHuespedIdOrderByFechaCreacionDesc(Long huespedId);

	List<Incidencia> findByHuespedIdAndEstadoInOrderByFechaCreacionDesc(Long huespedId, List<EstadoIncidencia> estados);
}
