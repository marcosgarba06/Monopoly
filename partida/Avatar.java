
package partida;


import monopoly.*;

import java.util.*;


public class Avatar {

    //Atributos de la clase Avatar
    private String id; //Identificador: una letra generada aleatoriamente.
    private String tipo; //Sombrero, Esfinge, Pelota, Coche
    private Jugador jugador; //Un jugador al que pertenece ese avatar.
    private Casilla casilla; //Los avatares se sitúan en casillas del tablero.
    private int posicion;// se guarda en que asilla esta el avatar en el tablero, del 0 añl 39
    private boolean enCarcel;//indica si true encarcelado o false libre
    private int turnosEnCarcel = 0;

    //Constructor vacío, esto permite crear un avatar sin inicializarlo
    public Avatar() {

    }

    /*Constructor principal. Requiere éstos parámetros:
     * Tipo del avatar, jugador al que pertenece, lugar en el que estará ubicado, y un arraylist con los
     * avatares creados (usado para crear un ID distinto del de los demás avatares).
     */
    public Avatar(String tipo, Jugador jugador, Casilla lugar, ArrayList<Avatar> avCreados){

        this.tipo = tipo;
        this.jugador = jugador;
        this.casilla = lugar;

        generarId(avCreados); //generamos un id
        avCreados.add(this); //añadimos el id nuevo para el cual lo creamos

    }

    //A continuación, tenemos otros métodos útiles para el desarrollo del juego.
    /*Método que permite mover a un avatar a una casilla concreta. Parámetros:
     * - Un array con las casillas del tablero. Se trata de un arrayList de arrayList de casillas (uno por lado).
     * - Un entero que indica el numero de casillas a moverse (será el valor sacado en la tirada de los dados).
     * EN ESTA VERSIÓN SUPONEMOS QUE valorTirada siemrpe es positivo.
     */

    public void moverAvatar(int pasos, Tablero tablero) {
        int posicionActual = this.posicion;
        int nuevaPos = (posicionActual + pasos) % 40; //calcular la nueva posición del avatar en el tablero, asegurándose de que no se salga del rango de casillas.

        // Si la nueva posición es menor que la actual, significa que pasó por la salida
        if (nuevaPos < posicionActual) {
            jugador.sumarFortuna(2000000);
            System.out.println("Has pasado por la casilla de salida. Recibes 2.000.000€.");
        }

        this.posicion = nuevaPos;

        Casilla nuevaCasilla = tablero.getCasilla(nuevaPos);
        setCasilla(nuevaCasilla); // ← unifica y mantiene listas de avatares

        System.out.println("El avatar " + id + " ha avanzado hasta la casilla " + nuevaCasilla.getNombre());
        nuevaCasilla.evaluarCasilla(jugador);
    }




    /*Método que permite generar un ID para un avatar. Sólo lo usamos en esta clase (por ello es privado).
     * El ID generado será una letra mayúscula. Parámetros:
     * - Un arraylist de los avatares ya creados, con el objetivo de evitar que se generen dos ID iguales.
     */
    private void generarId(ArrayList<Avatar> avCreados) {

        Random numero = new Random();//genera numeros aleatorios
        String ID; //variable donde guardaremos tremporalmente la letra candidata
        boolean repetido = true; //bandera, asi sabemos si la letra que escogimo esta libre o no

        //Algoritmo:
        //Genera una letra aleatoria
        //Si existe, se vuelve a intentar con otra, si no se la asigma con this.id y termina

        while (repetido) {
            repetido = false;//empezamos asumiendo que la nueva letra no esta repeida
            ID = String.valueOf((char) (numero.nextInt(26) + 'A'));// genera un numero entre el 0 y el 25 y lo 'A' lo desplaza al rango de códigos de las letras A..Z.
            //ID es una letra mayuscula aleatoria
            // a variable que representa un objeto tipo avatar, en cada dor toma el valor del array avCrados
            for (Avatar a : avCreados) { //recorremos todos los avatares creados
                if (a != null && a.getId().equals(ID)) {
                    repetido = true; //si encunetra uno igual mara que esta repetido y sale del bucle
                    break;
                }
            }
            if (!repetido) { //si no esta repetido, se asigna a ese id y se sale del bucle
                this.id = ID;

            }
        }
    }

    public String toString() {
        String pos = (casilla != null) ? casilla.getNombre() : "sin posición";
        return tipo + " (ID: " + id + ", en " + pos + ")";
    }

    /// SETERS Y GETTERS, acceden y modifican atributos provados

    public void setPosicion(int i) {//actualiza la posicion del avatar en el tablero

        this.posicion = i;
    }

    public void setEnCarcel(boolean b) {

        this.enCarcel = b;
    }

    public int getPosicion() {

        return posicion;
    }

    public String getId() {

        return id;
    }

    public Jugador getJugador() {

        return jugador;
    }

    public boolean isEnCarcel() {

        return enCarcel;

    }

    public void setCasilla(Casilla c) {
        if (this.casilla != null) this.casilla.eliminarAvatar(this);
        this.casilla = c;
        if (c != null) c.anhadirAvatar(this);
    }

    public String getTipo() {
        return tipo;
    }

    public boolean estaEnCarcel() {
        return enCarcel;
    }

    public int getTurnosEnCarcel() {
        return turnosEnCarcel;
    }

    public void setTurnosEnCarcel(int t) {
        turnosEnCarcel = t;
    }

    public void incrementarTurnosEnCarcel() {
        turnosEnCarcel++;
    }

    public Casilla getCasilla() {
        return this.casilla;
    }

}