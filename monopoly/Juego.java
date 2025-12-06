package monopoly;

import monopoly.Cartas.Carta;
import monopoly.Casillas.Propiedades.Propiedad;
import monopoly.Casillas.Propiedades.Solar;
import partida.*;
import monopoly.Casillas.*;
import monopoly.excepciones.*;
import monopoly.Interfaces.Comando;
import monopoly.Trato;
import monopoly.Interfaces.Consola;
import monopoly.Interfaces.ConsolaNormal;

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

public class Juego implements Comando { // la clase menu
    // Es buena práctica declarar la variable con el tipo de la interfaz (Consola)
    // e instanciarla con la implementación (ConsolaNormal).
    public static ConsolaNormal consola = new ConsolaNormal();

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

    private int contadorTratos = 0;

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

    public void iniciarPartida() throws excepcionMonopoly {

        menuSetUp(); //imprime el menu de inicializacion

        while (!juegoIniciado) { //si aun no se ha iniciado la partida

            String comando = consola.leer("[setup]> ");
            analizarComando(comando); // redirige a analizarComandoSetup() mientras no haya empezado el juego
        }

        // Cuando hay 2-4 jugadores y se ejecuta 'empezar', arranca la fase de juego
        iniciarJuego();

    }

    @Override
    public void iniciarJuego() throws excepcionMonopoly {

        juegoIniciado = true; //El juego ha iniciado correctamente
        consola.imprimir("Comandos disponibles:");
        menuComandos(); //imprime el menu de comados

        while (true) {
            Jugador actual = jugadores.get(turno); //obtiene el jugador que tiene el turno actual

            consola.imprimir("\nTurno de " + actual.getNombre());
            String comando =  consola.leer("> ");
            analizarComando(comando); //analiza el comando
        }
    }

