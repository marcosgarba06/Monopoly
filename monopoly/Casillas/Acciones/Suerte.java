package monopoly.Casillas.Acciones;

import monopoly.Juego;
import partida.Jugador;
import monopoly.Tablero;
import monopoly.Cartas.Carta;

/**
 * Clase final para casillas de Suerte
 */
public final class Suerte extends Accion {

    public Suerte(String nombre, int posicion) {
        super(nombre, posicion);
    }

    @Override
    public void ejecutarAccion(Jugador jugador, Tablero tablero) {
        Juego.consola.imprimir("Has caído en Suerte. Robas una carta...");
        Carta carta = Carta.seleccionarCarta("suerte");
        carta.aplicarAccion(jugador, tablero);
    }

    @Override
    public String toString() {
        return "Casilla de Suerte: " + nombre + " (Posición: " + posicion + ")";
    }
}

