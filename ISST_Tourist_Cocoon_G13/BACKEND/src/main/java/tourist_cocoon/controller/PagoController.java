package tourist_cocoon.controller;
 
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import tourist_cocoon.dto.PaymentIntentResponseDTO;
 
@RestController
@RequestMapping("/pagos")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:5173" })
public class PagoController {
 
    // Precio por noche en euros
    private static final int PRECIO_NOCHE_EUR = 25;
 
    @Value("${stripe.secret-key}")
    private String secretKey;
 
    @Value("${stripe.publishable-key}")
    private String publishableKey;
 
    /**
     * Crea un PaymentIntent en Stripe por el importe correspondiente a las noches indicadas.
     * El frontend usa el clientSecret devuelto para confirmar el pago con la tarjeta.
     *
     * @param noches número de noches de la reserva
     * @return clientSecret + publishableKey + importe + noches
     */
    @PostMapping("/crear-intent")
    public ResponseEntity<PaymentIntentResponseDTO> crearPaymentIntent(
            @RequestParam int noches) throws StripeException {
 
        if (noches <= 0 || noches > 30) {
            return ResponseEntity.badRequest().build();
        }
 
        Stripe.apiKey = secretKey;
 
        // Stripe trabaja en céntimos
        long importeCentimos = (long) noches * PRECIO_NOCHE_EUR * 100;
 
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(importeCentimos)
                .setCurrency("eur")
                .addPaymentMethodType("card")
                .putMetadata("noches", String.valueOf(noches))
                .build();
 
        PaymentIntent intent = PaymentIntent.create(params);
 
        PaymentIntentResponseDTO response = new PaymentIntentResponseDTO(
                intent.getClientSecret(),
                publishableKey,
                importeCentimos,
                noches
        );
 
        return ResponseEntity.ok(response);
    }
}