package monopoly;

import partida.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Juego {

    // Instancias principales del juego
    private Tablero tablero;
    private Jugador banca;
    private ArrayList<Jugador> jugadores;
    private ArrayList<Avatar> avatares;
    
    // Dados del juego
    private Dado dado1;
    private Dado dado2;
    
    // Control de turnos
    private int turno = 0;
    private boolean tirado = false;
    private int contadorDobles = 0;
    private boolean repetirTurno = false;
    private boolean intentoSalirCarcel = false;
    
    // Estado del juego
    private boolean juegoIniciado = false;
    
    // Sets para validación
    private final Set<String> nombresUsados = new HashSet<>();
    private final String[] avataresPermitidos = {"coche", "sombrero", "pelota", "esfinge"};
    private final Set<String> avataresUsados = new HashSet<>();
    
    // Edificaciones
    private List<Edificacion> edificaciones = new ArrayList<>();
    private int contadorCasas = 0;
    private int contadorHoteles = 0;
    private int contadorPiscinas = 0;
    private int contadorPistas = 0;
    
    // Scanner para entrada
    private Scanner scanner;
    
    // Referencia al menú
    private Menu menu;

    /**
     * Constructor de la clase Juego
     * Inicializa todas las instancias necesarias para el desarrollo del juego
     */
    public Juego() {
        this.scanner = new Scanner(System.in);
        this.jugadores = new ArrayList<>();
        this.avatares = new ArrayList<>();
        this.dado1 = new Dado();
        this.dado2 = new Dado();
        
        // Crear la banca
        this.banca = new Jugador();
        
        // Crear el tablero
        this.tablero = new Tablero(banca);
        
        // Crear el menú asociado a este juego
        this.menu = new Menu(this);
        
        // Vincular tablero con menú para notificaciones
        this.tablero.setMenu(menu);
        
        System.out.println("Juego inicializado correctamente.");
    }

    /**
     * Inicia la partida con el setup inicial
     */
    public void iniciarPartida() {
        menu.iniciarPartida();
    }

    /**
     * Procesa un comando del jugador
     */
    public void procesarComando(String comando) {
        menu.analizarComando(comando);
    }

    /**
     * Bucle principal del juego
     */
    public void iniciar() {
        System.out.println("Bienvenido a Monopoly ETSE.");
        
        boolean continuar = true;
        while (continuar) {
            mostrarComandosDisponibles();
            
            String comando = scanner.nextLine().trim();
            
            if (comando.equalsIgnoreCase("salir")) {
                System.out.println("Saliendo del juego...");
                continuar = false;
            } else {
                procesarComando(comando);
            }
        }
        
        cerrar();
    }

    /**
     * Muestra los comandos disponibles
     */
    private void mostrarComandosDisponibles() {
        System.out.println("\n=== COMANDOS DISPONIBLES ===");
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
        System.out.println("  - 'comprar jugador <nombre>'");
        System.out.println("  - 'salir carcel'");
        System.out.println("  - 'listar edificios'");
        System.out.println("  - 'listar edificios <grupo>'");
        System.out.println("  - 'edificar <tipo>'");
        System.out.println("  - 'comandos <ruta/al/archivo.txt>' (ejecutar comandos desde archivo)");
        System.out.println("  - 'salir' (cerrar el juego)");
    }

    /**
     * Cierra los recursos del juego
     */
    public void cerrar() {
        if (scanner != null) {
            scanner.close();
        }
        System.out.println("Juego finalizado. ¡Hasta pronto!");
    }

    // ==================== GETTERS Y SETTERS ====================

    public Tablero getTablero() {
        return tablero;
    }

    public Jugador getBanca() {
        return banca;
    }

    public ArrayList<Jugador> getJugadores() {
        return jugadores;
    }

    public ArrayList<Avatar> getAvatares() {
        return avatares;
    }

    public Dado getDado1() {
        return dado1;
    }

    public Dado getDado2() {
        return dado2;
    }

    public int getTurno() {
        return turno;
    }

    public void setTurno(int turno) {
        this.turno = turno;
    }

    public boolean isTirado() {
        return tirado;
    }

    public void setTirado(boolean tirado) {
        this.tirado = tirado;
    }

    public int getContadorDobles() {
        return contadorDobles;
    }

    public void setContadorDobles(int contadorDobles) {
        this.contadorDobles = contadorDobles;
    }

    public boolean isRepetirTurno() {
        return repetirTurno;
    }

    public void setRepetirTurno(boolean repetirTurno) {
        this.repetirTurno = repetirTurno;
    }

    public boolean isIntentoSalirCarcel() {
        return intentoSalirCarcel;
    }

    public void setIntentoSalirCarcel(boolean intentoSalirCarcel) {
        this.intentoSalirCarcel = intentoSalirCarcel;
    }

    public boolean isJuegoIniciado() {
        return juegoIniciado;
    }

    public void setJuegoIniciado(boolean juegoIniciado) {
        this.juegoIniciado = juegoIniciado;
    }

    public Set<String> getNombresUsados() {
        return nombresUsados;
    }

    public String[] getAvataresPermitidos() {
        return avataresPermitidos;
    }

    public Set<String> getAvataresUsados() {
        return avataresUsados;
    }

    public List<Edificacion> getEdificaciones() {
        return edificaciones;
    }

    public int getContadorCasas() {
        return contadorCasas;
    }

    public void setContadorCasas(int contadorCasas) {
        this.contadorCasas = contadorCasas;
    }

    public void incrementarContadorCasas() {
        this.contadorCasas++;
    }

    public int getContadorHoteles() {
        return contadorHoteles;
    }

    public void setContadorHoteles(int contadorHoteles) {
        this.contadorHoteles = contadorHoteles;
    }

    public void incrementarContadorHoteles() {
        this.contadorHoteles++;
    }

    public int getContadorPiscinas() {
        return contadorPiscinas;
    }

    public void setContadorPiscinas(int contadorPiscinas) {
        this.contadorPiscinas = contadorPiscinas;
    }

    public void incrementarContadorPiscinas() {
        this.contadorPiscinas++;
    }

    public int getContadorPistas() {
        return contadorPistas;
    }

    public void setContadorPistas(int contadorPistas) {
        this.contadorPistas = contadorPistas;
    }

    public void incrementarContadorPistas() {
        this.contadorPistas++;
    }

    public Menu getMenu() {
        return menu;
    }

    public Scanner getScanner() {
        return scanner;
    }
}