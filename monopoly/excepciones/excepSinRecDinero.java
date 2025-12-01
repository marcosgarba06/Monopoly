package monopoly.excepciones;

public class excepSinRecDinero extends excepSinRecursos {
    public excepSinRecDinero(String mensaje) {
        super("No tienes suficiente dinero" + mensaje);
    }
}
