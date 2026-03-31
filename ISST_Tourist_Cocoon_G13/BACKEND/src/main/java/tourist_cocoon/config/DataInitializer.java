package tourist_cocoon.config;

import tourist_cocoon.model.Capsula;
import tourist_cocoon.model.Usuario;
import tourist_cocoon.repository.CapsulaRepository;
import tourist_cocoon.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Carga datos iniciales al arrancar la aplicación si la BD está vacía.
 * Crea las 10 cápsulas del hostal y un usuario administrador de demo.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CapsulaRepository capsulaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedCapsulas();
        seedAdminDemo();
    }

    private void seedCapsulas() {
        if (capsulaRepository.count() > 0) return;

        // Planta 1
        for (int i = 1; i <= 5; i++) {
            Capsula c = new Capsula();
            c.setId(String.format("C-10%d", i));
            c.setPlanta(1);
            c.setEstado("Disponible");
            capsulaRepository.save(c);
        }
        // Planta 2
        for (int i = 1; i <= 5; i++) {
            Capsula c = new Capsula();
            c.setId(String.format("C-20%d", i));
            c.setPlanta(2);
            c.setEstado("Disponible");
            capsulaRepository.save(c);
        }
        System.out.println("[Init] 10 cápsulas creadas.");
    }

    private void seedAdminDemo() {
        if (usuarioRepository.findByEmail("admin@cocoon.com").isPresent()) return;

        Usuario admin = new Usuario();
        admin.setNif("00000000A");
        admin.setNombre("Administrador Demo");
        admin.setEmail("admin@cocoon.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setTelefono("600000000");
        admin.setRol("ADMIN");
        usuarioRepository.save(admin);
        System.out.println("[Init] Usuario admin creado: admin@cocoon.com / admin123");
    }
}
