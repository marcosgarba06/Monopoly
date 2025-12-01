package monopoly;

import partida.*;
import java.util.ArrayList;

public abstract class Carta {

    protected String descripcion;
    protected String tipo; // "suerte" o "caja"
    protected int id;      // número identificador de la carta

    // Contadores y mazos compartidos
    public static int contadorSuerte = 0;
    public static int contadorCaja = 0;

    public static ArrayList<Carta> mazoSuerte = null;
    public static ArrayList<Carta> mazoCaja = null;

    // Lista global de jugadores
    private static ArrayList<Jugador> jugadores;

    // Constructor común
    public Carta(String descripcion, String tipo, int id) {
        this.descripcion = descripcion;
        this.tipo = tipo.toLowerCase();
        this.id = id;
    }

    public String getDescripcion() { return descripcion; }
    public String getTipo() { return tipo; }
    public int getId() { return id; }

    // Método abstracto: cada subclase implementa su acción
    public abstract void aplicarAccion(Jugador jugador, Tablero tablero);

    // Gestión de jugadores global
    public static void setJugadores(ArrayList<Jugador> listaJugadores) {
        jugadores = listaJugadores;
    }
    public static ArrayList<Jugador> getJugadores() {
        return jugadores;
    }

    // Inicialización de mazos
    public static void inicializarMazos() {
        mazoSuerte = new ArrayList<>();
        mazoSuerte.add(new CartaSuerte("Decides hacer un viaje de placer. Avanza hasta Solar19", 1));
        mazoSuerte.add(new CartaSuerte("Los acreedores te persiguen por impago. Ve a la Cárcel.", 2));
        mazoSuerte.add(new CartaSuerte("¡Has ganado el bote de la lotería! Recibe 1.000.000€", 3));
        mazoSuerte.add(new CartaSuerte("Has sido elegido presidente de la junta directiva. Paga a cada jugador 250.000€", 4));
        mazoSuerte.add(new CartaSuerte("¡Hora punta de tráfico! Retrocede tres casillas.", 5));
        mazoSuerte.add(new CartaSuerte("Te multan por usar el móvil mientras conduces. Paga 150.000€.", 6));
        mazoSuerte.add(new CartaSuerte("Avanza hasta la casilla de transporte más cercana. Si no tiene dueño, puedes comprarla. Si tiene dueño, paga el doble.", 7));

        mazoCaja = new ArrayList<>();
        mazoCaja.add(new CartaCajaComunidad("Paga 500.000€ por un fin de semana en un balneario de 5 estrellas", 1));
        mazoCaja.add(new CartaCajaComunidad("Te investigan por fraude de identidad. Ve a la Cárcel.", 2));
        mazoCaja.add(new CartaCajaComunidad("Colócate en la casilla de Salida. Cobra 2.000.000€", 3));
        mazoCaja.add(new CartaCajaComunidad("Devolución de Hacienda. Cobra 500.000€", 4));
        mazoCaja.add(new CartaCajaComunidad("Retrocede hasta Solar1 para comprar antigüedades exóticas.", 5));
        mazoCaja.add(new CartaCajaComunidad("Ve a Solar20 para disfrutar del San Fermín. Si pasas por la casilla de Salida, cobra 2.000.000€.", 6));
    }

    // Selección de carta circular
    public static Carta seleccionarCarta(String tipo) {
        if (mazoSuerte == null || mazoCaja == null) inicializarMazos();

        if ("suerte".equalsIgnoreCase(tipo)) {
            Carta carta = mazoSuerte.get(contadorSuerte % mazoSuerte.size());
            contadorSuerte++;
            return carta;
        } else {
            Carta carta = mazoCaja.get(contadorCaja % mazoCaja.size());
            contadorCaja++;
            return carta;
        }
    }

    public static void resetearContadores() {
        contadorSuerte = 0;
        contadorCaja = 0;
    }
}
