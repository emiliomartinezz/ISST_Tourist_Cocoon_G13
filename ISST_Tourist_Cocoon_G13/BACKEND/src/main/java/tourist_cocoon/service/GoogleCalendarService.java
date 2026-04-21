package tourist_cocoon.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import tourist_cocoon.model.Reserva;

/**
 * Servicio de integración con Google Calendar (requisito obligatorio del SDD).
 *
 * Configuración necesaria en application.properties:
 *   google.calendar.credentials-path=ruta/a/service-account.json
 *   google.calendar.calendar-id=ID_del_calendario_compartido
 *
 * Para el MVP puede usarse una Service Account de Google Cloud con acceso
 * al calendario del hostal. El fichero JSON de credenciales se descarga
 * desde Google Cloud Console > IAM > Service Accounts.
 *
 * Utiliza horario de Madrid para las fechas de inicio/fin de eventos.
 */
@Service
public class GoogleCalendarService {

    private static final String APP_NAME = "Tourist Cocoon";
    private static final ZoneId ZONE = ZoneId.of("Europe/Madrid");
    private static final Logger logger = LoggerFactory.getLogger(GoogleCalendarService.class);

    @Value("${google.calendar.credentials-path:#{null}}")
    private String credentialsPath;

    @Value("${google.calendar.calendar-id:primary}")
    private String calendarId;

