package monopoly.Casillas.Acciones;
import partida.Jugador;
import monopoly.Tablero;
import monopoly.Cartas.Carta;


public final class CajaComunidad extends Accion {

    public CajaComunidad(String nombre, int posicion) {
        super(nombre, posicion);
    }

    @Override
    public void ejecutarAccion(Jugador jugador, Tablero tablero) {
        System.out.println("Has caído en Caja de Comunidad. Robas una carta...");
        Carta carta = Carta.seleccionarCarta("caja");
        carta.aplicarAccion(jugador, tablero);
    }

    @Override
    public String toString() {
        return "Casilla de Caja de Comunidad: " + nombre + " (Posición: " + posicion + ")";
    }
}

