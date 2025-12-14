package monopoly.excepciones;

public class excepSinRecursos extends excepcionMonopoly {
    public excepSinRecursos(String motivo) {
        super("No tienes recursos suficientes " + motivo);
    }

}
