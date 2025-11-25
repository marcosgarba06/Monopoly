package monopoly.Edificios;

import partida.Jugador;
import monopoly.Casilla;

public class Casa extends Edificacion {
    public Casa(String id, Jugador propietario, Casilla casilla, float coste){
        super(id, propietario, casilla, coste);
    }


    @Override
    public String obtenerTipo() {
        return "casa";
    }

    @Override
    public boolean puedeEdificar(Jugador jugador, Casilla casilla) {
        // En un solar se puede construir un único hotel si en ese solar ya se ha construido,
        // no se puede edificar mas casas
        if (casilla.tieneHotel()) {
            System.out.println("No se puede edificar casas en esta casilla porque hay un hotel construido en ella.");
            return false;
        }

        // No se pueden edificar mas de cuatro casas
        if (casilla.getNumCasas() >= 4) {
            System.out.println("No se puede edificar ninguna casa más porque ya se han edificado cuatro casas en esta casilla.");
            return false;
        }

        float coste = casilla.getPrecioCasa();
        if (jugador.getFortuna() < coste) {
            System.out.println("La fortuna de " + jugador.getNombre() +
                    " no es suficiente para edificar una casa en la casilla " +
                    casilla.getNombre() + ".");
            return false;
        }

        return true;
    }

    // Método específico para construir la casa
    public void construir(Jugador jugador, Casilla casilla) {
        casilla.construirCasas(jugador, 1);
        float coste = casilla.getPrecioCasa();
        System.out.println("Se ha edificado una casa en " + casilla.getNombre() +
                ". La fortuna de " + jugador.getNombre() +
                " se reduce en " + (long)coste + "€.");
    }



}
