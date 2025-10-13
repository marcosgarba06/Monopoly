//package partida;
//
//
//import monopoly.*;
//
//import java.util.ArrayList;
//
//
//public class Avatar {
//
//    //Atributos
//    private String id; //Identificador: una letra generada aleatoriamente.
//    private String tipo; //Sombrero, Esfinge, Pelota, Coche
//    private Jugador jugador; //Un jugador al que pertenece ese avatar.
//    private Casilla lugar; //Los avatares se sitúan en casillas del tablero.
//
//    //Constructor vacío
//    public Avatar() {
//    }
//
//    /*Constructor principal. Requiere éstos parámetros:
//    * Tipo del avatar, jugador al que pertenece, lugar en el que estará ubicado, y un arraylist con los
//    * avatares creados (usado para crear un ID distinto del de los demás avatares).
//     */
//    public Avatar(String tipo, Jugador jugador, Casilla lugar, ArrayList<Avatar> avCreados) {
//        this.tipo = tipo;
//        this.jugador = jugador;
//        this. lugar = null;
//    }
//
//    //A continuación, tenemos otros métodos útiles para el desarrollo del juego.
//    /*Método que permite mover a un avatar a una casilla concreta. Parámetros:
//    * - Un array con las casillas del tablero. Se trata de un arrayList de arrayList de casillas (uno por lado).
//    * - Un entero que indica el numero de casillas a moverse (será el valor sacado en la tirada de los dados).
//    * EN ESTA VERSIÓN SUPONEMOS QUE valorTirada siemrpe es positivo.
//     */
//    public void moverAvatar(ArrayList<ArrayList<Casilla>> casillas, int valorTirada) {
//    }
//
//    /*Método que permite generar un ID para un avatar. Sólo lo usamos en esta clase (por ello es privado).
//    * El ID generado será una letra mayúscula. Parámetros:
//    * - Un arraylist de los avatares ya creados, con el objetivo de evitar que se generen dos ID iguales.
//     */
//    private void generarId(ArrayList<Avatar> avCreados) {
//
//    }
//}

package partida;


import monopoly.*;

        import java.util.*;


public class Avatar {

    //Atributos
    private String id; //Identificador: una letra generada aleatoriamente.
    private String tipo; //Sombrero, Esfinge, Pelota, Coche
    private Jugador jugador; //Un jugador al que pertenece ese avatar.
    private Casilla lugar; //Los avatares se sitúan en casillas del tablero.

    //Constructor vacío
    public Avatar() {

    }

    /*Constructor principal. Requiere éstos parámetros:
     * Tipo del avatar, jugador al que pertenece, lugar en el que estará ubicado, y un arraylist con los
     * avatares creados (usado para crear un ID distinto del de los demás avatares).
     */
    public Avatar(String tipo, Jugador jugador, Casilla lugar, ArrayList<Avatar> avCreados){
        this.tipo = tipo;
        this.jugador = jugador;
        this.lugar = lugar;

        generarId(avCreados); //generamos un id
        avCreados.add(this); //añadimos el id nuevo para el cual lo creamos

    }

    //A continuación, tenemos otros métodos útiles para el desarrollo del juego.
    /*Método que permite mover a un avatar a una casilla concreta. Parámetros:
     * - Un array con las casillas del tablero. Se trata de un arrayList de arrayList de casillas (uno por lado).
     * - Un entero que indica el numero de casillas a moverse (será el valor sacado en la tirada de los dados).
     * EN ESTA VERSIÓN SUPONEMOS QUE valorTirada siemrpe es positivo.
     */

    public String getId() {
        return id;
    }

    public void moverAvatar(ArrayList<ArrayList<Casilla>> casillas, int valorTirada) {
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
    private int posicion;// se guarda en que asilla esta el avatar en el tablero, del 0 añl 39
    private boolean enCarcel;//indica si true encarcelado o false libre

    public void setPosicion(int i) {//actualiza la posicion del avatar en el tablero
        this.posicion = i;
    }

    public void setEnCarcel(boolean b) {
        this.enCarcel = b;
    }

    public int getPosicion() {
        return posicion;
    }

    public boolean isEnCarcel() {
        return enCarcel;
    }

    public String toString() {
        String pos = (lugar != null) ? lugar.getNombre() : "sin posición";
        return tipo + " (ID: " + id + ", en " + pos + ")";
    }

}