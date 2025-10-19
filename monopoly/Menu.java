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

public class Menu { // la clase menu

    // Atributos
    private Tablero tablero;
    private Jugador banca;
    private ArrayList<Jugador> jugadores; //lista de jugadores
    private ArrayList<Avatar> avatares; //lista de avatares

    //private int lanzamientos;
    private Dado dado1;
    private Dado dado2;

    private int turno = 0;
    private boolean tirado = false;

    //private boolean solvente;
    private int contadorDobles = 0;
    private boolean repetirTurno = false;
    private boolean intentoSalirCarcel = false;

    //False: Juego en estado de Setup (crear personajes y esas cositas)
    //True: Empieza la partida y ya no se pueden crear jugadores
    private boolean juegoIniciado = false;


    // Sets para evitar nombres y avatares duplicados
    private final Set<String> nombresUsados = new HashSet<>();
    private final Set<String> avataresUsados = new HashSet<>();

    //los avatares que estan permitidos
    private final String[] avataresPermitidos = {"coche", "sombrero", "pelota", "esfinge"};


    public Menu() {
        this.banca = new Jugador();
        this.tablero = new Tablero(banca);
        this.tablero.setMenu(this);
        this.jugadores = new ArrayList<>();
        this.avatares = new ArrayList<>();
        this.dado1 = new Dado();
        this.dado2 = new Dado();
    }

    public void iniciarPartida() {

        System.out.println("Modo configuración de partida:"); //comandos que se pueden usar si la partida no está empezada
        System.out.println("- 'crear jugador <nombre> <avatar>' (avatares: sombrero, coche, esfinge, pelota)");
        System.out.println("- 'comandos <ruta/al/archivo.txt>' (ejecutar comandos desde archivo)");
        System.out.println("- 'listar jugadores'");
        System.out.println("- 'empezar' (requiere entre 2 y 4 jugadores)");
        System.out.println("- 'salir'");

        Scanner sc = new Scanner(System.in);
        while (!juegoIniciado) { //si aun no se ha iniciado la partida
            System.out.print("[setup]> ");
            String comando = sc.nextLine();
            analizarComando(comando); // redirige a analizarComandoSetup() mientras no haya empezado el juego
        }

        // Cuando hay 2-4 jugadores y se ejecuta 'empezar', arranca la fase de juego
        iniciarJuego();
    }

    public void iniciarJuego() {
        juegoIniciado = true; //El juego ha iniciado correctamente
        Scanner sc = new Scanner(System.in);
        System.out.println("¡Comienza el juego!");
        System.out.println("Comandos disponibles:");
        System.out.println("  - 'listar jugadores' / 'jugadores'");
        System.out.println("  - 'jugador' (ver turno actual)");
        System.out.println("  - 'ver tablero'");
        System.out.println("  - 'tirar dados'");
        System.out.println("  - 'forzar dados'");
        System.out.println("  - 'acabar turno'");
        System.out.println("  - 'describir <casilla>'");
        System.out.println("  - 'describir jugador <nombre>'");
        System.out.println("  - 'describir avatarX'");
        System.out.println("  - 'listar venta' (casillas disponibles)");
        System.out.println("  - 'listar avatares'");
        System.out.println("  - 'comprar <casilla>'");
        System.out.println("  - 'salir carcel'");
        System.out.println("  - 'comandos <ruta/al/archivo.txt>' (ejecutar comandos desde archivo)");
        System.out.println("  - 'salir' (cerrar el juego)");

        while (true) {

            System.out.println("TABLERO:"); //mostrar el tablero y quien tiene el turno todo el tiempo
            System.out.println("-----------------------------------");
            System.out.println(tablero);
            System.out.println("-----------------------------------");
            Jugador actual = jugadores.get(turno);
            System.out.println("\nTurno de " + actual.getNombre());
            System.out.print("> ");
            String comando = sc.nextLine();

            analizarComando(comando);

        }
    }

