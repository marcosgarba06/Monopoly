package monopoly;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.*;

import partida.*;
//En menu estan definidas lo que serian en C las variables globales, menu tiene la informacion de la partida
public class Menu {

    //Atributos
    private ArrayList<Jugador> jugadores; //Jugadores de la partida.
    private ArrayList<Avatar> avatares; //Avatares en la partida.
    private int turno = 0; //Índice correspondiente a la posición en el arrayList del jugador (y el avatar) que tienen el turno
    private int lanzamientos; //Variable para contar el número de lanzamientos de un jugador en un turno.
    //private Tablero tablero; //Tablero en el que se juega.
    private Dado dado1; //Dos dados para lanzar y avanzar casillas.
    private Dado dado2;
    private Jugador banca; //El jugador banca.
    private boolean tirado; //Booleano para comprobar si el jugador que tiene el turno ha tirado o no.
    private boolean solvente; //Booleano para comprobar si el jugador que tiene el turno es solvente, es decir, si ha pagado sus deudas.

    //Se puede empezar el juego en Menu tambien
    public Menu(){
        iniciarPartida();
    }

    // Método para inciar una partida: crea los jugadores y avatares.
    private void iniciarPartida() {
        this.jugadores = new ArrayList<>();
        this.avatares = new ArrayList<>();
        this.dado1 = new Dado();
        this.dado2 = new Dado();
        this.banca = new Jugador();
        int numJugadores;

        Set<String> nombresUsados = new HashSet<>();
        Set<String> avataresUsados = new HashSet<>();

        Scanner sc = new Scanner(System.in);

        System.out.println("Cuantos jugadores van a participar (1-4): ");
        numJugadores = sc.nextInt();
        sc.nextLine();

        for (int i = 0; i < numJugadores; i++) {
            // --- pedir nombre hasta que sea válido ---
            String nombre;
            while (true) {
                System.out.println("Introduce el nombre del jugador " + (i+1) + ":");
                nombre = sc.nextLine();
                if (nombresUsados.add(nombre.toLowerCase())) {
                    break; // nombre válido
                } else {
                    System.out.println("Ese nombre ya está en uso. Intenta con otro.");
                }
            }

            // --- pedir avatar hasta que sea válido ---
            String avatar;
            while (true) {
                System.out.println("Elige tipo de avatar (Coche, Sombrero, Esfinge, Pelota):");
                avatar = sc.nextLine();
                if (avataresUsados.add(avatar.toLowerCase())) {
                    break; // avatar válido
                } else {
                    System.out.println("Ese avatar ya está en uso. Elige otro.");
                }
            }

            // Aquí deberías pasar la casilla de salida real de tu tablero
            Casilla salida = null; // TODO: sustituir por tablero.getSalida()

            Jugador j = new Jugador(nombre, avatar, salida, avatares);
            jugadores.add(j);

            System.out.println("Jugador " + nombre + " creado con avatar " + avatar);
        }
        System.out.println("Jugadores creados correctamente.\n");
    }
    
    /*Método que interpreta el comando introducido y toma la accion correspondiente.
    * Parámetro: cadena de caracteres (el comando).
    */
    private void analizarComando(String comando) {
    }

    /*Método que realiza las acciones asociadas al comando 'describir jugador'.
    * Parámetro: comando introducido
     */
    private void descJugador(String[] partes) {
    }

    /*Método que realiza las acciones asociadas al comando 'describir avatar'.
    * Parámetro: id del avatar a describir.
    */
    private void descAvatar(String ID) {
    }

    /* Método que realiza las acciones asociadas al comando 'describir nombre_casilla'.
    * Parámetros: nombre de la casilla a describir.
    */
    private void descCasilla(String nombre) {
    }

    //Método que ejecuta todas las acciones relacionadas con el comando 'lanzar dados'.
    private void lanzarDados() {
    }

    /*Método que ejecuta todas las acciones realizadas con el comando 'comprar nombre_casilla'.
    * Parámetro: cadena de caracteres con el nombre de la casilla.
     */
    private void comprar(String nombre) {
    }

    //Método que ejecuta todas las acciones relacionadas con el comando 'salir carcel'. 
    private void salirCarcel() {
    }

    // Método que realiza las acciones asociadas al comando 'listar enventa'.
    private void listarVenta() {
    }

    // Método que realiza las acciones asociadas al comando 'listar jugadores'.
    public void listarJugadores() {
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores en la partida.");
            return;
        }

        System.out.println("$> listar jugadores");

        for (int i = 0; i < jugadores.size(); i++) {
            Jugador j = jugadores.get(i);

            System.out.println("{");
            System.out.println("nombre: " + j.getNombre() + ",");
            System.out.println("avatar: " + (j.getAvatar() != null ? j.getAvatar().toString() : "-") + ",");
            System.out.println("fortuna: " + (long) j.getFortuna() + ",");

            // Propiedades
            if (j.getPropiedades().isEmpty()) {
                System.out.println("propiedades: -,");
            } else {
                String props = j.getPropiedades().stream().map(Casilla::getNombre).collect(Collectors.joining(", "));
                System.out.println("propiedades: [" + props + "],");
            }

            // Hipotecas y edificios: de momento no tienes listas en Jugador,
            // así que mostramos "-" como placeholder
            System.out.println("hipotecas: -,");
            System.out.println("edificios: -");

            System.out.print("}");
            if (i < jugadores.size() - 1) {
                System.out.println(","); // coma entre jugadores
            } else {
                System.out.println();
            }
        }
    }

    // Método que realiza las acciones asociadas al comando 'listar avatares'.
    private void listarAvatares() {
    }

    // Método que realiza las acciones asociadas al comando 'acabar turno'.
    private void acabarTurno() {
    }

}
