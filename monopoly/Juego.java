package monopoly;

import partida.*;

import monopoly.Edificios.*;
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

public class Juego { // la clase menu

    ////////Atributos//////////

    private Tablero tablero; //representa el tablero
    private Jugador banca; //la banca, jugador especial que gestiona el dinero y las propiedades

    private ArrayList<Jugador> jugadores; //lista de jugadores
    private ArrayList<Avatar> avatares; //lista de avatares

    // Sets para evitar nombres y avatares duplicados
    private final Set<String> nombresUsados = new HashSet<>(); //evita que dos jugadores tengan el mismo nombre
    private final String[] avataresPermitidos = {"coche", "sombrero", "pelota", "esfinge"};
    private final Set<String> avataresUsados = new HashSet<>(); //evita que dos jugadores tengan el mismo avatar

    private Dado dado1; //dado uno
    private Dado dado2; // dado dos
    private int turno = 0; //indice del jugador actual
    private boolean tirado = false; //uindica si un jugador a tirado o no en esa partida
    private int contadorDobles = 0; //hay o no dobles
    private boolean repetirTurno = false; // si repite o no tirada
    private boolean intentoSalirCarcel = false; // si intento salir de la carcel en ese turno

    //False: Juego en estado de Setup (crear personajes y esas cositas)
    //True: Empieza la partida y ya no se pueden crear jugadores
    private boolean juegoIniciado = false;

    private List<Edificacion> edificaciones = new ArrayList<>(); //lista de edificios construidos
    private int contadorCasas = 0;
    private int contadorHoteles = 0;
    private int contadorPiscinas = 0;
    private int contadorPistas = 0;

    ///CONSTRUCTOR sin parametros, que se inicializa pasandole nosotros los valores
    public Juego() {
        this.banca = new Jugador();
        this.tablero = new Tablero(banca);
        this.tablero.setMenu(this);
        this.jugadores = new ArrayList<>();
        this.avatares = new ArrayList<>();
        this.dado1 = new Dado();
        this.dado2 = new Dado();
    }

    public void iniciarPartida() {

        menuSetUp(); //immprime el menu de inicializacion

        Scanner sc = new Scanner(System.in); //objeto para leer comandos desde consola

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
        System.out.println("Comandos disponibles:");
        menuComandos(); //imprime el menu de comados


        while (true) {
            Jugador actual = jugadores.get(turno); //obtiene el jugador que tiene el turno actual
            System.out.println("\nTurno de " + actual.getNombre());
            System.out.print("> ");
            String comando = sc.nextLine(); //lee lo que el jugador escribe

            analizarComando(comando); //analiza el comando
        }
    }

    //metodo para crear un jugador con su avatar asociado
    private void crearJugador(String nombre, String avatarElegido) {

        if (jugadores.size() >= 4) { //no crea mas si hay 4, LIMITE MAX 4
            System.out.println("Ya hay 4 jugadores. No se pueden crear mÃ¡s.");
            return;
        }
        if (nombre == null || nombre.isBlank()) { //el nombre lo puede ser null ni vacio
            System.out.println("El nombre no puede estar vacÃ­o.");
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
            System.out.println("Avatar no vÃ¡lido. Avatares permitidos: coche, sombrero, pelota, esfinge");
            return;
        }

        if (!nombresUsados.add(nombre.toLowerCase(Locale.ROOT))) { //garantiza que el nombre es unico
            System.out.println("Ese nombre ya estÃ¡ en uso. Elige otro.");
            return;
        }
        if (!avataresUsados.add(avatarElegido.toLowerCase(Locale.ROOT))) {
            System.out.println("Ese avatar ya estÃ¡ en uso. Elige otro.");
            nombresUsados.remove(nombre.toLowerCase(Locale.ROOT));
            //Si esta usado se quita de la memoria que guarda los nombres usados
            return;
        }

        //Poner en la salida
        Casilla salida = tablero.encontrarCasilla("Salida"); //Poner en la salida
        Jugador j = new Jugador(nombre, avatarElegido.toLowerCase(Locale.ROOT), salida, avatares);

        Avatar av = j.getAvatar(); // se obtiene el avatar
        salida.anhadirAvatar(av); //ponemos el avatar en la casilla de salida
        avatares.add(av); //se guarda en la lista
        jugadores.add(j); //se guarda el jugador

        System.out.println("Jugador " + nombre + " creado con avatar " + avatarElegido.toLowerCase(Locale.ROOT));
        System.out.println("Jugadores totales: " + jugadores.size() + " (mÃ­nimo 2, mÃ¡ximo 4)");
    }



    public void analizarComando(String comando) {

        if (!juegoIniciado) { //Si estamos en el apartado de setup (la partida no empezÃ³ se usa otra opcion)
            analizarComandoSetup(comando);
            return;
        }

        Jugador actual = jugadores.get(turno);
        if (actual.getDeudaPendiente() > 0) {
            String cmd = comando.trim().toLowerCase();

            // Solo permitir hipotecar y declarar bancarrota
            if (!cmd.startsWith("hipotecar") && !cmd.equals("declarar bancarrota")) {
                System.out.println("\nTienes una deuda pendiente:");
                System.out.println("Debes pagar: " + (long)actual.getDeudaPendiente() + "â‚¬");
                System.out.println("Tu fortuna: " + (long)actual.getFortuna() + "â‚¬");
                System.out.println("Faltante: " + (long)(actual.getDeudaPendiente() - actual.getFortuna()) + "â‚¬");
                System.out.println("\nComandos permitidos:");
                System.out.println("  - hipotecar <casilla>");
                System.out.println("  - declarar bancarrota");
                return;
            }
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


    //MÃ©todo para analizar comandos en la parte de setup (en la que se pueden crear personajes)
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
        System.out.println("EstÃ¡s en modo configuraciÃ³n. Comandos:");
        menuSetUp();
    }

    private boolean procesarComandoDados(String comandoOriginal) {
        Pattern patronDados = Pattern.compile(
                "^(tirar|lanzar)\\s+dados(?:\\s+(\\d)\\s*\\+\\s*(\\d))?$",
                Pattern.CASE_INSENSITIVE
        );

//        ^(tirar|lanzar)         // El comando debe empezar por "tirar" o "lanzar"
//        \\s+                    // Uno o mÃ¡s espacios
//        dados                   // Literal "dados"
//        (?:                     // Inicio de grupo opcional (no captura)
//        \\s+                 // Uno o mÃ¡s espacios
//        (\\d)                // Primer nÃºmero (dado1), un solo dÃ­gito
//        \\s*\\+\\s*          // El signo + con espacios opcionales
//        (\\d)                // Segundo nÃºmero (dado2), un solo dÃ­gito
//        )?                      // Fin del grupo opcional
//        $                       // Fin de la lÃ­nea
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
            lanzarDados();// Tirada aleatoria normal
        }
        return true;
    }

