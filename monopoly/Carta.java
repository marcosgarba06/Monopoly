package monopoly;

import partida.*;

import java.util.ArrayList;

public class Carta {

    private String descripcion;
    private String tipo;// "suerte" o "caja"
    private int id; //indica el numero que identifica a la carta dentro del mazo

    private static int contadorSuerte = 0; //carta actual de suerte
    private static int contadorCaja = 0; //carta actual de caja

    private static ArrayList<Carta> mazoSuerte = null; //listas que contienen las cartas de tipo suerte
    private static ArrayList<Carta> mazoCaja = null; //lista que contiene las cartas de tipo caja

    private static ArrayList<Jugador> jugadores; //lista de jugadores

    public Carta(String descripcion, String tipo, int id) {
        this.descripcion = descripcion;
        this.tipo = tipo.toLowerCase();
        this.id = id;
    }

    public static void setJugadores(ArrayList<Jugador> listaJugadores) { // metodo para asignar la lista de jugadores
        jugadores = listaJugadores; // asigna la lista de jugadores al sistema de cartas, hace que haya una sola lista para todas las cartas
    }


    public static void inicializarMazos() { // metodo para inicializar los mazos de cartas, se llama al iniciar la partida
        // Inicializar mazo de suerte
        mazoSuerte = new ArrayList<>();
        mazoSuerte.add(new Carta("Decides hacer un viaje de placer. Avanza hasta Solar19", "suerte", 1));
        mazoSuerte.add(new Carta("Los acreedores te persiguen por impago. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida y sin cobrar los 2.000.000€", "suerte", 2));
        mazoSuerte.add(new Carta("¡Has ganado el bote de la lotería! Recibe 1.000.000€", "suerte", 3));
        mazoSuerte.add(new Carta("Has sido elegido presidente de la junta directiva. Paga a cada jugador 250.000€", "suerte", 4));
        mazoSuerte.add(new Carta("¡Hora punta de tráfico! Retrocede tres casillas.", "suerte", 5));
        mazoSuerte.add(new Carta("Te multan por usar el móvil mientras conduces. Paga 150.000€.", "suerte", 6));
        mazoSuerte.add(new Carta("Avanza hasta la casilla de transporte más cercana. Si no tiene dueño, puedes comprarla. Si tiene dueño, paga al dueño el doble de la operación indicada.", "suerte" , 7));

        // Inicializar mazo de caja
        mazoCaja = new ArrayList<>();
        mazoCaja.add(new Carta("Paga 500.000€ por un fin de semana en un balneario de 5 estrellas", "caja", 1));
        mazoCaja.add(new Carta("Te investigan por fraude de identidad. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida y sin cobrar los 2.000.000€", "caja", 2));
        mazoCaja.add(new Carta("Colócate en la casilla de Salida. Cobra 2.000.000€", "caja", 3));
        mazoCaja.add(new Carta("Devolución de Hacienda. Cobra 500.000€", "caja", 4));
        mazoCaja.add(new Carta("Retrocede hasta Solar1 para comprar antigüedades exóticas.", "caja", 5));
        mazoCaja.add(new Carta("Ve a Solar20 para disfrutar del San Fermín. Si pasas por la casilla de Salida, cobra 2.000.000€.", "caja", 6));

    }

    public static Carta seleccionarCarta(String tipo) { // metodo para seleccionar una carta del mazo correspondiente

        if (mazoSuerte == null || mazoCaja == null) {
            inicializarMazos();
        }
        //cartaSeleccionada es un objeto que tiene valor tipo null hasta que se le asigne un vaalor
        //cuando hagamos mazoSuerte.get(), ahi ya apunta a un objeto real de tipo carta
        Carta cartaSeleccionada; //aqui se va a aguardar la carta seleccionada

        if (tipo.equalsIgnoreCase("suerte")) {
            if (mazoSuerte.isEmpty()) {
                System.out.println("Error: No hay cartas de Suerte disponibles.");
                return null;
            }
            cartaSeleccionada = mazoSuerte.get(contadorSuerte % mazoSuerte.size()); //contadorSuerte % mazoSuerte.size() devuelve un valor entre 0 y el tamaño del mazo -1
            //El uso de % mazo.size() hace que el orden sea circular: después de la última carta, vuelve a la primera.
            contadorSuerte++; //para que la próxima vez se avance a la siguiente carta.
        } else {
            if (mazoCaja.isEmpty()) {
                System.out.println("Error: No hay cartas de Caja disponibles.");
                return null;
            }
            cartaSeleccionada = mazoCaja.get(contadorCaja % mazoCaja.size()); //contadorCaja % mazoCaja.size() devuelve un valor entre 0 y el tamaño del mazo -1
            //El uso de % mazo.size() hace que el orden sea circular: después de la última carta, vuelve a la primera.
            contadorCaja++; //para que la próxima vez se avance a la siguiente carta.
        }
        return cartaSeleccionada;
    }

