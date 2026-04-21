package tourist_cocoon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentIntentResponseDTO {
    private String clientSecret;
    private String publishableKey;
    private long importe;      // en céntimos
    private int noches;
}