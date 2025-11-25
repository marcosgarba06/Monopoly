package monopoly.Edificios;

import partida.Jugador;
import monopoly.Casilla;

public class Pista extends Edificacion{
    public Pista(String id, Jugador propietario, Casilla casilla, float coste){
        super(id, propietario, casilla, coste);
    }

    @Override
    public String obtenerTipo() {
        return "pista";
    }

    @Override
    public boolean puedeEdificar(Jugador jugador, Casilla casilla) {
        //En un solar se puede construir una única pista de deporte si se ha construido un hotel y una piscina.
        if (!casilla.tieneHotel() || !casilla.tienePiscina()) {
            System.out.println("No se puede edificar una pista de deporte, ya que no se dispone de un hotel y una piscina.");
            return false;
        }

        // Verificar que no hay pista ya, solo puede tener una unica pista
        if (casilla.tienePista()) {
            System.out.println("No se puede edificar ningún edificio más en esta casilla ni en el grupo al que la casilla pertenece.");
            return false;
        }

        float coste = casilla.getPrecioPista();
        if (jugador.getFortuna() < coste) {
            System.out.println("La fortuna de " + jugador.getNombre() +
                    " no es suficiente para edificar una pista de deporte en la casilla " +
                    casilla.getNombre() + ".");
            return false;
        }

        // Construir en esta funcion, manejamos la fortuna del jugador
        casilla.construirPista(jugador);

        System.out.println("Se ha edificado una pista de deporte en " + casilla.getNombre() +
                ". La fortuna de " + jugador.getNombre() +
                " se reduce en " + (long)coste + "€.");
        return true;
    }
    public void construir(Jugador jugador, Casilla casilla) {
        casilla.construirPista(jugador);
        float coste = casilla.getPrecioPista();
        System.out.println("Se ha edificado una pista de deporte en " + casilla.getNombre() +
                ". La fortuna de " + jugador.getNombre() +
                " se reduce en " + (long)coste + "€.");
    }
}
