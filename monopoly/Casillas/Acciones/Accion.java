package monopoly.Casillas.Acciones;

import monopoly.Casillas.Casilla;
import partida.Jugador;
import monopoly.Tablero;

public abstract class Accion extends Casilla {

    public Accion(String nombre, int posicion) {
        super(nombre, posicion);
    }

    public abstract void ejecutarAccion(Jugador jugador, Tablero tablero);

    @Override
    public void evaluarCasilla(Jugador jugador, Tablero tablero) {
        incrementarVisita();
        ejecutarAccion(jugador, tablero);
    }
}