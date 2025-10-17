package monopoly;

import partida.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Menu {

    // Atributos
    private Tablero tablero;
    private Jugador banca;
    private ArrayList<Jugador> jugadores;
    private ArrayList<Avatar> avatares;

    private int lanzamientos;
    private Dado dado1;
    private Dado dado2;

    private int turno = 0;
    private boolean tirado = false;
    private boolean solvente;
    private int contadorDobles = 0;
    private boolean repetirTurno = false;

    // Sets para evitar nombres y avatares duplicados
    private Set<String> nombresUsados = new HashSet<>();
    private Set<String> avataresUsados = new HashSet<>();


    public Menu() {
        this.banca = new Jugador();
        this.tablero = new Tablero(banca);
        this.jugadores = new ArrayList<>();
        this.avatares = new ArrayList<>();
        this.dado1 = new Dado();
        this.dado2 = new Dado();
    }

    public void iniciarPartida() {
        Scanner sc = new Scanner(System.in);
        System.out.println("¡Comienza el juego!");
        System.out.println("Comandos:");
        System.out.println("- crear jugador <nombre> <avatar>");
        System.out.println("- comandos [ruta]");
        System.out.println("- listar jugadores | jugadores");
        System.out.println("- jugador");
        System.out.println("- tirar dado");
        System.out.println("- acabar turno");
        System.out.println("- ver tablero");
        System.out.println("- describir <casilla>");
        System.out.println("- describir jugador <nombre>");
        System.out.println("- listar avatares");
        System.out.println("- listar venta");
        System.out.println("- comprar <casilla>");
        System.out.println("- salir carcel");
        System.out.println("- salir");

        // Creación de jugadores
        int numJugadores;
        do {
            System.out.println("¿Cuántos jugadores van a participar? 2 - 4: ");
            numJugadores = sc.nextInt();
            sc.nextLine();
            if (numJugadores < 2 || numJugadores > 4) {
                System.out.println("Número inválido. Debe ser entre 2 y 4.");
            }
        } while (numJugadores < 2 || numJugadores > 4);

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

            String avatarElegido;
            String[] avataresPermitidos = {"coche", "sombrero", "pelota", "esfinge"};
            Set<String> avataresUsados = new HashSet<>(); // Mueve esto FUERA del bucle principal

            while (true) {
                System.out.println("Elige un avatar (coche, sombrero, pelota, esfinge):");
                avatarElegido = sc.nextLine().trim().toLowerCase();

                boolean valido = false;
                for (String avatar : avataresPermitidos) {
                    if (avatar.equals(avatarElegido)) {
                        valido = true;
                        break;
                    }
                }

                if (!valido) {
                    System.out.println("Avatar no válido. Intenta de nuevo.");
                    continue;
                }

                if (avataresUsados.contains(avatarElegido)) {
                    System.out.println("Ese avatar ya está en uso. Elige otro.");
                    continue;
                }

                avataresUsados.add(avatarElegido);
                break; // Avatar válido y no repetido → salimos del bucle
            }



            Casilla salida = tablero.encontrar_casilla("Salida");
            Jugador j = new Jugador(nombre, avatarElegido, salida, avatares);
            Avatar av = j.getAvatar();
            salida.anhadirAvatar(av);
            avatares.add(av);

            jugadores.add(j);

            System.out.println("Jugador " + nombre + " creado con avatar " + avatarElegido);
        }

        System.out.println("Jugadores creados correctamente.\n");

        // Iniciar el bucle del juego
        iniciarJuego();
    }

    public void iniciarJuego() {
        Scanner sc = new Scanner(System.in);
        System.out.println("¡Comienza el juego!");
        System.out.println("Comandos: 'listar jugadores', 'jugador', 'acabar turno', 'ver tablero', 'describir <casilla>', 'describir jugador <nombre>', 'listar avatares', 'listar venta', 'tirar dado', 'comprar <casilla>', 'salir carcel' o 'salir'.");

        while (true) {
            Jugador actual = jugadores.get(turno);
            System.out.println("\nTurno de " + actual.getNombre());
            System.out.print("Comando: ");
            String comando = sc.nextLine();

            analizarComando(comando);
        }
    }


    /*Método que interpreta el comando introducido y toma la accion correspondiente.
     * Parámetro: cadena de caracteres (el comando).
     */
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
            Jugador actual = jugadores.get(turno);
            salirCarcel(actual);
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
        } else if (partes.length == 2 && partes[0].equals("describir") && partes[1].startsWith("avatar")) {
            descAvatar(partes[1].substring(7)); // si usas 'describir avatarX'
        } else {
            System.out.println("Comando no reconocido. Prueba con: 'listar jugadores', 'jugador', 'acabar turno', 'ver tablero', 'describir <casilla>', 'describir jugador <nombre>', 'listar avatares', 'listar venta', 'tirar dado', 'comprar <casilla>', 'salir carcel' o 'salir'.");
        }
    }


    /*Método que realiza las acciones asociadas al comando 'describir jugador'.
     * Parámetro: comando introducido
     */
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
                System.out.println("Cartas para salir de la cárcel: " + j.getCartasSalirCarcel());



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


    /*Método que realiza las acciones asociadas al comando 'describir avatar'.
     * Parámetro: id del avatar a describir.
     */
    private void descAvatar(String ID) {

        for (Avatar av : avatares) {
            if (av.getId().equalsIgnoreCase(ID)) {
                System.out.println("$> describir avatar " + ID);
                System.out.println("{");
                System.out.println("tipo: " + av.getJugador().getAvatar().getTipo() + ",");
                System.out.println("jugador: " + av.getJugador().getNombre() + ",");
                System.out.println("casilla: " + (av.getCasilla() != null ? av.getCasilla().getNombre() : "sin posición") + ",");
                System.out.println("enCarcel: " + av.estaEnCarcel() + ",");
                System.out.println("turnosCarcel: " + av.getTurnosEnCarcel());
                System.out.println("}");
                return;
            }
        }
        System.out.println("No se encontró ningún avatar con ID '" + ID + "'.");


    }


    /* Método que realiza las acciones asociadas al comando 'describir nombre_casilla'.
     * Parámetros: nombre de la casilla a describir.
     */
    private void descCasilla(String nombre) {
        Casilla casilla = tablero.encontrar_casilla(nombre);
        if (casilla != null) {
            System.out.println(casilla.describir());
        } else {
            System.out.println("No se encontró la casilla '" + nombre + "'.");
        }
    }




    //Metodo que se usa para lazar dados y mover el icono
    private void lanzarDados() {
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores en la partida.");
            return;
        }

        if (tirado) {
            System.out.println("Ya has tirado los dados este turno.");
            return;
        }

        Jugador actual = jugadores.get(turno);
        Avatar av = actual.getAvatar();

        // Si está en la cárcel, abre menú de opciones
        if (actual.isEnCarcel() || (av != null && av.estaEnCarcel())) {
            salirCarcel(actual);
            return;
        }

        int d1 = dado1.hacerTirada();
        int d2 = dado2.hacerTirada();
        int total = d1 + d2;

        System.out.println("Has sacado " + d1 + " y " + d2 + " → total: " + total);

        if (d1 == d2) {
            contadorDobles++;
            System.out.println("¡Dados dobles! (" + contadorDobles + " seguidos)");
            if (contadorDobles == 3) {
                System.out.println("¡Tres dobles seguidos! Vas directo a la cárcel.");
                actual.irACarcel(tablero);
                contadorDobles = 0;
                salirCarcel(actual);
                return;
            }
        } else {
            contadorDobles = 0;
        }

        av.moverAvatar(total, tablero);

        // Si al movernos hemos acabado en cárcel, abrir menú ya
        if (actual.isEnCarcel() || (av != null && av.estaEnCarcel())) {
            salirCarcel(actual);
            return;
        }

        // Si no estamos en cárcel, seguimos normal
        tirado = true;
        if (d1 == d2 && contadorDobles < 3) {
            tirado = false; // puede repetir
        }
    }


    /*Método que ejecuta todas las acciones realizadas con el comando 'comprar nombre_casilla'.
     * Parámetro: cadena de caracteres con el nombre de la casilla.
     */
    private void comprar(String nombre) {
        Casilla casilla = tablero.encontrar_casilla(nombre);
        if (casilla == null) {
            System.out.println("No se encontró la casilla '" + nombre + "'.");
            return;
        }

        Jugador actual = jugadores.get(turno);
        casilla.comprarCasilla(actual, banca);
    }


    //Método que ejecuta todas las acciones relacionadas con el comando 'salir carcel'.
    private void salirCarcel(Jugador jugador) {
        if (!jugador.isEnCarcel() && !jugador.getAvatar().estaEnCarcel()) {
            System.out.println("No estás en la cárcel. No necesitas salir.");
            return;
        }
        Avatar av = jugador.getAvatar();

        // Después de 3 turnos en la cárcel, pago obligatorio
        if (av.getTurnosEnCarcel() >= 3) {
            System.out.println("Has estado 3 turnos en la cárcel. Pago obligatorio de 500.000€ y avanzas.");
            if (jugador.getFortuna() < 500000) { //Si no tiene 500.000 de fortuna -> bancarrota
                System.out.println("No puedes pagar. Bancarrota.");
                jugador.setBancarrota(true);
                tirado = true;
                return;
            }

            //Se le resta 500.000 al jugador y tira los dados
            jugador.restarFortuna(500000);
            jugador.sumarGastos(500000);
            av.setEnCarcel(false);
            jugador.setEnCarcel(false);
            av.setTurnosEnCarcel(0);

            int d1 = dado1.hacerTirada();
            int d2 = dado2.hacerTirada();
            int total = d1 + d2;
            System.out.println("Has sacado " + d1 + " y " + d2 + " → total: " + total);
            av.moverAvatar(total, tablero);
            tirado = true;
            return;
        }

        //Si aun no ha estado 3 turnos se dan las 3 opciones de salir, pagar, carta o dobles
        java.util.Scanner sc = new java.util.Scanner(System.in);
        while (true) {
            System.out.println("\nEstás en la cárcel (Turno " + (av.getTurnosEnCarcel() + 1) + "/3). Elige opción:");
            System.out.println("1) Pagar 500.000€ (sales, sin mover)");
            System.out.println("2) Usar carta de 'Salir de la cárcel'");
            System.out.println("3) Tirar buscando dobles");
            System.out.print("Opción: ");

            String op = sc.nextLine().trim();

            if (op.equals("1")) { //Si el jugador elige la opcion 1 entonces paga
                if (jugador.getFortuna() >= 500000) {
                    jugador.restarFortuna(500000);
                    jugador.sumarGastos(500000);
                    av.setEnCarcel(false);
                    jugador.setEnCarcel(false);
                    av.setTurnosEnCarcel(0);
                    System.out.println("Has salido de la cárcel pagando 500.000€ (no te mueves este turno).");
                    tirado = true;
                } else {
                    System.out.println("No tienes suficiente dinero para pagar.");
                }
                break;

            } else if (op.equals("2")) { //Si elige la opcion 2 y tiene carta sale si no no sale
                if (jugador.usarCartaSalirCarcel()) {
                    av.setEnCarcel(false);
                    jugador.setEnCarcel(false);
                    av.setTurnosEnCarcel(0);
                    System.out.println("Has salido de la cárcel usando una carta (no te mueves este turno).");
                    tirado = true;
                } else {
                    System.out.println("No tienes carta de 'Salir de la cárcel'.");
                }
                break;

            } else if (op.equals("3")) { //Si elige la opcion 3 tira los dados
                int d1 = dado1.hacerTirada();
                int d2 = dado2.hacerTirada();
                System.out.println("Has sacado " + d1 + " y " + d2);

                if (d1 == d2) {
                    av.setEnCarcel(false);
                    jugador.setEnCarcel(false);
                    av.setTurnosEnCarcel(0);
                    System.out.println("¡Dobles! Sales y avanzas " + (d1 + d2));
                    av.moverAvatar(d1 + d2, tablero);
                } else {
                    av.incrementarTurnosEnCarcel();
                    System.out.println("No son dobles. Permaneces en la cárcel y pierdes este turno.");
                    System.out.println("Turnos en cárcel: " + av.getTurnosEnCarcel() + "/3");
                }
                tirado = true;
                break;
            } else {
                System.out.println("Opción inválida.");
            }
        }
    }


    //Metodo que indica el jugador que tiene el turno
    private void indicarTurno() {
        if (jugadores == null || jugadores.isEmpty()) { //Si no hay jugadores no funciona
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


    // Método que realiza las acciones asociadas al comando 'listar enventa'.
    private void listarVenta() {
        ArrayList<Casilla> enVenta = tablero.getCasillasEnVenta(); // Asegúrate de tener este método en Tablero

        if (enVenta.isEmpty()) {
            System.out.println("No hay casillas en venta.");
            return;
        }

        System.out.println("$> listar venta");
        for (Casilla c : enVenta) {
            System.out.println(c.casEnVenta());
        }
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


    // Método que realiza las acciones asociadas al comando 'listar avatares'.
    private void listarAvatares() {
        if (avatares.isEmpty()) {
            System.out.println("No hay avatares en juego.");
            return;
        }

        System.out.println("$> listar avatares");
        for (Avatar av : avatares) {
            System.out.println("{");
            System.out.println("id: " + av.getId() + ",");
            System.out.println("tipo: " + av.getJugador().getAvatar().getTipo() + ",");
            System.out.println("jugador: " + av.getJugador().getNombre() + ",");
            System.out.println("casilla: " + (av.getCasilla() != null ? av.getCasilla().getNombre() : "sin posición") + ",");
            System.out.println("enCarcel: " + av.estaEnCarcel());
            System.out.println("}");
        }
    }



    // Método que realiza las acciones asociadas al comando 'acabar turno'.
    private void acabarTurno() {
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores en la partida.");
            return;
        }

        if (!repetirTurno) {
            turno = (turno + 1) % jugadores.size();
        }

        tirado = false;
        repetirTurno = false;
        contadorDobles = 0;

        Jugador actual = jugadores.get(turno);
        System.out.println("Turno acabado. Ahora le toca a:");
        System.out.println("$> jugador");
        System.out.println("{");
        System.out.println("nombre: " + actual.getNombre() + ",");
        System.out.println("avatar: " + actual.getAvatar().getId());
        System.out.println("}");
    }
}