package monopoly;

import partida.*;
import java.util.ArrayList;
import java.util.HashMap;







/*
public class Tablero {
    //Atributos.
    private ArrayList<ArrayList<Casilla>> posiciones; //Posiciones del tablero: se define como un arraylist de arraylists de casillas (uno por cada lado del tablero).
    //El funcionamiento será que crearemos 4 listas, por cada lado. Luego en una lista metemos todos los lados, y si queremos recorrer el tablero completo
    //solo tendremos que concatenar los cuatro lados ( las cuatro listas ).

    private HashMap<String, Grupo> grupos; //Grupos del tablero, almacenados como un HashMap con clave String (será el color del grupo).
    //Sirve para saber el color y el grupo al que pertenece cada solar

    private Jugador banca; //Un jugador que será la banca.

    // Identificadores de lado (para posiciones.get(...))
    private static final int SUR = 0, OESTE = 1, NORTE = 2, ESTE = 3;

    //Constructor: únicamente le pasamos el jugador banca (que se creará desde el menú).
    public Tablero(Jugador banca) {
        this.banca = banca; //recibimos a partir de la funcion la banca, entonces la inicializamos. Guardamos dentro de tablero la referencia

        this.posiciones = new ArrayList<>(); //creamos la lista principal
        //ahora vamos a crear los cuatro lados y a meterlos en el array

        this.posiciones.add(new ArrayList<>());
        //acabamos de incluir en la lista un nuevo elemento que es lista. Para acceder a el debemos de introducir el indice 0, que corresponde al valor de la variable SUR
        this.posiciones.add(new ArrayList<>());
        //hacemos lo mismo con el siguiente lado, es decir el oeste 1.
        this.posiciones.add(new ArrayList<>());
        //ahora ya estamos en el lado norte, que se representa con la variable NORTE  que tiene el valor dos, es decir que esta en la posicion dos de nuestro array de arrays
        this.posiciones.add(new ArrayList<>());
        //el último lado, el lado ESTE.



    }

*/




//    //Método para crear todas las casillas del tablero. Formado a su vez por cuatro métodos (1/lado).
//    private void generarCasillas() {
//        this.insertarLadoSur();
//        this.insertarLadoOeste();
//        this.insertarLadoNorte();
//        this.insertarLadoEste();
//    }
    
//    //Método para insertar las casillas del lado norte.
//    private void insertarLadoNorte() {
//    }
//
//    //Método para insertar las casillas del lado sur.
//    private void insertarLadoSur() {
//    }
//
//    //Método que inserta casillas del lado oeste.
//    private void insertarLadoOeste() {
//    }
//
//    //Método que inserta las casillas del lado este.
//    private void insertarLadoEste() {
//    }
//
//    //Para imprimir el tablero, modificamos el método toString().
//    @Override
//    public String toString() {
//    }
//
//    //Método usado para buscar la casilla con el nombre pasado como argumento:
//    public Casilla encontrar_casilla(String nombre){
//    }
//}
