package tourist_cocoon.dto;

public class LoginResponseDTO {
    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private String telefono;

    public LoginResponseDTO(Long id, String nombre, String email, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
    }

    public LoginResponseDTO(Long id, String nombre, String email, String rol, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.telefono = telefono;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getRol() { return rol; }
    public String getTelefono() { return telefono; }
}
