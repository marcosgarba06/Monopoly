package monopoly.Cartas;

import monopoly.Casillas.Casilla;
import monopoly.Tablero;
import monopoly.*;
import partida.*;
import java.util.ArrayList;

public class CartaCajaComunidad extends Carta {

    public CartaCajaComunidad(String descripcion, int id) {
        super(descripcion, "caja",id);
    }

    @Override
    public void aplicarAccion(Jugador jugador, Tablero tablero) {
        Juego.consola.imprimir("Carta de la Caja Comunidad seleccionada: " + descripcion);

        ArrayList<Jugador> jugadores = Carta.getJugadores();

        if (jugadores == null || jugadores.isEmpty()) {
            Juego.consola.imprimir("ERROR: Lista de jugadores no inicializada.");
            return;
        }

        Casilla casillaActual = jugador.getAvatar().getCasilla();
        Casilla casillaDestino;

        switch (id) {
            case 1: // Balneario: pagar 500.000€
                if (jugador.getFortuna() < 500000) {
                    Juego.consola.imprimir("¡No tienes suficiente dinero! Debes declararte en bancarrota.");
                    jugador.declararBancarrota(null);
                    tablero.notificarBancarrota(jugador);
                    return;
                }
                jugador.restarFortuna(500000);
                jugador.sumarGastos(500000);
                jugador.sumarPagoTasasEImpuestos(500000);
                Juego.consola.imprimir("Pagas 500.000€ por un fin de semana en un balneario de 5 estrellas.");
                break;

            case 2: // Cárcel
                jugador.irACarcel(tablero);
                break;

            case 3: // Ir a Salida y cobrar 2.000.000€
                casillaActual.eliminarAvatar(jugador.getAvatar());
                casillaDestino = tablero.encontrarCasilla("Salida");
                jugador.getAvatar().setCasilla(casillaDestino);
                jugador.getAvatar().setPosicion(casillaDestino.getPosicion());
                casillaDestino.anhadirAvatar(jugador.getAvatar());
                jugador.sumarFortuna(2000000);
                jugador.sumarSalida(2000000);
                Juego.consola.imprimir("Has cobrado 2.000.000€ al llegar a Salida.");
                casillaDestino.evaluarCasilla(jugador, tablero);
                break;

            case 4: // Devolución de Hacienda: +500.000€
                jugador.sumarFortuna(500000);
                jugador.sumarPremios(500000);
                Juego.consola.imprimir("Devolución de Hacienda. Recibes 500.000€.");
                break;

            case 5: // Retroceder a Solar1
                casillaDestino = tablero.encontrarCasilla("Sol1");
                if (casillaDestino != null) {
                    Juego.consola.imprimir("Retrocedes hasta Solar1 para comprar antigüedades exóticas.");
                    casillaActual.eliminarAvatar(jugador.getAvatar());
                    jugador.getAvatar().setCasilla(casillaDestino);
                    jugador.getAvatar().setPosicion(casillaDestino.getPosicion());
                    casillaDestino.anhadirAvatar(jugador.getAvatar());
                    casillaDestino.evaluarCasilla(jugador, tablero);
                }
                break;

            case 6: // Ir a Solar20 (cobrando salida si corresponde)
                casillaDestino = tablero.encontrarCasilla("Sol20");
                if (casillaDestino != null) {
                    Juego.consola.imprimir("Vas a Solar20 para disfrutar del San Fermín.");

                    if (casillaDestino.getPosicion() < casillaActual.getPosicion()) {
                        jugador.sumarFortuna(2000000);
                        jugador.sumarSalida(2000000);
                        Juego.consola.imprimir("Has pasado por la casilla de salida. Recibes 2.000.000€.");
                    }

                    casillaActual.eliminarAvatar(jugador.getAvatar());
                    jugador.getAvatar().setCasilla(casillaDestino);
                    jugador.getAvatar().setPosicion(casillaDestino.getPosicion());
                    casillaDestino.anhadirAvatar(jugador.getAvatar());
                    casillaDestino.evaluarCasilla(jugador, tablero);
                }
                break;
        }
    }
}
