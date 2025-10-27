package monopoly;

import partida.*;

import java.util.ArrayList;

public class Carta {

    private String descripcion;
    private String tipo;
    private int id;// "suerte" o "caja"

    private static int contadorSuerte = 0;
    private static int contadorCaja = 0;

    private static ArrayList<Carta> mazoSuerte = null;
    private static ArrayList<Carta> mazoCaja = null;

    private static ArrayList<Jugador> jugadores;

    public Carta(String descripcion, String tipo, int id) {
        this.descripcion = descripcion;
        this.tipo = tipo.toLowerCase();
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public int getId() {
        return id;
    }

    public static void setJugadores(ArrayList<Jugador> listaJugadores) {
        jugadores = listaJugadores;
    }


    public static void inicializarMazos() {
        // Inicializar mazo de SUERTE
        mazoSuerte = new ArrayList<>();
        mazoSuerte.add(new Carta("Decides hacer un viaje de placer. Avanza hasta Solar19", "suerte", 1));
        mazoSuerte.add(new Carta("Los acreedores te persiguen por impago. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida y sin cobrar los 2.000.000€", "suerte", 2));
        mazoSuerte.add(new Carta("¡Has ganado el bote de la lotería! Recibe 1.000.000€", "suerte", 3));
        mazoSuerte.add(new Carta("Has sido elegido presidente de la junta directiva. Paga a cada jugador 250.000€", "suerte", 4));
        mazoSuerte.add(new Carta("¡Hora punta de tráfico! Retrocede tres casillas.", "suerte", 5));
        mazoSuerte.add(new Carta("Te multan por usar el móvil mientras conduces. Paga 150.000€.", "suerte", 6));

        // Inicializar mazo de CAJA DE COMUNIDAD
        mazoCaja = new ArrayList<>();
        mazoCaja.add(new Carta("Paga 500.000€ por un fin de semana en un balneario de 5 estrellas", "caja", 1));
        mazoCaja.add(new Carta("Te investigan por fraude de identidad. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida y sin cobrar los 2.000.000€", "caja", 2));
        mazoCaja.add(new Carta("Colócate en la casilla de Salida. Cobra 2.000.000€", "caja", 3));
        mazoCaja.add(new Carta("Tu compañía de Internet obtiene beneficios. Recibe 2.000.000€", "caja", 4));
        mazoCaja.add(new Carta("Paga 1.000.000€ por invitar a todos tus amigos a un viaje a Solar12", "caja", 5));
        mazoCaja.add(new Carta("Alquilas a tus compañeros una villa en Solar7 durante una semana. Paga 200.000€ a cada jugador", "caja", 6));

        System.out.println("Mazos de cartas inicializados: " + mazoSuerte.size() + " cartas de Suerte, " + mazoCaja.size() + " cartas de Caja.");
    }

    public static Carta seleccionarCarta(String tipo) {
        if (mazoSuerte == null || mazoCaja == null) {
            inicializarMazos();
        }

        Carta cartaSeleccionada;

        if (tipo.equalsIgnoreCase("suerte")) {
            if (mazoSuerte.isEmpty()) {
                System.out.println("Error: No hay cartas de Suerte disponibles.");
                return null;
            }
            cartaSeleccionada = mazoSuerte.get(contadorSuerte % mazoSuerte.size());
            contadorSuerte++;
        } else {
            if (mazoCaja.isEmpty()) {
                System.out.println("Error: No hay cartas de Caja disponibles.");
                return null;
            }
            cartaSeleccionada = mazoCaja.get(contadorCaja % mazoCaja.size());
            contadorCaja++;
        }

        return cartaSeleccionada;
    }

    public void aplicarAccion(Jugador jugador, Tablero tablero) {
        switch (descripcion) {
            // Cartas de Suerte
            case "Decides hacer un viaje de placer. Avanza hasta Solar19":
                Casilla destino = tablero.encontrarCasilla("Sol19");
                if (destino != null) {
                    // Mensaje ANTES de mover y evaluar
                    System.out.println("Avanzas a la casilla Sol19.");

                    // Eliminar avatar de casilla actual
                    Casilla casillaActual = jugador.getAvatar().getCasilla();
                    if (casillaActual != null) {
                        casillaActual.eliminarAvatar(jugador.getAvatar());
                    }

                    // Mover avatar a la nueva casilla
                    jugador.getAvatar().setCasilla(destino);
                    jugador.getAvatar().setPosicion(destino.getPosicion());

                    // Evaluar la nueva casilla
                    destino.evaluarCasilla(jugador);
                } else {
                    System.out.println("Error: No se encontró la casilla Sol19.");
                }
                break;
            case "Los acreedores te persiguen por impago. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida y sin cobrar los 2.000.000€":
                jugador.irACarcel(tablero);
                break;

            case "¡Has ganado el bote de la lotería! Recibe 1.000.000€":
                jugador.sumarFortuna(1000000);
                System.out.println("¡Has recibido 1000000€ por el bote de la loteria!");
                break;

            case "Has sido elegido presidente de la junta directiva. Paga a cada jugador 250.000€":
                int i = 0;

                for (Jugador c : jugadores) {
                    if (!c.equals(jugador)) {
                        c.sumarGastos(250000);
                        i++;
                    }
                }
                jugador.restarFortuna(250000 * i);
                System.out.println("Pagas " + (250000 * i) + "€ como presidente de la junta.");

                break;

            case "¡Hora punta de tráfico! Retrocede tres casillas.":
                Casilla casillaActual = jugador.getAvatar().getCasilla();
                int totalCasillas = 40;
                int nuevaPosicion = casillaActual.getPosicion() - 3;
                if (nuevaPosicion < 0) {
                    nuevaPosicion = totalCasillas + nuevaPosicion; // ejemplo: -2 → 40 - 2 = 38
                }
                Casilla casillaDestino = tablero.getCasilla(nuevaPosicion);
                if(casillaDestino != null){
                    System.out.println("Retrocedes tres casillas hasta " + casillaDestino.getNombre());
                    casillaActual.eliminarAvatar(jugador.getAvatar());

                    jugador.getAvatar().setCasilla(casillaDestino);
                    jugador.getAvatar().setPosicion(nuevaPosicion);

                    casillaDestino.evaluarCasilla(jugador);
                } else {
                    System.out.println("No se encontró la casilla de destino.");
                }

                break;


            case "Te multan por usar el móvil mientras conduces. Paga 150.000€.":
                jugador.restarFortuna(150000);
                jugador.sumarGastos(150000);
                System.out.println("Pagas 150.000€ por conducir indevidamente.");
                break;


            case "Avanza hasta la casilla de transporte más cercana. Si no tiene dueño, puedes comprarla. Si tiene dueño, paga al dueño el doble de la operación indicada.":

                break;

            case "Paga 500.000€ por un fin de semana en un balneario de 5 estrellas.":
                jugador.restarFortuna(500000);
                jugador.sumarGastos(500000);
                System.out.println("Pagas 150.000€ por conducir indevidamente.");
                break;

            case "Te investigan por fraude de identidad. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida sin cobrar los 2.000.000€.":
                jugador.irACarcel(tablero);
                break;

            case "Colócate en la casilla de Salida. Cobra 2.000.000€.":
                casillaActual = jugador.getAvatar().getCasilla();
                casillaActual.eliminarAvatar(jugador.getAvatar());

                casillaDestino = tablero.encontrarCasilla("Salida");
                jugador.getAvatar().setCasilla(casillaDestino);
                jugador.getAvatar().setPosicion(casillaDestino.getPosicion());

                // Evaluar la nueva casilla
                casillaDestino.evaluarCasilla(jugador);
            case "Devolución de Hacienda. Cobra 500.000€.":
                jugador.sumarGastos(5000000);
                System.out.println("Has recibido 500.000€ por devolucion a de hacienda");
            case "Retrocede hasta Solar1 para comprar antigüedades exóticas":
                casillaActual = jugador.getAvatar().getCasilla();
                casillaActual.eliminarAvatar(jugador.getAvatar());
                casillaDestino = tablero.encontrarCasilla("Solar1");
                jugador.getAvatar().setCasilla(casillaDestino);
                jugador.getAvatar().setPosicion(casillaDestino.getPosicion());
                System.out.println("Has avanzado hasta la casilla " + casillaDestino.getNombre());

            case "Ve a Solar20 para disfrutar del San Fermín. Si pasas por la casilla de Salida, cobra 2.000.000€.":
                casillaActual = jugador.getAvatar().getCasilla();
                casillaActual.eliminarAvatar(jugador.getAvatar());

                casillaDestino = tablero.encontrarCasilla("Solar20");

                jugador.getAvatar().setCasilla(casillaDestino);
                jugador.getAvatar().setPosicion(casillaDestino.getPosicion());
                System.out.println("Has avanzado hasta la casilla " + casillaDestino.getNombre());
        }
    }

    public static void resetearContadores() {
        contadorSuerte = 0;
        contadorCaja = 0;
    }
}
