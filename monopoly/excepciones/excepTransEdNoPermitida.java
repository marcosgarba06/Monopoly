package monopoly.excepciones;

public class excepTransEdNoPermitida extends excepTransaccion {
    public excepTransEdNoPermitida(String motivo) {
        super("Acción de edificar permitida, porque " + motivo);
    }
}
