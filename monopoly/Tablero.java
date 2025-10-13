package monopoly;

import partida.*;
import java.util.ArrayList;
import java.util.HashMap;

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

        this.posiciones = new ArrayList<>();
        for (int i = 0; i < 4; i++){
            this.posiciones.add(new ArrayList<>());
        }
        //creamos la lista principal
        //ahora vamos a crear los cuatro lados y a meterlos en el array

        this.grupos = new HashMap<>();
        generarCasillas();

        this.posiciones.add(new ArrayList<>());
        //acabamos de incluir en la lista un nuevo elemento que es lista. Para acceder a el debemos de introducir el indice 0, que corresponde al valor de la variable SUR
        this.posiciones.add(new ArrayList<>());
        //hacemos lo mismo con el siguiente lado, es decir el oeste 1.
        this.posiciones.add(new ArrayList<>());
        //ahora ya estamos en el lado norte, que se representa con la variable NORTE  que tiene el valor dos, es decir que esta en la posicion dos de nuestro array de arrays
        this.posiciones.add(new ArrayList<>());
        //el último lado, el lado ESTE.

    }

    //Método para crear todas las casillas del tablero. Formado a su vez por cuatro métodos (1/lado).
    private void generarCasillas() {
        this.insertarLadoSur();
        this.insertarLadoOeste();
        this.insertarLadoNorte();
        this.insertarLadoEste();
    }

    //Método para insertar las casillas del lado norte.
    private void insertarLadoNorte() {
        ArrayList<Casilla> norte = posiciones.get(NORTE);

        norte.add(new Casilla("IrCarcel", "Especial", 30, banca));
        norte.add(new Casilla("Solar17", "Solar", 29, 2800000, banca));
        norte.add(new Casilla("Serv2", "Servicio", 28, 1500000, banca));
        norte.add(new Casilla("Solar16", "Solar", 27, 2600000, banca));
        norte.add(new Casilla("Solar15", "Solar", 26, 2600000, banca));
        norte.add(new Casilla("Trans3", "Transporte", 25, 2000000, banca));
        norte.add(new Casilla("Solar14", "Solar", 24, 2400000, banca));
        norte.add(new Casilla("Solar13", "Solar", 23, 2200000, banca));
        norte.add(new Casilla("Suerte", "Suerte", 22, banca));
        norte.add(new Casilla("Solar12", "Solar", 21, 2200000, banca));
        norte.add(new Casilla("Parking", "Especial", 20, banca));


    }

    //Método para insertar las casillas del lado sur.
    private void insertarLadoSur() {
        ArrayList<Casilla> sur = posiciones.get(SUR);

        sur.add(new Casilla("Carcel", "Especial", 10, banca));
        sur.add(new Casilla("Solar5", "Solar", 9, 1200000, banca));
        sur.add(new Casilla("Solar4", "Solar", 8, 1000000, banca));
        sur.add(new Casilla("Suerte", "Suerte", 7, banca));
        sur.add(new Casilla("Solar3", "Solar", 6, 1000000, banca));
        sur.add(new Casilla("Trans1", "Transporte", 5, 500000, banca));
        sur.add(new Casilla("Imp1", "Impuesto", 4, 200000, banca));
        sur.add(new Casilla("Solar2", "Solar", 3, 600000, banca));
        sur.add(new Casilla("Caja", "Caja", 2, banca));
        sur.add(new Casilla("Solar1", "Solar", 1, 600000, banca));
        sur.add(new Casilla("Salida", "Especial", 0, banca));
    }

    //Método que inserta casillas del lado oeste.
    private void insertarLadoOeste() {
        ArrayList<Casilla> oeste = posiciones.get(OESTE);

        oeste.add(new Casilla("Solar6", "Solar", 11, 1400000, banca));
        oeste.add(new Casilla("Serv1", "Servicio", 12, 1500000, banca));
        oeste.add(new Casilla("Solar7", "Solar", 13, 1400000, banca));
        oeste.add(new Casilla("Solar8", "Solar", 14, 1600000, banca));
        oeste.add(new Casilla("Trans2", "Transporte", 15, 2000000, banca));
        oeste.add(new Casilla("Solar9", "Solar", 16, 1800000, banca));
        oeste.add(new Casilla("Caja", "Caja", 17, banca));
        oeste.add(new Casilla("Solar10", "Solar", 18, 1800000, banca));
        oeste.add(new Casilla("Solar11", "Solar", 19, 2000000, banca));

    }

    //Método que inserta las casillas del lado este.
    private void insertarLadoEste() {
        ArrayList<Casilla> este = posiciones.get(ESTE);

        este.add(new Casilla("Solar18", "Solar", 31, 3000000, banca));
        este.add(new Casilla("Solar19", "Solar", 32, 3000000, banca));
        este.add(new Casilla("Caja", "Caja", 33, banca));
        este.add(new Casilla("Solar20", "Solar", 34, 3200000, banca));
        este.add(new Casilla("Trans4", "Transporte", 35, 2000000, banca));
        este.add(new Casilla("Suerte", "Suerte", 36, banca));
        este.add(new Casilla("Solar21", "Solar", 37, 3500000, banca));
        este.add(new Casilla("Imp2", "Impuesto", 38, 200000, banca));
        este.add(new Casilla("Solar22", "Solar", 39, 4000000, banca));
    }


    private String formatCasilla(Casilla c) {
        String nombre = c.getNombre();
        return String.format("[%-12s]", nombre); // ancho fijo de 12 + corchetes
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        ArrayList<Casilla> sur = posiciones.get(SUR);
        ArrayList<Casilla> norte = posiciones.get(NORTE);
        ArrayList<Casilla> oeste = posiciones.get(OESTE);
        ArrayList<Casilla> este = posiciones.get(ESTE);

        int anchoCasilla = 14; // lo que ocupa cada casilla formateada

        // --- Línea superior (NORTE, de derecha a izquierda) ---
        for (int i = norte.size() - 1; i >= 0; i--) {
            sb.append(formatCasilla(norte.get(i)));
        }
        sb.append("\n");

        // --- Lados OESTE y ESTE ---
        int alto = Math.max(oeste.size(), este.size());
        for (int i = 0; i < alto; i++) {
            // OESTE (de arriba a abajo)
            if (i < oeste.size()) {
                sb.append(formatCasilla(oeste.get(oeste.size() - 1 - i)));
            } else {
                sb.append(" ".repeat(anchoCasilla));
            }

            // Espacios en el centro
            sb.append(" ".repeat((norte.size() - 2) * anchoCasilla));

            // ESTE (de arriba a abajo)
            if (i < este.size()) {
                sb.append(formatCasilla(este.get(i)));
            }
            sb.append("\n");
        }

        // --- Línea inferior (SUR, de izquierda a derecha) ---
        for (Casilla c : sur) {
            sb.append(formatCasilla(c));
        }
        sb.append("\n");

        return sb.toString();
    }

    //Método usado para buscar la casilla con el nombre pasado como argumento:
    public Casilla encontrar_casilla(String nombre){
        for (ArrayList<Casilla> lado : posiciones) {
            for (Casilla c : lado) {
                if (c.getNombre().equalsIgnoreCase(nombre)) {
                    return c;
                }
            }
        }
        return null;
        }
}
