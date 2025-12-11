package monopoly;

import monopoly.excepciones.excepcionMonopoly;

public class MonopolyETSE {

    /*
     * Centralizamos todo el estado del Monopoly en la clase Juego.
     * Alli se crean todas las instancias y objetos necesarios jugadores,
     * tablero, tratos, etc... En este main solo inicializamos el Juego y
     * llamamos a iniciarPartida(), toda la lógica de la partida en la clase Juego.
     */

    public static void main(String[] args) throws excepcionMonopoly {
        Juego juego = new Juego(); //creamos el objeto juego que gestiona los comandos
        juego.iniciarPartida();// inicamos la partida
    }
}





















