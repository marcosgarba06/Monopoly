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

//        ^(tirar|lanzar)         // El comando debe empezar por "tirar" o "lanzar"
//        \\s+                    // Uno o más espacios
//        dados                   // Literal "dados"
//        (?:                     // Inicio de grupo opcional (no captura)
//        \\s+                 // Uno o más espacios
//        (\\d)                // Primer número (dado1), un solo dígito
//        \\s*\\+\\s*          // El signo + con espacios opcionales
//        (\\d)                // Segundo número (dado2), un solo dígito
//        )?                      // Fin del grupo opcional
//        $                       // Fin de la línea
//

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
            case "listar jugadores":
            case "jugadores":
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
            case "listar edificios":
                listarEdificaciones();
                return true;

            case "acabar turno":
                acabarTurno();
                return true;

            case "estadisticas juego":
                mostrarEstadisticasJuego();
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
        if (palabras.length == 4 && palabras[0].equals("vender")) {
            // tipo de edificación: casas | hotel | piscina | pista
            String tipo = palabras[1];

            // nombre de la casilla: puede venir como "Sol5"
            // Nota: procesarComandoConParametros usa comandoMinusculas
            String nombreCasilla = palabras[2];

            // cantidad a vender (entero)
            int cantidad;
            // Pasar a entero el numero a vender
            try {
                cantidad = Integer.parseInt(palabras[3]);
            } catch (NumberFormatException e) {
                System.out.println("Cantidad inválida. Debe ser un entero positivo.");
                return true; // comando reconocido y gestionado con error
            }

            vender(tipo, nombreCasilla, cantidad);
            return true;
        }

        /*// Comando: vender <tipo> <casilla> <cantidad>
        // - Acepta tipo: casas | hotel | piscina | pista
        // - La casilla puede venir como "SolX" o como "SolarX" (se normaliza dentro del método vender)
        if (comandoMinusculas.startsWith("vender ")) {
            String[] p = comandoOriginal.trim().split("\\s+");
            if (p.length == 4) {
                String tipo = p[1];     // casas | hotel | piscina | pista
                String casilla = p[2];  // nombre de casilla (Sol5 / Solar5 / ...)
                int cantidad;
                try {
                    cantidad = Integer.parseInt(p[3]); // validamos que la cantidad sea numérica
                } catch (NumberFormatException e) {
                    System.out.println("Cantidad inválida. Debe ser un entero positivo.");
                    return true; // hemos manejado este comando
                }
                vender(tipo, casilla, cantidad); // ejecutamos la venta
                return true; // comando reconocido
            } else {
                // Ayuda de uso si el número de parámetros no es el correcto
                System.out.println("Uso: vender <casas|hotel|piscina|pista> <casilla> <cantidad>");
                return true;
            }
        }*/

        if(palabras.length == 2 && palabras[0].equals("deshipotecar")){
            String nombreCasilla = palabras[1];
            deshipotecar(nombreCasilla);
            return true;
        }
        if (palabras.length == 3 && palabras[0].equals("listar") && palabras[1].equals("edificios")) {
            String nombreGrupo = palabras[2];
            listarEdificiosGrupo(nombreGrupo);
            return true;
        }

        // "listar edificios" (sin grupo, lista todos)
        if (palabras.length == 2 && palabras[0].equals("listar") && palabras[1].equals("edificios")) {
            listarEdificaciones();
            return true;
        }

        if(palabras.length == 2 && palabras[0].equals("estadisticas")){
            mostrarEstadisticasUnJugador(palabras[1]);
            return true;
        }
        if(palabras.length == 2 && palabras[0].equals("hipotecar")){
            String nombreCasilla = palabras[1];
            hipotecar(nombreCasilla);
            return true;
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

                if (j.getPropiedades().isEmpty()) {
                    System.out.println("hipotecas: -,");
                } else {
                    List<String> hipotecas = j.getPropiedades().stream()
                            .filter(Casilla::estaHipotecada)
                            .map(Casilla::getNombre)
                            .collect(Collectors.toList());
                    if (hipotecas.isEmpty()) {
                        System.out.println("hipotecas: -,");
                    } else {
                        System.out.println("hipotecas: " + hipotecas + ",");
                    }
                }
                System.out.print("edificios: ");
                if (j.getEdificaciones().isEmpty()) {
                    System.out.println("-");
                } else {
                    List<String> ids = j.getEdificaciones().stream()
                            .map(Edificacion::getId)
                            .collect(Collectors.toList());
                    System.out.println(ids);
                }

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
        System.out.println("  - 'hipotecar <casilla>'");
        System.out.println("  - 'deshipotecar <casilla>'");
        System.out.println("  - 'vender <tipo> <casilla> <cantidad>'");
        System.out.println("  - 'estadisticas <jugador>'");
        System.out.println("  - 'estadisticas juego'");
        System.out.println("  - 'salir carcel'");
        System.out.println("  - 'estadistas juego'");
        System.out.println("  - 'listar edificios'");
        System.out.println("  - 'listar edificios <grupo>'");
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

        if (casilla.getGrupo() != null) {
            for (Casilla c : casilla.getGrupo().getMiembros()) {
                if (c.estaHipotecada()) {
                    System.out.println("No puedes edificar en el grupo " +
                            casilla.getGrupo().getNombre() +
                            ". La casilla " + c.getNombre() + " está hipotecada.");
                    return;
                }
            }
        }

        // Verificar que no esté hipotecada
        if (casilla.estaHipotecada()) {
            System.out.println("No puedes edificar en una casilla hipotecada.");
            return;
        }

        String tipoNormalizado = tipo.toLowerCase().trim();

        switch (tipoNormalizado) {
            case "casa":
                if (edificarCasa(jugadorActual, casilla)) {
                    String id = generarId("casa");
                    Edificacion nueva = new Edificacion(id, jugadorActual, casilla, "casa", casilla.getPrecioCasa());
                    edificaciones.add(nueva);
                    jugadorActual.agregarEdificacion(nueva);

                }
                break;

            case "hotel":
                if (edificarHotel(jugadorActual, casilla)) {
                    String id = generarId("hotel");
                    Edificacion nueva = new Edificacion(id, jugadorActual, casilla, "hotel", casilla.getPrecioCasa());
                    edificaciones.add(nueva);
                    jugadorActual.agregarEdificacion(nueva);

                }
                break;

            case "piscina":
                if (edificarPiscina(jugadorActual, casilla)) {
                    String id = generarId("piscina");
                    Edificacion nueva = new Edificacion(id, jugadorActual, casilla, "piscina", casilla.getPrecioCasa());
                    edificaciones.add(nueva);
                    jugadorActual.agregarEdificacion(nueva);

                }
                break;

            case "pista":
            case "pista deporte":
            case "pista_deporte":
                if (edificarPista(jugadorActual, casilla)) {
                    String id = generarId("pista");
                    Edificacion nueva = new Edificacion(id, jugadorActual, casilla, "pista", casilla.getPrecioCasa());
                    edificaciones.add(nueva);
                    jugadorActual.agregarEdificacion(nueva);

                }
                break;
            default:
                System.out.println("Tipo de edificación no válido. Usa: casa, hotel, piscina o pista deporte");
                break;
        }
    }

    private boolean edificarCasa(Jugador jugador, Casilla casilla) {
        // Verificar que no hay hotel
        if (casilla.tieneHotel()) {
            System.out.println("No se puede edificar ningún edificio más en esta casilla ni en el grupo al que la casilla pertenece.");
            return false;
        }

        // Verificar que no hay 4 casas
        if (casilla.getNumCasas() >= 4) {
            System.out.println("No se puede edificar ningún edificio más en esta casilla ni en el grupo al que la casilla pertenece.");
            return false;
        }

        // Verificar dinero
        float coste = casilla.getPrecioCasa();
        if (jugador.getFortuna() < coste) {
            System.out.println("La fortuna de " + jugador.getNombre() +
                    " no es suficiente para edificar una casa en la casilla " +
                    casilla.getNombre() + ".");
            return false;
        }

        // Construir
        casilla.construirCasas(jugador, 1);
//        jugador.setFortuna(jugador.getFortuna() - coste);
//
//        // Generar ID automático (ejemplo: casa-1, casa-2, etc.)
//        int numCasa = casilla.getNumCasas();
//        String idEdificio = "casa-" + numCasa;
//
//        casilla.construirCasas(jugador, 1);
        System.out.println("Se ha edificado una casa en " + casilla.getNombre() +
                ". La fortuna de " + jugador.getNombre() +
                " se reduce en " + (long)coste + "€.");
        return true;
    }

    private boolean edificarHotel(Jugador jugador, Casilla casilla) {
        // Verificar que hay exactamente 4 casas
        if (casilla.getNumCasas() != 4) {
            System.out.println("No se puede edificar un hotel. Necesitas exactamente 4 casas.");
            return false;
        }

        // Verificar que no hay hotel ya
        if (casilla.tieneHotel()) {
            System.out.println("No se puede edificar ningún edificio más en esta casilla ni en el grupo al que la casilla pertenece.");
            return false;
        }

        // Verificar dinero
        float coste = casilla.getPrecioHotel();
        if (jugador.getFortuna() < coste) {
            System.out.println("La fortuna de " + jugador.getNombre() +
                    " no es suficiente para edificar un hotel en la casilla " +
                    casilla.getNombre() + ".");
            return false;
        }

        // Construir
        casilla.construirHotel(jugador);
        // Eliminar las 4 casas
        List<Edificacion> casasAEliminar = new ArrayList<>();
        for (Edificacion e : edificaciones) {
            if (e.getCasilla().equals(casilla) && e.getTipo().equals("casa")) {
                casasAEliminar.add(e);
            }
        }
        edificaciones.removeAll(casasAEliminar);
        jugador.getEdificaciones().removeAll(casasAEliminar);


        System.out.println("Se ha edificado un hotel en " + casilla.getNombre() +
                ". La fortuna de " + jugador.getNombre() +
                " se reduce en " + (long)coste + "€.");
        return true;
    }

    private boolean edificarPiscina(Jugador jugador, Casilla casilla) {
        // Verificar que hay hotel
        if (!casilla.tieneHotel()) {
            System.out.println("No se puede edificar una piscina, ya que no se dispone de un hotel.");
            return false;
        }

        // Verificar que no hay piscina ya
        if (casilla.tienePiscina()) {
            System.out.println("No se puede edificar ningún edificio más en esta casilla ni en el grupo al que la casilla pertenece.");
            return false;
        }

        // Verificar dinero
        float coste = casilla.getPrecioPiscina();
        if (jugador.getFortuna() < coste) {
            System.out.println("La fortuna de " + jugador.getNombre() +
                    " no es suficiente para edificar una piscina en la casilla " +
                    casilla.getNombre() + ".");
            return false;
        }

        // Construir
        casilla.construirPiscina(jugador);

        System.out.println("Se ha edificado una piscina en " + casilla.getNombre() +
                ". La fortuna de " + jugador.getNombre() +
                " se reduce en " + (long)coste + "€.");
        return true;
    }


    private boolean edificarPista(Jugador jugador, Casilla casilla) {
        // Verificar que hay hotel y piscina
        if (!casilla.tieneHotel() || !casilla.tienePiscina()) {
            System.out.println("No se puede edificar una pista de deporte, ya que no se dispone de un hotel y una piscina.");
            return false;
        }

        // Verificar que no hay pista ya
        if (casilla.tienePista()) {
            System.out.println("No se puede edificar ningún edificio más en esta casilla ni en el grupo al que la casilla pertenece.");
            return false;
        }

        // Verificar dinero
        float coste = casilla.getPrecioPista();
        if (jugador.getFortuna() < coste) {
            System.out.println("La fortuna de " + jugador.getNombre() +
                    " no es suficiente para edificar una pista de deporte en la casilla " +
                    casilla.getNombre() + ".");
            return false;
        }

        // Construir
        casilla.construirPista(jugador);

        System.out.println("Se ha edificado una pista de deporte en " + casilla.getNombre() +
                ". La fortuna de " + jugador.getNombre() +
                " se reduce en " + (long)coste + "€.");
        return true;
    }

    private String generarId(String tipo) {
        switch (tipo.toLowerCase()) {
            case "casa":
                return "casa-" + (++contadorCasas);
            case "hotel":
                return "hotel-" + (++contadorHoteles);
            case "piscina":
                return "piscina-" + (++contadorPiscinas);
            case "pista":           // ✅ Añadir este case
            case "pista_deporte":   // ✅ Y mantener este por compatibilidad
            case "pista deporte":   // ✅ Por si llega con espacio
                return "pista-" + (++contadorPistas);
            default:
                return "edif-" + UUID.randomUUID();
        }
    }

    public void listarEdificaciones() {
        if (edificaciones.isEmpty()) {
            System.out.println("No hay edificaciones construidas.");
            return;
        }

        for (Edificacion e : edificaciones) {
            System.out.println("{");
            System.out.println("  id: " + e.getId() + ",");
            System.out.println("  propietario: " + e.getPropietario().getNombre() + ",");
            System.out.println("  casilla: " + e.getCasilla().getNombre() + ",");
            System.out.println("  grupo: " + e.getCasilla().getGrupo().getNombre() + ",");
            System.out.println("  coste: " + (long) e.getCoste() + "€");
            System.out.println("}");
        }
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

    private void listarEdificiosGrupo(String nombreGrupo) {
        // Buscar el grupo en el tablero
        Grupo grupo = tablero.getGrupo(nombreGrupo);

        if (grupo == null) {
            System.out.println("No existe el grupo '" + nombreGrupo + "'.");
            return;
        }

        ArrayList<Casilla> casillasGrupo = grupo.getMiembros();

        if (casillasGrupo.isEmpty()) {
            System.out.println("El grupo '" + nombreGrupo + "' no tiene casillas.");
            return;
        }

        System.out.println("$> listar edificios " + nombreGrupo);

        // Variables para contar qué se puede construir
        boolean puedeCasas = false;
        boolean puedeHoteles = false;
        boolean puedePiscinas = false;
        boolean puedePistas = false;
        int totalCasas = 0;
        int totalHoteles = 0;
        int totalPiscinas = 0;
        int totalPistas = 0;

        // Recorrer las casillas del grupo
        for (Casilla casilla : casillasGrupo) {
            // Solo mostrar si tiene edificaciones o si pertenece a un jugador
            if (casilla.getDuenho() != null && "solar".equalsIgnoreCase(casilla.getTipo())) {
                System.out.println("{");
                System.out.println("  propiedad: " + casilla.getNombre() + ",");

                // Listar hoteles
                if (casilla.tieneHotel()) {
                    System.out.print("  hoteles: [");
                    // Buscar el ID del hotel en las edificaciones
                    List<String> hotelesIds = new ArrayList<>();
                    for (Edificacion e : edificaciones) {
                        if (e.getCasilla().equals(casilla) && e.getTipo().equals("hotel")) {
                            hotelesIds.add(e.getId());
                        }
                    }
                    System.out.print(String.join(", ", hotelesIds));
                    System.out.println("],");
                    totalHoteles++;
                } else {
                    System.out.println("  hoteles: -,");
                }

                // Listar casas
                if (casilla.getNumCasas() > 0) {
                    System.out.print("  casas: [");
                    List<String> casasIds = new ArrayList<>();
                    for (Edificacion e : edificaciones) {
                        if (e.getCasilla().equals(casilla) && e.getTipo().equals("casa")) {
                            casasIds.add(e.getId());
                        }
                    }
                    System.out.print(String.join(", ", casasIds));
                    System.out.println("],");
                    totalCasas += casilla.getNumCasas();
                } else {
                    System.out.println("  casas: -,");
                }

                // Listar piscinas
                if (casilla.tienePiscina()) {
                    System.out.print("  piscinas: [");
                    List<String> piscinasIds = new ArrayList<>();
                    for (Edificacion e : edificaciones) {
                        if (e.getCasilla().equals(casilla) && e.getTipo().equals("piscina")) {
                            piscinasIds.add(e.getId());
                        }
                    }
                    System.out.print(String.join(", ", piscinasIds));
                    System.out.println("],");
                    totalPiscinas++;
                } else {
                    System.out.println("  piscinas: -,");
                }

                // Listar pistas de deporte
                if (casilla.tienePista()) {
                    System.out.print("  pistasDeDeporte: [");
                    List<String> pistasIds = new ArrayList<>();
                    for (Edificacion e : edificaciones) {
                        if (e.getCasilla().equals(casilla) && e.getTipo().equals("pista")) {
                            pistasIds.add(e.getId());
                        }
                    }
                    System.out.print(String.join(", ", pistasIds));
                    System.out.println("],");
                    totalPistas++;
                } else {
                    System.out.println("  pistasDeDeporte: -,");
                }

                // Mostrar alquiler
                float alquiler = casilla.calcularAlquilerTotal();
                System.out.println("  alquiler: " + (long)alquiler);
                System.out.println("},");
            }
        }

        // Determinar qué se puede construir
        for (Casilla casilla : casillasGrupo) {
            if (casilla.getDuenho() != null && grupo.perteneceEnteramenteA(casilla.getDuenho())) {
                if (casilla.puedeConstruirCasa(casilla.getDuenho())) {
                    puedeCasas = true;
                }
                if (casilla.puedeConstruirHotel()) {
                    puedeHoteles = true;
                }
                if (casilla.puedeConstruirPiscina()) {
                    puedePiscinas = true;
                }
                if (casilla.puedeConstruirPista()) {
                    puedePistas = true;
                }
            }
        }

        // Mostrar resumen de lo que se puede construir
        System.out.println();
        List<String> puedeConstruir = new ArrayList<>();
        List<String> noPuedeConstruir = new ArrayList<>();

        if (puedePiscinas) {
            puedeConstruir.add("piscinas");
        } else {
            noPuedeConstruir.add("piscinas");
        }

        if (puedePistas) {
            puedeConstruir.add("pistas de deporte");
        } else {
            noPuedeConstruir.add("pistas de deporte");
        }

        if (puedeCasas) {
            puedeConstruir.add("casas");
        } else {
            noPuedeConstruir.add("casas");
        }

        if (puedeHoteles) {
            puedeConstruir.add("hoteles");
        } else {
            noPuedeConstruir.add("hoteles");
        }

        if (!puedeConstruir.isEmpty()) {
            System.out.println("Aún se puede edificar " + String.join(", ", puedeConstruir) + ".");
        }

        if (!noPuedeConstruir.isEmpty()) {
            System.out.println("Ya no se pueden construir " + String.join(" ni ", noPuedeConstruir) + ".");
        }
    }

    public void hipotecar(String nombreCasilla) {
        Jugador jugador = jugadores.get(turno);
        Casilla casilla = tablero.encontrarCasilla(nombreCasilla);

        if (casilla == null) {
            System.out.println("La casilla " + nombreCasilla + " no existe.");
            return;
        }

        if (!jugador.equals(casilla.getDuenho())) {
            System.out.println(jugador.getNombre() + " no puede hipotecar " +
                    nombreCasilla + ". No es una propiedad que le pertenece.");
            return;
        }

        if (casilla.estaHipotecada()) {
            System.out.println(jugador.getNombre() + " no puede hipotecar " +
                    nombreCasilla + ". Ya está hipotecada.");
            return;
        }

        if (!casilla.esHipotecable()) {
            System.out.println("La casilla " + nombreCasilla + " no se puede hipotecar.");
            return;
        }

        if ("solar".equalsIgnoreCase(casilla.getTipo())) {
            if (casilla.tieneEdificios()) {
                System.out.println(jugador.getNombre() + " no puede hipotecar " +
                        nombreCasilla + ". Antes de hipotecar la propiedad se " +
                        "deberán vender todos los edificios.");
                return;
            }
        }

        float valorHipoteca = casilla.getValor() / 2;
        jugador.sumarFortuna(valorHipoteca);
        casilla.hipotecar();

        // 7. Mensaje con el nombre del GRUPO (no de la casilla)
        String nombreGrupo = (casilla.getGrupo() != null) ?
                casilla.getGrupo().getNombre() : "sin grupo";

        System.out.println(jugador.getNombre() + " recibe " + (long)valorHipoteca +
                "€ por la hipoteca de " + nombreCasilla +
                ". No puede recibir alquileres ni edificar en el grupo " +
                nombreGrupo + ".");
    }

    public void deshipotecar(String nombreCasilla) {
        Jugador jugador = jugadores.get(turno);
        Casilla casilla = tablero.encontrarCasilla(nombreCasilla);

        if (casilla == null) {
            System.out.println("La casilla " + nombreCasilla + " no existe.");
            return;
        }

        if (!jugador.equals(casilla.getDuenho())) {
            System.out.println(jugador.getNombre() + " no puede deshipotecar " +
                    nombreCasilla + ". No es una propiedad que le pertenece.");
            return;
        }

        if (!casilla.estaHipotecada()) {
            System.out.println(jugador.getNombre() + " no puede deshipotecar " +
                    nombreCasilla + ". No está hipotecada.");
            return;
        }

        float costoDeshipoteca = casilla.getValor() / 2;

        if (jugador.getFortuna() < costoDeshipoteca) {
            System.out.println(jugador.getNombre() + " no tiene suficiente dinero para " +
                    "deshipotecar " + nombreCasilla + ". Necesita " +
                    (long)costoDeshipoteca + "€.");
            return;
        }

        jugador.restarFortuna(costoDeshipoteca);
        jugador.sumarGastos(costoDeshipoteca);
        casilla.deshipotecar();

        String nombreGrupo = (casilla.getGrupo() != null) ?
                casilla.getGrupo().getNombre() : "sin grupo";

        System.out.println(jugador.getNombre() + " paga " + (long)costoDeshipoteca +
                "€ por deshipotecar " + nombreCasilla +
                ". Ahora puede recibir alquileres y edificar en el grupo " +
                nombreGrupo + ".");
    }

    // Vender edificaciones desde el menú.
    // Sintaxis: vender <casas|hotel|piscina|pista> <casilla> <cantidad>
//    private void vender(String tipo, String nombreCasilla, int cantidad) {
//        // Obtenemos el jugador en turno
//        Jugador jugadorActual = jugadores.get(turno);
//
//        // Buscamos la casilla por nombre exactamente como llega
//        Casilla c = tablero.encontrarCasilla(nombreCasilla);
//
//        /*// Soportar el alias "SolarX" -> "SolX"
//        // Si no se encontró la casilla y el nombre empieza por "Solar", generamos "Sol" + sufijo
//        if (c == null && nombreCasilla != null && nombreCasilla.toLowerCase(Locale.ROOT).startsWith("solar")) {
//            String alterno = "Sol" + nombreCasilla.substring("Solar".length());
//            c = tablero.encontrarCasilla(alterno);
//        }*/
//
//        // Si no existe la casilla se avisa al user y se termina
//        if (c == null) {
//            System.out.println("No existe la casilla " + nombreCasilla + ".");
//            return;
//        }
//        // LLamar al metodo de venderEdificacion definido en casilla que contiene toda la logica que se usa
//        // para vender un edificio
//        c.venderEdificacion(tipo, jugadorActual, cantidad);
//    }

    // En Menu.java, método vender()
    private void vender(String tipo, String nombreCasilla, int cantidad) {
        Jugador jugadorActual = jugadores.get(turno);
        Casilla c = tablero.encontrarCasilla(nombreCasilla);

        if (c == null) {
            System.out.println("No existe la casilla " + nombreCasilla + ".");
            return;
        }

        // Guardar el número de edificaciones ANTES de vender
        int casasAntes = c.getNumCasas();
        boolean hotelAntes = c.tieneHotel();
        boolean piscinaAntes = c.tienePiscina();
        boolean pistaAntes = c.tienePista();

        // Llamar al método de venta en Casilla
        c.venderEdificacion(tipo, jugadorActual, cantidad);

        // DESPUÉS de vender, eliminar de las listas globales según lo que cambió
        String tipoNorm = tipo.toLowerCase().trim();

        if (tipoNorm.equals("casa") || tipoNorm.equals("casas")) {
            int casasVendidas = casasAntes - c.getNumCasas();
            eliminarEdificaciones(c, "casa", casasVendidas, jugadorActual);
        }
        else if (tipoNorm.equals("hotel") || tipoNorm.equals("hoteles")) {
            if (hotelAntes && !c.tieneHotel()) {
                eliminarEdificaciones(c, "hotel", 1, jugadorActual);
            }
        }
        else if (tipoNorm.equals("piscina") || tipoNorm.equals("piscinas")) {
            if (piscinaAntes && !c.tienePiscina()) {
                eliminarEdificaciones(c, "piscina", 1, jugadorActual);
            }
        }
        else if (tipoNorm.equals("pista") || tipoNorm.contains("pista")) {
            if (pistaAntes && !c.tienePista()) {
                eliminarEdificaciones(c, "pista", 1, jugadorActual);
            }
        }
    }

    // Método auxiliar para eliminar edificaciones
    private void eliminarEdificaciones(Casilla casilla, String tipo, int cantidad, Jugador jugador) {
        List<Edificacion> aEliminar = new ArrayList<>();
        int eliminadas = 0;

        for (Edificacion e : edificaciones) {
            if (e.getCasilla().equals(casilla) &&
                    e.getTipo().equals(tipo) &&
                    eliminadas < cantidad) {
                aEliminar.add(e);
                eliminadas++;
            }
        }

        // Eliminar de ambas listas
        edificaciones.removeAll(aEliminar);
        jugador.getEdificaciones().removeAll(aEliminar);
    }

    private void mostrarEstadisticasJuego() {
        // Casilla más rentable (la que más alquiler ha generado)
        Casilla casillaMasRentable = null;
        float maxRenta = -1;
        for (Jugador j : jugadores) {
            for (Casilla c : j.getPropiedades()) {
                float renta = c.getIngresosGenerados(); // acumulador en Casilla
                if (renta > maxRenta) {
                    maxRenta = renta;
                    casillaMasRentable = c;
                }
            }
        }

        // Grupo más rentable (suma de ingresos de todas las casillas del grupo)
        Grupo grupoMasRentable = null;
        float maxGrupo = -1;
        for (Grupo g : tablero.getGrupos()) { // ahora Tablero tiene getGrupos()
            float total = 0;
            for (Casilla c : g.getMiembros()) { // en Grupo usas getMiembros()
                total += c.getIngresosGenerados();
            }
            if (total > maxGrupo) {
                maxGrupo = total;
                grupoMasRentable = g;
            }
        }

        // Casilla más frecuentada (la que más veces han caído avatares)
        Casilla casillaMasFrecuentada = null;
        int maxVisitas = -1;
        for (Casilla c : tablero.getCasillas()) { // ahora Tablero tiene getCasillas()
            if (c.getVecesVisitada() > maxVisitas) {
                maxVisitas = c.getVecesVisitada();
                casillaMasFrecuentada = c;
            }
        }

        // Jugador con más vueltas
        Jugador jugadorMasVueltas = null;
        int maxVueltas = 0;
        for (Jugador j : jugadores) {
            if (j.getVueltas() > maxVueltas) {
                maxVueltas = j.getVueltas();
                jugadorMasVueltas = j;
            }
        }

        // Jugador en cabeza (fortuna + valor propiedades + edificios)
        Jugador jugadorEnCabeza = null;
        float maxValorTotal = -1;
        for (Jugador j : jugadores) {
            float valorPropiedades = 0;
            for (Casilla c : j.getPropiedades()) {
                valorPropiedades += c.getValor();
            }
            float valorTotal = j.getFortuna() + valorPropiedades;
            if (valorTotal > maxValorTotal) {
                maxValorTotal = valorTotal;
                jugadorEnCabeza = j;
            }
        }

        // Imprimir resultado
        System.out.println("$> estadisticas");
        System.out.println("{");
        System.out.println("casillaMasRentable: " + (casillaMasRentable != null ? casillaMasRentable.getNombre() : "-") + ",");
        System.out.println("grupoMasRentable: " + (grupoMasRentable != null ? grupoMasRentable.getNombre() : "-") + ",");
        System.out.println("casillaMasFrecuentada: " + (casillaMasFrecuentada != null ? casillaMasFrecuentada.getNombre() : "-") + ",");
        System.out.println("jugadorMasVueltas: " + (jugadorMasVueltas != null ? jugadorMasVueltas.getNombre() : "-") + ",");
        System.out.println("jugadorEnCabeza: " + (jugadorEnCabeza != null ? jugadorEnCabeza.getNombre() : "-"));
        System.out.println("}");
    }






}