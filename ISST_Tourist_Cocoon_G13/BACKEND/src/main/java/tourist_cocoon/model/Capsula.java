package tourist_cocoon.model;

import jakarta.persistence.*;
import tourist_cocoon.model.converter.EstadoCapsulaConverter;
import tourist_cocoon.model.enums.EstadoCapsula;

@Entity
@Table(name = "capsulas")
public class Capsula {
    @Id
    private String id;

    @Column(nullable = false)
    private Integer planta;

    @Column(nullable = false)
    @Convert(converter = EstadoCapsulaConverter.class)
    private EstadoCapsula estado;

    private Long hostalId;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Integer getPlanta() { return planta; }
    public void setPlanta(Integer planta) { this.planta = planta; }
    public EstadoCapsula getEstado() { return estado; }
    public void setEstado(EstadoCapsula estado) { this.estado = estado; }
    public Long getHostalId() { return hostalId; }
    public void setHostalId(Long hostalId) { this.hostalId = hostalId; }
}