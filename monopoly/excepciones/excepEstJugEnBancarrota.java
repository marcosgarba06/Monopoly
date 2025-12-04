package monopoly.excepciones;

public class excepEstJugEnBancarrota extends excepEstadoJuego {
    public excepEstJugEnBancarrota(String jugador) {
        super("Acción no permitida: el jugador " + jugador + " está en bancarrota.");
    }
}
