package monopoly.Casillas.Acciones;

import partida.Jugador;
import monopoly.Tablero;

public final class Parking extends Accion {

    public Parking(String nombre, int posicion) {
        super(nombre, posicion);
    }

    @Override
    public void ejecutarAccion(Jugador jugador, Tablero tablero) {
        float premio = tablero.recogerParking();

        if (premio > 0) {
            jugador.sumarFortuna(premio);
            jugador.sumarPremios(premio);
            System.out.println("¡Has recogido " + (long)premio + "€ del parking gratuito!");
        } else {
            System.out.println("El parking está vacío. No hay premio.");
        }
    }

    @Override
    public String toString() {
        return "Parking Gratuito: " + nombre + " (Posición: " + posicion + ")";
    }
}