    //metodo para crear un jugador con su avatar asociado
    @Override
    public void crearJugador(String nombre, String avatarElegido) {

        if (jugadores.size() >= 4) { //no crea mas si hay 4, LIMITE MAX 4
            consola.imprimir("Ya hay 4 jugadores. No se pueden crear mas.");
            return;
        }
        if (nombre == null || nombre.isBlank()) { //el nombre lo puede ser null ni vacio
            consola.imprimir("El nombre no puede estar vacio.");
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
            consola.imprimir("Avatar no válido. Avatares permitidos: coche, sombrero, pelota, esfinge");
            return;
        }

        if (!nombresUsados.add(nombre.toLowerCase(Locale.ROOT))) { //garantiza que el nombre es unico
            consola.imprimir("Ese nombre ya esta en uso. Elige otro.");
            return;
        }
        if (!avataresUsados.add(avatarElegido.toLowerCase(Locale.ROOT))) {
            consola.imprimir("Ese avatar ya esta en uso. Elige otro.");
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

        consola.imprimir("Jugador " + nombre + " creado con avatar " + avatarElegido.toLowerCase(Locale.ROOT));

        consola.imprimir("Jugadores totales: " + jugadores.size() + " (minimo 2, maximo 4)");
    }



    public void analizarComando(String comando) throws excepcionMonopoly {
        try {
            if (!juegoIniciado) { //Si estamos en el apartado de setup (la partida no empezÃ³ se usa otra opcion)
                analizarComandoSetup(comando);
                return;
            }

            Jugador actual = jugadores.get(turno);

            if (actual.isBancarrota()) {
                throw new excepEstJugEnBancarrota(actual.getNombre());
            }

            if (actual.tieneDeudaPendiente()) {
                String cmd = comando.trim().toLowerCase();

                // Solo permitir hipotecar y declarar bancarrota
                if (!cmd.startsWith("hipotecar") && !cmd.equals("declarar bancarrota")) {
                    consola.imprimir("\nTienes una deuda pendiente:");
                    consola.imprimir("Debes pagar: " + (long) actual.getDeudaPendiente() + "â‚¬");
                    consola.imprimir("Tu fortuna: " + (long) actual.getFortuna() + "â‚¬");
                    consola.imprimir("Faltante: " + (long) (actual.getDeudaPendiente() - actual.getFortuna()) + "â‚¬");
                    consola.imprimir("\nComandos permitidos:");
                    consola.imprimir("  - hipotecar <casilla>");
                    consola.imprimir("  - declarar bancarrota");
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

            throw new excepComandoInvalido("Comando introducido incorrecto");
        } catch (excepSinRecDinero e) {
            consola.imprimir(e.getMessage());
            consola.imprimir("Opciones:");
            consola.imprimir("   - Hipoteca propiedades: 'hipotecar <casilla>'");
            consola.imprimir("   - Declara bancarrota: 'declarar bancarrota'");

        } catch (excepSinRecPropInsuficientes e) {
            consola.imprimir(e.getMessage());

        } catch (excepSinRecursos e) {
            consola.imprimir(e.getMessage());

        } catch (excepTransPropHipotecada e) {
            consola.imprimir(e.getMessage());
            consola.imprimir("Sugerencia: Deshipoteca la propiedad con 'deshipotecar <casilla>'");

        } catch (excepTransEdNoPermitida e) {
            consola.imprimir(e.getMessage());
            consola.imprimir("Sugerencia: Verifica que posees el grupo completo y no hay hipotecas");

        } catch (excepTransPropNoPermitida e) {
            consola.imprimir(e.getMessage());

        } catch (excepTransaccion e) {
            consola.imprimir(e.getMessage());

        } catch (excepEstTurnoInvalido e) {
            consola.imprimir(e.getMessage());

        } catch (excepEstJugEnCarcel e) {
            consola.imprimir(e.getMessage());
            consola.imprimir("Usa 'salir carcel' para intentar salir de la cárcel");

        } catch (excepEstJugEnBancarrota e) {
            consola.imprimir(e.getMessage());

        } catch (excepEstadoJuego e) {
            consola.imprimir(e.getMessage());

        } catch (excepNoExisteObjeto e) {
            consola.imprimir(e.getMessage());
            consola.imprimir("💡 Sugerencia: Usa 'ver tablero' o 'listar enventa' para ver las opciones");

        } catch (excepComandoInvalido e) {
            consola.imprimir(e.getMessage());
            consola.imprimir("Comandos disponibles:");
            menuComandos();

        } catch (excepcionMonopoly e) {
            consola.imprimir("Error: " + e.getMessage());

        } catch (Exception e) {
            consola.imprimir("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
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
            consola.imprimir("Saliendo...");
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
        consola.imprimir("EstÃ¡s en modo configuraciÃ³n. Comandos:");
        menuSetUp();
    }

    private boolean procesarComandoDados(String comandoOriginal) throws excepcionMonopoly {
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
//        \\s*\\+\\s* // El signo + con espacios opcionales
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
            consola.imprimir("Uso: comandos <ruta/al/archivo.txt>");
        } else {
            String rutaArchivo = partes[1].trim();
            ejecutarComandosDesdeArchivo(rutaArchivo);
        }
        return true;
    }

    private boolean procesarComandoSimple(String comandoMinusculas) throws excepcionMonopoly {

        switch (comandoMinusculas) {
            case "listar jugadores":
            case "jugadores":
                listarJugadores();
                return true;

            case "ver tablero":
                verTablero();
                return true;

            case "salir":
                consola.imprimir("Saliendo del juego...");
                System.exit(0);
                return true;

            case "jugador":
                indicarTurno();
                return true;

            case "salir carcel":
                salirCarcel();
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
                declararBancarrota();
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

    private boolean procesarComandoConParametros(String comandoMinusculas, String comandoOriginal) throws excepcionMonopoly {
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
            descJugador(palabras[2]);
            return true;
        }

        // "describir <casilla>" (puede tener espacios en el nombre)
        if (palabras.length == 2 && palabras[0].equals("describir")) {
            // Extraer el nombre completo de la casilla (despuÃ©s de "describir ")
            String nombreCasilla = comandoOriginal.substring(comandoOriginal.indexOf(' ') + 1).trim();

            if (nombreCasilla.isEmpty()) {
                consola.imprimir("Uso: describir <casilla>");
            } else {
                descCasilla(nombreCasilla);
            }
            return true;
        }
        // En procesarComandoConParametros
        if (comandoMinusculas.startsWith("trato ")) {
            return procesarComandoTrato(comandoMinusculas, comandoOriginal);
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
                consola.imprimir("Cantidad invalida. Debe ser un entero positivo.");
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

    //esta funcion procesa el comando trato, y permite ejecutar la funcion proponerTrato con el nombre del receptor (al que se le propone el trato) y el contenido del trato
    private boolean procesarComandoTrato(String comandoMinusculas, String comandoOriginal) throws excepcionMonopoly {
        // Patrón: trato <jugador>: cambiar (...)
        Pattern patronTrato = Pattern.compile(
                "^trato\\s+(\\w+):\\s*cambiar\\s*\\((.+)\\)$",
                Pattern.CASE_INSENSITIVE
        );

        Matcher m = patronTrato.matcher(comandoOriginal.trim());
        if (m.matches()) {
            String nombreReceptor = m.group(1);
            String contenidoTrato = m.group(2);
            proponerTrato(nombreReceptor, contenidoTrato);
            return true;
        }
        return false;
    }
    // Metodo helper para ejecutar comandos desde un archivo
    private void ejecutarComandosDesdeArchivo(String rutaArchivo) {
        // Crear un objeto Path a partir de la ruta recibida
        Path path = Paths.get(rutaArchivo);
        consola.imprimir("Intentando abrir archivo en: " + path.toAbsolutePath());

        if (!Files.exists(path)) { //Si exsiste el archivo entonces
            consola.imprimir("No se encuentra el archivo: " + path.toAbsolutePath());
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
                    consola.imprimir("[Turno: " + actual.getNombre() + "] [archivo:" + numLinea + "] " + comando);
                } else {
                    consola.imprimir("[archivo:" + numLinea + "] " + comando);
                }

                analizarComando(comando);
            }
        } catch (IOException e) {
            // Captura errores y los muestra
            consola.imprimir("Error leyendo el archivo: " + e.getMessage());
        } catch (excepcionMonopoly e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void descJugador(String nombreBuscado) { // Se modificó la firma para la interfaz, recibía String[]
        // String nombreBuscado = partes[2].toLowerCase(); // Lógica original usaba array

        for (Jugador j : jugadores) { //un for each para encontrar el jugador entre los que hay en la partida

            if (j.getNombre().toLowerCase().equals(nombreBuscado.toLowerCase())) { //si lo encuentra lo imprime
                consola.imprimir("$> describir jugador " + j.getNombre());
                consola.imprimir("{");
                consola.imprimir("nombre: " + j.getNombre() + ",");
                consola.imprimir("avatar: " + j.getAvatar().getId() + ",");
                consola.imprimir("fortuna: " + (long) j.getFortuna() + ",");
                consola.imprimir("Cartas para salir de la cÃ¡rcel: " + j.getCartasSalirCarcel());

                if (j.getPropiedades().isEmpty()) {
                    consola.imprimir("propiedades: -,");
                } else {
                    String props = j.getPropiedades().stream().map(c -> c.getNombre() + (c.estaHipotecada() ? " (hipotecada)" : "")).collect(Collectors.joining(", "));
                    consola.imprimir("propiedades: [" + props + "],");
                }

                if (j.getPropiedades().isEmpty()) {
                    consola.imprimir("hipotecas: -,");
                } else {
                    List<String> hipotecas = j.getPropiedades().stream()
                            .filter(c -> (c instanceof Propiedad) && ((Propiedad) c).estaHipotecada())
                            .map(Casilla::getNombre)
                            .collect(Collectors.toList());
                    if (hipotecas.isEmpty()) {
                        consola.imprimir("hipotecas: -,");
                    } else {
                        consola.imprimir("hipotecas: " + hipotecas + ",");
                    }
                }

                if (j.getEdificaciones().isEmpty()) {
                    consola.imprimir("edificios: -");
                } else {
                    List<String> ids = j.getEdificaciones().stream()
                            .map(Edificacion::getId)
                            .collect(Collectors.toList());
                    consola.imprimir("edificios: " + ids.toString());
                }

                consola.imprimir("}");
                return;
            }
        }
        consola.imprimir("No se encuentra ningun jugador con el nombre '" + nombreBuscado + "'.");
    }


    @Override
    public void descAvatar(String ID) {

        for (Avatar av : avatares) { //for each para recorrer los avatares de los jugadores creados
            if (av.getId().equalsIgnoreCase(ID)) { //si lo encuentra imprime su tipo, el jugador y la casilla en la que esta
                consola.imprimir("$> describir avatar " + ID);
                consola.imprimir("{");
                consola.imprimir("tipo: " + av.getJugador().getAvatar().getTipo() + ",");
                consola.imprimir("jugador: " + av.getJugador().getNombre() + ",");
                consola.imprimir("casilla: " + (av.getCasilla() != null ? av.getCasilla().getNombre() : "sin posiciÃ³n") + ",");
                consola.imprimir("}");
                return;
            }
        }
        consola.imprimir("No se encontra ningun avatar con ID '" + ID + "'.");
    }

    @Override
    public void descCasilla(String nombre) {
        //Busca la casilla en el tablero
        Casilla casilla = tablero.encontrarCasilla(nombre);
        if (casilla != null) {
            if (casilla instanceof Propiedad) {
                Propiedad p = (Propiedad) casilla;
                consola.imprimir(p.describir());
            } else {
                consola.imprimir(casilla.getNombre());
            }
        } else {
            consola.imprimir("No se encuentra la casilla '" + nombre + "'.");
        }
    }

    @Override
    public void lanzarDados() throws excepcionMonopoly{
        if (jugadores == null || jugadores.isEmpty()) {
            throw new excepEstadoJuego("No hay jugadores en la partida");
        }

        if (tirado) {
            throw new excepEstTurnoInvalido("tirar dados");
        }

        Jugador actual = jugadores.get(turno);
        Avatar av = actual.getAvatar();

        // *** CAMBIO CLAVE: Si estÃ¡ en carcel, NO mostrar menu aqui ***
        if (actual.isEnCarcel() || av.estaEnCarcel()) {
            throw new excepEstJugEnCarcel("tirar dados", actual.getNombre());
        }

        // Tirada normal (fuera de cÃ¡rcel)
        int d1 = dado1.hacerTirada();
        int d2 = dado2.hacerTirada();
        int total = d1 + d2;
        tablero.setUltimaTirada(total);
        consola.imprimir("Has sacado " + d1 + " y " + d2 + " total: " + total);

        // GestiÃ³n de dobles
        if (d1 == d2) {
            contadorDobles++;
            consola.imprimir("¡Dados dobles! (" + contadorDobles + " seguidos)");
            if (contadorDobles == 3) {
                consola.imprimir("¡Tres dobles seguidos! Vas directo a la carcel.");
                actual.irACarcel(tablero);
                contadorDobles = 0;
                tirado = true;
                return; // Changed return int to return void logic
            }
        } else {
            contadorDobles = 0;
        }

        // Mover avatar
        av.moverAvatar(total, tablero);

        // Si caemos en IrCarcel al movernos
        if (actual.isEnCarcel() || av.estaEnCarcel()) {
            consola.imprimir("Has caido en 'Ir a Carcel'. Usa 'salir carcel'.");
            tirado = true;
            contadorDobles = 0;
            return; // Changed return int to return void logic
        }

        // Marcar tirada completada
        tirado = true;
        if (d1 == d2 && contadorDobles < 3) {
            tirado = false; // Puede volver a tirar
        }


        consola.imprimir(tablero.toString());
    }

    @Override
    public void lanzarDadosForzados(int d1, int d2) {
        if (jugadores == null || jugadores.isEmpty()) {
            consola.imprimir("No hay jugadores en la partida.");
            return; // return 0 -> return
        }

        if (tirado) {
            consola.imprimir("Ya has tirado los dados este turno.");
            return; // return 0 -> return
        }

        if (d1 < 1 || d1 > 6 || d2 < 1 || d2 > 6) {
            consola.imprimir("Valores invalidos. Usa numeros entre 1 y 6.");
            return; // return 0 -> return
        }

        Jugador actual = jugadores.get(turno);
        Avatar av = actual.getAvatar();

        // *** CAMBIO: No mostrar menÃº automÃ¡ticamente ***
        if (actual.isEnCarcel() || av.estaEnCarcel()) {
            consola.imprimir("Estas en la Carcel. Usa el comando 'salir carcel'.");
            return; // return 0 -> return
        }

        // Guardar valores en los dados
        dado1.setValor(d1);
        dado2.setValor(d2);

        int total = d1 + d2;
        tablero.setUltimaTirada(total);
        consola.imprimir("Has forzado " + d1 + " y " + d2 + " â†’ total: " + total);

        // GestiÃ³n de dobles
        if (d1 == d2) {
            contadorDobles++;
            consola.imprimir("¡Dados dobles! (" + contadorDobles + " seguidos)");
            if (contadorDobles == 3) {
                consola.imprimir("¡Tres dobles seguidos! Vas directo a la carcel.");
                actual.irACarcel(tablero);
                contadorDobles = 0;
                tirado = true;
                return; // return total -> return
            }
        } else {
            contadorDobles = 0;
        }

        av.moverAvatar(total, tablero);

        if (actual.isEnCarcel() || av.estaEnCarcel()) {
            consola.imprimir("Has caido en 'Ir a Carcel'. Usa 'salir carcel'.");
            tirado = true;
            contadorDobles = 0;
            return; // return total -> return
        }

        tirado = true;
        if (d1 == d2 && contadorDobles < 3) {
            tirado = false;
        }

        consola.imprimir(tablero.toString());
        // return total; // Removed return
    }


    @Override
    public void salirCarcel() { // Se modificó firma (sin args) para interfaz, se obtiene jugador del turno
        Jugador jugador = jugadores.get(turno);

        // Verificar que estÃ¡ en cÃ¡rcel
        if (!jugador.isEnCarcel() && !jugador.getAvatar().estaEnCarcel()) {
            consola.imprimir("No estas en la Carcel.");
            return;
        }

        // Verificar que no se haya intentado ya este turno
        if (intentoSalirCarcel) {
            consola.imprimir("Ya has intentado salir de la Carcel este turno.");
            return;
        }

        Avatar av = jugador.getAvatar();
        int turnosEnCarcel = av.getTurnosEnCarcel();

        consola.imprimir("\n=== ESTAS EN LA CARCEL ===");
        consola.imprimir("Turno en Carcel: " + (turnosEnCarcel + 1) + "/3");

        // *** CASO 1: Tercer turno â†’ PAGO OBLIGATORIO ***
        if (turnosEnCarcel >= 2) {
            consola.imprimir("\n¡Has estado 3 turnos en la carcel!");
            consola.imprimir("Debes pagar 500.000‚ obligatoriamente y avanzar con tu tirada.");

            if (jugador.getFortuna() < 500000) {
                consola.imprimir("No tienes dinero suficiente. BANCARROTA.");
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
            consola.imprimir("Has pagado 500.000â‚¬ y sales de la Carcel.");

            // Tirar dados y moverse
            int d1 = dado1.hacerTirada();
            int d2 = dado2.hacerTirada();
            int total = d1 + d2;
            tablero.setUltimaTirada(total);
            consola.imprimir("Tiras los dados: " + d1 + " y " + d2 + " al total: " + total);
            av.moverAvatar(total, tablero);
            consola.imprimir(tablero.toString());

            tirado = true;
            intentoSalirCarcel = true;
            return;
        }

        // *** CASO 2: Turno 1 o 2 âl menu de opciones ***
        while (true) {
            consola.imprimir("\nOpciones:");
            consola.imprimir("1) Pagar 500.000$ (sales, y te puedes mover en este turno)");
            consola.imprimir("2) Usar carta de 'Salir de la Carcel' (sales, pero NO te mueves)");
            consola.imprimir("3) Intentar sacar dobles (si sacas dobles: sales y mueves)");

            String opcion = consola.leer("Elige opcion: ").trim();

            // OPCION 1: PAGAR
            if (opcion.equals("1")) {
                if (jugador.getFortuna() < 500000) {
                    consola.imprimir("No tienes suficiente dinero. Elige otra opcion.");
                    continue;
                }

                jugador.restarFortuna(500000);
                jugador.sumarGastos(500000);
                jugador.sumarPagoTasasEImpuestos(500000);
                av.setEnCarcel(false);
                jugador.setEnCarcel(false);
                av.setTurnosEnCarcel(0);
                consola.imprimir("Has pagado 500.000‚ y sales de la carcel.");
                consola.imprimir("Tira los dados.");

                tirado = false; // asÃ­ puede tirar los dados
                intentoSalirCarcel = true;
                break;
            }

            // OPCIONN 2: CARTA
            else if (opcion.equals("2")) {
                if (!jugador.usarCartaSalirCarcel()) {
                    consola.imprimir("No tienes ninguna carta. Elige otra opcion.");
                    continue;
                }

                av.setEnCarcel(false);
                jugador.setEnCarcel(false);
                av.setTurnosEnCarcel(0);
                consola.imprimir("Has usado una carta y sales de la Carcel.");
                consola.imprimir("No te mueves este turno. Usa 'acabar turno'.");

                tirado = true; // Bloquea tirar dados
                intentoSalirCarcel = true;
                break;
            }

            // OPCIÃ“N 3: INTENTAR DOBLES
            else if (opcion.equals("3")) {
                int d1 = dado1.hacerTirada();
                int d2 = dado2.hacerTirada();
                consola.imprimir("Has sacado: " + d1 + " y " + d2);

                if (d1 == d2) {
                    // Â¡DOBLES! â†’ SALE Y SE MUEVE
                    av.setEnCarcel(false);
                    jugador.setEnCarcel(false);
                    av.setTurnosEnCarcel(0);
                    int total = d1 + d2;
                    consola.imprimir("¡DOBLES! Sales de la Carcel y avanzas " + total + " casillas.");
                    av.moverAvatar(total, tablero);
                    consola.imprimir(tablero.toString());
                } else {
                    // NO DOBLES â†’ Incrementa turno en cÃ¡rcel
                    av.incrementarTurnosEnCarcel();
                    consola.imprimir("No son dobles. Pierdes el turno.");
                    consola.imprimir("Llevas " + av.getTurnosEnCarcel() + " turno(s) en la Carcel.");
                }

                tirado = true;
                intentoSalirCarcel = true;
                break;
            } else {
                consola.imprimir("Opcion no valida. Elige 1, 2 o 3.");
            }
        }
    }


    //Metodo que indica el jugador que tiene el turno
    @Override
    public void indicarTurno() {
        if (jugadores == null || jugadores.isEmpty()) {
            consola.imprimir("No hay jugadores en la partida.");
            return;
        }

        Jugador actual = jugadores.get(turno);

        consola.imprimir("$> jugador");
        consola.imprimir("{");
        consola.imprimir("nombre: " + actual.getNombre() + ",");
        consola.imprimir("avatar: " + actual.getAvatar().getId());
        consola.imprimir("}");

        // *** SOLO INFORMAR si estÃ¡ en cÃ¡rcel ***
        if (actual.isEnCarcel() || actual.getAvatar().estaEnCarcel()) {
            consola.imprimir("\n  Estas en la Carcel. Usa 'salir carcel' para intentar salir.");
        }
    }


    //Metodo que muestra las casillas que estÃ¡n a la venta actualmente
    @Override
    public void listarVenta() {
        ArrayList<Casilla> enVenta = tablero.getCasillasEnVenta();

        if (enVenta.isEmpty()) {
            consola.imprimir("No hay propiedades en venta.");
            return;
        }

        consola.imprimir("$> listar enventa");
        for (Casilla c : enVenta) {
            consola.imprimir("{");
            consola.imprimir("nombre: " + c.getNombre() + ",");
            consola.imprimir("tipo: " + c.getTipo() + ",");

            if ("solar".equalsIgnoreCase(c.getTipo())) {
                consola.imprimir("grupo: " + c.getGrupo() + ",");
            }

            // Cast a Propiedad para acceder a getValor()
            if (c instanceof Propiedad) {
                Propiedad p = (Propiedad) c;
                consola.imprimir("valor: " + (long) p.getValor());
            } else {
                consola.imprimir("valor: -"); // si no es propiedad, no tiene valor
            }

            consola.imprimir("},");
        }
    }



    // MÃ©todo que realiza las acciones asociadas al comando 'listar jugadores'.
    // MÃ©todo que realiza las acciones asociadas al comando 'listar jugadores'.
    @Override
    public void listarJugadores() {
        if (jugadores == null || jugadores.isEmpty()) {
            consola.imprimir("No hay jugadores en la partida.");
            return;
        }

        consola.imprimir("$> listar jugadores");

        for (int i = 0; i < jugadores.size(); i++) {
            Jugador j = jugadores.get(i);

            consola.imprimir("{");
            consola.imprimir("nombre: " + j.getNombre() + ",");
            consola.imprimir("avatar: " + (j.getAvatar() != null ? j.getAvatar().toString() : "-") + ",");
            consola.imprimir("fortuna: " + (long) j.getFortuna() + ",");

            // PROPIEDADES
            if (j.getPropiedades().isEmpty()) {
                consola.imprimir("propiedades: -,");
            } else {
                String props = j.getPropiedades().stream() // stream -> crea un flujo de datos
                        .map(Casilla::getNombre) // convierte cada casilla en su nombre
                        .collect(Collectors.joining(", ")); // las recoge en una lista
                consola.imprimir("propiedades: [" + props + "],");
            }

            // HIPOTECAS - mostrar solo las propiedades hipotecadas
            List<String> hipotecas = j.getPropiedades().stream()
                    .filter(c -> (c instanceof Propiedad) && ((Propiedad) c).estaHipotecada())
                    // filtramos solo las que estÃ¡n hipotecadas
                    .map(Casilla::getNombre)
                    .collect(Collectors.toList());

            if (hipotecas.isEmpty()) {
                consola.imprimir("hipotecas: -,");
            } else {
                consola.imprimir("hipotecas: " + hipotecas + ",");
            }

            // EDIFICIOS - mostrar IDs de todas las edificaciones del jugador
            if (j.getEdificaciones().isEmpty()) {
                consola.imprimir("edificios: -");
            } else {
                List<String> idsEdificios = j.getEdificaciones().stream()
                        .map(Edificacion::getId)
                        .collect(Collectors.toList());
                consola.imprimir("edificios: " + idsEdificios);
            }

            // Decide si lleva coma o no y se imprime todo junto
            if (i < jugadores.size() - 1) {
                consola.imprimir("},");
            } else {
                consola.imprimir("}");
            }
        }
    }

    // MÃ©todo que realiza las acciones asociadas al comando 'listar avatares'.
    @Override
    public void listarAvatares() {
        if (avatares.isEmpty()) {
            consola.imprimir("No hay avatares en juego.");
            return;
        }

        consola.imprimir("$> listar avatares");
        for (Avatar av : avatares) { //busca los avatares y los va imprimiendo con un for each
            consola.imprimir("{");
            consola.imprimir("id: " + av.getId() + ",");
            consola.imprimir("tipo: " + av.getJugador().getAvatar().getTipo() + ",");
            consola.imprimir("jugador: " + av.getJugador().getNombre() + ",");
            consola.imprimir("casilla: " + (av.getCasilla() != null ? av.getCasilla().getNombre() : "sin posiciÃ³n") + ",");
            consola.imprimir("enCarcel: " + av.estaEnCarcel());
            consola.imprimir("}");
        }
    }

    @Override
    public void acabarTurno() throws excepcionMonopoly{
        if (jugadores == null || jugadores.isEmpty()) {
            throw new excepEstadoJuego("No hay jugadores en la partida");
        }

        Jugador actual = jugadores.get(turno);

        if (actual.getDeudaPendiente() > 0) {
            throw new excepSinRecDinero((long)actual.getDeudaPendiente(), (long)actual.getFortuna());
        }

        if (!tirado) {
            throw new excepEstTurnoInvalido("acabar turno (no has tirado los dados)");
        }

        if (contadorDobles > 0) {
            throw new excepEstTurnoInvalido("acabar turno (has sacado dobles, debes volver a tirar)");
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
            consola.imprimir("¡La partida ha terminado!");
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
                    throw new excepEstadoJuego("No se encontró jugador activo");
                }
            } while (jugadores.get(turno).isBancarrota());
        }

        // Resetear flags
        tirado = false;
        repetirTurno = false;
        contadorDobles = 0;
        intentoSalirCarcel = false;

        Jugador actualTurno = jugadores.get(turno);
        consola.imprimir("Turno acabado. Ahora le toca a:");
        consola.imprimir("$> jugador");
        consola.imprimir("{");
        consola.imprimir("nombre: " + actualTurno.getNombre() + ",");
        consola.imprimir("avatar: " + actualTurno.getAvatar().getId());
        consola.imprimir("}");

        if (actual.isEnCarcel() || actual.getAvatar().estaEnCarcel()) {
            consola.imprimir("CUIDADO " + actual.getNombre() + " esta en la Carcel.");
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
            consola.imprimir("Â¡" + ganador.getNombre() + " ha ganado la partida!");
            consola.imprimir("Fortuna final: " + (long) ganador.getFortuna() + "â‚¬");
            consola.imprimir("Propiedades: " + ganador.getPropiedades().size());
            consola.imprimir("Gastos totales: " + (long) ganador.getGastos() + "â‚¬");
            // AquÃ­ puedes terminar el juego o bloquear mÃ¡s comandos
        }
    }


    public void menuComandos() {
        consola.imprimir("  - 'listar jugadores' / 'jugadores'");
        consola.imprimir("  - 'jugador' (ver turno actual)");
        consola.imprimir("  - 'tirar dados' (tirada aleatoria)");
        consola.imprimir("  - 'tirar dados X+Y' o 'lanzar dados X+Y' (forzar valores 1-6)");
        consola.imprimir("  - 'acabar turno'");
        consola.imprimir("  - 'describir <casilla>'");
        consola.imprimir("  - 'ver tablero'");
        consola.imprimir("  - 'describir jugador <nombre>'");
        consola.imprimir("  - 'describir avatar X'");
        consola.imprimir("  - 'listar enventa' (casillas disponibles)");
        consola.imprimir("  - 'listar avatares'");
        consola.imprimir("  - 'comprar <casilla>'");
        consola.imprimir("  - 'hipotecar <casilla>'");
        consola.imprimir("  - 'deshipotecar <casilla>'");
        consola.imprimir("  - 'vender <tipo> <casilla> <cantidad>'");
        consola.imprimir("  - 'estadisticas <jugador>'");
        consola.imprimir("  - 'estadisticas juego'");
        consola.imprimir("  - 'salir carcel'");
        consola.imprimir("  - 'estadistas juego'");
        consola.imprimir("  - 'listar edificios'");
        consola.imprimir("  - 'trato <jugador>: cambiar (<prop1>, <prop2>)'");
        consola.imprimir("  - 'listar edificios <grupo>'");
        consola.imprimir("  - 'edificar <tipo>'");
        consola.imprimir("  - 'comandos <ruta/al/archivo.txt>' (ejecutar comandos desde archivo)");
        consola.imprimir("  - 'salir' (cerrar el juego)");
    }

    private void menuSetUp() {
        consola.imprimir("Modo configuracion de partida:"); //comandos que se pueden usar si la partida no estÃ¡ empezada
        consola.imprimir("- 'crear jugador <nombre> <avatar>' (avatares: sombrero, coche, esfinge, pelota)");
        consola.imprimir("- 'comandos <ruta/al/archivo.txt>' (ejecutar comandos desde archivo)");
        consola.imprimir("- 'listar jugadores'");
        consola.imprimir("- 'empezar' (requiere entre 2 y 4 jugadores)");
        consola.imprimir("- 'salir'");

    }

    private void validarPartida() {
        if (jugadores.size() < 2) {
            consola.imprimir("Debes crear al menos 2 jugadores para empezar.");
        } else if (jugadores.size() > 4) {
            consola.imprimir("No puedes tener mas de 4 jugadores.");
        } else {
            consola.imprimir("Iniciando partida...");

            Carta.resetearContadores();
            Carta.setJugadores(jugadores);
            Carta.inicializarMazos();

            juegoIniciado = true;
        }
    }

    @Override
    public void comprarCasilla(String nombreCasilla) throws excepcionMonopoly {
        Jugador jugador = jugadores.get(turno);
        Casilla casilla = tablero.encontrarCasilla(nombreCasilla);

        if (casilla == null) {
            throw new excepNoExisteObjeto("La casilla", nombreCasilla);
        }

        // Verifica que el jugador esté en la casilla
        Casilla casillaActual = jugador.getAvatar().getCasilla();
        if (!casillaActual.equals(casilla)) {
            throw new excepTransPropNoPermitida("No estás en la casilla " + nombreCasilla);
        }

        // Solo propiedades son comprables
        if (!(casilla instanceof Propiedad)) {
            throw new excepTransPropNoPermitida("esta casilla no se puede comprar.");
        }

        Propiedad propiedad = (Propiedad) casilla;

        if (casilla.getDuenho() != null) {
            throw new excepTransPropNoPermitida("la casilla ya tiene dueño (" + propiedad.getDuenho() + ").");
        }

        if (jugador.getFortuna() < propiedad.getValor()) {
            throw new excepSinRecDinero(((Propiedad) casilla).getValor(), jugador.getFortuna());
        }

        // Compra
        jugador.restarFortuna(propiedad.getValor());
        jugador.sumarDineroInvertido(propiedad.getValor());
        propiedad.setDuenho(jugador);
        jugador.anhadirPropiedad(propiedad);

        consola.imprimir("Has comprado " + propiedad.getNombre() + " por " + (long) propiedad.getValor() + "€.");
    }

    @Override
    public void edificarPropiedad(String tipo) throws excepcionMonopoly {
        Jugador jugadorActual = jugadores.get(turno);
        Avatar avatar = jugadorActual.getAvatar();
        Casilla casilla = avatar.getCasilla();

        if (!(casilla instanceof Solar)) {
            throw new excepTransEdNoPermitida("solo se puede edificar en solares");
        }

        Solar solar = (Solar) casilla;

        if (solar.getDuenho() == null || !solar.getDuenho().equals(jugadorActual)) {
            throw new excepTransEdNoPermitida("no eres dueño de " + casilla.getNombre());
        }

        if (!jugadorActual.poseeGrupoCompleto(solar, tablero)) {
            throw new excepTransEdNoPermitida("no posees todo el grupo " + casilla.getGrupo().getNombre());
        }

        if (solar.estaHipotecada()) {
            throw new excepTransPropHipotecada(tipo);
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
                throw  new excepComandoInvalido("Tipo de edificación no válido. Usa: casa, hotel, piscina o pista deporte");
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

    @Override
    public void listarEdificaciones() {
        if (edificaciones.isEmpty()) { //si la lista global de edificios esta vacia, es que no hay edificios
            consola.imprimir("No hay edificaciones construidas.");
            return;
        }

        for (Edificacion e : edificaciones) { //impimos para cada elemente del array sus atributos
            consola.imprimir("{");
            consola.imprimir("  id: " + e.getId() + ",");
            consola.imprimir("  propietario: " + e.getPropietario().getNombre() + ",");
            consola.imprimir("  casilla: " + e.getSolar().getNombre() + ",");
            consola.imprimir("  grupo: " + e.getSolar().getGrupo().getNombre() + ",");
            consola.imprimir("  coste: " + (long) e.getCoste() + "â‚¬");
            consola.imprimir("}");
        }
    }

    @Override
    public void listarEdificiosGrupo(String nombreGrupo) {

        // Buscar el grupo en el tablero
        Grupo grupo = tablero.getGrupo(nombreGrupo);

        if (grupo == null) {
            consola.imprimir("No existe el grupo '" + nombreGrupo + "'.");
            return;
        }

        ArrayList<Propiedad> casillasGrupo = (ArrayList<Propiedad>) grupo.getMiembros();

        if (casillasGrupo.isEmpty()) {
            consola.imprimir("El grupo '" + nombreGrupo + "' no tiene casillas.");
            return;
        }

        consola.imprimir("$> listar edificios " + nombreGrupo);

        boolean puedeCasas = false;
        boolean puedeHoteles = false;
        boolean puedePiscinas = false;
        boolean puedePistas = false;

        // Recorrer las casillas del grupo
        for (Casilla casilla : casillasGrupo) {
            // Solo nos interesan los solares que tengan dueño
            if (casilla.getDuenho() != null && "solar".equalsIgnoreCase(casilla.getTipo())) {

                Solar solar = (Solar) casilla;

                consola.imprimir("{");
                consola.imprimir("  propiedad: " + solar.getNombre() + ",");

                // --- HOTELES ---
                if (solar.tieneHotel()) {
                    List<String> hotelesIds = new ArrayList<>();
                    for (Edificacion e : edificaciones) {
                        if (e.getSolar().equals(solar) && e.obtenerTipo().equals("hotel")) {
                            hotelesIds.add(e.getId());
                        }
                    }
                    consola.imprimir("  hoteles: [" + String.join(", ", hotelesIds) + "],");
                } else {
                    consola.imprimir("  hoteles: -,");
                }

                // --- CASAS ---
                if (solar.getNumCasas() > 0) {
                    List<String> casasIds = new ArrayList<>();
                    for (Edificacion e : edificaciones) {
                        if (e.getSolar().equals(solar) && e.obtenerTipo().equals("casa")) {
                            casasIds.add(e.getId());
                        }
                    }
                    consola.imprimir("  casas: [" + String.join(", ", casasIds) + "],");
                } else {
                    consola.imprimir("  casas: -,");
                }

                // --- PISCINAS ---
                if (solar.tienePiscina()) {
                    List<String> piscinasIds = new ArrayList<>();
                    for (Edificacion e : edificaciones) {
                        if (e.getSolar().equals(solar) && e.obtenerTipo().equals("piscina")) {
                            piscinasIds.add(e.getId());
                        }
                    }
                    consola.imprimir("  piscinas: [" + String.join(", ", piscinasIds) + "],");
                } else {
                    consola.imprimir("  piscinas: -,");
                }

                // --- PISTAS ---
                if (solar.tienePista()) {
                    List<String> pistasIds = new ArrayList<>();
                    for (Edificacion e : edificaciones) {
                        if (e.getSolar().equals(solar) && e.obtenerTipo().equals("pista")) {
                            pistasIds.add(e.getId());
                        }
                    }
                    consola.imprimir("  pistasDeDeporte: [" + String.join(", ", pistasIds) + "],");
                } else {
                    consola.imprimir("  pistasDeDeporte: -,");
                }

                // Alquiler actual de la casilla
                float alquiler = solar.alquiler(tablero.getUltimaTirada());
                consola.imprimir("  alquiler: " + (long) alquiler);
                consola.imprimir("},");
            }
        }

        // Lógica para determinar qué se puede construir en el futuro en este grupo
        // (Se asume que si un jugador tiene el grupo completo, se evalúan sus opciones)
        for (Casilla casilla : casillasGrupo) {
            if (casilla.getDuenho() != null && grupo.perteneceEnteramenteA(casilla.getDuenho()) && "solar".equalsIgnoreCase(casilla.getTipo())) {
                Solar solar = (Solar) casilla;
                if (solar.getNumCasas() < 4) puedeCasas = true;
                if (!solar.tieneHotel() && solar.getNumCasas() == 4) puedeHoteles = true;
                if (solar.tieneHotel() && !solar.tienePiscina()) puedePiscinas = true;
                if (solar.tieneHotel() && solar.tienePiscina() && !solar.tienePista()) puedePistas = true;
            }
        }

        // Mostrar resumen final
        consola.imprimir("");
        List<String> puedeConstruir = new ArrayList<>();
        List<String> noPuedeConstruir = new ArrayList<>();

        if (puedePiscinas) puedeConstruir.add("piscinas"); else noPuedeConstruir.add("piscinas");
        if (puedePistas) puedeConstruir.add("pistas de deporte"); else noPuedeConstruir.add("pistas de deporte");
        if (puedeCasas) puedeConstruir.add("casas"); else noPuedeConstruir.add("casas");
        if (puedeHoteles) puedeConstruir.add("hoteles"); else noPuedeConstruir.add("hoteles");

        if (!puedeConstruir.isEmpty()) {
            consola.imprimir("Aún se puede edificar " + String.join(", ", puedeConstruir) + ".");
        }
        if (!noPuedeConstruir.isEmpty()) {
            consola.imprimir("No se pueden construir " + String.join(" ni ", noPuedeConstruir) + ".");
        }
    }


    @Override
    public void hipotecarPropiedad(String nombreCasilla) throws excepcionMonopoly {
        Jugador jugador = jugadores.get(turno);
        Casilla casilla = tablero.encontrarCasilla(nombreCasilla);

        if (casilla == null) {
            throw new excepNoExisteObjeto("la casilla", nombreCasilla);
        }

        if (!(casilla instanceof Propiedad)) {
            throw new excepTransaccion("no es hipotecable");
        }

        Propiedad propiedad = (Propiedad) casilla;

        if (!propiedad.perteneceAJugador(jugador)) {
            throw new excepTransPropNoPermitida("la propiedad no pertenece a ningún jugador");
        }

        propiedad.hipotecar(); //Hipotecar en Propiedad, metodo que realiza la hipoteca
    }

    @Override
    public void deshipotecarPropiedad(String nombreCasilla) throws excepcionMonopoly{
        Jugador jugador = jugadores.get(turno);
        Casilla casilla = tablero.encontrarCasilla(nombreCasilla);

        if (casilla == null) {
            throw new excepNoExisteObjeto("la casilla", nombreCasilla);
        }

        if (!(casilla instanceof Propiedad)) {
            consola.imprimir("La casilla '" + nombreCasilla + "' no es una propiedad.");
            return;
        }

        Propiedad propiedad = (Propiedad) casilla;

        if (!propiedad.perteneceAJugador(jugador)) {
            throw new excepTransPropNoPermitida("no eres dueño de esta propiedad");
        }

        propiedad.deshipotecar(); //Deshipotecar en Propiedad, metodo que realiza la deshipoteca
    }



    @Override
    public void venderPropiedad(String tipo, String nombreCasilla, int cantidad) throws excepcionMonopoly {
        Jugador jugador = jugadores.get(turno);
        Casilla casilla = tablero.encontrarCasilla(nombreCasilla);

        if (casilla == null) {
            throw new excepNoExisteObjeto("la casilla", nombreCasilla);
        }

        if (!(casilla instanceof Solar)) {
            throw new excepEstadoJuego("La casilla" + nombreCasilla + "no es un solar edificable");
        }

        if (cantidad <= 0) {
            throw new excepComandoInvalido("Cantidad incorrecta de propiedades.");
        }

        Solar solar = (Solar) casilla;

        if (!solar.getDuenho().equals(jugador)) {
            throw new excepTransPropNoPermitida("la propiedad " + nombreCasilla + " no te pertenece");
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
                consola.imprimir("Tipo de edificación no válido. Usa: casa, hotel, piscina o pista.");
                break;
        }
    }

    @Override
    public void mostrarEstadisticasUnJugador(String nombreJugador) {
        for (Jugador j : jugadores) {
            if (j.getNombre().equalsIgnoreCase(nombreJugador)) {
                consola.imprimir("$> estadisticas " + j.getNombre());
                consola.imprimir("{");
                consola.imprimir("dineroInvertido: " + (long) j.getDineroInvertido() + ","); //cada que compra una casilla (Juego sumarDineroInvertido)
                consola.imprimir("pagoTasasEImpuestos: " + (long) j.getPagoTasasEImpuestos() + ","); //cada que paga impuestos al salir de la carcel o si caigo en impuestos
                consola.imprimir("pagoDeAlquileres: " + (long) j.getPagoDeAlquileres() + ",");
                consola.imprimir("cobroDeAlquileres: " + (long) j.getCobroDeAlquileres() + ",");
                consola.imprimir("pasarPorCasillaDeSalida: " + (long) j.getPasarPorCasillaDeSalida() + ",");
                consola.imprimir("premiosInversionesOBote: " + (long) j.getPremiosInversionesOBote() + ",");
                consola.imprimir("vecesEnLaCarcel: " + j.getVecesEnLaCarcel());
                consola.imprimir("}");
                return;
            }
        }
        consola.imprimir("No se encontrÃ³ ningÃºn jugador con el nombre '" + nombreJugador + "'.");
    }

    @Override
    public void mostrarEstadisticasJuego() {

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
        consola.imprimir("$> estadisticas");
        consola.imprimir("{");
        consola.imprimir("casillaMasRentable: " + (casillaMasRentable != null ? casillaMasRentable.getNombre() : "-") + ",");
        consola.imprimir("grupoMasRentable: " + (grupoMasRentable != null ? grupoMasRentable.getNombre() : "-") + ",");
        consola.imprimir("casillaMasFrecuentada: " + (casillaMasFrecuentada != null ? casillaMasFrecuentada.getNombre() : "-") + ",");
        consola.imprimir("jugadorMasVueltas: " + (jugadorMasVueltas != null ? jugadorMasVueltas.getNombre() : "-") + ",");
        consola.imprimir("jugadorEnCabeza: " + (jugadorEnCabeza != null ? jugadorEnCabeza.getNombre() : "-"));
        consola.imprimir("}");
    }

    @Override
    public void verTablero() {
        consola.imprimir(tablero.toString());
    }

    @Override
    public void declararBancarrota() {
        Jugador jugadorActuaal = jugadores.get(turno);
        if (jugadorActuaal.getDeudaPendiente() > 0) {
            jugadorActuaal.declararBancarrota(jugadorActuaal.getAcreedorDeuda());
            tablero.notificarBancarrota(jugadorActuaal);
        } else {
            consola.imprimir("No tienes deudas pendientes.");
            consola.imprimir("No puedes declararte en bancarrota voluntariamente.");
        }
    }
    private void proponerTrato(String nombreReceptor, String contenido) throws excepcionMonopoly {
        Jugador proponente = jugadores.get(turno); // Jugador que propone el trato
        Jugador receptor = buscarJugador(nombreReceptor); // Jugador que recibe el trato, al que se le propone

        if (receptor == null) {
            throw new excepNoExisteObjeto("jugador", nombreReceptor); // Verificar que el receptor existe
        }

        if (receptor.equals(proponente)) {
            throw new excepTransaccion("no puedes hacer un trato contigo mismo"); // No permitir tratos consigo mismo
        }

        // Parsear el contenido del trato, analizar y descomponer
        parsearYCrearTrato(proponente, receptor, contenido);
    }
    private void parsearYCrearTrato(Jugador proponente, Jugador receptor, String contenido)
            throws excepcionMonopoly {

        // Dividir por coma
        String[] partes;
        partes = contenido.split(",", 2);

        if (partes.length != 2) {
            throw new excepComandoInvalido("Formato de trato inválido");
        }

        String ofrece = partes[0].trim(); // Lo que ofrece el proponente
        String recibe = partes[1].trim(); // Lo que recibe el proponente (ofrece el receptor)

        // Parsear lo que ofrece y lo que recibe
        TratoParseado ofrecido = parsearElemento(ofrece, proponente);
        TratoParseado recibido = parsearElemento(recibe, receptor);

        // Crear el trato
        crearTrato(proponente, receptor, ofrecido, recibido);
    }

    // Clase interna simple, servira como contenedor para almacenar los resultados de los tratos
    private class TratoParseado {
        Propiedad propiedad;
        float dinero;
    }

    private TratoParseado parsearElemento(String elemento, Jugador duenho) throws excepcionMonopoly {
        TratoParseado resultado = new TratoParseado();

        // Si contiene "y", tiene propiedad Y dinero o otra propiedad
        if (elemento.contains(" y ")) {
            String[] partes = elemento.split("\\s+y\\s+");

            for (String parte : partes) {
                parte = parte.trim(); // Limpiar espacios
                if (esNumero(parte)) { // Si es un numero, es dinero
                    resultado.dinero = Float.parseFloat(parte);
                } else {
                    resultado.propiedad = buscarPropiedad(parte, duenho); // Si no es numero, es propiedad
                }
            }
        } else {
            // Solo una cosa: propiedad O dinero
            elemento = elemento.trim();
            if (esNumero(elemento)) {
                resultado.dinero = Float.parseFloat(elemento);
            } else {
                resultado.propiedad = buscarPropiedad(elemento, duenho);
            }
        }

        return resultado;
    }

    private boolean esNumero(String str) {
        try {
            Float.parseFloat(str.trim());  // Intenta convertir a número
            return true;                    // Si funciona → es número
        } catch (NumberFormatException e) {  // Si falla → no es número
            return false;
        }
    }

    private Propiedad buscarPropiedad(String nombre, Jugador duenho) throws excepcionMonopoly {
        Casilla casilla = tablero.encontrarCasilla(nombre.trim());

        if (casilla == null) {
            throw new excepNoExisteObjeto("propiedad", nombre);
        }

        if (!(casilla instanceof Propiedad)) {
            throw new excepTransaccion(nombre + " no es una propiedad");
        }

        Propiedad prop = (Propiedad) casilla;

        if (!prop.perteneceAJugador(duenho)) {
            throw new excepTransaccion(nombre + " no pertenece a " + duenho.getNombre());
        }

        if (prop.estaHipotecada()) {
            throw new excepTransPropHipotecada("incluir en un trato");
        }

        // No permitir propiedades con edificios
        if (prop instanceof Solar) {
            Solar solar = (Solar) prop;
            if (solar.getNumCasas() > 0 || solar.tieneHotel() ||
                    solar.tienePiscina() || solar.tienePista()) {
                throw new excepTransaccion(nombre + " tiene edificaciones");
            }
        }

        return prop;
    }

    private void crearTrato(Jugador proponente, Jugador receptor, TratoParseado ofrecido, TratoParseado recibido)
            throws excepcionMonopoly {

        // Validaciones básicas
        if (ofrecido.dinero > 0 && proponente.getFortuna() < ofrecido.dinero) {
            throw new excepSinRecDinero((long)ofrecido.dinero, (long)proponente.getFortuna());
        }

        if (recibido.dinero > 0 && receptor.getFortuna() < recibido.dinero) {
            throw new excepTransaccion(receptor.getNombre() + " no tiene suficiente dinero");
        }

        if (ofrecido.propiedad == null && ofrecido.dinero == 0) {
            throw new excepTransaccion("debes ofrecer algo");
        }

        if (recibido.propiedad == null && recibido.dinero == 0) {
            throw new excepTransaccion(receptor.getNombre() + " debe ofrecer algo");
        }

        // Crear el trato
        String id = "trato-" + (++contadorTratos);
        Trato trato = new Trato(id, proponente, receptor,
                ofrecido.propiedad, recibido.propiedad,
                ofrecido.dinero, recibido.dinero);

        // Guardar en listas
        proponente.agregarTratoPropuesto(trato);
        receptor.agregarTratoRecibido(trato);

        // Mostrar mensaje
        consola.imprimir(trato.toString());
        consola.imprimir("Trato propuesto con ID: " + id);
        consola.imprimir(receptor.getNombre() + " puede aceptarlo con: aceptar trato " + id);
    }

    private Jugador buscarJugador(String nombre) {
        for (Jugador j : jugadores) {
            if (j.getNombre().equalsIgnoreCase(nombre)) {
                return j;
            }
        }
        return null;
    }


}