    private boolean procesarComandoArchivo(String comandoOriginal, String comandoMinusculas) {
        if (!comandoMinusculas.startsWith("comandos")) {
            return false;
        }

        // Dividir en mÃ¡ximo 2 partes: "comandos" + ruta
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

            case "declarar bancarrota":

                Jugador jugadorActuaal = jugadores.get(turno);
                if (jugadorActuaal.getDeudaPendiente() > 0) {
                    jugadorActuaal.declararBancarrota(jugadorActuaal.getAcreedorDeuda());
                    tablero.notificarBancarrota(jugadorActuaal);
                } else {
                    System.out.println("No tienes deudas pendientes.");
                    System.out.println("No puedes declararte en bancarrota voluntariamente.");
                }
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
            comprarCasilla(palabras[1]);
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
            // Extraer el nombre completo de la casilla (despuÃ©s de "describir ")
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
            // tipo de edificaciÃ³n: casas | hotel | piscina | pista
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
                System.out.println("Cantidad invÃ¡lida. Debe ser un entero positivo.");
                return true; // comando reconocido y gestionado con error
            }

            venderPropiedad(tipo, nombreCasilla, cantidad);
            return true;
        }

        if(palabras.length == 2 && palabras[0].equals("deshipotecar")){
            String nombreCasilla = palabras[1];
            deshipotecarPropiedad(nombreCasilla);
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
            hipotecarPropiedad(nombreCasilla);
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
            System.out.println("No se encontrÃ³ el archivo: " + path.toAbsolutePath());
            return;
        }
        // Usamos try catch para coger los posibles errores que nos saque el archivo
        // Abrir un BufferedReader con UTF-8 (se cierra solo)
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String linea;
            int numLinea = 0;

            // Leer lÃ­nea a lÃ­nea hasta que no haya mÃ¡s
            while ((linea = br.readLine()) != null) {
                numLinea++;
                String comando = linea.strip(); //Elimina los espacios al final
                if (comando.isEmpty()) continue; // Ignora lÃ­neas vacÃ­as

                if (juegoIniciado && !jugadores.isEmpty()) {
                    Jugador actual = jugadores.get(turno);
                    System.out.println("[Turno: " + actual.getNombre() + "] [archivo:" + numLinea + "] " + comando);
                } else {
                    System.out.println("[archivo:" + numLinea + "] " + comando);
                }

                analizarComando(comando);
            }
        } catch (IOException e) {
            // Captura errores y los muestra
            System.out.println("Error leyendo el archivo: " + e.getMessage());
        }
    }

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
                System.out.println("Cartas para salir de la cÃ¡rcel: " + j.getCartasSalirCarcel());

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
        System.out.println("No se encontrÃ³ ningÃºn jugador con el nombre '" + nombreBuscado + "'.");
    }


    private void descAvatar(String ID) {

        for (Avatar av : avatares) { //for each para recorrer los avatares de los jugadores creados
            if (av.getId().equalsIgnoreCase(ID)) { //si lo encuentra imprime su tipo, el jugador y la casilla en la que esta
                System.out.println("$> describir avatar " + ID);
                System.out.println("{");
                System.out.println("tipo: " + av.getJugador().getAvatar().getTipo() + ",");
                System.out.println("jugador: " + av.getJugador().getNombre() + ",");
                System.out.println("casilla: " + (av.getCasilla() != null ? av.getCasilla().getNombre() : "sin posiciÃ³n") + ",");
                System.out.println("}");
                return;
            }
        }
        System.out.println("No se encontrÃ³ ningÃºn avatar con ID '" + ID + "'.");
    }

    private void descCasilla(String nombre) {
        //Busca la casilla en el tablero
        Casilla casilla = tablero.encontrarCasilla(nombre);
        if (casilla != null) {
            System.out.println(casilla.describir());
        } else {
            System.out.println("No se encontrÃ³ la casilla '" + nombre + "'.");
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

        // *** CAMBIO CLAVE: Si estÃ¡ en cÃ¡rcel, NO mostrar menÃº aquÃ­ ***
        if (actual.isEnCarcel() || av.estaEnCarcel()) {
            System.out.println("EstÃ¡s en la cÃ¡rcel. Usa el comando 'salir carcel' para intentar salir.");
            return 0;
        }

        // Tirada normal (fuera de cÃ¡rcel)
        int d1 = dado1.hacerTirada();
        int d2 = dado2.hacerTirada();
        int total = d1 + d2;
        tablero.setUltimaTirada(total);
        System.out.println("Has sacado " + d1 + " y " + d2 + " â†’ total: " + total);

        // GestiÃ³n de dobles
        if (d1 == d2) {
            contadorDobles++;
            System.out.println("Â¡Dados dobles! (" + contadorDobles + " seguidos)");
            if (contadorDobles == 3) {
                System.out.println("Â¡Tres dobles seguidos! Vas directo a la cÃ¡rcel.");
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
            System.out.println("Has caÃ­do en 'Ir a CÃ¡rcel'. Usa 'salir carcel'.");
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
            System.out.println("Valores invÃ¡lidos. Usa nÃºmeros entre 1 y 6.");
            return 0;
        }

        Jugador actual = jugadores.get(turno);
        Avatar av = actual.getAvatar();

        // *** CAMBIO: No mostrar menÃº automÃ¡ticamente ***
        if (actual.isEnCarcel() || av.estaEnCarcel()) {
            System.out.println("EstÃ¡s en la cÃ¡rcel. Usa el comando 'salir carcel'.");
            return 0;
        }

        // Guardar valores en los dados
        dado1.setValor(d1);
        dado2.setValor(d2);

        int total = d1 + d2;
        tablero.setUltimaTirada(total);
        System.out.println("Has forzado " + d1 + " y " + d2 + " â†’ total: " + total);

        // GestiÃ³n de dobles
        if (d1 == d2) {
            contadorDobles++;
            System.out.println("Â¡Dados dobles! (" + contadorDobles + " seguidos)");
            if (contadorDobles == 3) {
                System.out.println("Â¡Tres dobles seguidos! Vas directo a la cÃ¡rcel.");
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
            System.out.println("Has caÃ­do en 'Ir a CÃ¡rcel'. Usa 'salir carcel'.");
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


    public void comprarCasilla(String nombreCasilla) {
        //Jugador y casilla actuales
        Jugador jugador = jugadores.get(turno);
        Casilla casilla = tablero.encontrarCasilla(nombreCasilla);

        if (casilla == null) {
            System.out.println("No se encontrÃ³ la casilla '" + nombreCasilla + "'.");
            return;
        }

        // Verifica que el jugador estÃ© en la casilla
        Casilla casillaActual = jugador.getAvatar().getCasilla();
        if (!casillaActual.equals(casilla)) {
            System.out.println("No puedes comprar esta casilla porque no estÃ¡s en ella.");
            return;
        }

        // Verifica que la casilla estÃ© en venta
        if (casilla.getDuenho() != null) {
            System.out.println("Esta casilla ya tiene dueÃ±o.");
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
        jugador.sumarDineroInvertido(casilla.getValor()); //para las estadisticas
        casilla.setDuenho(jugador);
        jugador.anhadirPropiedad(casilla);
        System.out.println("Has comprado " + casilla.getNombre() + " por " + (long) casilla.getValor() + "â‚¬.");
    }


    private void salirCarcel(Jugador jugador) {
        // Verificar que estÃ¡ en cÃ¡rcel
        if (!jugador.isEnCarcel() && !jugador.getAvatar().estaEnCarcel()) {
            System.out.println("No estÃ¡s en la cÃ¡rcel.");
            return;
        }

        // Verificar que no se haya intentado ya este turno
        if (intentoSalirCarcel) {
            System.out.println("Ya has intentado salir de la cÃ¡rcel este turno.");
            return;
        }

        Avatar av = jugador.getAvatar();
        int turnosEnCarcel = av.getTurnosEnCarcel();

        System.out.println("\n=== ESTÃS EN LA CÃRCEL ===");
        System.out.println("Turno en cÃ¡rcel: " + (turnosEnCarcel + 1) + "/3");

        // *** CASO 1: Tercer turno â†’ PAGO OBLIGATORIO ***
        if (turnosEnCarcel >= 2) {
            System.out.println("\nÂ¡Has estado 3 turnos en la cÃ¡rcel!");
            System.out.println("Debes pagar 500.000â‚¬ obligatoriamente y avanzar con tu tirada.");

            if (jugador.getFortuna() < 500000) {
                System.out.println("No tienes dinero suficiente. BANCARROTA.");
                jugador.declararBancarrota(null);
                tablero.notificarBancarrota(jugador);
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
            System.out.println("Has pagado 500.000â‚¬ y sales de la cÃ¡rcel.");

            // Tirar dados y moverse
            int d1 = dado1.hacerTirada();
            int d2 = dado2.hacerTirada();
            int total = d1 + d2;
            tablero.setUltimaTirada(total);
            System.out.println("Tiras los dados: " + d1 + " y " + d2 + " â†’ total: " + total);
            av.moverAvatar(total, tablero);
            System.out.println(tablero);

            tirado = true;
            intentoSalirCarcel = true;
            return;
        }

        // *** CASO 2: Turno 1 o 2 â†’ MENÃš DE OPCIONES ***
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\nOpciones:");
            System.out.println("1) Pagar 500.000â‚¬ (sales, y te puedes mover en este turno)");
            System.out.println("2) Usar carta de 'Salir de la cÃ¡rcel' (sales, pero NO te mueves)");
            System.out.println("3) Intentar sacar dobles (si sacas dobles: sales y mueves)");
            System.out.print("Elige opciÃ³n: ");

            String opcion = sc.nextLine().trim();

            // OPCIÃ“N 1: PAGAR
            if (opcion.equals("1")) {
                if (jugador.getFortuna() < 500000) {
                    System.out.println("No tienes suficiente dinero. Elige otra opciÃ³n.");
                    continue;
                }

                jugador.restarFortuna(500000);
                jugador.sumarGastos(500000);
                jugador.sumarPagoTasasEImpuestos(500000);
                av.setEnCarcel(false);
                jugador.setEnCarcel(false);
                av.setTurnosEnCarcel(0);
                System.out.println("Has pagado 500.000â‚¬ y sales de la cÃ¡rcel.");
                System.out.println("Tira los dados.");

                tirado = false; // asÃ­ puede tirar los dados
                intentoSalirCarcel = true;
                break;
            }

            // OPCIÃ“N 2: CARTA
            else if (opcion.equals("2")) {
                if (!jugador.usarCartaSalirCarcel()) {
                    System.out.println("No tienes ninguna carta. Elige otra opciÃ³n.");
                    continue;
                }

                av.setEnCarcel(false);
                jugador.setEnCarcel(false);
                av.setTurnosEnCarcel(0);
                System.out.println("Has usado una carta y sales de la cÃ¡rcel.");
                System.out.println("No te mueves este turno. Usa 'acabar turno'.");

                tirado = true; // Bloquea tirar dados
                intentoSalirCarcel = true;
                break;
            }

            // OPCIÃ“N 3: INTENTAR DOBLES
            else if (opcion.equals("3")) {
                int d1 = dado1.hacerTirada();
                int d2 = dado2.hacerTirada();
                System.out.println("Has sacado: " + d1 + " y " + d2);

                if (d1 == d2) {
                    // Â¡DOBLES! â†’ SALE Y SE MUEVE
                    av.setEnCarcel(false);
                    jugador.setEnCarcel(false);
                    av.setTurnosEnCarcel(0);
                    int total = d1 + d2;
                    System.out.println("Â¡DOBLES! Sales de la cÃ¡rcel y avanzas " + total + " casillas.");
                    av.moverAvatar(total, tablero);
                    System.out.println(tablero);
                } else {
                    // NO DOBLES â†’ Incrementa turno en cÃ¡rcel
                    av.incrementarTurnosEnCarcel();
                    System.out.println("No son dobles. Pierdes el turno.");
                    System.out.println("Llevas " + av.getTurnosEnCarcel() + " turno(s) en la cÃ¡rcel.");
                }

                tirado = true;
                intentoSalirCarcel = true;
                break;
            } else {
                System.out.println("OpciÃ³n no vÃ¡lida. Elige 1, 2 o 3.");
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

        // *** SOLO INFORMAR si estÃ¡ en cÃ¡rcel ***
        if (actual.isEnCarcel() || actual.getAvatar().estaEnCarcel()) {
            System.out.println("\n  EstÃ¡s en la cÃ¡rcel. Usa 'salir carcel' para intentar salir.");
        }
    }


    //Metodo que muestra las casillas que estÃ¡n a la venta actualmente
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


    // MÃ©todo que realiza las acciones asociadas al comando 'listar jugadores'.
    // MÃ©todo que realiza las acciones asociadas al comando 'listar jugadores'.
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

            // PROPIEDADES
            if (j.getPropiedades().isEmpty()) {
                System.out.println("propiedades: -,");
            } else {
                String props = j.getPropiedades().stream() // stream -> crea un flujo de datos
                        .map(Casilla::getNombre) // convierte cada casilla en su nombre
                        .collect(Collectors.joining(", ")); // las recoge en una lista
                System.out.println("propiedades: [" + props + "],");
            }

            // HIPOTECAS - mostrar solo las propiedades hipotecadas
            List<String> hipotecas = j.getPropiedades().stream()
                    .filter(Casilla::estaHipotecada) // filtramos solo las que estÃ¡n hipotecadas
                    .map(Casilla::getNombre)
                    .collect(Collectors.toList());

            if (hipotecas.isEmpty()) {
                System.out.println("hipotecas: -,");
            } else {
                System.out.println("hipotecas: " + hipotecas + ",");
            }

            // EDIFICIOS - mostrar IDs de todas las edificaciones del jugador
            if (j.getEdificaciones().isEmpty()) {
                System.out.println("edificios: -");
            } else {
                List<String> idsEdificios = j.getEdificaciones().stream()
                        .map(Edificacion::getId)
                        .collect(Collectors.toList());
                System.out.println("edificios: " + idsEdificios);
            }

            System.out.print("}");
            if (i < jugadores.size() - 1) {
                System.out.println(","); // Separa los jugadores con ","
            } else {
                System.out.println();  // Ãšltima entrada sin coma final
            }
        }
    }

    // MÃ©todo que realiza las acciones asociadas al comando 'listar avatares'.
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
            System.out.println("casilla: " + (av.getCasilla() != null ? av.getCasilla().getNombre() : "sin posiciÃ³n") + ",");
            System.out.println("enCarcel: " + av.estaEnCarcel());
            System.out.println("}");
        }
    }

    private void acabarTurno() {
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("No hay jugadores en la partida.");
            return;
        }

        Jugador actual = jugadores.get(turno);

        if (actual.getDeudaPendiente() > 0) {
            System.out.println("No puedes acabar el turno con una deuda pendiente.");
            System.out.println("Deuda: " + (long)actual.getDeudaPendiente() + "â‚¬");
            System.out.println("Tu fortuna: " + (long)actual.getFortuna() + "â‚¬");
            System.out.println("\nOPCIONES:");
            System.out.println("1. Hipotecar mÃ¡s propiedades");
            System.out.println("2. Declarar bancarrota");
            return;
        }

        if (!tirado) {
            System.out.println("No puedes acabar el turno sin haber tirado los dados o intentado salir de la cÃ¡rcel.");
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
            System.out.println("Â¡La partida ha terminado!");
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
                    System.out.println("Error: No se encontrÃ³ jugador activo.");
                    return;
                }
            } while (jugadores.get(turno).isBancarrota());
        }

        // Resetear flags
        tirado = false;
        repetirTurno = false;
        contadorDobles = 0;
        intentoSalirCarcel = false;

        Jugador actualTurno = jugadores.get(turno);
        System.out.println("Turno acabado. Ahora le toca a:");
        System.out.println("$> jugador");
        System.out.println("{");
        System.out.println("nombre: " + actualTurno.getNombre() + ",");
        System.out.println("avatar: " + actualTurno.getAvatar().getId());
        System.out.println("}");

        if (actual.isEnCarcel() || actual.getAvatar().estaEnCarcel()) {
            System.out.println("CUIDADO " + actual.getNombre() + " estÃ¡ en la cÃ¡rcel.");
        }
    }

    // Determina si hay un Ãºnico jugador activo (no en bancarrota) y lo retorna como ganador.
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
            System.out.println("Â¡" + ganador.getNombre() + " ha ganado la partida!");
            System.out.println("Fortuna final: " + (long) ganador.getFortuna() + "â‚¬");
            System.out.println("Propiedades: " + ganador.getPropiedades().size());
            System.out.println("Gastos totales: " + (long) ganador.getGastos() + "â‚¬");
            // AquÃ­ puedes terminar el juego o bloquear mÃ¡s comandos
        }
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
        System.out.println("Modo configuraciÃ³n de partida:"); //comandos que se pueden usar si la partida no estÃ¡ empezada
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
            System.out.println("No puedes tener mÃ¡s de 4 jugadores.");
        } else {
            System.out.println("Iniciando partida...");

            Carta.resetearContadores();
            Carta.setJugadores(jugadores);
            Carta.inicializarMazos();

            juegoIniciado = true;
        }
    }

    private void edificarPropiedad(String tipo) {
        Jugador jugadorActual = jugadores.get(turno);
        Avatar avatar = jugadorActual.getAvatar();
        Casilla casilla = avatar.getCasilla();

        // Verificaciones comunes
        if (!"solar".equalsIgnoreCase(casilla.getTipo())) {
            System.out.println("No puedes edificar en esta casilla porque no es tipo solar.");
            return;
        }

        if (casilla.getDuenho() == null || !casilla.getDuenho().equals(jugadorActual)) {
            System.out.println("No eres dueÃ±o de " + casilla.getNombre() + ", no puedes edificar en ella.");
            return;
        }

        if (!jugadorActual.poseeGrupoCompleto(casilla, tablero)) {
            System.out.println("No puedes construir aquÃ­. No posees todo el grupo " +
                    casilla.getGrupo().getNombre() + ".");
            return;
        }

        if (casilla.getGrupo() != null) {
            for (Casilla c : casilla.getGrupo().getMiembros()) {
                if (c.estaHipotecada()) {
                    System.out.println("No puedes edificar en el grupo " +
                            casilla.getGrupo().getNombre() +
                            ". La casilla " + c.getNombre() + " estÃ¡ hipotecada.");
                    return;
                }
            }
        }

        String tipoNormalizado = tipo.toLowerCase().trim();

        switch (tipoNormalizado) {
            case "casa":
                Casa casa = new Casa("", jugadorActual, casilla, casilla.getPrecioCasa());
                if (casa.puedeEdificar(jugadorActual, casilla)) {
                    String id = generarId("casa");
                    Casa nuevaCasa = new Casa(id, jugadorActual, casilla, casilla.getPrecioCasa());
                    nuevaCasa.construir(jugadorActual, casilla);
                    edificaciones.add(nuevaCasa);
                    jugadorActual.agregarEdificacion(nuevaCasa);
                }
                break;

            case "hotel":
                Hotel hotel = new Hotel("", jugadorActual, casilla, casilla.getPrecioHotel());
                if (hotel.puedeEdificar(jugadorActual, casilla)) {
                    String id = generarId("hotel");
                    Hotel nuevoHotel = new Hotel(id, jugadorActual, casilla, casilla.getPrecioHotel());
                    nuevoHotel.construir(jugadorActual, casilla, edificaciones);
                    edificaciones.add(nuevoHotel);
                    jugadorActual.agregarEdificacion(nuevoHotel);
                }
                break;

            case "piscina":
                Piscina piscina = new Piscina("", jugadorActual, casilla, casilla.getPrecioPiscina());
                if (piscina.puedeEdificar(jugadorActual, casilla)) {
                    String id = generarId("piscina");
                    Piscina nuevaPiscina = new Piscina(id, jugadorActual, casilla, casilla.getPrecioPiscina());
                    nuevaPiscina.construir(jugadorActual, casilla);
                    edificaciones.add(nuevaPiscina);
                    jugadorActual.agregarEdificacion(nuevaPiscina);
                }
                break;

            case "pista":
            case "pista deporte":
            case "pista_deporte":
                Pista pista = new Pista("", jugadorActual, casilla, casilla.getPrecioPista());
                if (pista.puedeEdificar(jugadorActual, casilla)) {
                    String id = generarId("pista");
                    Pista nuevaPista = new Pista(id, jugadorActual, casilla, casilla.getPrecioPista());
                    nuevaPista.construir(jugadorActual, casilla);
                    edificaciones.add(nuevaPista);
                    jugadorActual.agregarEdificacion(nuevaPista);
                }
                break;

            default:
                System.out.println("Tipo de edificaciÃ³n no vÃ¡lido. Usa: casa, hotel, piscina o pista deporte");
                break;
        }
    }

    private String generarId(String tipo) {
        //para generar el id lo que vamos a hacer es ponerle el numero que tiene de casas y hoteles el tablero, asi nos aseguramos que nunca se va a sepetir id
        switch (tipo.toLowerCase()) {
            case "casa":
                return "casa-" + (++contadorCasas);
            case "hotel":
                return "hotel-" + (++contadorHoteles);
            case "piscina":
                return "piscina-" + (++contadorPiscinas);
            case "pista":
            case "pista_deporte":
            case "pista deporte":
                return "pista-" + (++contadorPistas);
            default:
                return "edif-" + UUID.randomUUID();
        }
    }

    public void listarEdificaciones() {
        if (edificaciones.isEmpty()) { //si la lista global de edificios esta vacia, es que no hay edificios
            System.out.println("No hay edificaciones construidas.");
            return;
        }

        for (Edificacion e : edificaciones) { //impimos para cada elemente del array sus atributos
            System.out.println("{");
            System.out.println("  id: " + e.getId() + ",");
            System.out.println("  propietario: " + e.getPropietario().getNombre() + ",");
            System.out.println("  casilla: " + e.getCasilla().getNombre() + ",");
            System.out.println("  grupo: " + e.getCasilla().getGrupo().getNombre() + ",");
            System.out.println("  coste: " + (long) e.getCoste() + "â‚¬");
            System.out.println("}");
        }
    }

    private void listarEdificiosGrupo(String nombreGrupo) {

        // Buscar el grupo en el tablero
        Grupo grupo = tablero.getGrupo(nombreGrupo);

        if (grupo == null) {
            System.out.println("No existe el grupo '" + nombreGrupo + "'.");
            return;
        }

        ArrayList<Casilla> casillasGrupo = grupo.getMiembros(); //todas las casillas del grupo a listar

        if (casillasGrupo.isEmpty()) {
            System.out.println("El grupo '" + nombreGrupo + "' no tiene casillas.");
            return;
        }

        System.out.println("$> listar edificios " + nombreGrupo);


        boolean puedeCasas = false;
        boolean puedeHoteles = false;
        boolean puedePiscinas = false;
        boolean puedePistas = false;

        // Recorrer las casillas del grupo
        for (Casilla casilla : casillasGrupo) {
            // Solo mostrar si tiene edificaciones -> que tenga dueÃ±o
            if (casilla.getDuenho() != null && "solar".equalsIgnoreCase(casilla.getTipo())) {

                System.out.println("{");
                System.out.println("  propiedad: " + casilla.getNombre() + ",");

                // Listar hoteles
                if (casilla.tieneHotel()) {
                    System.out.print("  hoteles: [");
                    // Buscar el ID del hotel en las edificaciones
                    List<String> hotelesIds = new ArrayList<>();
                    for (Edificacion e : edificaciones) {
                        if (e.getCasilla().equals(casilla) && e.obtenerTipo().equals("hotel")) {
                            hotelesIds.add(e.getId());
                        }
                    }
                    System.out.print(String.join(", ", hotelesIds));
                    System.out.println("],");
                } else {
                    System.out.println("  hoteles: -,");
                }

                // Listar casas
                if (casilla.getNumCasas() > 0) {
                    System.out.print("  casas: [");
                    List<String> casasIds = new ArrayList<>();
                    for (Edificacion e : edificaciones) {
                        if (e.getCasilla().equals(casilla) && e.obtenerTipo().equals("casa")) {
                            casasIds.add(e.getId());
                        }
                    }
                    System.out.print(String.join(", ", casasIds));
                    System.out.println("],");
                } else {
                    System.out.println("  casas: -,");
                }

                // Listar piscinas
                if (casilla.tienePiscina()) {
                    System.out.print("  piscinas: [");
                    List<String> piscinasIds = new ArrayList<>();
                    for (Edificacion e : edificaciones) {
                        if (e.getCasilla().equals(casilla) && e.obtenerTipo().equals("piscina")) {
                            piscinasIds.add(e.getId());
                        }
                    }
                    System.out.print(String.join(", ", piscinasIds));
                    System.out.println("],");
                } else {
                    System.out.println("  piscinas: -,");
                }

                // Listar pistas de deporte
                if (casilla.tienePista()) {
                    System.out.print("  pistasDeDeporte: [");
                    List<String> pistasIds = new ArrayList<>();
                    for (Edificacion e : edificaciones) {
                        if (e.getCasilla().equals(casilla) && e.obtenerTipo().equals("pista")) {
                            pistasIds.add(e.getId());
                        }
                    }
                    System.out.print(String.join(", ", pistasIds));
                    System.out.println("],");
                } else {
                    System.out.println("  pistasDeDeporte: -,");
                }

                // Mostrar alquiler
                float alquiler = casilla.getAlquiler();
                System.out.println("  alquiler: " + (long)alquiler);
                System.out.println("},");
            }
        }

        // Determinar quÃ© se puede construir
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
            System.out.println("AÃºn se puede edificar " + String.join(", ", puedeConstruir) + ".");
        }

        if (!noPuedeConstruir.isEmpty()) {
            System.out.println("No se pueden construir " + String.join(" ni ", noPuedeConstruir) + ".");
        }
    }

    public void hipotecarPropiedad(String nombreCasilla) {

        Jugador jugador = jugadores.get(turno);
        Casilla casilla = tablero.encontrarCasilla(nombreCasilla);

        if (casilla == null) {
            System.out.println("La casilla " + nombreCasilla + " no existe.");
            return;
        }

        if (!jugador.equals(casilla.getDuenho())) { //si no te pertenece la propiedad no puedes hipotecrla
            System.out.println(jugador.getNombre() + " no puede hipotecar " +
                    nombreCasilla + ". No es una propiedad que le pertenece.");
            return;
        }

        if (casilla.estaHipotecada()) { //si ya esta hipotecada, no se puede hipotecar
            System.out.println(jugador.getNombre() + " no puede hipotecar " +
                    nombreCasilla + ". Ya estÃ¡ hipotecada.");
            return;
        }

        if (!casilla.esHipotecable()) {
            System.out.println("La casilla " + nombreCasilla + " no se puede hipotecar.");
            return;
        }

        if ("solar".equalsIgnoreCase(casilla.getTipo())) { //antes de hipotecar debe vender todos los edificios
            if (casilla.tieneEdificios()) {
                System.out.println(jugador.getNombre() + " no puede hipotecar " +
                        nombreCasilla + ". Antes de hipotecar la propiedad se " +
                        "deberÃ¡n vender todos los edificios.");
                return;
            }
        }

        float valorHipoteca = casilla.getHipoteca();
        jugador.sumarFortuna(valorHipoteca);
        casilla.hipotecar();

        String nombreGrupo = (casilla.getGrupo() != null) ? casilla.getGrupo().getNombre() : "sin grupo"; // para el mensaje si la casilla no teiene grupo

        System.out.println(jugador.getNombre() + " recibe " + (long)valorHipoteca +
                "â‚¬ por la hipoteca de " + nombreCasilla +
                ". No puede recibir alquileres ni edificar en el grupo " +
                nombreGrupo + ".");

        if (jugador.getDeudaPendiente() > 0) {
            casilla.procesarPagoDeuda(jugador, tablero);
        }
    }

    public void deshipotecarPropiedad(String nombreCasilla) {

        //para deshipotecar debe pagar la misma cantidad de dinero que la que gano al hipotecarla
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
                    nombreCasilla + ". No estÃ¡ hipotecada.");
            return;
        }

        float costoDeshipoteca = casilla.getHipoteca();

        if (jugador.getFortuna() < costoDeshipoteca) {
            System.out.println(jugador.getNombre() + " no tiene suficiente dinero para " +
                    "deshipotecar " + nombreCasilla + ". Necesita " +
                    (long)costoDeshipoteca + "â‚¬.");
            return;
        }

        jugador.restarFortuna(costoDeshipoteca); //restamos, como en compra
        jugador.sumarGastos(costoDeshipoteca);
        casilla.deshipotecar();

        String nombreGrupo = (casilla.getGrupo() != null) ?
                casilla.getGrupo().getNombre() : "sin grupo";

        System.out.println(jugador.getNombre() + " paga " + (long)costoDeshipoteca +
                "â‚¬ por deshipotecar " + nombreCasilla +
                ". Ahora puede recibir alquileres y edificar en el grupo " +
                nombreGrupo + ".");
    }

    public void venderPropiedad(String tipo, String nombreCasilla, int cantidad) {

        Jugador jugadorActual = jugadores.get(turno); //el jugador actual
        Casilla c = tablero.encontrarCasilla(nombreCasilla); //cogemos la casilla que nos introdujo el jugador por linea de comandos

        if (c == null) {
            System.out.println("No existe la casilla " + nombreCasilla + ".");
            return;
        }

        //guardar el nÃºmero de edificaciones ANTES de vender
        int casasAntes = c.getNumCasas();
        boolean hotelAntes = c.tieneHotel();
        boolean piscinaAntes = c.tienePiscina();
        boolean pistaAntes = c.tienePista();

        // Llamar al mÃ©todo de venta en Casilla
        c.venderEdificacion(tipo, jugadorActual, cantidad);

        // DESPUÃ‰S de vender, eliminar de las listas globales segÃºn lo que cambiÃ³
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

    // MÃ©todo auxiliar para eliminar edificaciones
    public void eliminarEdificaciones(Casilla casilla, String tipo, int cantidad, Jugador jugador) {

        List<Edificacion> aEliminar = new ArrayList<>();
        int eliminadas = 0;

        for (Edificacion e : edificaciones) {
            if (e.getCasilla().equals(casilla) &&
                    e.obtenerTipo().equals(tipo) &&
                    eliminadas < cantidad) {
                aEliminar.add(e);
                eliminadas++;
            }
        }
        // Eliminar de ambas listas, de la global de edificaciones y de las del jugador
        edificaciones.removeAll(aEliminar);
        jugador.getEdificaciones().removeAll(aEliminar);
    }


    private void mostrarEstadisticasUnJugador(String nombreJugador) {
        for (Jugador j : jugadores) {
            if (j.getNombre().equalsIgnoreCase(nombreJugador)) {
                System.out.println("$> estadisticas " + j.getNombre());
                System.out.println("{");
                System.out.println("dineroInvertido: " + (long) j.getDineroInvertido() + ","); //cada que compra una casilla (Juego sumarDineroInvertido)
                System.out.println("pagoTasasEImpuestos: " + (long) j.getPagoTasasEImpuestos() + ","); //cada que paga impuestos al salir de la carcel o si caigo en impuestos
                System.out.println("pagoDeAlquileres: " + (long) j.getPagoDeAlquileres() + ",");
                System.out.println("cobroDeAlquileres: " + (long) j.getCobroDeAlquileres() + ",");
                System.out.println("pasarPorCasillaDeSalida: " + (long) j.getPasarPorCasillaDeSalida() + ",");
                System.out.println("premiosInversionesOBote: " + (long) j.getPremiosInversionesOBote() + ",");
                System.out.println("vecesEnLaCarcel: " + j.getVecesEnLaCarcel());
                System.out.println("}");
                return;
            }
        }
        System.out.println("No se encontrÃ³ ningÃºn jugador con el nombre '" + nombreJugador + "'.");
    }

    private void mostrarEstadisticasJuego() {

        // Busca quÃ© casilla individual ha generado mÃ¡s ingresos por alquileres
        Casilla casillaMasRentable = null; // Guarda la casilla ganadora
        float maxRenta = -1; // Inicializa en -1 para que cualquier valor (incluso 0) sea mayor

        // Recorre todos los jugadores y sus propiedades
        for (Jugador j : jugadores) {
            for (Casilla c : j.getPropiedades()) { // Itera entre las casillas que estÃ¡n vendidas
                float renta = c.getIngresosGenerados(); // Obtiene el total acumulado de alquileres cobrados
                if (renta > maxRenta) { // Si es mÃ¡s rentable que la actual campeona
                    maxRenta = renta; // Actualiza el mÃ¡ximo
                    casillaMasRentable = c; // Guarda esta casilla como la mÃ¡s rentable
                }
            }
        }

        // Busca quÃ© grupo de color ha generado mÃ¡s ingresos en total (suma de todas sus casillas)
        Grupo grupoMasRentable = null; // Guarda el grupo ganador
        float maxGrupo = -1; // Inicializa en -1 para comparar

        for (Grupo g : tablero.getGrupos()) { // Recorre todos los grupos del tablero (rojo, verde, azul, etc.)
            float total = 0; // Acumulador para sumar los ingresos de todas las casillas del grupo

            for (Casilla c : g.getMiembros()) { // Recorre cada casilla del grupo
                total += c.getIngresosGenerados(); // Suma los ingresos que cada casilla ha generado
                // Nota: cada vez que se paga un alquiler tambiÃ©n se suma en evaluarCasilla()
            }

            if (total > maxGrupo) { // Si este grupo generÃ³ mÃ¡s que el campeÃ³n actual
                maxGrupo = total; // Actualiza el mÃ¡ximo
                grupoMasRentable = g; // Guarda este grupo como el mÃ¡s rentable
            }
        }

        // Busca en quÃ© casilla han caÃ­do mÃ¡s veces los jugadores
        Casilla casillaMasFrecuentada = null; // Guarda la casilla mÃ¡s visitada
        int maxVisitas = -1; // Inicializa en -1 para comparar

        for (Casilla c : tablero.getCasillas()) { // Recorre todas las casillas del tablero (40 casillas)
            if (c.getVecesVisitada() > maxVisitas) { // Si esta casilla ha sido mÃ¡s visitada
                maxVisitas = c.getVecesVisitada(); // Actualiza el mÃ¡ximo de visitas
                casillaMasFrecuentada = c; // Guarda esta casilla como la mÃ¡s frecuentada
            }
        }
        //el contador vecesVisitada se incrementa en Casilla.evaluarCasilla() cada vez que alguien cae

        // ========== 4. Jugador con mÃ¡s vueltas ==========
        // Busca quÃ© jugador ha dado mÃ¡s vueltas completas al tablero
        Jugador jugadorMasVueltas = null; // Guarda el jugador ganador
        int maxVueltas = 0; // Inicializa en 0 (nadie empieza con vueltas)

        for (Jugador j : jugadores) { // Recorre todos los jugadores
            if (j.getVueltas() > maxVueltas) { // Si este jugador tiene mÃ¡s vueltas
                maxVueltas = j.getVueltas(); // Actualiza el mÃ¡ximo
                jugadorMasVueltas = j; // Guarda este jugador
            }
        }
        //las vueltas se incrementan al pasar por la casilla Salida

        // Calcula quÃ© jugador tiene mÃ¡s patrimonio total: fortuna en efectivo + valor de propiedades + edificaciones
        Jugador jugadorEnCabeza = null; // Guarda el jugador mÃ¡s rico
        float maxValorTotal = -1; // Inicializa en -1 para comparar

        for (Jugador j : jugadores) { // Recorre todos los jugadores
            float valorPropiedades = 0; // Acumulador para calcular el valor de todas sus propiedades

            // Calcula el valor total de las propiedades del jugador
            for (Casilla c : j.getPropiedades()) { // Recorre cada propiedad del jugador
                valorPropiedades += c.getValor(); // Suma el valor base de la casilla

                // Suma el valor de las casas construidas
                if(c.getNumCasas() > 0){
                    valorPropiedades += c.getNumCasas() * c.getPrecioCasa(); // Cada casa suma su valor
                }

                // Suma el valor de las edificaciones especiales
                if (c.tieneHotel()) valorPropiedades += c.getPrecioHotel(); // AÃ±ade valor del hotel
                if (c.tienePiscina()) valorPropiedades += c.getPrecioPiscina(); // AÃ±ade valor de piscina
                if (c.tienePista()) valorPropiedades += c.getPrecioPista(); // AÃ±ade valor de pista
            }

            // Calcula el patrimonio total: dinero en efectivo + valor de propiedades y edificios
            float valorTotal = j.getFortuna() + valorPropiedades;

            if (valorTotal > maxValorTotal) { // Si este jugador es mÃ¡s rico que el actual campeÃ³n
                maxValorTotal = valorTotal; // Actualiza el mÃ¡ximo
                jugadorEnCabeza = j; // Guarda este jugador como el lÃ­der
            }
        }

        // Muestra todas las estadÃ­sticas en formato
        System.out.println("$> estadisticas");
        System.out.println("{");


        // Si la variable es null, muestra "-", si no, muestra el nombre
        System.out.println("casillaMasRentable: " + (casillaMasRentable != null ? casillaMasRentable.getNombre() : "-") + ",");//usa operador ternario: (condiciÃ³n ? siVerdadero : siFalso)
        System.out.println("grupoMasRentable: " + (grupoMasRentable != null ? grupoMasRentable.getNombre() : "-") + ",");
        System.out.println("casillaMasFrecuentada: " + (casillaMasFrecuentada != null ? casillaMasFrecuentada.getNombre() : "-") + ",");
        System.out.println("jugadorMasVueltas: " + (jugadorMasVueltas != null ? jugadorMasVueltas.getNombre() : "-") + ",");
        System.out.println("jugadorEnCabeza: " + (jugadorEnCabeza != null ? jugadorEnCabeza.getNombre() : "-")); // Sin coma (Ãºltimo elemento)

        System.out.println("}");
    }


}