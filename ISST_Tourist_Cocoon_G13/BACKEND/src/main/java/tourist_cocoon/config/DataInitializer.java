package tourist_cocoon.config;

import tourist_cocoon.model.Capsula;
import tourist_cocoon.model.Reserva;
import tourist_cocoon.model.Usuario;
import tourist_cocoon.repository.CapsulaRepository;
import tourist_cocoon.repository.ReservaRepository;
import tourist_cocoon.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Carga datos iniciales al arrancar la aplicación si la BD está vacía.
 * Crea cápsulas del hostal en varias plantas y un usuario administrador de demo.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CapsulaRepository capsulaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ReservaRepository reservaRepository;

    @Value("${app.capsulas.planta1:C-101,C-102,C-103,C-104,C-105}")
    private String capsulasPlanta1;

    @Value("${app.capsulas.planta2:C-201,C-202,C-203,C-204,C-205}")
    private String capsulasPlanta2;

    @Value("${app.capsulas.planta3:C-301,C-302,C-303,C-304,C-305}")
    private String capsulasPlanta3;

    @Override
    public void run(String... args) {
        seedCapsulas();
        seedAdminDemo();
    }

    private void seedCapsulas() {
        if (capsulaRepository.count() > 0) return;

        int total = 0;
        total += seedPlanta(1, capsulasPlanta1);
        total += seedPlanta(2, capsulasPlanta2);
        total += seedPlanta(3, capsulasPlanta3);

        System.out.println("[Init] " + total + " cápsulas creadas en distintas plantas.");
    }

    private int seedPlanta(Integer planta, String csvIds) {
        List<String> ids = Arrays.stream(csvIds.split(","))
            .map(String::trim)
            .filter(id -> !id.isBlank())
            .collect(Collectors.toList());

        for (String id : ids) {
            Capsula c = new Capsula();
            c.setId(id);
            c.setPlanta(planta);
            c.setEstado("Disponible");
            c.setHostalId(1L);
            capsulaRepository.save(c);
        }

        return ids.size();
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
        System.out.println("[Init] Usuario admin creado: admin@cocoon.com / admin123 con ID: " + admin.getId());
        // 2. Crear una Reserva de prueba para este Admin
        if (reservaRepository.count() == 0) {
            // Buscamos la primera cápsula que creamos antes
            Capsula capsula = capsulaRepository.findById("C-101").orElse(null);
            
            if (capsula != null) {
                Reserva reservaPrueba = new Reserva();
                reservaPrueba.setHuesped(admin);
                reservaPrueba.setCapsula(capsula);
                reservaPrueba.setEstado("ACTIVA"); // O el estado que uses
                reservaPrueba.setFechaInicio(java.time.LocalDate.now().minusDays(1));
                reservaPrueba.setFechaFinal(java.time.LocalDate.now().plusDays(2));
                
                reservaPrueba = reservaRepository.save(reservaPrueba);
                
                System.out.println("[Init] Reserva de prueba creada - ID Reserva: " + reservaPrueba.getId());
                System.out.println("[TEST] Para probar el checkout usa: PATCH http://localhost:8080/reservas/" + reservaPrueba.getId() + "/checkout");
            }
        }
    }
}
