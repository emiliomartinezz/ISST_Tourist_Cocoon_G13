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

    @Column(length = 50)
    private String categoria = "STANDARD";

    private Long hostalId;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Integer getPlanta() { return planta; }
    public void setPlanta(Integer planta) { this.planta = planta; }
    public EstadoCapsula getEstado() { return estado; }
    public void setEstado(EstadoCapsula estado) { this.estado = estado; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public Long getHostalId() { return hostalId; }
    public void setHostalId(Long hostalId) { this.hostalId = hostalId; }
}