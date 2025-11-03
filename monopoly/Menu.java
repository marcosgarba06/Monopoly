package monopoly;

import partida.*;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.BufferedReader;
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

    private int contadorCasas = 0;
    private int contadorHoteles = 0;
    private int contadorPiscinas = 0;
    private int contadorPistas = 0;

    private List<Edificacion> edificaciones = new ArrayList<>();


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

        menuSetUp();
        Scanner sc = new Scanner(System.in);
        while (!juegoIniciado) { //si aun no se ha iniciado la partida
            System.out.print("[setup]> ");
            String comando = sc.nextLine();
            analizarComando(comando); // redirige a analizarComandoSetup() mientras no haya empezado el juego
        }
        Carta.resetearContadores();
        Carta.inicializarMazos();
        Carta.setJugadores(jugadores);

        //Para debugear, esto lo comentamos luego
        System.out.println("Cartas inicializadas correctamente con " + jugadores.size() + " jugadores.\n");

        // Cuando hay 2-4 jugadores y se ejecuta 'empezar', arranca la fase de juego
        iniciarJuego();
        // Inicializar los mazos de cartas una sola vez

    }

    public void iniciarJuego() {

        juegoIniciado = true; //El juego ha iniciado correctamente
        Scanner sc = new Scanner(System.in);
        System.out.println("Comandos disponibles:");
        menuComandos();

        while (true) {
            Jugador actual = jugadores.get(turno);
            System.out.println("\nTurno de " + actual.getNombre());
            System.out.print("> ");
            String comando = sc.nextLine();

            analizarComando(comando);
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
        Casilla salida = tablero.encontrarCasilla("Salida"); //Poner en la salida
        Jugador j = new Jugador(nombre, avatarElegido.toLowerCase(Locale.ROOT), salida, avatares);
        Avatar av = j.getAvatar();
        salida.anhadirAvatar(av);
        avatares.add(av);
        jugadores.add(j);

        System.out.println("Jugador " + nombre + " creado con avatar " + avatarElegido.toLowerCase(Locale.ROOT));
        System.out.println("Jugadores totales: " + jugadores.size() + " (mínimo 2, máximo 4)");
    }


    /*Método que interpreta el comando introducido y toma la accion correspondiente.
     * Parámetro: cadena de caracteres (el comando).
    < */
    public void analizarComando(String comando) {

        if (!juegoIniciado) { //Si estamos en el apartado de setup (la partida no empezó se usa otra opcion)
            analizarComandoSetup(comando);
            return;
        }
        String comandoLimpio = comando.trim(); //quita espacio del principio y del final
        String comandoMinusculas = comandoLimpio.toLowerCase(Locale.ROOT);// pone todo en minusculas

        if (procesarComandoDados(comandoLimpio)) { // para procesar el tirar dados con diferentes parametros
            return;
        }

        if (procesarComandoArchivo(comandoLimpio, comandoMinusculas)) { //para comandos desde archivos
            return;
        }

        if (procesarComandoSimple(comandoMinusculas)) {
            return;
        }

        if (procesarComandoConParametros(comandoMinusculas, comandoLimpio)) {
            return;
        }

        System.out.println("Comando no reconocido. Prueba con alguno de estos:");
        menuComandos();

    }


    //Método para analizar comandos en la parte de setup (en la que se pueden crear personajes)
    private void analizarComandoSetup(String comando) {
        String comandoLimpio = comando.trim();
        String comandoMinusculas = comandoLimpio.toLowerCase(Locale.ROOT);
        String[] partes = comandoLimpio.split("\\s+");

        if (procesarComandoArchivo(comandoLimpio, comandoMinusculas)) {
            return;
        }

        if (comandoMinusculas.equals("salir")) {
            IO.println("Saliendo...");
            System.exit(0);
            return;
        }
        if (comandoMinusculas.equals("listar jugadores") || comandoMinusculas.equals("jugadores")) {
            listarJugadores();
            return;
        }

        if (comandoMinusculas.equals("listar avatares")) {
            listarAvatares();
            return;
        }

        if (partes.length >= 4 && partes[0].equalsIgnoreCase("crear") && partes[1].equalsIgnoreCase("jugador")) {
            String nombreJugador = partes[2];
            String avatarElegido = partes[3].toLowerCase(Locale.ROOT);
            crearJugador(nombreJugador, avatarElegido);
        }

        if (comandoMinusculas.equals("empezar")) {
            validarPartida();
            return;
        }
        System.out.println("Estás en modo configuración. Comandos:");
        menuSetUp();
    }

    private boolean procesarComandoDados(String comandoOriginal) {
        Pattern patronDados = Pattern.compile(
                "^(tirar|lanzar)\\s+dados(?:\\s+(\\d)\\s*\\+\\s*(\\d))?$",
                Pattern.CASE_INSENSITIVE
        );

        Matcher coincidencia = patronDados.matcher(comandoOriginal);

        if (!coincidencia.matches()) {
            return false;
        }

        String valorDado1 = coincidencia.group(2);
        String valorDado2 = coincidencia.group(3);

        // Si se especificaron valores para los dados
        if (valorDado1 != null && valorDado2 != null) {
            int dado1 = Integer.parseInt(valorDado1);
            int dado2 = Integer.parseInt(valorDado2);
            lanzarDadosForzados(dado1, dado2);

        } else {
            // Tirada aleatoria normal
            lanzarDados();

        }

        return true;
    }

    private boolean procesarComandoArchivo(String comandoOriginal, String comandoMinusculas) {
        if (!comandoMinusculas.startsWith("comandos")) {
            return false;
        }

        // Dividir en máximo 2 partes: "comandos" + ruta
        String[] partes = comandoOriginal.split("\\s+", 2);

        if (partes.length < 2 || partes[1].isBlank()) {
            System.out.println("Uso: comandos <ruta/al/archivo.txt>");
        } else {
            String rutaArchivo = partes[1].trim();
            ejecutarComandosDesdeArchivo(rutaArchivo);
        }

        return true;
    }

    private boolean procesarComandoSimple(String comandoMinusculas) {

        switch (comandoMinusculas) {
            case "listar jugadores": case "jugadores":
                listarJugadores();
                return true;

            case "ver tablero":
                System.out.println(tablero);
                return true;

            case "salir":
                System.out.println("Saliendo del juego...");
                System.exit(0);
                return true;

            case "jugador":
                indicarTurno();
                return true;

            case "forzar dados":
                // Comando legacy - se mantiene por compatibilidad
                forzarDados();
                System.out.println(tablero);
                return true;

            case "salir carcel":
                Jugador jugadorActual = jugadores.get(turno);
                salirCarcel(jugadorActual);
                return true;

            case "listar enventa":
                listarVenta();
                return true;

            case "listar avatares":
                listarAvatares();
                return true;

            case "acabar turno":
                acabarTurno();
                return true;

            default:
                return false;
        }
    }

    private boolean procesarComandoConParametros(String comandoMinusculas, String comandoOriginal) {
        String[] palabras = comandoMinusculas.split("\\s+");

        // "comprar <propiedad>"
        if (palabras.length == 2 && palabras[0].equals("comprar")) {
            comprar(palabras[1]);
            return true;
        }

        // "describir avatar <id>"
        if (palabras.length == 3 && palabras[0].equals("describir") && palabras[1].equals("avatar")) {
            String idAvatar = palabras[2];
            descAvatar(idAvatar);
            return true;
        }

        // "describir jugador <nombre>"
        if (palabras.length >= 3 && palabras[0].equals("describir") && palabras[1].equals("jugador")) {
            descJugador(palabras);
            return true;
        }

        // "describir <casilla>" (puede tener espacios en el nombre)
        if (palabras.length == 2 && palabras[0].equals("describir")) {
            // Extraer el nombre completo de la casilla (después de "describir ")
            String nombreCasilla = comandoOriginal.substring(comandoOriginal.indexOf(' ') + 1).trim();

            if (nombreCasilla.isEmpty()) {
                System.out.println("Uso: describir <casilla>");
            } else {
                descCasilla(nombreCasilla);
            }
            return true;
        }
        if (palabras.length >= 2 && palabras[0].equals("edificar")) {
            String tipo;

            // Detectar "pista deporte" (2 palabras)
            if (palabras.length >= 3 && palabras[1].equals("pista") && palabras[2].equals("deporte")) {
                tipo = "pista deporte";
            } else {
                tipo = palabras[1];
            }

            edificarPropiedad(tipo);
            return true;
        }

        if(palabras.length == 2 && palabras[0].equals("estadisticas")) {
            mostrarEstadisticasUnJugador(palabras[1]);
        }
        return false;
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
        Casilla casilla = tablero.encontrarCasilla(nombre);
        if (casilla != null) {
            System.out.println(casilla.describir());
        } else {
            System.out.println("No se encontró la casilla '" + nombre + "'.");
        }
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

        // *** CAMBIO CLAVE: Si está en cárcel, NO mostrar menú aquí ***
        if (actual.isEnCarcel() || av.estaEnCarcel()) {
            System.out.println("Estás en la cárcel. Usa el comando 'salir carcel' para intentar salir.");
            return 0;
        }

        // Tirada normal (fuera de cárcel)
        int d1 = dado1.hacerTirada();
        int d2 = dado2.hacerTirada();
        int total = d1 + d2;
        tablero.setUltimaTirada(total);
        System.out.println("Has sacado " + d1 + " y " + d2 + " → total: " + total);

        // Gestión de dobles
        if (d1 == d2) {
            contadorDobles++;
            System.out.println("¡Dados dobles! (" + contadorDobles + " seguidos)");
            if (contadorDobles == 3) {
                System.out.println("¡Tres dobles seguidos! Vas directo a la cárcel.");
                actual.irACarcel(tablero);
                contadorDobles = 0;
                tirado = true;
                return total;
            }
        } else {
            contadorDobles = 0;
        }

        // Mover avatar
        av.moverAvatar(total, tablero);

        // Si caemos en IrCarcel al movernos
        if (actual.isEnCarcel() || av.estaEnCarcel()) {
            System.out.println("Has caído en 'Ir a Cárcel'. Usa 'salir carcel'.");
            tirado = true;
            contadorDobles = 0;
            return total;
        }

        // Marcar tirada completada
        tirado = true;
        if (d1 == d2 && contadorDobles < 3) {
            tirado = false; // Puede volver a tirar
        }

        System.out.println(tablero);
        return total;
    }


    private int lanzarDadosForzados(int d1, int d2) {
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores en la partida.");
            return 0;
        }

        if (tirado) {
            System.out.println("Ya has tirado los dados este turno.");
            return 0;
        }

        if (d1 < 1 || d1 > 6 || d2 < 1 || d2 > 6) {
            System.out.println("Valores inválidos. Usa números entre 1 y 6.");
            return 0;
        }

        Jugador actual = jugadores.get(turno);
        Avatar av = actual.getAvatar();

        // *** CAMBIO: No mostrar menú automáticamente ***
        if (actual.isEnCarcel() || av.estaEnCarcel()) {
            System.out.println("Estás en la cárcel. Usa el comando 'salir carcel'.");
            return 0;
        }

        // Guardar valores en los dados
        dado1.setValor(d1);
        dado2.setValor(d2);

        int total = d1 + d2;
        tablero.setUltimaTirada(total);
        System.out.println("Has forzado " + d1 + " y " + d2 + " → total: " + total);

        // Gestión de dobles
        if (d1 == d2) {
            contadorDobles++;
            System.out.println("¡Dados dobles! (" + contadorDobles + " seguidos)");
            if (contadorDobles == 3) {
                System.out.println("¡Tres dobles seguidos! Vas directo a la cárcel.");
                actual.irACarcel(tablero);
                contadorDobles = 0;
                tirado = true;
                return total;
            }
        } else {
            contadorDobles = 0;
        }

        av.moverAvatar(total, tablero);

        if (actual.isEnCarcel() || av.estaEnCarcel()) {
            System.out.println("Has caído en 'Ir a Cárcel'. Usa 'salir carcel'.");
            tirado = true;
            contadorDobles = 0;
            return total;
        }

        tirado = true;
        if (d1 == d2 && contadorDobles < 3) {
            tirado = false;
        }

        System.out.println(tablero);
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

        // Si está en la cárcel, abrir menú de opciones
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

        System.out.println(tablero);
        // Redirigir a la nueva lógica unificada
        return lanzarDadosForzados(d1, d2);
    }


    /*Método que ejecuta todas las acciones realizadas con el comando 'comprar nombre_casilla'.
     * Parámetro: cadena de caracteres con el nombre de la casilla.
     */
    public void comprar(String nombreCasilla) {
        //Jugador y casilla actuales
        Jugador jugador = jugadores.get(turno);
        Casilla casilla = tablero.encontrarCasilla(nombreCasilla);

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
        jugador.sumarDineroInvertido(casilla.getValor());
        casilla.setDuenho(jugador);
        jugador.anhadirPropiedad(casilla);
        System.out.println("Has comprado " + casilla.getNombre() + " por " + (long) casilla.getValor() + "€.");
    }


    //Método que ejecuta todas las acciones relacionadas con el comando 'salir carcel'.
    private void salirCarcel(Jugador jugador) {
        // Verificar que está en cárcel
        if (!jugador.isEnCarcel() && !jugador.getAvatar().estaEnCarcel()) {
            System.out.println("No estás en la cárcel.");
            return;
        }

        // Verificar que no se haya intentado ya este turno
        if (intentoSalirCarcel) {
            System.out.println("Ya has intentado salir de la cárcel este turno.");
            return;
        }

        Avatar av = jugador.getAvatar();
        int turnosEnCarcel = av.getTurnosEnCarcel();

        System.out.println("\n=== ESTÁS EN LA CÁRCEL ===");
        System.out.println("Turno en cárcel: " + (turnosEnCarcel + 1) + "/3");

        // *** CASO 1: Tercer turno → PAGO OBLIGATORIO ***
        if (turnosEnCarcel >= 2) {
            System.out.println("\n¡Has estado 3 turnos en la cárcel!");
            System.out.println("Debes pagar 500.000€ obligatoriamente y avanzar con tu tirada.");

            if (jugador.getFortuna() < 500000) {
                System.out.println("No tienes dinero suficiente. BANCARROTA.");
                jugador.setBancarrota(true);
                tirado = true;
                intentoSalirCarcel = true;
                return;
            }

            // Pagar y salir
            jugador.restarFortuna(500000);
            jugador.sumarGastos(500000);
            jugador.sumarPagoTasasEImpuestos(500000);
            av.setEnCarcel(false);
            jugador.setEnCarcel(false);
            av.setTurnosEnCarcel(0);
            System.out.println("Has pagado 500.000€ y sales de la cárcel.");

            // Tirar dados y moverse
            int d1 = dado1.hacerTirada();
            int d2 = dado2.hacerTirada();
            int total = d1 + d2;
            tablero.setUltimaTirada(total);
            System.out.println("Tiras los dados: " + d1 + " y " + d2 + " → total: " + total);
            av.moverAvatar(total, tablero);
            System.out.println(tablero);

            tirado = true;
            intentoSalirCarcel = true;
            return;
        }

        // *** CASO 2: Turno 1 o 2 → MENÚ DE OPCIONES ***
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\nOpciones:");
            System.out.println("1) Pagar 500.000€ (sales, pero NO te mueves este turno)");
            System.out.println("2) Usar carta de 'Salir de la cárcel' (sales, pero NO te mueves)");
            System.out.println("3) Intentar sacar dobles (si sacas dobles: sales y mueves)");
            System.out.print("Elige opción: ");

            String opcion = sc.nextLine().trim();

            // OPCIÓN 1: PAGAR
            if (opcion.equals("1")) {
                if (jugador.getFortuna() < 500000) {
                    System.out.println("No tienes suficiente dinero. Elige otra opción.");
                    continue;
                }

                jugador.restarFortuna(500000);
                jugador.sumarGastos(500000);
                jugador.sumarPagoTasasEImpuestos(500000);
                av.setEnCarcel(false);
                jugador.setEnCarcel(false);
                av.setTurnosEnCarcel(0);
                System.out.println("Has pagado 500.000€ y sales de la cárcel.");
                System.out.println("No te mueves este turno. Usa 'acabar turno'.");

                tirado = true; // Bloquea tirar dados
                intentoSalirCarcel = true;
                break;
            }

            // OPCIÓN 2: CARTA
            else if (opcion.equals("2")) {
                if (!jugador.usarCartaSalirCarcel()) {
                    System.out.println("No tienes ninguna carta. Elige otra opción.");
                    continue;
                }

                av.setEnCarcel(false);
                jugador.setEnCarcel(false);
                av.setTurnosEnCarcel(0);
                System.out.println("Has usado una carta y sales de la cárcel.");
                System.out.println("No te mueves este turno. Usa 'acabar turno'.");

                tirado = true; // Bloquea tirar dados
                intentoSalirCarcel = true;
                break;
            }

            // OPCIÓN 3: INTENTAR DOBLES
            else if (opcion.equals("3")) {
                int d1 = dado1.hacerTirada();
                int d2 = dado2.hacerTirada();
                System.out.println("Has sacado: " + d1 + " y " + d2);

                if (d1 == d2) {
                    // ¡DOBLES! → SALE Y SE MUEVE
                    av.setEnCarcel(false);
                    jugador.setEnCarcel(false);
                    av.setTurnosEnCarcel(0);
                    int total = d1 + d2;
                    System.out.println("¡DOBLES! Sales de la cárcel y avanzas " + total + " casillas.");
                    av.moverAvatar(total, tablero);
                    System.out.println(tablero);
                } else {
                    // NO DOBLES → Incrementa turno en cárcel
                    av.incrementarTurnosEnCarcel();
                    System.out.println("No son dobles. Pierdes el turno.");
                    System.out.println("Llevas " + av.getTurnosEnCarcel() + " turno(s) en la cárcel.");
                }

                tirado = true;
                intentoSalirCarcel = true;
                break;
            } else {
                System.out.println("Opción no válida. Elige 1, 2 o 3.");
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

        System.out.println("$> jugador");
        System.out.println("{");
        System.out.println("nombre: " + actual.getNombre() + ",");
        System.out.println("avatar: " + actual.getAvatar().getId());
        System.out.println("}");

        // *** SOLO INFORMAR si está en cárcel ***
        if (actual.isEnCarcel() || actual.getAvatar().estaEnCarcel()) {
            System.out.println("\n  Estás en la cárcel. Usa 'salir carcel' para intentar salir.");
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
        for (Casilla c : enVenta) {
            //for each para encontrar las casillas en venta e imprimirlas
            System.out.println("{");
            System.out.println("nombre: " + c.getNombre() + ",");
            System.out.println("tipo: " + c.getTipo() + ",");
            if ("solar".equalsIgnoreCase(c.getTipo())) {
                System.out.println("grupo: " + c.getGrupo() + ",");
            }
            System.out.println("valor: " + (long) c.getValor());
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
            System.out.println("No puedes acabar el turno sin haber tirado los dados o intentado salir de la cárcel.");
            return;
        }

        if (contadorDobles > 0) {
            System.out.println("Has sacado dobles. Debes volver a tirar antes de acabar el turno.");
            return;
        }

        // Contar jugadores activos
        int jugadoresActivos = 0;
        for (Jugador j : jugadores) {
            if (!j.isBancarrota()) {
                jugadoresActivos++;
            }
        }

        // Si solo queda un jugador activo, terminar el juego
        if (jugadoresActivos <= 1) {
            System.out.println("¡La partida ha terminado!");
            return;
        }

        // Avanzar al siguiente jugador activo
        if (!repetirTurno) {
            int intentos = 0;
            int maxIntentos = jugadores.size(); // Evitar bucle infinito

            do {
                turno = (turno + 1) % jugadores.size();
                intentos++;

                if (intentos >= maxIntentos) {
                    System.out.println("Error: No se encontró jugador activo.");
                    return;
                }
            } while (jugadores.get(turno).isBancarrota());
        }

        // Resetear flags
        tirado = false;
        repetirTurno = false;
        contadorDobles = 0;
        intentoSalirCarcel = false;

        Jugador actual = jugadores.get(turno);
        System.out.println("Turno acabado. Ahora le toca a:");
        System.out.println("$> jugador");
        System.out.println("{");
        System.out.println("nombre: " + actual.getNombre() + ",");
        System.out.println("avatar: " + actual.getAvatar().getId());
        System.out.println("}");

        if (actual.isEnCarcel() || actual.getAvatar().estaEnCarcel()) {
            System.out.println("CUIDADO " + actual.getNombre() + " está en la cárcel.");
        }
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
            System.out.println("Fortuna final: " + (long) ganador.getFortuna() + "€");
            System.out.println("Propiedades: " + ganador.getPropiedades().size());
            System.out.println("Gastos totales: " + (long) ganador.getGastos() + "€");
            // Aquí puedes terminar el juego o bloquear más comandos
        }
    }

    public List<Jugador> getJugadores() {
        return this.jugadores;
    }

    public void menuComandos() {
        System.out.println("  - 'listar jugadores' / 'jugadores'");
        System.out.println("  - 'jugador' (ver turno actual)");
        System.out.println("  - 'tirar dados' (tirada aleatoria)");
        System.out.println("  - 'tirar dados X+Y' o 'lanzar dados X+Y' (forzar valores 1-6)");
        System.out.println("  - 'acabar turno'");
        System.out.println("  - 'describir <casilla>'");
        System.out.println("  - 'ver tablero'");
        System.out.println("  - 'describir jugador <nombre>'");
        System.out.println("  - 'describir avatar X'");
        System.out.println("  - 'listar enventa' (casillas disponibles)");
        System.out.println("  - 'listar avatares'");
        System.out.println("  - 'comprar <casilla>'");
        System.out.println("  - 'estadisticas <jugador>'");
        System.out.println("  - 'salir carcel'");
        System.out.println("  - 'edificar <tipo>'");
        System.out.println("  - 'comandos <ruta/al/archivo.txt>' (ejecutar comandos desde archivo)");
        System.out.println("  - 'salir' (cerrar el juego)");
    }

    private void menuSetUp() {
        System.out.println("Modo configuración de partida:"); //comandos que se pueden usar si la partida no está empezada
        System.out.println("- 'crear jugador <nombre> <avatar>' (avatares: sombrero, coche, esfinge, pelota)");
        System.out.println("- 'comandos <ruta/al/archivo.txt>' (ejecutar comandos desde archivo)");
        System.out.println("- 'listar jugadores'");
        System.out.println("- 'empezar' (requiere entre 2 y 4 jugadores)");
        System.out.println("- 'salir'");

    }

    private void validarPartida() {
        if (jugadores.size() < 2) {
            System.out.println("Debes crear al menos 2 jugadores para empezar.");
        } else if (jugadores.size() > 4) {
            System.out.println("No puedes tener más de 4 jugadores.");
        } else {
            System.out.println("Iniciando partida...");
            juegoIniciado = true;
        }
    }
    private void edificarPropiedad(String tipo) {
        Jugador jugadorActual = jugadores.get(turno);
        Avatar avatar = jugadorActual.getAvatar();
        Casilla casilla = avatar.getCasilla();

        // Verificar que es un solar
        if (!"solar".equalsIgnoreCase(casilla.getTipo())) {
            System.out.println("No puedes edificar en esta casilla.");
            return;
        }

        // Verificar que el jugador es dueño
        if (casilla.getDuenho() == null || !casilla.getDuenho().equals(jugadorActual)) {
            System.out.println("No eres dueño de " + casilla.getNombre());
            return;
        }

        // Verificar que posee todo el grupo
        if (!jugadorActual.poseeGrupoCompleto(casilla, tablero)) {
            System.out.println("No puedes construir aquí. No posees todo el grupo " +
                    casilla.getGrupo().getNombre() + ".");
            return;
        }

        // Verificar que no esté hipotecada
        if (casilla.estaHipotecada()) {
            System.out.println("No puedes edificar en una casilla hipotecada.");
            return;
        }

        String tipoNormalizado = tipo.toLowerCase().trim();

        switch (tipoNormalizado) {
            case "casa":
                edificarCasa(jugadorActual, casilla);
                break;

            case "hotel":
                edificarHotel(jugadorActual, casilla);
                break;

            case "piscina":
                edificarPiscina(jugadorActual, casilla);
                break;

            case "pista":
            case "pista deporte":
            case "pista_deporte":
                edificarPista(jugadorActual, casilla);
                break;

            default:
                System.out.println("Tipo de edificación no válido. Usa: casa, hotel, piscina o pista deporte");
                break;
        }
    }

    private void edificarCasa(Jugador jugador, Casilla casilla) {
        // Verificar que no hay hotel
        if (casilla.tieneHotel()) {
            System.out.println("No se puede edificar ningún edificio más en esta casilla ni en el grupo al que la casilla pertenece.");
            return;
        }

        // Verificar que no hay 4 casas
        if (casilla.getNumCasas() >= 4) {
            System.out.println("No se puede edificar ningún edificio más en esta casilla ni en el grupo al que la casilla pertenece.");
            return;
        }

        // Verificar dinero
        float coste = casilla.getPrecioCasa();
        if (jugador.getFortuna() < coste) {
            System.out.println("La fortuna de " + jugador.getNombre() +
                    " no es suficiente para edificar una casa en la casilla " +
                    casilla.getNombre() + ".");
            return;
        }

        // Construir
        casilla.construirCasas(jugador, 1);

        // Generar ID automático (ejemplo: casa-1, casa-2, etc.)
        int numCasa = casilla.getNumCasas();
        String idEdificio = "casa-" + numCasa;

        System.out.println("Se ha edificado una casa en " + casilla.getNombre() +
                ". La fortuna de " + jugador.getNombre() +
                " se reduce en " + (long)coste + "€.");
    }

    private void edificarHotel(Jugador jugador, Casilla casilla) {
        // Verificar que hay exactamente 4 casas
        if (casilla.getNumCasas() != 4) {
            System.out.println("No se puede edificar un hotel. Necesitas exactamente 4 casas.");
            return;
        }

        // Verificar que no hay hotel ya
        if (casilla.tieneHotel()) {
            System.out.println("No se puede edificar ningún edificio más en esta casilla ni en el grupo al que la casilla pertenece.");
            return;
        }

        // Verificar dinero
        float coste = casilla.getPrecioHotel();
        if (jugador.getFortuna() < coste) {
            System.out.println("La fortuna de " + jugador.getNombre() +
                    " no es suficiente para edificar un hotel en la casilla " +
                    casilla.getNombre() + ".");
            return;
        }

        // Construir
        casilla.construirHotel(jugador);

        System.out.println("Se ha edificado un hotel en " + casilla.getNombre() +
                ". La fortuna de " + jugador.getNombre() +
                " se reduce en " + (long)coste + "€.");
    }

    private void edificarPiscina(Jugador jugador, Casilla casilla) {
        // Verificar que hay hotel
        if (!casilla.tieneHotel()) {
            System.out.println("No se puede edificar una piscina, ya que no se dispone de un hotel.");
            return;
        }

        // Verificar que no hay piscina ya
        if (casilla.tienePiscina()) {
            System.out.println("No se puede edificar ningún edificio más en esta casilla ni en el grupo al que la casilla pertenece.");
            return;
        }

        // Verificar dinero
        float coste = casilla.getPrecioPiscina();
        if (jugador.getFortuna() < coste) {
            System.out.println("La fortuna de " + jugador.getNombre() +
                    " no es suficiente para edificar una piscina en la casilla " +
                    casilla.getNombre() + ".");
            return;
        }

        // Construir
        casilla.construirPiscina(jugador);

        System.out.println("Se ha edificado una piscina en " + casilla.getNombre() +
                ". La fortuna de " + jugador.getNombre() +
                " se reduce en " + (long)coste + "€.");
    }

    private void edificarPista(Jugador jugador, Casilla casilla) {
        // Verificar que hay hotel y piscina
        if (!casilla.tieneHotel() || !casilla.tienePiscina()) {
            System.out.println("No se puede edificar una pista de deporte, ya que no se dispone de un hotel y una piscina.");
            return;
        }

        // Verificar que no hay pista ya
        if (casilla.tienePista()) {
            System.out.println("No se puede edificar ningún edificio más en esta casilla ni en el grupo al que la casilla pertenece.");
            return;
        }

        // Verificar dinero
        float coste = casilla.getPrecioPista();
        if (jugador.getFortuna() < coste) {
            System.out.println("La fortuna de " + jugador.getNombre() +
                    " no es suficiente para edificar una pista de deporte en la casilla " +
                    casilla.getNombre() + ".");
            return;
        }

        // Construir
        casilla.construirPista(jugador);

        System.out.println("Se ha edificado una pista de deporte en " + casilla.getNombre() +
                ". La fortuna de " + jugador.getNombre() +
                " se reduce en " + (long)coste + "€.");
    }

    private void mostrarEstadisticasUnJugador(String nombreJugador) {
        for (Jugador j : jugadores) {
            if (j.getNombre().equalsIgnoreCase(nombreJugador)) {
                System.out.println("$> estadisticas " + j.getNombre());
                System.out.println("{");
                System.out.println("dineroInvertido: " + (long) j.getDineroInvertido() + ",");
                System.out.println("pagoTasasEImpuestos: " + (long) j.getPagoTasasEImpuestos() + ",");
                System.out.println("pagoDeAlquileres: " + (long) j.getPagoDeAlquileres() + ",");
                System.out.println("cobroDeAlquileres: " + (long) j.getCobroDeAlquileres() + ",");
                System.out.println("pasarPorCasillaDeSalida: " + (long) j.getPasarPorCasillaDeSalida() + ",");
                System.out.println("premiosInversionesOBote: " + (long) j.getPremiosInversionesOBote() + ",");
                System.out.println("vecesEnLaCarcel: " + j.getVecesEnLaCarcel());
                System.out.println("}");
                return;
            }
        }
        System.out.println("No se encontró ningún jugador con el nombre '" + nombreJugador + "'.");
    }



}