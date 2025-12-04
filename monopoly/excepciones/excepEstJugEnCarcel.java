package monopoly.excepciones;

public class excepEstJugEnCarcel extends excepEstadoJuego {
    public excepEstJugEnCarcel(String movimiento, String jugador) {
        super("Acción de" + movimiento + " no permitida: el jugador " + jugador + " está en la cárcel");
    }
}
