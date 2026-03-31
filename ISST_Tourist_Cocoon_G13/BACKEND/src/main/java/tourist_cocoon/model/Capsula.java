package tourist_cocoon.model;

import jakarta.persistence.*;

@Entity
@Table(name = "capsulas")
public class Capsula {
    @Id
    private String id;

    @Column(nullable = false)
    private Integer planta;

    @Column(nullable = false)
    private String estado;

    private Long hostalId;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Integer getPlanta() { return planta; }
    public void setPlanta(Integer planta) { this.planta = planta; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Long getHostalId() { return hostalId; }
    public void setHostalId(Long hostalId) { this.hostalId = hostalId; }
}