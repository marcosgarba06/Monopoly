package monopoly;

import monopoly.Cartas.Carta;
import monopoly.Casillas.Propiedades.Propiedad;
import monopoly.Casillas.Propiedades.Solar;
import partida.*;
import monopoly.Casillas.*;

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
            System.out.println("Ya hay 4 jugadores. No se pueden crear mas.");
            return;
        }
        if (nombre == null || nombre.isBlank()) { //el nombre lo puede ser null ni vacio
            System.out.println("El nombre no puede estar vacio.");
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
            System.out.println("Ese nombre ya esta en uso. Elige otro.");
            return;
        }
        if (!avataresUsados.add(avatarElegido.toLowerCase(Locale.ROOT))) {
            System.out.println("Ese avatar ya esta en uso. Elige otro.");
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
        System.out.println("Jugadores totales: " + jugadores.size() + " (minimo 2, maximo 4)");
    }



    public void analizarComando(String comando) {

        if (!juegoIniciado) { //Si estamos en el apartado de setup (la partida no empezÃ³ se usa otra opcion)
            analizarComandoSetup(comando);
            return;
        }

        Jugador actual = jugadores.get(turno);
        if (actual.tieneDeudaPendiente()) {
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
                System.out.println("Cantidad invalida. Debe ser un entero positivo.");
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
            System.out.println("No se encuentra el archivo: " + path.toAbsolutePath());
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
                            .filter(c -> (c instanceof Propiedad) && ((Propiedad) c).estaHipotecada())
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
        System.out.println("No se encuentra ningun jugador con el nombre '" + nombreBuscado + "'.");
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
        System.out.println("No se encontra ningun avatar con ID '" + ID + "'.");
    }

    private void descCasilla(String nombre) {
        //Busca la casilla en el tablero
        Casilla casilla = tablero.encontrarCasilla(nombre);
        if (casilla != null) {
            if (casilla instanceof Propiedad) {
                Propiedad p = (Propiedad) casilla;
                System.out.println(p.describir());
            } else {
                System.out.println(casilla.getNombre());
            }
        } else {
            System.out.println("No se encuentra la casilla '" + nombre + "'.");
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
            System.out.println("Estas en la carcel. Usa el comando 'salir carcel' para intentar salir.");
            return 0;
        }

        // Tirada normal (fuera de cÃ¡rcel)
        int d1 = dado1.hacerTirada();
        int d2 = dado2.hacerTirada();
        int total = d1 + d2;
        tablero.setUltimaTirada(total);
        System.out.println("Has sacado " + d1 + " y " + d2 + " total: " + total);

        // GestiÃ³n de dobles
        if (d1 == d2) {
            contadorDobles++;
            System.out.println("¡Dados dobles! (" + contadorDobles + " seguidos)");
            if (contadorDobles == 3) {
                System.out.println("¡Tres dobles seguidos! Vas directo a la carcel.");
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
            System.out.println("Has caido en 'Ir a Carcel'. Usa 'salir carcel'.");
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
            System.out.println("Valores invalidos. Usa numeros entre 1 y 6.");
            return 0;
        }

        Jugador actual = jugadores.get(turno);
        Avatar av = actual.getAvatar();

        // *** CAMBIO: No mostrar menÃº automÃ¡ticamente ***
        if (actual.isEnCarcel() || av.estaEnCarcel()) {
            System.out.println("Estas en la Carcel. Usa el comando 'salir carcel'.");
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
            System.out.println("¡Dados dobles! (" + contadorDobles + " seguidos)");
            if (contadorDobles == 3) {
                System.out.println("¡Tres dobles seguidos! Vas directo a la carcel.");
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
            System.out.println("Has caido en 'Ir a Carcel'. Usa 'salir carcel'.");
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


    private void salirCarcel(Jugador jugador) {
        // Verificar que estÃ¡ en cÃ¡rcel
        if (!jugador.isEnCarcel() && !jugador.getAvatar().estaEnCarcel()) {
            System.out.println("No estas en la Carcel.");
            return;
        }

        // Verificar que no se haya intentado ya este turno
        if (intentoSalirCarcel) {
            System.out.println("Ya has intentado salir de la Carcel este turno.");
            return;
        }

        Avatar av = jugador.getAvatar();
        int turnosEnCarcel = av.getTurnosEnCarcel();

        System.out.println("\n=== ESTAS EN LA CARCEL ===");
        System.out.println("Turno en Carcel: " + (turnosEnCarcel + 1) + "/3");

        // *** CASO 1: Tercer turno â†’ PAGO OBLIGATORIO ***
        if (turnosEnCarcel >= 2) {
            System.out.println("\n¡Has estado 3 turnos en la carcel!");
            System.out.println("Debes pagar 500.000‚ obligatoriamente y avanzar con tu tirada.");

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
            System.out.println("Has pagado 500.000â‚¬ y sales de la Carcel.");

            // Tirar dados y moverse
            int d1 = dado1.hacerTirada();
            int d2 = dado2.hacerTirada();
            int total = d1 + d2;
            tablero.setUltimaTirada(total);
            System.out.println("Tiras los dados: " + d1 + " y " + d2 + " al total: " + total);
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
            System.out.println("2) Usar carta de 'Salir de la Carcel' (sales, pero NO te mueves)");
            System.out.println("3) Intentar sacar dobles (si sacas dobles: sales y mueves)");
            System.out.print("Elige opcion: ");

            String opcion = sc.nextLine().trim();

            // OPCIÃ“N 1: PAGAR
            if (opcion.equals("1")) {
                if (jugador.getFortuna() < 500000) {
                    System.out.println("No tienes suficiente dinero. Elige otra opcion.");
                    continue;
                }

                jugador.restarFortuna(500000);
                jugador.sumarGastos(500000);
                jugador.sumarPagoTasasEImpuestos(500000);
                av.setEnCarcel(false);
                jugador.setEnCarcel(false);
                av.setTurnosEnCarcel(0);
                System.out.println("Has pagado 500.000‚ y sales de la carcel.");
                System.out.println("Tira los dados.");

                tirado = false; // asÃ­ puede tirar los dados
                intentoSalirCarcel = true;
                break;
            }

            // OPCIÃ“N 2: CARTA
            else if (opcion.equals("2")) {
                if (!jugador.usarCartaSalirCarcel()) {
                    System.out.println("No tienes ninguna carta. Elige otra opcion.");
                    continue;
                }

                av.setEnCarcel(false);
                jugador.setEnCarcel(false);
                av.setTurnosEnCarcel(0);
                System.out.println("Has usado una carta y sales de la Carcel.");
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
                    System.out.println("¡DOBLES! Sales de la Carcel y avanzas " + total + " casillas.");
                    av.moverAvatar(total, tablero);
                    System.out.println(tablero);
                } else {
                    // NO DOBLES â†’ Incrementa turno en cÃ¡rcel
                    av.incrementarTurnosEnCarcel();
                    System.out.println("No son dobles. Pierdes el turno.");
                    System.out.println("Llevas " + av.getTurnosEnCarcel() + " turno(s) en la Carcel.");
                }

                tirado = true;
                intentoSalirCarcel = true;
                break;
            } else {
                System.out.println("Opcion no valida. Elige 1, 2 o 3.");
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
            System.out.println("\n  Estas en la Carcel. Usa 'salir carcel' para intentar salir.");
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
            System.out.println("{");
            System.out.println("nombre: " + c.getNombre() + ",");
            System.out.println("tipo: " + c.getTipo() + ",");

            if ("solar".equalsIgnoreCase(c.getTipo())) {
                System.out.println("grupo: " + c.getGrupo() + ",");
            }

            // Cast a Propiedad para acceder a getValor()
            if (c instanceof Propiedad) {
                Propiedad p = (Propiedad) c;
                System.out.println("valor: " + (long) p.getValor());
            } else {
                System.out.println("valor: -"); // si no es propiedad, no tiene valor
            }

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
                    .filter(c -> (c instanceof Propiedad) && ((Propiedad) c).estaHipotecada())
                    // filtramos solo las que estÃ¡n hipotecadas
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
                    System.out.println("Error: No se encontra jugador activo.");
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
            System.out.println("CUIDADO " + actual.getNombre() + " esta en la Carcel.");
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
        System.out.println("Modo configuracion de partida:"); //comandos que se pueden usar si la partida no estÃ¡ empezada
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
            System.out.println("No puedes tener mas de 4 jugadores.");
        } else {
            System.out.println("Iniciando partida...");

            Carta.resetearContadores();
            Carta.setJugadores(jugadores);
            Carta.inicializarMazos();

            juegoIniciado = true;
        }
    }

    public void comprarCasilla(String nombreCasilla) {
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

        // Solo propiedades son comprables
        if (!(casilla instanceof Propiedad)) {
            System.out.println("Esta casilla no se puede comprar.");
            return;
        }

        Propiedad propiedad = (Propiedad) casilla;

        if (!propiedad.estaEnVenta()) {
            System.out.println("Esta casilla ya tiene dueño.");
            return;
        }

        if (jugador.getFortuna() < propiedad.getValor()) {
            System.out.println("No tienes suficiente dinero para comprar esta casilla.");
            return;
        }

        // Compra
        jugador.restarFortuna(propiedad.getValor());
        jugador.sumarDineroInvertido(propiedad.getValor());
        propiedad.setDuenho(jugador);
        jugador.anhadirPropiedad(propiedad);

        System.out.println("Has comprado " + propiedad.getNombre() + " por " + (long) propiedad.getValor() + "€.");
    }

    private void edificarPropiedad(String tipo) {
        Jugador jugadorActual = jugadores.get(turno);
        Avatar avatar = jugadorActual.getAvatar();
        Casilla casilla = avatar.getCasilla();

        if (!(casilla instanceof Solar)) {
            System.out.println("No puedes edificar en esta casilla porque no es un solar.");
            return;
        }

        Solar solar = (Solar) casilla;

        if (solar.getDuenho() == null || !solar.getDuenho().equals(jugadorActual)) {
            System.out.println("No eres dueño de " + solar.getNombre() + ", no puedes edificar en ella.");
            return;
        }

        if (!jugadorActual.poseeGrupoCompleto(solar, tablero)) {
            System.out.println("No puedes construir aquí. No posees todo el grupo " +
                    solar.getGrupo().getNombre() + ".");
            return;
        }

        if (solar.estaHipotecada()) {
            System.out.println("No puedes edificar en " + solar.getNombre() + " porque está hipotecada.");
            return;
        }

        String tipoNormalizado = tipo.toLowerCase().trim();

        switch (tipoNormalizado) {
            case "casa":
                Casa nuevaCasa = new Casa(generarId("casa"), jugadorActual, solar, (float) solar.getPrecioCasa());
                if (nuevaCasa.puedeEdificar(jugadorActual, solar)) {
                    nuevaCasa.construir(jugadorActual, solar);
                    edificaciones.add(nuevaCasa);
                    jugadorActual.agregarEdificacion(nuevaCasa);
                }
                break;

            case "hotel":
                Hotel nuevoHotel = new Hotel(generarId("hotel"), jugadorActual, solar, (float) solar.getPrecioHotel());
                if (nuevoHotel.puedeEdificar(jugadorActual, solar)) {
                    List<Edificacion> casasAEliminar = new ArrayList<>();
                    for (Edificacion e : edificaciones) {
                        if (e.getSolar().equals(solar) && e instanceof Casa) {
                            casasAEliminar.add(e);
                        }
                    }
                    edificaciones.removeAll(casasAEliminar);
                    jugadorActual.getEdificaciones().removeAll(casasAEliminar);

                    // Ahora construir el hotel
                    nuevoHotel.construir(jugadorActual, solar);
                    edificaciones.add(nuevoHotel);
                    jugadorActual.agregarEdificacion(nuevoHotel);
                }
                break;

            case "piscina":
                Piscina nuevaPiscina = new Piscina(generarId("piscina"), jugadorActual, solar, (float) solar.getPrecioPiscina());
                if (nuevaPiscina.puedeEdificar(jugadorActual, solar)) {
                    nuevaPiscina.construir(jugadorActual, solar);
                    edificaciones.add(nuevaPiscina);
                    jugadorActual.agregarEdificacion(nuevaPiscina);
                }
                break;

            case "pista":
            case "pista deporte":
                Pista nuevaPista = new Pista(generarId("pista"), jugadorActual, solar, (float) solar.getPrecioPista());
                if (nuevaPista.puedeEdificar(jugadorActual, solar)) {
                    nuevaPista.construir(jugadorActual, solar);
                    edificaciones.add(nuevaPista);
                    jugadorActual.agregarEdificacion(nuevaPista);
                }
                break;

            default:
                System.out.println("Tipo de edificación no válido. Usa: casa, hotel, piscina o pista deporte");
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
            System.out.println("  casilla: " + e.getSolar().getNombre() + ",");
            System.out.println("  grupo: " + e.getSolar().getGrupo().getNombre() + ",");
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

        ArrayList<Propiedad> casillasGrupo = (ArrayList<Propiedad>) grupo.getMiembros();

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
            if (casilla.getDuenho() != null && "solar".equalsIgnoreCase(casilla.getTipo())) {

                Solar solar = (Solar) casilla;

                System.out.println("{");
                System.out.println("  propiedad: " + solar.getNombre() + ",");

                // Hoteles
                if (solar.tieneHotel()) {
                    System.out.print("  hoteles: [");
                    List<String> hotelesIds = new ArrayList<>();
                    for (Edificacion e : edificaciones) {
                        if (e.getSolar().equals(solar) && e.obtenerTipo().equals("hotel")) {
                            hotelesIds.add(e.getId());
                        }
                    }
                    System.out.print(String.join(", ", hotelesIds));
                    System.out.println("],");
                } else {
                    System.out.println("  hoteles: -,");
                }

                // Casas
                if (solar.getNumCasas() > 0) {
                    System.out.print("  casas: [");
                    List<String> casasIds = new ArrayList<>();
                    for (Edificacion e : edificaciones) {
                        if (e.getSolar().equals(solar) && e.obtenerTipo().equals("casa")) {
                            casasIds.add(e.getId());
                        }
                    }
                    System.out.print(String.join(", ", casasIds));
                    System.out.println("],");
                } else {
                    System.out.println("  casas: -,");
                }

                // Piscinas
                if (solar.tienePiscina()) {
                    System.out.print("  piscinas: [");
                    List<String> piscinasIds = new ArrayList<>();
                    for (Edificacion e : edificaciones) {
                        if (e.getSolar().equals(solar) && e.obtenerTipo().equals("piscina")) {
                            piscinasIds.add(e.getId());
                        }
                    }
                    System.out.print(String.join(", ", piscinasIds));
                    System.out.println("],");
                } else {
                    System.out.println("  piscinas: -,");
                }

                // Pistas
                if (solar.tienePista()) {
                    System.out.print("  pistasDeDeporte: [");
                    List<String> pistasIds = new ArrayList<>();
                    for (Edificacion e : edificaciones) {
                        if (e.getSolar().equals(solar) && e.obtenerTipo().equals("pista")) {
                            pistasIds.add(e.getId());
                        }
                    }
                    System.out.print(String.join(", ", pistasIds));
                    System.out.println("],");
                } else {
                    System.out.println("  pistasDeDeporte: -,");
                }

                // Alquiler
                float alquiler = solar.alquiler(tablero.getUltimaTirada());
                System.out.println("  alquiler: " + (long) alquiler);
                System.out.println("},");
            }
        }

        // Determinar qué se puede construir
        for (Casilla casilla : casillasGrupo) {
            if (casilla.getDuenho() != null && grupo.perteneceEnteramenteA(casilla.getDuenho()) && "solar".equalsIgnoreCase(casilla.getTipo())) {
                Solar solar = (Solar) casilla;
                if (solar.getNumCasas() < 4) puedeCasas = true;
                if (!solar.tieneHotel() && solar.getNumCasas() == 4) puedeHoteles = true;
                if (solar.tieneHotel() && !solar.tienePiscina()) puedePiscinas = true;
                if (solar.tieneHotel() && solar.tienePiscina() && !solar.tienePista()) puedePistas = true;
            }
        }

        // Mostrar resumen
        System.out.println();
        List<String> puedeConstruir = new ArrayList<>();
        List<String> noPuedeConstruir = new ArrayList<>();

        if (puedePiscinas) puedeConstruir.add("piscinas"); else noPuedeConstruir.add("piscinas");
        if (puedePistas) puedeConstruir.add("pistas de deporte"); else noPuedeConstruir.add("pistas de deporte");
        if (puedeCasas) puedeConstruir.add("casas"); else noPuedeConstruir.add("casas");
        if (puedeHoteles) puedeConstruir.add("hoteles"); else noPuedeConstruir.add("hoteles");

        if (!puedeConstruir.isEmpty()) {
            System.out.println("Aún se puede edificar " + String.join(", ", puedeConstruir) + ".");
        }
        if (!noPuedeConstruir.isEmpty()) {
            System.out.println("No se pueden construir " + String.join(" ni ", noPuedeConstruir) + ".");
        }
    }


    private void hipotecarPropiedad(String nombreCasilla) {
        Jugador jugador = jugadores.get(turno);
        Casilla casilla = tablero.encontrarCasilla(nombreCasilla);

        if (!(casilla instanceof Propiedad)) {
            System.out.println("La casilla '" + nombreCasilla + "' no es hipotecable.");
            return;
        }

        Propiedad propiedad = (Propiedad) casilla;

        if (!propiedad.perteneceAJugador(jugador)) {
            System.out.println("No eres dueño de " + propiedad.getNombre() + ".");
            return;
        }

        if (propiedad.estaHipotecada()) {
            System.out.println("La propiedad ya está hipotecada.");
            return;
        }

        propiedad.hipotecar(); //Hipotecar en Propiedad, metodo que realiza la hipoteca
    }

    private void deshipotecarPropiedad(String nombreCasilla) {
        Jugador jugador = jugadores.get(turno);
        Casilla casilla = tablero.encontrarCasilla(nombreCasilla);

        if (!(casilla instanceof Propiedad)) {
            System.out.println("La casilla '" + nombreCasilla + "' no es una propiedad.");
            return;
        }

        Propiedad propiedad = (Propiedad) casilla;

        if (!propiedad.perteneceAJugador(jugador)) {
            System.out.println("No eres dueño de " + propiedad.getNombre() + ".");
            return;
        }

        propiedad.deshipotecar(); //Deshipotecar en Propiedad, metodo que realiza la deshipoteca
    }


    private void venderPropiedad(String tipo, String nombreCasilla, int cantidad) {
        Jugador jugador = jugadores.get(turno);
        Casilla casilla = tablero.encontrarCasilla(nombreCasilla);

        if (!(casilla instanceof Solar)) {
            System.out.println("La casilla '" + nombreCasilla + "' no es un solar edificable.");
            return;
        }

        Solar solar = (Solar) casilla;

        if (!solar.getDuenho().equals(jugador)) {
            System.out.println("No eres dueño de " + solar.getNombre() + ".");
            return;
        }

        switch (tipo.toLowerCase()) {
            case "casa":
                solar.venderCasa(jugador, cantidad);
                break;
            case "hotel":
                solar.venderHotel(jugador);
                break;
            case "piscina":
                solar.venderPiscina(jugador);
                break;
            case "pista":
                solar.venderPista(jugador);
                break;
            default:
                System.out.println("Tipo de edificación no válido. Usa: casa, hotel, piscina o pista.");
                break;
        }
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

        // 1. Casilla más rentable
        Propiedad casillaMasRentable = null;
        float maxRenta = -1;

        for (Jugador j : jugadores) {
            for (Propiedad p : j.getPropiedades()) {
                float renta = p.getIngresosGenerados();
                if (renta > maxRenta) {
                    maxRenta = renta;
                    casillaMasRentable = p;
                }
            }
        }

        // 2. Grupo más rentable
        Grupo grupoMasRentable = null;
        float maxGrupo = -1;

        for (Grupo g : tablero.getGrupos()) {
            float total = 0;
            for (Casilla c : g.getMiembros()) {
                if (c instanceof Propiedad) {
                    total += ((Propiedad) c).getIngresosGenerados();
                }
            }
            if (total > maxGrupo) {
                maxGrupo = total;
                grupoMasRentable = g;
            }
        }

        // 3. Casilla más frecuentada
        Casilla casillaMasFrecuentada = null;
        int maxVisitas = -1;

        for (Casilla c : tablero.getCasillas()) {
            if (c.getVecesVisitada() > maxVisitas) {
                maxVisitas = c.getVecesVisitada();
                casillaMasFrecuentada = c;
            }
        }

        // 4. Jugador con más vueltas
        Jugador jugadorMasVueltas = null;
        int maxVueltas = 0;

        for (Jugador j : jugadores) {
            if (j.getVueltas() > maxVueltas) {
                maxVueltas = j.getVueltas();
                jugadorMasVueltas = j;
            }
        }

        // 5. Jugador con más patrimonio total
        Jugador jugadorEnCabeza = null;
        float maxValorTotal = -1;

        for (Jugador j : jugadores) {
            float valorPropiedades = 0;

            for (Propiedad p : j.getPropiedades()) {
                valorPropiedades += p.getValor();

                if (p instanceof Solar) {
                    Solar s = (Solar) p;
                    valorPropiedades += s.getNumCasas() * s.getPrecioCasa();
                    if (s.tieneHotel()) valorPropiedades += s.getPrecioHotel();
                    if (s.tienePiscina()) valorPropiedades += s.getPrecioPiscina();
                    if (s.tienePista()) valorPropiedades += s.getPrecioPista();
                }
            }

            float valorTotal = j.getFortuna() + valorPropiedades;
            if (valorTotal > maxValorTotal) {
                maxValorTotal = valorTotal;
                jugadorEnCabeza = j;
            }
        }

        // Mostrar resultados
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