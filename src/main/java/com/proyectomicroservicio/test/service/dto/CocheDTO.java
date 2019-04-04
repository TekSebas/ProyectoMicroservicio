package com.proyectomicroservicio.test.service.dto;

import java.time.LocalDate;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Coche entity.
 */
public class CocheDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String marca;

    @NotNull
    @Size(max = 100)
    private String modelo;

    @NotNull
    private LocalDate fechaITV;

    private Long usuarioId;

    private String usuarioNombre;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public LocalDate getFechaITV() {
        return fechaITV;
    }

    public void setFechaITV(LocalDate fechaITV) {
        this.fechaITV = fechaITV;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CocheDTO cocheDTO = (CocheDTO) o;
        if (cocheDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), cocheDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CocheDTO{" +
            "id=" + getId() +
            ", marca='" + getMarca() + "'" +
            ", modelo='" + getModelo() + "'" +
            ", fechaITV='" + getFechaITV() + "'" +
            ", usuario=" + getUsuarioId() +
            ", usuario='" + getUsuarioNombre() + "'" +
            "}";
    }
}
