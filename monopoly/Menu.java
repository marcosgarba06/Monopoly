package monopoly;

import partida.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final Set<String> nombresUsados = new HashSet<>();
    private final Set<String> avataresUsados = new HashSet<>();


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
        System.out.println("Comandos disponibles:");
        System.out.println("  - 'listar jugadores' / 'jugadores'");
        System.out.println("  - 'jugador' (ver turno actual)");
        System.out.println("  - 'tirar dado'");
        System.out.println("  - 'acabar turno'");
        System.out.println("  - 'ver tablero'");
        System.out.println("  - 'describir <casilla>'");
        System.out.println("  - 'describir jugador <nombre>'");
        System.out.println("  - 'describir avatarX'");
        System.out.println("  - 'listar venta' (casillas disponibles)");
        System.out.println("  - 'listar avatares'");
        System.out.println("  - 'comprar <casilla>'");
        System.out.println("  - 'hipotecar' (solo si estás en bancarrota)");
        System.out.println("  - 'edificar <solar> <tipo> [cantidad]' (casa, hotel, piscina, pista)");
        System.out.println("  - 'salir carcel'");
        System.out.println("  - 'comandos <ruta/al/archivo.txt>' (ejecutar comandos desde archivo)");
        System.out.println("  - 'salir' (cerrar el juego)");
        System.out.print("> ");
        while (true) {
            Jugador actual = jugadores.get(turno);
            System.out.println("\nTurno de " + actual.getNombre());
            System.out.print("> ");
            String comando = sc.nextLine();

            analizarComando(comando);
            tablero.inicializarCartas();

        }
    }


    /*Método que interpreta el comando introducido y toma la accion correspondiente.
     * Parámetro: cadena de caracteres (el comando).
     */
    public void analizarComando(String comando) {
        // Conserva el original para no romper rutas con mayúsculas
        String comandoOriginal = comando.trim();
        String comandoLC = comandoOriginal.toLowerCase(Locale.ROOT);

        // Manejo del comando: "comandos <ruta/al/archivo.txt>"
        if (comandoLC.startsWith("comandos")) {
            // Dividimos en máximo 2 partes: la palabra 'comandos' y el resto (la ruta)
            String[] partesCmd = comandoOriginal.split("\\s+", 2);

            // Si no hay segunda parte (ruta) o está vacía, mostramos el uso correcto
            if (partesCmd.length < 2 || partesCmd[1].isBlank()) {
                System.out.println("Uso: comandos <ruta/al/archivo.txt>");
            } else {
                // Usamos la ruta tal cual (sin pasarla a minúsculas)
                String ruta = partesCmd[1].trim();
                ejecutarComandosDesdeArchivo(ruta);
            }
            // Terminamos aquí para no seguir procesando este comando en otros casos
            return;
        }
        // A partir de aquí, tu lógica actual (usando minúsculas para comparar)
        String[] partes = comandoLC.split("\\s+");

        if (comandoLC.equals("listar jugadores") || comandoLC.equals("jugadores")) {
            listarJugadores();
        } else if (comandoLC.equals("ver tablero")) {
            System.out.println(tablero);
        } else if (comandoLC.equals("salir")) {
            System.out.println("Saliendo del juego...");
            System.exit(0);
        } else if (comandoLC.equals("jugador")) {
            indicarTurno();
        } else if (comandoLC.equals("tirar dado")) {
            lanzarDados();

        } else if (partes.length == 2 && partes[0].equals("comprar")) {
            comprar(partes[1]);
        } else if (comandoLC.equals("salir carcel")) {
            Jugador actual = jugadores.get(turno);
            salirCarcel(actual);
        } else if (comandoLC.equals("listar venta")) {
            listarVenta();
        } else if (comandoLC.equals("listar avatares")) {
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
        } else if (comandoLC.equals("acabar turno")) {
            acabarTurno();
        } else if (comandoLC.equals("hipotecar")) {
            hipotecar();
        }else if (partes.length == 2 && partes[0].equals("describir") && partes[1].startsWith("avatar")) {
            descAvatar(partes[1].substring(7)); // si usas 'describir avatarX'
        } else if (partes.length >= 4 && partes[0].equals("edificar")) {
            String nombreCasilla = partes[1];
            String tipo = partes[2];
            int cantidad = Integer.parseInt(partes[3]);
            edificar(nombreCasilla, tipo, cantidad);
        }else {
            System.out.println("Comando no reconocido. Prueba con alguno de estos:");
            System.out.println("  - 'listar jugadores' / 'jugadores'");
            System.out.println("  - 'jugador' (ver turno actual)");
            System.out.println("  - 'tirar dado'");
            System.out.println("  - 'acabar turno'");
            System.out.println("  - 'ver tablero'");
            System.out.println("  - 'describir <casilla>'");
            System.out.println("  - 'describir jugador <nombre>'");
            System.out.println("  - 'describir avatarX'");
            System.out.println("  - 'listar venta' (casillas disponibles)");
            System.out.println("  - 'listar avatares'");
            System.out.println("  - 'comprar <casilla>'");
            System.out.println("  - 'hipotecar' (solo si estás en bancarrota)");
            System.out.println("  - 'edificar <tipo> [cantidad]' (casa, hotel, piscina, pista)");
            System.out.println("  - 'salir carcel'");
            System.out.println("  - 'comandos <ruta/al/archivo.txt>' (ejecutar comandos desde archivo)");
            System.out.println("  - 'salir' (cerrar el juego)");
        }
    }

    // Metodo helper para ejecutar comandos desde un archivo
    private void ejecutarComandosDesdeArchivo(String rutaArchivo) {
        // Crear un objeto Path a partir de la ruta recibida
        Path path = Paths.get(rutaArchivo);
        System.out.println("Intentando abrir archivo en: " + path.toAbsolutePath());

        if (!Files.exists(path)) { //Si exsiste el archivo entonces
            System.out.println("No se encontró el archivo: " + path.toAbsolutePath());
            return;
        }
        // Usamos try catch para coger los posibles errores que nos saque el archivo
        // Abrir un BufferedReader con UTF-8 usando try-with-resources (se cierra solo)
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String linea;
            int numLinea = 0;

            // Leer línea a línea hasta que no haya más (readLine() devuelve null cuando no hay más líneas)
            while ((linea = br.readLine()) != null) {
                numLinea++;
                String comando = linea.strip(); //Eliminar los espacios al final \t, \n, ...

                if (comando.isEmpty()) continue; // ignora líneas vacías
                System.out.println("[archivo:" + numLinea + "] " + comando); //Mostrar que comando que ha leído e intentará usar
                analizarComando(comando);
            }
        } catch (IOException e) {
            // Captura errores de E/S (permiso, encoding, archivo bloqueado, etc.) y los muestra
            System.out.println("Error leyendo el archivo: " + e.getMessage());
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

    public void hipotecar() {
        Jugador jugador = jugadores.get(turno);

        if (!jugador.isBancarrota()) {
            System.out.println("Solo puedes hipotecar si estás en bancarrota.");
            return;
        }

        Casilla casilla = jugador.getAvatar().getCasilla();

        if (!jugador.equals(casilla.getDuenho())) {
            System.out.println("No eres dueño de esta casilla.");
            return;
        }

        if (!casilla.esHipotecable()) {
            System.out.println("Esta casilla no se puede hipotecar.");
            return;
        }

        if (casilla.estaHipotecada()) {
            System.out.println("La casilla ya está hipotecada.");
            return;
        }

        float valorHipoteca = casilla.getValor() / 2;
        jugador.sumarFortuna(valorHipoteca);
        casilla.hipotecar();
        System.out.println("Has hipotecado " + casilla.getNombre() + " y recibes " + (long)valorHipoteca + "€.");
    }




    private int lanzarDados() {
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores en la partida.");
            return 0;
        }

        if (tirado) {
            System.out.println("Ya has tirado los dados este turno.");
            return 0;
        }

        Jugador actual = jugadores.get(turno);
        Avatar av = actual.getAvatar();

        // Si está en la cárcel, abre menú de opciones
        if (actual.isEnCarcel() || (av != null && av.estaEnCarcel())) {
            salirCarcel(actual);
            return 0;
        }

        int d1 = dado1.hacerTirada();
        int d2 = dado2.hacerTirada();
        int total = d1 + d2;
        tablero.setUltimaTirada(total);
        System.out.println("Has sacado " + d1 + " y " + d2 + " → total: " + total);

        if (d1 == d2) {
            contadorDobles++;
            System.out.println("¡Dados dobles! (" + contadorDobles + " seguidos)");
            if (contadorDobles == 3) {
                System.out.println("¡Tres dobles seguidos! Vas directo a la cárcel.");
                actual.irACarcel(tablero);
                contadorDobles = 0;
                salirCarcel(actual);
                return total;
            }
        } else {
            contadorDobles = 0;
        }

        av.moverAvatar(total, tablero);

        // Si al movernos hemos acabado en cárcel, abrir menú ya
        if (actual.isEnCarcel() || (av != null && av.estaEnCarcel())) {
            salirCarcel(actual);
            return total;
        }

        // Si no estamos en cárcel, seguimos normal
        tirado = true;
        if (d1 == d2 && contadorDobles < 3) {
            tirado = false; // puede repetir
        }

        return total;
    }


    /*Método que ejecuta todas las acciones realizadas con el comando 'comprar nombre_casilla'.
     * Parámetro: cadena de caracteres con el nombre de la casilla.
     */
    public void comprar(String nombreCasilla) {
        Jugador jugador = jugadores.get(turno);
        Casilla casilla = tablero.encontrar_casilla(nombreCasilla);

        if (casilla == null) {
            System.out.println("No se encontró la casilla '" + nombreCasilla + "'.");
            return;
        }

        // Verifica que el jugador esté en la casilla
        Casilla casillaActual = jugador.getAvatar().getCasilla();
        if (!casillaActual.equals(casilla)) {
            System.out.println("No puedes comprar esta casilla porque no estás en ella.");
            return;
        }

        // Verifica que la casilla esté en venta
        if (casilla.getDuenho() != null) {
            System.out.println("Esta casilla ya tiene dueño.");
            return;
        }

        // Verifica que sea comprable
        String tipo = casilla.getTipo().toLowerCase();
        if (!tipo.equals("solar") && !tipo.equals("transporte") && !tipo.equals("servicio")) {
            System.out.println("Esta casilla no se puede comprar.");
            return;
        }

        // Verifica que el jugador tenga suficiente dinero
        if (jugador.getFortuna() < casilla.getValor()) {
            System.out.println("No tienes suficiente dinero para comprar esta casilla.");
            return;
        }

        // Realiza la compra
        jugador.restarFortuna(casilla.getValor());
        casilla.setDuenho(jugador);
        jugador.anhadirPropiedad(casilla);
        System.out.println("Has comprado " + casilla.getNombre() + " por " + (long)casilla.getValor() + "€.");
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
    private void listarVenta() {
        ArrayList<Casilla> enVenta = tablero.getCasillasEnVenta();

        if (enVenta.isEmpty()) {
            System.out.println("No hay propiedades en venta.");
            return;
        }

        System.out.println("$> listar enventa");
        for (Casilla c : enVenta) {
            System.out.println("{");
            System.out.println("tipo: " + c.getTipo() + ",");
            if ("solar".equalsIgnoreCase(c.getTipo())) {
                System.out.println("grupo: " + c.getGrupo() + ",");
            }
            System.out.println("valor: " + (long)c.getValor());
            System.out.println("},");
        }
    }
    private void construir(String tipo, String nombreCasilla, int cantidad) {
        Casilla c = tablero.encontrar_casilla(nombreCasilla);
        Jugador jugador = jugadores.get(turno);

        if (!"Solar".equalsIgnoreCase(c.getTipo()) || c.getDuenho() != jugador) {
            System.out.println("No puedes construir en esta casilla.");
            return;
        }

        switch (tipo.toLowerCase()) {
            case "casa":
                if (c.puedeConstruirCasa(jugador)) {
                    c.construirCasas(jugador, cantidad);
                    System.out.println("Has construido " + cantidad + " casa(s) en " + nombreCasilla);
                } else {
                    System.out.println("No puedes construir casas aquí.");
                }
                break;
            case "hotel":
                if (c.puedeConstruirHotel()) {
                    c.construirHotel(jugador);
                    System.out.println("Has construido un hotel en " + nombreCasilla);
                } else {
                    System.out.println("No puedes construir un hotel aquí.");
                }
                break;
            case "piscina":
                if (c.puedeConstruirPiscina()) {
                    c.construirPiscina(jugador);
                    System.out.println("Has construido una piscina en " + nombreCasilla);
                } else {
                    System.out.println("No puedes construir una piscina aquí.");
                }
                break;
            case "pista":
                if (c.puedeConstruirPista()) {
                    c.construirPista(jugador);
                    System.out.println("Has construido una pista de deporte en " + nombreCasilla);
                } else {
                    System.out.println("No puedes construir una pista aquí.");
                }
                break;
            default:
                System.out.println("Tipo de edificación no reconocido.");
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


    public void edificar(String nombreCasilla, String tipo, int cantidad) {
        Jugador jugador = jugadores.get(turno);
        Casilla casilla = tablero.encontrar_casilla(nombreCasilla);

        if (casilla == null) {
            System.out.println("No se encontró la casilla '" + nombreCasilla + "'.");
            return;
        }

        if (!"solar".equalsIgnoreCase(casilla.getTipo())) {
            System.out.println("Solo se puede edificar en solares.");
            return;
        }

        if (!jugador.equals(casilla.getDuenho())) {
            System.out.println("No eres dueño de esta casilla.");
            return;
        }

        if (casilla.getGrupo() == null || !jugador.poseeGrupoCompleto(casilla, tablero)) {
            System.out.println("No puedes edificar. No posees todo el grupo o el grupo no está definido.");
            return;
        }

        switch (tipo.toLowerCase()) {
            case "casa":
                if (casilla.puedeConstruirCasa(jugador)) {
                    casilla.construirCasas(jugador, cantidad);
                    System.out.println("Has construido " + cantidad + " casa(s) en " + casilla.getNombre());
                } else {
                    System.out.println("No puedes construir casas aquí.");
                }
                break;
            case "hotel":
                if (casilla.puedeConstruirHotel()) {
                    casilla.construirHotel(jugador);
                    System.out.println("Has construido un hotel en " + casilla.getNombre());
                } else {
                    System.out.println("No puedes construir un hotel aquí.");
                }
                break;
            case "piscina":
                if (casilla.puedeConstruirPiscina()) {
                    casilla.construirPiscina(jugador);
                    System.out.println("Has construido una piscina en " + casilla.getNombre());
                } else {
                    System.out.println("No puedes construir una piscina aquí.");
                }
                break;
            case "pista":
                if (casilla.puedeConstruirPista()) {
                    casilla.construirPista(jugador);
                    System.out.println("Has construido una pista de deporte en " + casilla.getNombre());
                } else {
                    System.out.println("No puedes construir una pista aquí.");
                }
                break;
            default:
                System.out.println("Tipo de edificación no reconocido.");
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

    private Jugador verificarGanador() {
        ArrayList<Jugador> activos = new ArrayList<>();
        for (Jugador j : jugadores) {
            if (j.estaActivo()) {
                activos.add(j);
            }
        }
        return (activos.size() == 1) ? activos.get(0) : null;
    }

    public void verificarGanadorTrasBancarrota() {
        Jugador ganador = verificarGanador();
        if (ganador != null) {
            System.out.println("🎉 ¡" + ganador.getNombre() + " ha ganado la partida! 🎉");
            System.out.println("Fortuna final: " + (long)ganador.getFortuna() + "€");
            System.out.println("Propiedades: " + ganador.getPropiedades().size());
            System.out.println("Gastos totales: " + (long)ganador.getGastos() + "€");
            // Aquí puedes terminar el juego o bloquear más comandos
        }
    }

    public void ofrecerConstruccion(Jugador jugador, Casilla casilla) {
        if (!"solar".equalsIgnoreCase(casilla.getTipo())) return;
        if (!casilla.getDuenho().equals(jugador)) return;

        if (!jugador.poseeGrupoCompleto(casilla, tablero)) {
            System.out.println("No puedes construir aquí. No posees todo el grupo.");
            return;
        }

        System.out.println("¿Quieres construir en " + casilla.getNombre() + "?");

        System.out.println("Opciones disponibles:");

        if (casilla.puedeConstruirCasa(jugador)) System.out.println("1. Construir casas");
        if (casilla.puedeConstruirHotel()) System.out.println("2. Construir hotel");
        if (casilla.puedeConstruirPiscina()) System.out.println("3. Construir piscina");
        if (casilla.puedeConstruirPista()) System.out.println("4. Construir pista de deporte");

        // Aquí puedes leer la opción del usuario y llamar al método correspondiente
    }



}