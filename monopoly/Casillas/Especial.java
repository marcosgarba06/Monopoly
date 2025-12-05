package monopoly.Casillas;


import monopoly.Juego;
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
                Juego.consola.imprimir("Estás en la casilla de Salida.");
                break;

            case "carcel":
                if (jugador.isEnCarcel()) {
                    Juego.consola.imprimir("Estás en la cárcel. Tiradas fallidas: " +
                            jugador.getTiradasCarcel());
                } else {
                    Juego.consola.imprimir("Estás de visita en la cárcel.");
                }
                break;

            case "ircarcel":
                Juego.consola.imprimir("¡Vas a la cárcel!");
                jugador.irACarcel(tablero);
                break;

            default:
                Juego.consola.imprimir("Casilla especial: " + nombre);
        }
    }

    public String getTipoEspecial() {
        return tipoEspecial;
    }


}

