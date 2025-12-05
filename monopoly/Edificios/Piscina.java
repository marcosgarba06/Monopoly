package monopoly.Edificios;

import monopoly.Juego;
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
            Juego.consola.imprimir("Necesitas un hotel para construir una piscina.");
            return false;
        }
        if (solar.tienePiscina()) {
            Juego.consola.imprimir("Ya hay una piscina en este solar.");
            return false;
        }
        double coste = solar.getPrecioPiscina();
        if (jugador.getFortuna() < coste) {
            Juego.consola.imprimir("No tienes suficiente dinero.");
            return false;
        }
        return true;
    }

    public void construir(Jugador jugador, Solar solar) {
        solar.construirPiscina(jugador);
        double coste = solar.getPrecioPiscina();
        Juego.consola.imprimir("Se ha edificado una piscina en " + solar.getNombre() +
                ". La fortuna de " + jugador.getNombre() +
                " se reduce en " + (long)coste + "€.");
    }
}
