package monopoly.Edificios;
import monopoly.Casillas.Casilla;
import monopoly.Casillas.Propiedades.Solar;

import partida.Jugador;

public class Casa extends Edificacion {
    public Casa(String id, Jugador propietario, Solar solar, float coste){
        super(id, propietario, solar, coste);
    }


    @Override
    public String obtenerTipo() {
        return "casa";
    }

    @Override
    public boolean puedeEdificar(Jugador jugador, Solar solar) {
        // En un solar se puede construir un único hotel si en ese solar ya se ha construido,
        // no se puede edificar mas casas
        if (solar.tieneHotel()) {
            System.out.println("No se puede edificar casas en esta casilla porque hay un hotel construido en ella.");
            return false;
        }

        // No se pueden edificar mas de cuatro casas
        if (solar.getNumCasas() >= 4) {
            System.out.println("No se puede edificar ninguna casa más porque ya se han edificado cuatro casas en esta casilla.");
            return false;
        }

        double coste = solar.getPrecioCasa();
        if (jugador.getFortuna() < coste) {
            System.out.println("La fortuna de " + jugador.getNombre() +
                    " no es suficiente para edificar una casa en la casilla " +
                    solar.getNombre() + ".");
            return false;
        }

        return true;
    }


    // Método específico para construir la casa
    public void construir(Jugador jugador, Solar solar) {
        solar.construirCasas(jugador, 1);
        double coste = solar.getPrecioCasa();
        System.out.println("Se ha edificado una casa en " + solar.getNombre() +
                ". La fortuna de " + jugador.getNombre() +
                " se reduce en " + (long)coste + "€.");
    }

}
