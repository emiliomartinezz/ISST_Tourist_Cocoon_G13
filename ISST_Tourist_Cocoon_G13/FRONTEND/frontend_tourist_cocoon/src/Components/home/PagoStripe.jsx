import { useState, useEffect } from "react";
import { loadStripe } from "@stripe/stripe-js";
import {
  Elements,
  CardElement,
  useStripe,
  useElements,
} from "@stripe/react-stripe-js";
import { apiCrearPaymentIntent } from "../../services/apiService";

// ── Formulario interno (necesita estar dentro de <Elements>) ──────────────────
function FormularioPago({ noches, precioTotal, clientSecret, onPagoExitoso, onCancelar }) {
  const stripe = useStripe();
  const elements = useElements();
  const [procesando, setProcesando] = useState(false);
  const [error, setError] = useState(null);

  const handlePagar = async (e) => {
    e.preventDefault();
    if (!stripe || !elements) return;

    setProcesando(true);
    setError(null);

    const card = elements.getElement(CardElement);

    const { error: stripeError, paymentIntent } = await stripe.confirmCardPayment(
      clientSecret,
      { payment_method: { card } }
    );

    if (stripeError) {
      setError(stripeError.message);
      setProcesando(false);
    } else if (paymentIntent.status === "succeeded") {
      onPagoExitoso(paymentIntent.id);
    }
  };

  const cardStyle = {
    style: {
      base: {
        fontSize: "16px",
        color: "#32325d",
        fontFamily: "sans-serif",
        "::placeholder": { color: "#aab7c4" },
      },
      invalid: { color: "#e24b4a" },
    },
  };

  return (
    <div style={{
      padding: "1.5rem",
      border: "0.5px solid var(--color-border-secondary)",
      borderRadius: "12px",
      marginTop: "1rem",
      background: "var(--color-background-secondary)",
    }}>
      <h3 style={{ fontSize: "16px", fontWeight: 500, marginBottom: "0.25rem" }}>
        Pago seguro con Stripe
      </h3>
      <p style={{ fontSize: "13px", color: "var(--color-text-secondary)", marginBottom: "1rem" }}>
        {noches} noche{noches > 1 ? "s" : ""} × 25 € = <strong>{precioTotal} €</strong>
      </p>

      <form onSubmit={handlePagar}>
        <div style={{
          padding: "12px",
          border: "0.5px solid var(--color-border-secondary)",
          borderRadius: "8px",
          marginBottom: "1rem",
          background: "var(--color-background-primary)",
        }}>
          <CardElement options={cardStyle} />
        </div>

        {error && (
          <p style={{
            color: "var(--color-text-danger)",
            fontSize: "13px",
            marginBottom: "0.75rem",
          }}>
            {error}
          </p>
        )}

        <div style={{ display: "flex", gap: "8px" }}>
          <button
            type="submit"
            disabled={procesando || !stripe}
            style={{
              flex: 1,
              padding: "10px",
              background: procesando ? "#9b97e0" : "#635bff",
              color: "white",
              border: "none",
              borderRadius: "8px",
              cursor: procesando ? "not-allowed" : "pointer",
              fontWeight: 500,
              fontSize: "15px",
              transition: "background 0.2s",
            }}
          >
            {procesando ? "Procesando..." : `Pagar ${precioTotal} €`}
          </button>

          <button
            type="button"
            onClick={onCancelar}
            disabled={procesando}
            style={{
              padding: "10px 16px",
              borderRadius: "8px",
              border: "0.5px solid var(--color-border-secondary)",
              background: "transparent",
              cursor: procesando ? "not-allowed" : "pointer",
              fontSize: "14px",
            }}
          >
            Cancelar
          </button>
        </div>

        <p style={{
          fontSize: "11px",
          color: "var(--color-text-secondary)",
          marginTop: "0.75rem",
          textAlign: "center",
        }}>
          Tarjeta de prueba: <strong>4242 4242 4242 4242</strong> · fecha futura · cualquier CVC
        </p>
      </form>
    </div>
  );
}

// ── Componente principal (carga Stripe y el PaymentIntent) ────────────────────
export default function PagoStripe({ noches, onPagoExitoso, onCancelar }) {
  const [stripePromise, setStripePromise] = useState(null);
  const [clientSecret, setClientSecret] = useState(null);
  const [cargando, setCargando] = useState(true);
  const [errorCarga, setErrorCarga] = useState(null);

  const precioTotal = noches * 25;

  useEffect(() => {
    const iniciarPago = async () => {
      try {
        setCargando(true);
        setErrorCarga(null);
        const { clientSecret, publishableKey } = await apiCrearPaymentIntent(noches);
        setStripePromise(loadStripe(publishableKey));
        setClientSecret(clientSecret);
      } catch (err) {
        setErrorCarga(err.message || "No se pudo iniciar el pago. Inténtalo de nuevo.");
      } finally {
        setCargando(false);
      }
    };

    iniciarPago();
  }, [noches]);

  if (cargando) {
    return (
      <div style={{
        padding: "1.5rem",
        border: "0.5px solid var(--color-border-tertiary)",
        borderRadius: "12px",
        marginTop: "1rem",
        textAlign: "center",
        color: "var(--color-text-secondary)",
        fontSize: "14px",
      }}>
        Preparando pasarela de pago...
      </div>
    );
  }

  if (errorCarga) {
    return (
      <div style={{
        padding: "1.5rem",
        border: "0.5px solid var(--color-border-danger)",
        borderRadius: "12px",
        marginTop: "1rem",
      }}>
        <p style={{ color: "var(--color-text-danger)", fontSize: "14px", marginBottom: "0.75rem" }}>
          {errorCarga}
        </p>
        <button
          type="button"
          onClick={onCancelar}
          style={{
            padding: "8px 16px",
            borderRadius: "8px",
            border: "0.5px solid var(--color-border-secondary)",
            cursor: "pointer",
            fontSize: "14px",
          }}
        >
          Volver
        </button>
      </div>
    );
  }

  return (
    <Elements stripe={stripePromise} options={{ clientSecret }}>
      <FormularioPago
        noches={noches}
        precioTotal={precioTotal}
        clientSecret={clientSecret}
        onPagoExitoso={onPagoExitoso}
        onCancelar={onCancelar}
      />
    </Elements>
  );
}