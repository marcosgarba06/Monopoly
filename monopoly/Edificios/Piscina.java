package monopoly.Edificios;

import partida.Jugador;
import monopoly.Casillas.Casilla;
import monopoly.Casillas.Propiedades.Solar;

public class Piscina extends Edificacion {
    public Piscina(String id, Jugador propietario, Solar solar, float coste){
        super(id, propietario, solar, coste);
    }

    @Override
    public String obtenerTipo() {
        return "piscina";
    }


    @Override
    public boolean puedeEdificar(Jugador jugador, Solar solar) {
        if (!solar.tieneHotel()) {
            System.out.println("Necesitas un hotel para construir una piscina.");
            return false;
        }
        if (solar.tienePiscina()) {
            System.out.println("Ya hay una piscina en este solar.");
            return false;
        }
        double coste = solar.getPrecioPiscina();
        if (jugador.getFortuna() < coste) {
            System.out.println("No tienes suficiente dinero.");
            return false;
        }
        return true;
    }

    public void construir(Jugador jugador, Solar solar) {
        solar.construirPiscina(jugador);
        double coste = solar.getPrecioPiscina();
        System.out.println("Se ha edificado una piscina en " + solar.getNombre() +
                ". La fortuna de " + jugador.getNombre() +
                " se reduce en " + (long)coste + "€.");
    }
}
