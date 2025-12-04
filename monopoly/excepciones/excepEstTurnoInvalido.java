package monopoly.excepciones;

public class excepEstTurnoInvalido extends excepEstadoJuego {
    public excepEstTurnoInvalido(String accion) {
        super(accion + " no permitida ahora. Turno inválido.");
    }
}
