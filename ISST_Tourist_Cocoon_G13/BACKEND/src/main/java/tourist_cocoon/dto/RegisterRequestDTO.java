package tourist_cocoon.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El NIF/DNI es obligatorio")
    @JsonAlias("dni")
    private String nif;

    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    private String telefono;

    @Email(message = "El correo electrónico no es válido")
    @NotBlank(message = "El correo electrónico es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotNull(message = "Debes indicar la aceptación de la política de privacidad")
    @AssertTrue(message = "Debes aceptar la política de privacidad")
    @JsonAlias({ "acceptedPrivacyPolicy", "privacyAccepted" })
    private Boolean aceptaPoliticaPrivacidad;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAceptaPoliticaPrivacidad() {
        return aceptaPoliticaPrivacidad;
    }

    public void setAceptaPoliticaPrivacidad(Boolean aceptaPoliticaPrivacidad) {
        this.aceptaPoliticaPrivacidad = aceptaPoliticaPrivacidad;
    }
}