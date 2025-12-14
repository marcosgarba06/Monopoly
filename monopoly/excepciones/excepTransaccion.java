package monopoly.excepciones;

public class excepTransaccion extends excepcionMonopoly{
    public excepTransaccion(String mensaje) {
        super("Acción de transacción inválida " + mensaje);
    }
}