    /*Método que interpreta el comando introducido y toma la accion correspondiente.
     * Parámetro: cadena de caracteres (el comando).
    < */
    public void analizarComando(String comando) {
        if (!juegoIniciado) { //Si estamos en el apartado de setup (la partida no empezó se usa otra opcion)
            analizarComandoSetup(comando);
            return;
        }
        String comandoOriginal = comando.trim(); //quita espacio del principio y del final
        String comandoLC = comandoOriginal.toLowerCase(Locale.ROOT);// pone todo en minusculas

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

        //Implementaciones de los comandos que se pueden usar
        if (comandoLC.equals("listar jugadores") || comandoLC.equals("jugadores")) {
            listarJugadores();
        } else if (comandoLC.equals("ver tablero")) {
            System.out.println(tablero);
        } else if (comandoLC.equals("salir")) {
            System.out.println("Saliendo del juego...");
            System.exit(0);
        } else if (comandoLC.equals("jugador")) {
            indicarTurno();
        } else if (comandoLC.equals("tirar dados")) {
            lanzarDados();
        } else if (comandoLC.equals("forzar dados")) {
            forzarDados();
        } else if (partes.length == 2 && partes[0].equals("comprar")) {
            comprar(partes[1]);
        } else if (comandoLC.equals("salir carcel")) {
            Jugador actual = jugadores.get(turno);
            salirCarcel(actual);
        } else if (comandoLC.equals("listar venta")) {
            listarVenta();
        }else if (partes.length == 3 && partes[0].equals("describir") && partes[1].equals("avatar")) {
            String id = partes[2].trim();
            descAvatar(id);
        } else if (comandoLC.equals("listar avatares")) {
            listarAvatares();
        } else if (partes.length >= 3 && partes[0].equals("describir") && partes[1].equals("jugador")) {
            descJugador(partes);
        } else if (partes.length == 2 && partes[0].equals("describir")) {
            String nombreCasilla = comandoOriginal.substring(comandoOriginal.indexOf(' ') + 1).trim(); //Coge todo lo que hay despies de la separacion como nombre de la casilla
            if (nombreCasilla.isEmpty()) {
                System.out.println("Uso: describir <casilla>");
            } else {
                descCasilla(nombreCasilla);
            }
        } else if (comandoLC.equals("acabar turno")) {
            acabarTurno();
        } else {
            System.out.println("Comando no reconocido. Prueba con alguno de estos:");
            System.out.println("  - 'listar jugadores' / 'jugadores'");
            System.out.println("  - 'jugador' (ver turno actual)");
            System.out.println("  - 'tirar dados'");
            System.out.println("  - 'forzar dados'");
            System.out.println("  - 'acabar turno'");
            System.out.println("  - 'describir <casilla>'");
            System.out.println("  - 'ver tablero'");
            System.out.println("  - 'describir jugador <nombre>'");
            System.out.println("  - 'describir avatar X'");
            System.out.println("  - 'listar venta' (casillas disponibles)");
            System.out.println("  - 'listar avatares'");
            System.out.println("  - 'comprar <casilla>'");
            System.out.println("  - 'salir carcel'");
            System.out.println("  - 'comandos <ruta/al/archivo.txt>' (ejecutar comandos desde archivo)");
            System.out.println("  - 'salir' (cerrar el juego)");
        }
    }

    //metodo para crear un jugador con su avatar asociado
    private void crearJugador(String nombre, String avatarElegido) {
        if (jugadores.size() >= 4) { //no crea mas si hay 4
            System.out.println("Ya hay 4 jugadores. No se pueden crear más.");
            return;
        }
        if (nombre == null || nombre.isBlank()) {
            System.out.println("El nombre no puede estar vacío.");
            return;
        }

        boolean valido = false;
        for (String avatar : avataresPermitidos) { //un for each para recorrer los avatares permitidos y ver si valen o no
            if (avatar.equalsIgnoreCase(avatarElegido)) {
                valido = true;
                break;
            }
        }
        if (!valido) {
            System.out.println("Avatar no válido. Avatares permitidos: coche, sombrero, pelota, esfinge");
            return;
        }

        if (!nombresUsados.add(nombre.toLowerCase(Locale.ROOT))) { //garantiza que el nombre es unico
            System.out.println("Ese nombre ya está en uso. Elige otro.");
            return;
        }
        if (!avataresUsados.add(avatarElegido.toLowerCase(Locale.ROOT))) {
            System.out.println("Ese avatar ya está en uso. Elige otro.");
            nombresUsados.remove(nombre.toLowerCase(Locale.ROOT));
            //Si esta usado se quita de la memoria que guarda los nombres usados
            return;
        }

        //Poner en la salida
        Casilla salida = tablero.encontrar_casilla("Salida"); //Poner en la salida
        Jugador j = new Jugador(nombre, avatarElegido.toLowerCase(Locale.ROOT), salida, avatares);
        Avatar av = j.getAvatar();
        salida.anhadirAvatar(av);
        avatares.add(av);
        jugadores.add(j);

        System.out.println("Jugador " + nombre + " creado con avatar " + avatarElegido.toLowerCase(Locale.ROOT));
        System.out.println("Jugadores totales: " + jugadores.size() + " (mínimo 2, máximo 4)");
    }

