package monopoly.Casillas;


import monopoly.Tablero;
import partida.Jugador;

public class Especial extends Casilla {

    private String tipoEspecial; // "salida", "carcel", "ircarcel"

    public Especial(String nombre, int posicion, String tipoEspecial) {
        super(nombre, posicion);
        this.tipoEspecial = tipoEspecial.toLowerCase();
    }

    @Override
    public void evaluarCasilla(Jugador jugador, Tablero tablero) {
        incrementarVisita();

        switch (tipoEspecial) {
            case "salida":
                System.out.println("Estás en la casilla de Salida.");
                break;

            case "carcel":
                if (jugador.isEnCarcel()) {
                    System.out.println("Estás en la cárcel. Tiradas fallidas: " +
                            jugador.getTiradasCarcel());
                } else {
                    System.out.println("Estás de visita en la cárcel.");
                }
                break;

            case "ircarcel":
                System.out.println("¡Vas a la cárcel!");
                jugador.irACarcel(tablero);
                break;

            default:
                System.out.println("Casilla especial: " + nombre);
        }
    }

    public String getTipoEspecial() {
        return tipoEspecial;
    }


}

