package tourist_cocoon.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.param.RefundCreateParams;

/**
 * Servicio para gestionar operaciones con Stripe.
 * Actualmente soporta reembolsos (refunds) de pagos.
 */
@Service
public class StripeService {

    @Value("${stripe.secret-key}")
    private String secretKey;

    /**
     * Realiza un reembolso completo de un PaymentIntent.
     * 
     * @param paymentIntentId ID del PaymentIntent a reembolsar
     * @return El objeto Refund creado en Stripe
     * @throws StripeException si hay error al procesar el reembolso
     */
    public Refund reembolsarPago(String paymentIntentId) throws StripeException {
        if (paymentIntentId == null || paymentIntentId.isBlank()) {
            throw new IllegalArgumentException("El ID del PaymentIntent no puede estar vacío");
        }

        Stripe.apiKey = secretKey;

        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId)
                .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER)
                .build();

        Refund refund = Refund.create(params);
        
        System.out.println("[INFO] Reembolso creado exitosamente. ID: " + refund.getId() + 
                           " | PaymentIntent: " + paymentIntentId + 
                           " | Estado: " + refund.getStatus());
        
        return refund;
    }

    /**
     * Realiza un reembolso parcial de un PaymentIntent.
     * 
     * @param paymentIntentId ID del PaymentIntent a reembolsar parcialmente
     * @param amountCents Cantidad a reembolsar en céntimos
     * @return El objeto Refund creado en Stripe
     * @throws StripeException si hay error al procesar el reembolso
     */
    public Refund reembolsarPagoParcial(String paymentIntentId, Long amountCents) throws StripeException {
        if (paymentIntentId == null || paymentIntentId.isBlank()) {
            throw new IllegalArgumentException("El ID del PaymentIntent no puede estar vacío");
        }

        if (amountCents <= 0) {
            throw new IllegalArgumentException("El monto a reembolsar debe ser positivo");
        }

        Stripe.apiKey = secretKey;

        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId)
                .setAmount(amountCents)
                .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER)
                .build();

        Refund refund = Refund.create(params);
        
        System.out.println("[INFO] Reembolso parcial creado exitosamente. ID: " + refund.getId() + 
                           " | PaymentIntent: " + paymentIntentId + 
                           " | Monto: " + amountCents + " céntimos" +
                           " | Estado: " + refund.getStatus());
        
        return refund;
    }
}
