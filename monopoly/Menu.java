package monopoly;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import partida.*;

public class Menu {

    // Atributos
    private final Tablero tablero;
    private ArrayList<Jugador> jugadores;
    private ArrayList<Avatar> avatares;
    private int turno = 0;
    private int lanzamientos;
    private Dado dado1;
    private Dado dado2;
    private Jugador banca;
    private boolean tirado;
    private boolean solvente;

    public Menu(Tablero tablero) {
        this.tablero = tablero;
        iniciarPartida();
    }

    private void iniciarPartida() {
        this.jugadores = new ArrayList<>();
        this.avatares = new ArrayList<>();
        this.dado1 = new Dado();
        this.dado2 = new Dado();
        this.banca = new Jugador();
        int numJugadores;
        tirado = false;

        Set<String> nombresUsados = new HashSet<>();
        Set<String> avataresUsados = new HashSet<>();

        Scanner sc = new Scanner(System.in);

        System.out.println("Cuantos jugadores van a participar (1-4): ");
        numJugadores = sc.nextInt();
        sc.nextLine();

        for (int i = 0; i < numJugadores; i++) {
            String nombre;
            while (true) {
                System.out.println("Introduce el nombre del jugador " + (i + 1) + ":");
                nombre = sc.nextLine();
                if (nombresUsados.add(nombre.toLowerCase())) {
                    break;
                } else {
                    System.out.println("Ese nombre ya está en uso. Intenta con otro.");
                }
            }

            String avatar;
            while (true) {
                System.out.println("Elige tipo de avatar (Coche, Sombrero, Esfinge, Pelota):");
                avatar = sc.nextLine();
                if (avataresUsados.add(avatar.toLowerCase())) {
                    break;
                } else {
                    System.out.println("Ese avatar ya está en uso. Elige otro.");
                }
            }

            Casilla salida = tablero.encontrar_casilla("Salida");// TODO: sustituir por tablero.getSalida()
            Jugador j = new Jugador(nombre, avatar, salida, avatares);
            Avatar av = j.getAvatar(); // ← el avatar ya fue creado dentro del jugador
            salida.anhadirAvatar(av); // ← lo colocas en la casilla de salida
            avatares.add(av);         // ← lo añades a la lista global de avatares


            jugadores.add(j);

            System.out.println("Jugador " + nombre + " creado con avatar " + avatar);
        }

        System.out.println("Jugadores creados correctamente.\n");
    }

    private void indicarTurno() {
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores en la partida.");
            return;
        }

