package com.touristcocoon.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "capsulas")
@Data
public class Capsula {
    @Id
    private String id; // Formato "C-XXX" según el SDD [3]

    @Column(nullable = false)
    private Integer planta;

    @Column(nullable = false)
    private String estado; // Ejemplo: "Disponible", "Ocupada", "Sucia" [3, 4]

    // Relación con el Hostal (opcional para el MVP inicial)
    private Long hostalId;
}