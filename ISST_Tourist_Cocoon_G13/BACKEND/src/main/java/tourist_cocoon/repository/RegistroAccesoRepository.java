package tourist_cocoon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tourist_cocoon.model.RegistroAcceso;

import java.util.List;

public interface RegistroAccesoRepository extends JpaRepository<RegistroAcceso, Long> {

    List<RegistroAcceso> findByHuespedIdOrderByFechaHoraDesc(Long huespedId);
    List<RegistroAcceso> findAllByOrderByFechaHoraDesc();
}