    //Método para analizar comandos en la parte de setup (en la que se pueden crear personajes)
    private void analizarComandoSetup(String comando) {
        String comandoOriginal = comando.trim();
        String comandoOriginalLc = comandoOriginal.toLowerCase(Locale.ROOT);
        String[] partes = comandoOriginal.split("\\s+");

        if (comandoOriginalLc.startsWith("comandos")) {
            String[] partesCmd = comandoOriginal.split("\\s+", 2);
            if (partesCmd.length < 2 || partesCmd[1].isBlank()) {
                System.out.println("Uso: comandos <ruta/al/archivo.txt>");
            } else {
                String ruta = partesCmd[1].trim();
                ejecutarComandosDesdeArchivo(ruta);
            }
            return;
        }

        if (comandoOriginalLc.startsWith("salir")) {
            IO.println("Saliendo...");
            System.exit(0);
        }else if (comandoOriginalLc.equals("listar jugadores") || comandoOriginalLc.equals("jugadores")) {
            listarJugadores();
        } else if (comandoOriginalLc.equals("listar avatares")) {
            listarAvatares();
        } else if (partes.length >= 4 && partes[0].equalsIgnoreCase("crear") && partes[1].equalsIgnoreCase("jugador")) {
            String nombre = partes[2];
            String avatarElegido = partes[3].toLowerCase(Locale.ROOT);
            crearJugador(nombre, avatarElegido);
        } else if (comandoOriginalLc.equals("empezar")) {
            if (jugadores.size() < 2) {
                System.out.println("Debes crear al menos 2 jugadores para empezar.");
            } else if (jugadores.size() > 4) {
                System.out.println("No puedes tener más de 4 jugadores.");
            } else {
                System.out.println("Iniciando partida...");
                juegoIniciado = true;
            }
        } else {
            System.out.println("Estás en modo configuración. Comandos:");
            System.out.println("- 'crear jugador <nombre> <avatar>'");
            System.out.println("- 'listar jugadores' | 'jugadores'");
            System.out.println("- 'listar avatares'");
            System.out.println("- 'comandos <ruta/al/archivo.txt>'");
            System.out.println("- 'empezar' (con 2-4 jugadores)");
            System.out.println("- 'salir'");
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
        // Abrir un BufferedReader con UTF-8 (se cierra solo)
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String linea;
            int numLinea = 0;

            // Leer línea a línea hasta que no haya más
            while ((linea = br.readLine()) != null) {
                numLinea++;
                String comando = linea.strip(); //Elimina los espacios al final

                if (comando.isEmpty()) continue; // Ignora líneas vacías
                System.out.println("[archivo:" + numLinea + "] " + comando); //Mostrar que comando que ha leído e intentará usar
                analizarComando(comando);
            }
        } catch (IOException e) {
            // Captura errores y los muestra
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

        for (Jugador j : jugadores) { //un for each para encontrar el jugador entre los que hay en la partida

            if (j.getNombre().toLowerCase().equals(nombreBuscado)) { //si lo encuentra lo imprime
                System.out.println("$> describir jugador " + j.getNombre());
                System.out.println("{");
                System.out.println("nombre: " + j.getNombre() + ",");
                System.out.println("avatar: " + j.getAvatar().getId() + ",");
                System.out.println("fortuna: " + (long) j.getFortuna() + ",");
                System.out.println("Cartas para salir de la cárcel: " + j.getCartasSalirCarcel());



                if (j.getPropiedades().isEmpty()) {
                    System.out.println("propiedades: -,");
                } else {
                    String props = j.getPropiedades().stream().map(c -> c.getNombre() + (c.estaHipotecada() ? " (hipotecada)" : "")).collect(Collectors.joining(", "));
                    System.out.println("propiedades: [" + props + "],");
                }

                System.out.println("hipotecas: -,");
                System.out.print("edificios: -");
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

        for (Avatar av : avatares) { //for each para recorrer los avatares de los jugadores creados
            if (av.getId().equalsIgnoreCase(ID)) { //si lo encuentra imprime su tipo, el jugador y la casilla en la que esta
                System.out.println("$> describir avatar " + ID);
                System.out.println("{");
                System.out.println("tipo: " + av.getJugador().getAvatar().getTipo() + ",");
                System.out.println("jugador: " + av.getJugador().getNombre() + ",");
                System.out.println("casilla: " + (av.getCasilla() != null ? av.getCasilla().getNombre() : "sin posición") + ",");
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
        //Busca la casilla en el tablero
        Casilla casilla = tablero.encontrar_casilla(nombre);
        if (casilla != null) {
            System.out.println(casilla.describir());
        } else {
            System.out.println("No se encontró la casilla '" + nombre + "'.");
        }
    }

    //Método usado para tirar los dados
    private int lanzarDados() {
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores en la partida.");
            return 0;
        }

        if (tirado) { //no permite volver a tirar si ya se han tirado los dados
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

        //Tirada aleatoria de los dados
        int d1 = dado1.hacerTirada();
        int d2 = dado2.hacerTirada();
        int total = d1 + d2;
        tablero.setUltimaTirada(total);
        System.out.println("Has sacado " + d1 + " y " + d2 + " → total: " + total);

        //Gestor de dobles y contador del numero que salen
        if (d1 == d2) {
            contadorDobles++;
            System.out.println("¡Dados dobles! (" + contadorDobles + " seguidos)");
            if (contadorDobles == 3) { //3 dobles a la carcel
                System.out.println("¡Tres dobles seguidos! Vas directo a la cárcel.");
                actual.irACarcel(tablero);
                contadorDobles = 0;
                salirCarcel(actual);
                return total;
            }
        } else {
            contadorDobles = 0; //Reinicia el contador de dobles si no hay mas
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

    private int forzarDados() {
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

        Scanner sc = new Scanner(System.in);
        int d1, d2;

        // Pedir valor del dado 1
        while (true) {
            System.out.print("Forzar valor dado1 (1-6): ");
            d1 = sc.nextInt();
            if (d1 >= 1 && d1 <= 6) break;
            System.out.println("Valor inválido, debe estar entre 1 y 6.");
        }

        // Pedir valor del dado 2
        while (true) {
            System.out.print("Forzar valor dado2 (1-6): ");
            d2 = sc.nextInt();
            if (d2 >= 1 && d2 <= 6) break;
            System.out.println("Valor inválido, debe estar entre 1 y 6.");
        }

        // Guardar valores en los dados
        dado1.setValor(d1);
        dado2.setValor(d2);

        int total = d1 + d2;
        tablero.setUltimaTirada(total);
        System.out.println("Has forzado " + d1 + " y " + d2 + " → total: " + total);

        // Reglas de dobles
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

        // Mover avatar
        av.moverAvatar(total, tablero);

        // Si al movernos hemos acabado en cárcel, abrir menú ya
        if (actual.isEnCarcel() || (av != null && av.estaEnCarcel())) {
            salirCarcel(actual);
            return total;
        }

        // Marcar tirada
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
        //Jugador y casilla actuales
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
        if (!jugador.isEnCarcel() && !jugador.getAvatar().estaEnCarcel()) { //Si no esta en la carcel no puedes salir
            System.out.println("No estás en la cárcel. No necesitas salir.");
            return;
        }
        Avatar av = jugador.getAvatar();

        if (intentoSalirCarcel) { //Solo puedes hacer un intento en este turno
            System.out.println("Ya has intentado salir de la cárcel este turno.");
            return;
        }
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
                    intentoSalirCarcel = true;
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
                    intentoSalirCarcel = true;
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
                    intentoSalirCarcel = true;
                } else {
                    av.incrementarTurnosEnCarcel();
                    System.out.println("No son dobles. Permaneces en la cárcel y pierdes este turno.");
                    System.out.println("Turnos en cárcel: " + av.getTurnosEnCarcel() + "/3");
                    intentoSalirCarcel = true;
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
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores en la partida.");
            return;
        }

        Jugador actual = jugadores.get(turno);
        intentoSalirCarcel = false; // reinicia el intento al comenzar turno

        System.out.println("$> jugador");
        System.out.println("{");
        System.out.println("nombre: " + actual.getNombre() + ",");
        System.out.println("avatar: " + actual.getAvatar().getId());
        System.out.println("}");

        // Si está en la cárcel, ejecutar automáticamente salirCarcel
        if (actual.isEnCarcel() || actual.getAvatar().estaEnCarcel()) {
            salirCarcel(actual); // solo se ejecuta una vez por turno
        }
    }

    //Metodo que muestra las casillas que están a la venta actualmente
    private void listarVenta() {
        ArrayList<Casilla> enVenta = tablero.getCasillasEnVenta();

        if (enVenta.isEmpty()) {
            System.out.println("No hay propiedades en venta.");
            return;
        }

        System.out.println("$> listar enventa");
        for (Casilla c : enVenta) { //for each para encontrar las casillas en venta e imprimirlas
            System.out.println("{");
            System.out.println("nombre: " + c.getNombre() + ",");
            System.out.println("tipo: " + c.getTipo() + ",");
            if ("solar".equalsIgnoreCase(c.getTipo())) {
                System.out.println("grupo: " + c.getGrupo() + ",");
            }
            System.out.println("valor: " + (long)c.getValor());
            System.out.println("},");
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
            // toString del Avatar debe ser informativo; en caso contrario, imprimir id y tipo por separado
            System.out.println("avatar: " + (j.getAvatar() != null ? j.getAvatar().toString() : "-") + ",");
            System.out.println("fortuna: " + (long) j.getFortuna() + ",");

            if (j.getPropiedades().isEmpty()) {
                System.out.println("propiedades: -,");
            } else {
                String props = j.getPropiedades().stream().map(Casilla::getNombre).collect(Collectors.joining(", "));
                System.out.println("propiedades: [" + props + "],");
            }

            // Por ahora, imprime guiones para hipotecas y edificios
            System.out.println("hipotecas: -,");
            System.out.println("edificios: -");

            System.out.print("}");
            if (i < jugadores.size() - 1) {
                System.out.println(","); //separa los jugadores con ","
            } else {
                System.out.println();  // Última entrada sin coma final
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
        for (Avatar av : avatares) { //busca los avatares y los va imprimiendo con un for each
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
        if (!tirado) {
            // Impide acabar turno sin haber tirado (salvo que reglas específicas lo permitan)
            System.out.println("No puedes acabar el turno sin tirar los dados.");
            return;
        }

        if (contadorDobles > 0) {
            // Si ha sacado dobles debe tirar de nuevo
            System.out.println("Has sacado dobles. Debes volver a tirar antes de acabar el turno.");
            return;
        }

        if (!repetirTurno) {
            // Avanza el turno al siguiente jugador activo (omite bancarrotas)
            do {
                turno = (turno + 1) % jugadores.size();
            } while (jugadores.get(turno).isBancarrota());
        }

        // Reset de estado de tirada y dobles para el siguiente jugador
        tirado = false;
        repetirTurno = false;
        contadorDobles = 0;
        // Nuevo jugador al que le toca
        Jugador actual = jugadores.get(turno);
        System.out.println("Turno acabado. Ahora le toca a:");
        System.out.println("$> jugador");
        System.out.println("{");
        System.out.println("nombre: " + actual.getNombre() + ",");
        System.out.println("avatar: " + actual.getAvatar().getId());
        System.out.println("}");
    }

    // Determina si hay un único jugador activo (no en bancarrota) y lo retorna como ganador.
    private Jugador verificarGanador() {
        ArrayList<Jugador> activos = new ArrayList<>();
        for (Jugador j : jugadores) {
            if (j.estaActivo()) {
                activos.add(j);
            }
        }
        return (activos.size() == 1) ? activos.get(0) : null; //si hay un solo jugador no en bancarrota lo devuelve, si no devuelve null
    }

    //Comprueba si hay ganador, es decir si queda uno solo sin bancarrota, en caso de que haya uno imprime los resultados finales
    public void verificarGanadorTrasBancarrota() {
        Jugador ganador = verificarGanador(); //en la funcion verificar ganador, si solo queda uno devuleve el nombre de ese jugador, si no null
        if (ganador != null) {
            System.out.println("¡" + ganador.getNombre() + " ha ganado la partida!");
            System.out.println("Fortuna final: " + (long)ganador.getFortuna() + "€");
            System.out.println("Propiedades: " + ganador.getPropiedades().size());
            System.out.println("Gastos totales: " + (long)ganador.getGastos() + "€");
            // Aquí puedes terminar el juego o bloquear más comandos
        }
    }
}