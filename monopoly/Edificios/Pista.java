package monopoly.Edificios;
import monopoly.Casillas.Casilla;
import monopoly.Casillas.Propiedades.Solar;
import partida.Jugador;

public class Pista extends Edificacion {

    public Pista(String id, Jugador propietario, Solar solar, float coste) {
        super(id, propietario, solar, coste);
    }

    @Override
    public String obtenerTipo() {
        return "pista";
    }

    @Override
    public boolean puedeEdificar(Jugador jugador, Solar solar) {
        // En un solar se puede construir una única pista de deporte
        // si se ha construido un hotel y una piscina.
        if (!solar.tieneHotel() || !solar.tienePiscina()) {
            System.out.println("No se puede edificar una pista de deporte, ya que no se dispone de un hotel y una piscina.");
            return false;
        }

        // Verificar que no hay pista ya, solo puede tener una única pista
        if (solar.tienePista()) {
            System.out.println("No se puede edificar ningún edificio más en esta casilla ni en el grupo al que la casilla pertenece.");
            return false;
        }

        double coste = solar.getPrecioPista();
        if (jugador.getFortuna() < coste) {
            System.out.println("La fortuna de " + jugador.getNombre() +
                    " no es suficiente para edificar una pista de deporte en la casilla " +
                    solar.getNombre() + ".");
            return false;
        }

        return true;  // SOLO VALIDA, NO CONSTRUYE
    }


    // Método separado para construir la pista
    public void construir(Jugador jugador, Solar solar) {
        solar.construirPista(jugador);
        double coste = solar.getPrecioPista();
        System.out.println("Se ha edificado una pista de deporte en " + solar.getNombre() +
                ". La fortuna de " + jugador.getNombre() +
                " se reduce en " + (long)coste + "€.");
    }
}