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
        // Inicializar mazo de suerte
        mazoSuerte = new ArrayList<>();
        mazoSuerte.add(new Carta("Decides hacer un viaje de placer. Avanza hasta Solar19", "suerte", 1));
        mazoSuerte.add(new Carta("Los acreedores te persiguen por impago. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida y sin cobrar los 2.000.000€", "suerte", 2));
        mazoSuerte.add(new Carta("¡Has ganado el bote de la lotería! Recibe 1.000.000€", "suerte", 3));
        mazoSuerte.add(new Carta("Has sido elegido presidente de la junta directiva. Paga a cada jugador 250.000€", "suerte", 4));
        mazoSuerte.add(new Carta("¡Hora punta de tráfico! Retrocede tres casillas.", "suerte", 5));
        mazoSuerte.add(new Carta("Te multan por usar el móvil mientras conduces. Paga 150.000€.", "suerte", 6));

        // Inicializar mazo de caja
        mazoCaja = new ArrayList<>();
        mazoCaja.add(new Carta("Paga 500.000€ por un fin de semana en un balneario de 5 estrellas", "caja", 1));
        mazoCaja.add(new Carta("Te investigan por fraude de identidad. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida y sin cobrar los 2.000.000€", "caja", 2));
        mazoCaja.add(new Carta("Colócate en la casilla de Salida. Cobra 2.000.000€", "caja", 3));
        mazoCaja.add(new Carta("Tu compañía de Internet obtiene beneficios. Recibe 2.000.000€", "caja", 4));
        mazoCaja.add(new Carta("Paga 1.000.000€ por invitar a todos tus amigos a un viaje a Solar12", "caja", 5));
        mazoCaja.add(new Carta("Alquilas a tus compañeros una villa en Solar7 durante una semana. Paga 200.000€ a cada jugador", "caja", 6));

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
            cartaSeleccionada = mazoSuerte.get(contadorSuerte % mazoSuerte.size()); //El uso de % mazo.size() hace que el orden sea circular: después de la última carta, vuelve a la primera.
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
        System.out.println("Carta seleccionada: " + descripcion);

        // Verificación de seguridad
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("ERROR: Lista de jugadores no inicializada.");
            return;
        }

        Casilla casillaActual = jugador.getAvatar().getCasilla();
        Casilla casillaDestino;

        if (tipo.equals("suerte")) {
            switch (id) {
                case 1: // Avanza a Solar19
                    casillaDestino = tablero.encontrarCasilla("Sol19");
                    if (casillaDestino != null) {
                        System.out.println("Avanzas a la casilla Sol19.");
                        casillaActual.eliminarAvatar(jugador.getAvatar());
                        jugador.getAvatar().setCasilla(casillaDestino);
                        jugador.getAvatar().setPosicion(casillaDestino.getPosicion());
                        casillaDestino.anhadirAvatar(jugador.getAvatar());
                        casillaDestino.evaluarCasilla(jugador);
                    } else {
                        System.out.println("Error: No se encontró la casilla Sol19.");
                    }
                    break;

                case 2: // Ir a la cárcel
                    jugador.irACarcel(tablero);
                    break;

                case 3: // Ganar lotería
                    jugador.sumarFortuna(1000000);
                    System.out.println("¡Has recibido 1.000.000€ por el bote de la lotería!");
                    jugador.sumarPremios(1000000);
                    break;

                case 4: // Pagar a cada jugador (CORREGIDO)
                    int totalAPagar = 0;
                    for (Jugador c : jugadores) {
                        if (!c.equals(jugador) && !c.isBancarrota()) {
                            c.sumarFortuna(250000); // Los otros RECIBEN dinero
                            c.sumarPremios(250000);
                            totalAPagar += 250000;
                        }
                    }
                    if (jugador.getFortuna() < totalAPagar) {
                        System.out.println("¡No tienes suficiente dinero! Debes declararte en bancarrota.");
                        jugador.declararBancarrota(null);
                        tablero.notificarBancarrota(jugador);
                        return;
                    }

                    jugador.restarFortuna(totalAPagar);
                    jugador.sumarGastos(totalAPagar);
                    jugador.sumarPagoTasasEImpuestos(totalAPagar);

                    for (Jugador c : jugadores) {
                        if (!c.equals(jugador) && !c.isBancarrota()) {
                            c.sumarFortuna(250000);
                        }
                    }

                    System.out.println("Pagas " + totalAPagar + "€ como presidente de la junta.");
                    break;


                case 5: // Retroceder tres casillas
                    int nuevaPosicion = casillaActual.getPosicion() - 3;
                    if (nuevaPosicion < 0) nuevaPosicion += 40;
                    casillaDestino = tablero.getCasilla(nuevaPosicion);
                    if (casillaDestino != null) {
                        System.out.println("Retrocedes tres casillas hasta " + casillaDestino.getNombre());
                        casillaActual.eliminarAvatar(jugador.getAvatar());
                        jugador.getAvatar().setCasilla(casillaDestino);
                        jugador.getAvatar().setPosicion(nuevaPosicion);
                        casillaDestino.anhadirAvatar(jugador.getAvatar());
                        casillaDestino.evaluarCasilla(jugador);
                    } else {
                        System.out.println("No se encontró la casilla de destino.");
                    }
                    break;

                case 6: // Multa por móvil
                    if (jugador.getFortuna() < 150000) {
                        System.out.println("¡No tienes suficiente dinero! Debes declararte en bancarrota.");
                        jugador.declararBancarrota(null);
                        tablero.notificarBancarrota(jugador);
                        return;
                    }

                    jugador.restarFortuna(150000);
                    jugador.sumarGastos(150000);
                    jugador.sumarPagoTasasEImpuestos(150000);
                    System.out.println("Pagas 150.000€ por conducir indebidamente.");
                    break;
            }

        } else if (tipo.equals("caja")) {
            switch (id) {
                case 1: // Balneario
                    if (jugador.getFortuna() < 500000) {
                        System.out.println("¡No tienes suficiente dinero! Debes declararte en bancarrota.");
                        jugador.declararBancarrota(null);
                        tablero.notificarBancarrota(jugador);
                        return;
                    }

                    jugador.restarFortuna(500000);
                    jugador.sumarGastos(500000);
                    jugador.sumarPagoTasasEImpuestos(500000);
                    System.out.println("Pagas 500.000€ por un fin de semana en un balneario de 5 estrellas.");
                    break;

                case 2: // Cárcel
                    jugador.irACarcel(tablero);
                    break;

                case 3: // Ir a Salida y cobrar
                    casillaActual.eliminarAvatar(jugador.getAvatar());
                    casillaDestino = tablero.encontrarCasilla("Salida");
                    jugador.getAvatar().setCasilla(casillaDestino);
                    jugador.getAvatar().setPosicion(casillaDestino.getPosicion());
                    casillaDestino.anhadirAvatar(jugador.getAvatar());
                    jugador.sumarFortuna(2000000);
                    jugador.sumarSalida(2000000);
                    System.out.println("Has cobrado 2.000.000€ al llegar a Salida.");
                    casillaDestino.evaluarCasilla(jugador);
                    break;

                case 4: // Beneficio de empresa
                    jugador.sumarFortuna(2000000);
                    jugador.sumarPremios(2000000);
                    System.out.println("Tu compañía de Internet obtiene beneficios. Recibes 2.000.000€.");
                    break;

                case 5: // Viaje a Solar12
                    if (jugador.getFortuna() < 1000000) {
                        System.out.println("¡No tienes suficiente dinero! Debes declararte en bancarrota.");
                        jugador.declararBancarrota(null);
                        tablero.notificarBancarrota(jugador);
                        return;
                    }

                    jugador.restarFortuna(1000000);
                    jugador.sumarGastos(1000000);
                    System.out.println("Pagas 1.000.000€ por invitar a tus amigos a Solar12.");
                    break;

                case 6: // Alquiler villa (CORREGIDO)
                    int totalAlquiler = 0;
                    for (Jugador c : jugadores) {
                        if (!c.equals(jugador) && !c.isBancarrota()) {
                            c.sumarFortuna(200000); // Los otros RECIBEN dinero
                            c.sumarPremios(200000);
                            totalAlquiler += 200000;
                        }
                    }
                    if (jugador.getFortuna() < totalAlquiler) {
                        System.out.println("¡No tienes suficiente dinero! Debes declararte en bancarrota.");
                        jugador.declararBancarrota(null);
                        tablero.notificarBancarrota(jugador);
                        return;
                    }

                    jugador.restarFortuna(totalAlquiler);
                    jugador.sumarGastos(totalAlquiler);
                    jugador.sumarPagoTasasEImpuestos(totalAlquiler);

//                    for (Jugador c : jugadores) {
//                        if (!c.equals(jugador) && !c.isBancarrota()) {
//                            c.sumarFortuna(200000);
//                        }
//                    }

                    System.out.println("Pagas " + totalAlquiler + "€ por alquilar la villa a tus compañeros.");
                    break;
            }
        }
    }

    public static void resetearContadores() {
        contadorSuerte = 0;
        contadorCaja = 0;
    }
}
