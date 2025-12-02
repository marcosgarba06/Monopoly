package monopoly.Edificios;

import partida.Jugador;
import monopoly.Casillas.Propiedades.Solar;

public class Hotel extends Edificacion {

    public Hotel(String id, Jugador propietario, Solar solar, float coste) {
        super(id, propietario, solar, coste);
    }

    @Override
    public String obtenerTipo() {
        return "hotel";
    }

    @Override
    public boolean puedeEdificar(Jugador jugador, Solar solar) {
        if (solar.getNumCasas() != 4) {
            System.out.println("No se puede edificar un hotel. Necesitas exactamente 4 casas y en esta casilla hay " + solar.getNumCasas() + ".");
            return false;
        }

        if (solar.tieneHotel()) {
            System.out.println("No se puede edificar ningún edificio más en esta casilla.");
            return false;
        }

        double coste = solar.getPrecioHotel();
        if (jugador.getFortuna() < coste) {
            System.out.println("La fortuna de " + jugador.getNombre() +
                    " no es suficiente para edificar un hotel en la casilla " +
                    solar.getNombre() + ".");
            return false;
        }

        return true;
    }

    // ✅ Construcción sin necesidad de pasar edificacionesGlobal
    // La eliminación de casas la maneja Juego.java
    public void construir(Jugador jugador, Solar solar) {
        solar.construirHotel(jugador);
        double coste = solar.getPrecioHotel();

        System.out.println("Se ha edificado un hotel en " + solar.getNombre() +
                ". La fortuna de " + jugador.getNombre() +
                " se reduce en " + (long)coste + "€.");
    }
}