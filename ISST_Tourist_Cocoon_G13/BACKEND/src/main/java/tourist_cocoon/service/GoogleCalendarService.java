package tourist_cocoon.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import tourist_cocoon.model.Reserva;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.util.List;

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
 */
@Service
public class GoogleCalendarService {

    private static final String APP_NAME = "Tourist Cocoon";
    private static final ZoneId ZONE = ZoneId.of("Europe/Madrid");

    @Value("${google.calendar.credentials-path:#{null}}")
    private String credentialsPath;

    @Value("${google.calendar.calendar-id:primary}")
    private String calendarId;

    /**
     * Crea un evento en Google Calendar para la reserva dada.
     * El evento aparece en el calendario del hostal con la info del huésped y la cápsula.
     */
    public String crearEvento(Reserva reserva) throws Exception {
        Calendar service = buildCalendarService();

        Event evento = new Event()
            .setSummary("Reserva " + reserva.getId()
                + " – " + reserva.getHuesped().getNombre()
                + " [" + reserva.getCapsula().getId() + "]")
            .setDescription(
                "Huésped: " + reserva.getHuesped().getNombre()
                + "\nEmail: " + reserva.getHuesped().getEmail()
                + "\nCápsula: " + reserva.getCapsula().getId()
                + " (planta " + reserva.getCapsula().getPlanta() + ")"
                + "\nEstado reserva: " + reserva.getEstado()
            );

        EventDateTime inicio = new EventDateTime()
            .setDate(new com.google.api.client.util.DateTime(
                reserva.getFechaInicio().toString()));
        EventDateTime fin = new EventDateTime()
            .setDate(new com.google.api.client.util.DateTime(
                reserva.getFechaFinal().toString()));

        evento.setStart(inicio).setEnd(fin);

        Event creado = service.events().insert(calendarId, evento).execute();
        return creado.getId();
    }

    /**
     * Elimina el evento de Google Calendar cuando se cancela una reserva.
     */
    public void eliminarEvento(String googleEventId) throws Exception {
        buildCalendarService().events().delete(calendarId, googleEventId).execute();
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
