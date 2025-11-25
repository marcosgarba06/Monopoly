package monopoly.Edificios;

import partida.Jugador;
import monopoly.Casilla;

public class Piscina extends Edificacion {
    public Piscina(String id, Jugador propietario, Casilla casilla, float coste){
        super(id, propietario, casilla, coste);
    }

    @Override
    public String obtenerTipo() {
        return "piscina";
    }

    @Override
    public boolean puedeEdificar(Jugador jugador, Casilla casilla) {
        //En un solar se puede construir una única piscina si se ha construido un hotel
        if (!casilla.tieneHotel()) {
            System.out.println("No se puede edificar una piscina, ya que no se dispone de un hotel la casilla.");
            return false;
        }

        // Verificar que no hay piscina ya, solo se puede cosntruir una unica.
        if (casilla.tienePiscina()) {
            System.out.println("No se puede edificar ningún edificio más en esta casilla ni en el grupo al que la casilla pertenece.");
            return false;
        }

        float coste = casilla.getPrecioPiscina();
        if (jugador.getFortuna() < coste) {
            System.out.println("La fortuna de " + jugador.getNombre() +
                    " no es suficiente para edificar una piscina en la casilla " +
                    casilla.getNombre() + ".");
            return false;
        }

        // Construir en esta funcion, manejamos la fortuna del jugador
        casilla.construirPiscina(jugador);

        System.out.println("Se ha edificado una piscina en " + casilla.getNombre() +
                ". La fortuna de " + jugador.getNombre() +
                " se reduce en " + (long)coste + "€.");
        return true;
    }

    public void construir(Jugador jugador, Casilla casilla) {
        casilla.construirPiscina(jugador);
        float coste = casilla.getPrecioPiscina();
        System.out.println("Se ha edificado una piscina en " + casilla.getNombre() +
                ". La fortuna de " + jugador.getNombre() +
                " se reduce en " + (long)coste + "€.");
    }
}
