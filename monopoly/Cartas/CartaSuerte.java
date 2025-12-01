package monopoly.Cartas;

import monopoly.Casilla;
import monopoly.Tablero;
import partida.*;
import java.util.ArrayList;

public class CartaSuerte extends Carta {

    public CartaSuerte(String descripcion, int id) {
        super(descripcion, "suerte", id);
    }

    @Override
    public void aplicarAccion(Jugador jugador, Tablero tablero) {
        System.out.println("Carta de Suerte seleccionada: " + descripcion);

        ArrayList<Jugador> jugadores = Carta.getJugadores();

        if (jugadores == null || jugadores.isEmpty()) {
            System.out.println("ERROR: Lista de jugadores no inicializada.");
            return;
        }

        Casilla casillaActual = jugador.getAvatar().getCasilla();
        Casilla casillaDestino;

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
                if (nuevaPosicion < 0)
                    nuevaPosicion += 40; // Ajusta si la posición es negativa (vuelta al inicio del tablero)
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
                    if (distancia == 0)
                        distancia = 40; // Sí está en la misma casilla, considerar como 40 para evitar elegirla
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
                        System.out.println("Debes pagar el doble del alquiler: " + (long) alquilerDoble + "€");

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
    }
}
