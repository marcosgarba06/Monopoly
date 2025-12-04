package monopoly.excepciones;

public class excepTransPropNoPermitida extends excepTransaccion {
    public excepTransPropNoPermitida(String motivo) {
        super("Acción de transacción de propiedad no permitida porque " + motivo);
    }
}