    /**
     * Crea un evento en Google Calendar para la reserva dada.
     * El evento aparece en el calendario del hostal con la info del huésped y la cápsula.
     * 
     * El evento comienza a las 14:00 (check-in) del primer día
     * y finaliza a las 12:00 (check-out) del último día.
     *
     * @param reserva La reserva para la que crear el evento
     * @return ID del evento creado en Google Calendar
     * @throws Exception si hay error al conectar o crear el evento
     */
    public String crearEvento(Reserva reserva) throws Exception {
        if (!isConfigured()) {
            logger.warn("Google Calendar no configurado. Saltando sincronización de evento.");
            return null;
        }

        try {
            Calendar service = buildCalendarService();

            // Construir resumen del evento
            String summary = String.format(
                "Reserva #%d – %s [%s]",
                reserva.getId(),
                reserva.getHuesped().getNombre(),
                reserva.getCapsula().getId()
            );

            // Construir descripción con detalles
            String description = String.format(
                "Huésped: %s\nEmail: %s\nTeléfono: %s\nCápsula: %s (Planta %s)\nEstado: %s",
                reserva.getHuesped().getNombre(),
                reserva.getHuesped().getEmail(),
                reserva.getHuesped().getTelefono() != null ? reserva.getHuesped().getTelefono() : "No disponible",
                reserva.getCapsula().getId(),
                reserva.getCapsula().getPlanta(),
                reserva.getEstado()
            );

            Event evento = new Event()
                .setSummary(summary)
                .setDescription(description);

            // Hora de check-in: 14:00 del primer día
            LocalDateTime horaCheckIn = reserva.getFechaInicio().atTime(14, 0);
            ZonedDateTime zonedCheckIn = horaCheckIn.atZone(ZONE);
            EventDateTime inicio = new EventDateTime()
                .setDateTime(new DateTime(zonedCheckIn.toInstant().toEpochMilli()))
                .setTimeZone(ZONE.getId());

            // Hora de check-out: 12:00 del último día
            LocalDateTime horaCheckOut = reserva.getFechaFinal().atTime(12, 0);
            ZonedDateTime zonedCheckOut = horaCheckOut.atZone(ZONE);
            EventDateTime fin = new EventDateTime()
                .setDateTime(new DateTime(zonedCheckOut.toInstant().toEpochMilli()))
                .setTimeZone(ZONE.getId());

            evento.setStart(inicio).setEnd(fin);

            // Crear el evento
            Event creado = service.events().insert(calendarId, evento).execute();
            logger.info("Evento creado en Google Calendar: {} para la reserva {}", creado.getId(), reserva.getId());
            return creado.getId();

        } catch (Exception e) {
            logger.error("Error al crear evento en Google Calendar para reserva {}: {}", reserva.getId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Actualiza un evento existente en Google Calendar.
     * Útil cuando se modifican fechas de una reserva.
     *
     * @param googleEventId ID del evento en Google Calendar
     * @param reserva Datos actualizados de la reserva
     * @throws Exception si hay error al actualizar el evento
     */
    public void actualizarEvento(String googleEventId, Reserva reserva) throws Exception {
        if (googleEventId == null || googleEventId.isBlank()) {
            logger.warn("No hay googleEventId para actualizar. Creando nuevo evento.");
            crearEvento(reserva);
            return;
        }

        if (!isConfigured()) {
            logger.warn("Google Calendar no configurado. Saltando actualización de evento.");
            return;
        }

        try {
            Calendar service = buildCalendarService();
            Event evento = service.events().get(calendarId, googleEventId).execute();

            // Actualizar resumen y descripción
            evento.setSummary(String.format(
                "Reserva #%d – %s [%s]",
                reserva.getId(),
                reserva.getHuesped().getNombre(),
                reserva.getCapsula().getId()
            ));

            evento.setDescription(String.format(
                "Huésped: %s\nEmail: %s\nTeléfono: %s\nCápsula: %s (Planta %s)\nEstado: %s",
                reserva.getHuesped().getNombre(),
                reserva.getHuesped().getEmail(),
                reserva.getHuesped().getTelefono() != null ? reserva.getHuesped().getTelefono() : "No disponible",
                reserva.getCapsula().getId(),
                reserva.getCapsula().getPlanta(),
                reserva.getEstado()
            ));

            // Actualizar fechas/horas
            LocalDateTime horaCheckIn = reserva.getFechaInicio().atTime(14, 0);
            ZonedDateTime zonedCheckIn = horaCheckIn.atZone(ZONE);
            EventDateTime inicio = new EventDateTime()
                .setDateTime(new DateTime(zonedCheckIn.toInstant().toEpochMilli()))
                .setTimeZone(ZONE.getId());

            LocalDateTime horaCheckOut = reserva.getFechaFinal().atTime(12, 0);
            ZonedDateTime zonedCheckOut = horaCheckOut.atZone(ZONE);
            EventDateTime fin = new EventDateTime()
                .setDateTime(new DateTime(zonedCheckOut.toInstant().toEpochMilli()))
                .setTimeZone(ZONE.getId());

            evento.setStart(inicio).setEnd(fin);

            // Guardar cambios
            service.events().update(calendarId, googleEventId, evento).execute();
            logger.info("Evento {} actualizado en Google Calendar para la reserva {}", googleEventId, reserva.getId());

        } catch (Exception e) {
            logger.error("Error al actualizar evento {} en Google Calendar: {}", googleEventId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Elimina un evento de Google Calendar cuando se cancela una reserva.
     *
     * @param googleEventId ID del evento en Google Calendar
     * @throws Exception si hay error al eliminar
     */
    public void eliminarEvento(String googleEventId) throws Exception {
        if (googleEventId == null || googleEventId.isBlank()) {
            logger.warn("No hay googleEventId para eliminar.");
            return;
        }

        if (!isConfigured()) {
            logger.warn("Google Calendar no configurado. Saltando eliminación de evento.");
            return;
        }

        try {
            buildCalendarService().events().delete(calendarId, googleEventId).execute();
            logger.info("Evento {} eliminado de Google Calendar", googleEventId);
        } catch (Exception e) {
            logger.error("Error al eliminar evento {} de Google Calendar: {}", googleEventId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Verifica si Google Calendar está configurado correctamente.
     */
    public boolean isConfigured() {
        return credentialsPath != null && !credentialsPath.isBlank();
    }

    // ----- infraestructura -----

    private Calendar buildCalendarService() throws Exception {
        if (credentialsPath == null || credentialsPath.isBlank()) {
            throw new IOException(
                "google.calendar.credentials-path no configurado en application.properties");
        }
        GoogleCredentials credentials = GoogleCredentials
            .fromStream(new FileInputStream(credentialsPath))
            .createScoped(List.of("https://www.googleapis.com/auth/calendar"));

        return new Calendar.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            new HttpCredentialsAdapter(credentials))
            .setApplicationName(APP_NAME)
            .build();
    }
}
