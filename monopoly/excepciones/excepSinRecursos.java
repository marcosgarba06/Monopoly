package monopoly.excepciones;

public class excepSinRecursos extends excepcionMonopoly {
    public excepSinRecursos(String mensaje) {
        super("No tienes recursos suficientes" + mensaje);
    }

}
