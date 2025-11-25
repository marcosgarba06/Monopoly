package monopoly.Edificios;

import partida.Jugador;
import monopoly.Casilla;

import java.util.ArrayList;
import java.util.List;

public class Hotel extends Edificacion{
    public Hotel(String id, Jugador propietario, Casilla casilla, float coste){
        super(id, propietario, casilla, coste);
    }

    @Override
    public String obtenerTipo() {
        return "hotel";
    }

    @Override
    public boolean puedeEdificar(Jugador jugador, Casilla casilla) {
        if (casilla.getNumCasas() != 4) {
            System.out.println("No se puede edificar un hotel. Necesitas exactamente 4 casas y en esta casilla hay " + casilla.getNumCasas() + ".");
            return false;
        }

        if (casilla.tieneHotel()) {
            System.out.println("No se puede edificar ningún edificio más en esta casilla.");
            return false;
        }

        float coste = casilla.getPrecioHotel();
        if (jugador.getFortuna() < coste) {
            System.out.println("La fortuna de " + jugador.getNombre() +
                    " no es suficiente para edificar un hotel en la casilla " +
                    casilla.getNombre() + ".");
            return false;
        }

        return true;
    }

    public void construir(Jugador jugador, Casilla casilla, List<Edificacion> edificacionesGlobal) {
        casilla.construirHotel(jugador);

        // Eliminar las 4 casas para sustituirlas por el hotel
        List<Edificacion> casasAEliminar = new ArrayList<>();
        for (Edificacion e : edificacionesGlobal) {
            if (e.getCasilla().equals(casilla) && e instanceof Casa) {
                casasAEliminar.add(e);
            }
        }
        edificacionesGlobal.removeAll(casasAEliminar);
        jugador.getEdificaciones().removeAll(casasAEliminar);

        float coste = casilla.getPrecioHotel();
        System.out.println("Se ha edificado un hotel en " + casilla.getNombre() +
                ". La fortuna de " + jugador.getNombre() +
                " se reduce en " + (long)coste + "€.");
    }
}
