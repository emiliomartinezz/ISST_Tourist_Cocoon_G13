package tourist_cocoon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tourist_cocoon.model.RegistroAcceso;

import java.time.LocalDateTime;
import java.util.List;

public interface RegistroAccesoRepository extends JpaRepository<RegistroAcceso, Long> {

    List<RegistroAcceso> findByHuespedIdOrderByFechaHoraDesc(Long huespedId);

    List<RegistroAcceso> findAllByOrderByFechaHoraDesc();

    @Query("""
        SELECT r
        FROM RegistroAcceso r
        LEFT JOIN r.huesped h
        WHERE (:desde IS NULL OR r.fechaHora >= :desde)
          AND (:hasta IS NULL OR r.fechaHora <= :hasta)
          AND (:resultado IS NULL OR UPPER(r.resultado) = UPPER(:resultado))
          AND (
                :capsulaId IS NULL OR
                (UPPER(r.puerta) = 'CAPSULA' AND UPPER(r.objetivo) = UPPER(:capsulaId))
              )
          AND (
                :huesped IS NULL OR
                LOWER(h.nombre) LIKE LOWER(CONCAT('%', :huesped, '%')) OR
                LOWER(h.nif) LIKE LOWER(CONCAT('%', :huesped, '%'))
              )
        ORDER BY r.fechaHora DESC
    """)
    List<RegistroAcceso> buscarConFiltros(
        @Param("desde") LocalDateTime desde,
        @Param("hasta") LocalDateTime hasta,
        @Param("capsulaId") String capsulaId,
        @Param("huesped") String huesped,
        @Param("resultado") String resultado
    );
}