    //cartaSeleccionada.aplicarAccion(jugadorActual, tablero); estoy dentro de la carta
    public void aplicarAccion(Jugador jugador, Tablero tablero) {

        System.out.println("Carta seleccionada: " + descripcion);

        // Verificación de seguridad
        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("ERROR: Lista de jugadores no inicializada.");
            return;
        }

        Casilla casillaActual = jugador.getAvatar().getCasilla(); //cogemos la caslla del avatar
        Casilla casillaDestino; //cojemos la casilla destino

        /// ///////SUERTE
        if (tipo.equals("suerte")) {
            switch (id) {
                case 1: // Avanza a Solar19
                    casillaDestino = tablero.encontrarCasilla("Sol19"); //buscamos la casilla Sol19 en el tablero
                    if (casillaDestino != null) { //si la casilla existe
                        System.out.println("Avanzas a la casilla Sol19.");
                        casillaActual.eliminarAvatar(jugador.getAvatar()); //eliminamos el avatar de la casilla actual
                        jugador.getAvatar().setCasilla(casillaDestino); //asignamos la nueva casilla al avatar
                        jugador.getAvatar().setPosicion(casillaDestino.getPosicion()); //actualizamos la posicion del avatar
                        casillaDestino.anhadirAvatar(jugador.getAvatar()); //añadimos el avatar a la casilla destino
                        casillaDestino.evaluarCasilla(jugador); //evaluamos la casilla destino (compra o pago alquiler)
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
                    jugador.sumarPremios(1000000); // Contabiliza el premio
                    break;

                case 4: // Pagar a cada jugador
                    int totalAPagar = 0;
                    for (Jugador c : jugadores) { // Los demás JUGADORES RECIBEN dinero
                        if (!c.equals(jugador) && !c.isBancarrota()) { // Si no es el jugador que saca la carta y no está en bancarrota
                            c.sumarFortuna(250000); // Los otros RECIBEN dinero
                            c.sumarPremios(250000);
                            totalAPagar += 250000;
                        }
                    }
                    if (jugador.getFortuna() < totalAPagar) { // El jugador que saca la carta PAGA, si no tiene suficiente, se declara en bancarrota
                        System.out.println("¡No tienes suficiente dinero! Debes declararte en bancarrota.");
                        jugador.declararBancarrota(null);
                        tablero.notificarBancarrota(jugador);
                        return;
                    }

                    jugador.restarFortuna(totalAPagar); // El jugador que saca la carta PAGA
                    jugador.sumarGastos(totalAPagar); // Contabiliza el gasto
                    jugador.sumarPagoTasasEImpuestos(totalAPagar); // Contabiliza el pago de tasas e impuestos


                    System.out.println("Pagas " + totalAPagar + "€ como presidente de la junta.");
                    break;


                case 5: // Retroceder tres casillas
                    int nuevaPosicion = casillaActual.getPosicion() - 3; // Calcula la nueva posición retrocediendo tres casillas
                    if (nuevaPosicion < 0) nuevaPosicion += 40; // Ajusta si la posición es negativa (vuelta al inicio del tablero)
                    casillaDestino = tablero.getCasilla(nuevaPosicion); // Obtiene la casilla de destino

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
                case 7:
                    int posActual = casillaActual.getPosicion();
                    int[] posicionesTransporte = {5, 15, 25, 35}; // Tran1, Tran2, Tran3, Tran4

                    int posCercana = -1;
                    int distanciaMin = 40;

                    for (int posT : posicionesTransporte) {
                        int distancia = (posT - posActual + 40) % 40; // Distancia en sentido horario
                        if (distancia == 0) distancia = 40; // Si está en la misma casilla, considerar como 40 para evitar elegirla
                        if (distancia < distanciaMin) { // Encontrar la más cercana,
                            distanciaMin = distancia; // Actualizar la distancia mínima
                            posCercana = posT; // Actualizar la posición más cercana
                        }
                    }

                    casillaDestino = tablero.getCasilla(posCercana); // Obtener la casilla de destino, la más cercana
                    if (casillaDestino != null) {
                        System.out.println("Avanzas al transporte más cercano: " + casillaDestino.getNombre());

                        // Verificar si pasa por la salida
                        if (posCercana < posActual) {
                            jugador.sumarFortuna(2000000);
                            jugador.sumarSalida(2000000);
                            System.out.println("Has pasado por la casilla de salida. Recibes 2.000.000€.");
                        }

                        casillaActual.eliminarAvatar(jugador.getAvatar());
                        jugador.getAvatar().setCasilla(casillaDestino);
                        jugador.getAvatar().setPosicion(casillaDestino.getPosicion());
                        casillaDestino.anhadirAvatar(jugador.getAvatar());

                        // Evaluar la casilla (compra o pago doble)

                        if (casillaDestino.getDuenho() == null) {
                            System.out.println("La casilla está en venta. Puedes comprarla con: comprar " + casillaDestino.getNombre());
                        } else if (!casillaDestino.getDuenho().equals(jugador)) {
                            float alquilerDoble = casillaDestino.getAlquiler() * 2; // Calcular el alquiler doble
                            System.out.println("Debes pagar el doble del alquiler: " + (long)alquilerDoble + "€");

                            if (jugador.getFortuna() < alquilerDoble) {
                                System.out.println("No puedes pagar. Te declaras en bancarrota.");
                                jugador.declararBancarrota(casillaDestino.getDuenho());
                                tablero.notificarBancarrota(jugador);
                            } else {
                                jugador.pagar(alquilerDoble, casillaDestino.getDuenho());
                                jugador.sumarPagoAlquiler(alquilerDoble);
                                casillaDestino.getDuenho().sumarCobroAlquiler(alquilerDoble);
                                casillaDestino.sumarIngresos(alquilerDoble);
                            }
                        }
                    }
                    break;
            }


            /// ///////CAJA
        } else if (tipo.equals("caja")) { //
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

                case 4: // Devolución Hacienda
                    jugador.sumarFortuna(500000);
                    jugador.sumarPremios(500000);
                    System.out.println("Devolución de Hacienda. Recibes 500.000€.");
                    break;

                case 5: // Retroceder a Solar1
                    casillaDestino = tablero.encontrarCasilla("Sol1");
                    if (casillaDestino != null) {
                        System.out.println("Retrocedes hasta Solar1 para comprar antigüedades exóticas.");
                        casillaActual.eliminarAvatar(jugador.getAvatar());
                        jugador.getAvatar().setCasilla(casillaDestino);
                        jugador.getAvatar().setPosicion(casillaDestino.getPosicion());
                        casillaDestino.anhadirAvatar(jugador.getAvatar());
                        casillaDestino.evaluarCasilla(jugador);
                    }
                    break;

                case 6: // Ir a Solar20
                    casillaDestino = tablero.encontrarCasilla("Sol20");
                    if (casillaDestino != null) {
                        System.out.println("Vas a Solar20 para disfrutar del San Fermín.");

                        // Verificar si pasa por la salida
                        if (casillaDestino.getPosicion() < casillaActual.getPosicion()) {
                            jugador.sumarFortuna(2000000);
                            jugador.sumarSalida(2000000);
                            System.out.println("Has pasado por la casilla de salida. Recibes 2.000.000€.");
                        }

                        casillaActual.eliminarAvatar(jugador.getAvatar());
                        jugador.getAvatar().setCasilla(casillaDestino);
                        jugador.getAvatar().setPosicion(casillaDestino.getPosicion());
                        casillaDestino.anhadirAvatar(jugador.getAvatar());
                        casillaDestino.evaluarCasilla(jugador);
                    }
                    break;
            }
        }
    }

    public static void resetearContadores() {
        contadorSuerte = 0;
        contadorCaja = 0;
    }
}