        Jugador actual = jugadores.get(turno);
        System.out.println("$> jugador");
        System.out.println("{");
        System.out.println("nombre: " + actual.getNombre() + ",");
        System.out.println("avatar: " + actual.getAvatar().getId());
        System.out.println("}");
    }

    private void lanzarDados() {
        if (tirado) {
            System.out.println("Ya has tirado los dados este turno.");
            return;
        }

        int d1 = dado1.hacerTirada();
        int d2 = dado2.hacerTirada();
        int total = d1 + d2;

        System.out.println("Has sacado " + d1 + " y " + d2 + " → total: " + total);

        Jugador actual = jugadores.get(turno);
        Avatar av = actual.getAvatar();
        av.mover(total, tablero); // ← asegúrate de tener este método en Avatar

        tirado = true;
    }


    public void iniciarJuego() {
        Scanner sc = new Scanner(System.in);
        System.out.println("¡Comienza el juego!");
        System.out.println("¡Comandos: 'listar jugadores', 'jugador', 'acabar turno', 'ver tablero', 'describir <casilla>', 'describir jugador <nombre>', 'listar avatares', 'listar venta', 'tirar dado', 'comprar <casilla>', 'salir carcel' o 'salir'.\"");

        while (true) {
            Jugador actual = jugadores.get(turno);
            System.out.println("\nTurno de " + actual.getNombre());
            System.out.print("Comando: ");
            String comando = sc.nextLine();

            analizarComando(comando);
        }
    }


    public void analizarComando(String comando) {
        comando = comando.trim().toLowerCase();
        String[] partes = comando.split(" ");

        if (comando.equals("listar jugadores") || comando.equals("jugadores")) {
            listarJugadores();
        } else if (comando.equals("ver tablero")) {

            System.out.println(tablero);
        } else if (comando.equals("salir")) {
            System.out.println("Saliendo del juego...");
            System.exit(0);
        } else if (comando.equals("jugador")) {
            indicarTurno();
        } else if (comando.equals("tirar dado")) {
            lanzarDados();
        } else if (partes.length == 2 && partes[0].equals("comprar")) {
            comprar(partes[1]);
        } else if (comando.equals("salir carcel")) {
            salirCarcel();
         } else if (comando.equals("listar venta")) {
            listarVenta();
        } else if (comando.equals("listar avatares")) {
            listarAvatares();
        } else if (partes.length >= 3 && partes[0].equals("describir") && partes[1].equals("jugador")) {
            descJugador(partes);
        } else if (partes.length == 2 && partes[0].equals("describir")) {
            String nombreCasilla = partes[1];
            Casilla casilla = tablero.encontrar_casilla(nombreCasilla);
            if (casilla != null) {
                System.out.println(casilla.describir());
            } else {
                System.out.println("No se encontró la casilla '" + nombreCasilla + "'.");
            }
        } else if (comando.equals("acabar turno")) {
            acabarTurno();
        } else {
            System.out.println("Comando no reconocido. Prueba con: 'listar jugadores', 'jugador', 'acabar turno', 'ver tablero', 'describir <casilla>', 'describir jugador <nombre>', 'listar avatares', 'listar venta', 'tirar dado', 'comprar <casilla>', 'salir carcel' o 'salir'.");

        }
    }


    private void descJugador(String[] partes) {
        if (partes.length < 3) {
            System.out.println("Uso: describir jugador <nombre>");
            return;
        }

        String nombreBuscado = partes[2].toLowerCase();

        for (Jugador j : jugadores) {
            if (j.getNombre().toLowerCase().equals(nombreBuscado)) {
                System.out.println("$> describir jugador " + j.getNombre());
                System.out.println("{");
                System.out.println("nombre: " + j.getNombre() + ",");
                System.out.println("avatar: " + j.getAvatar().getId() + ",");
                System.out.println("fortuna: " + (long) j.getFortuna() + ",");

                if (j.getPropiedades().isEmpty()) {
                    System.out.println("propiedades: -,");
                } else {
                    String props = j.getPropiedades().stream().map(Casilla::getNombre).collect(Collectors.joining(", "));
                    System.out.println("propiedades: [" + props + "],");
                }

                System.out.println("hipotecas: -,");
                System.out.println("edificios: -");
                System.out.println("}");
                return;
            }
        }

        System.out.println("No se encontró ningún jugador con el nombre '" + nombreBuscado + "'.");
    }

    private void descAvatar(String ID) {
    }

    private void descCasilla(String nombre) {
    }


    private void comprar(String nombre) {
    }

    private void salirCarcel() {
    }

    private void listarVenta() {
    }

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

            if (j.getPropiedades().isEmpty()) {
                System.out.println("propiedades: -,");
            } else {
                String props = j.getPropiedades().stream().map(Casilla::getNombre).collect(Collectors.joining(", "));
                System.out.println("propiedades: [" + props + "],");
            }

            System.out.println("hipotecas: -,");
            System.out.println("edificios: -");

            System.out.print("}");
            if (i < jugadores.size() - 1) {
                System.out.println(",");
            } else {
                System.out.println();
            }
        }
    }

    private void listarAvatares() {
    }

    private void acabarTurno() {
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores en la partida.");
            return;
        }

        turno = (turno + 1) % jugadores.size(); // Avanza al siguiente jugador, vuelve a 0 si llega al final
        tirado = false;

        Jugador actual = jugadores.get(turno);
        System.out.println("Turno acabado. Ahora le toca a:");
        System.out.println("$> jugador");
        System.out.println("{");
        System.out.println("nombre: " + actual.getNombre() + ",");
        System.out.println("avatar: " + actual.getAvatar().getId());
        System.out.println("}");
    }
}
