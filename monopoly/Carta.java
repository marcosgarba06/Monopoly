package monopoly;

import partida.*;

public class Carta {
    private String descripcion;
    private String tipo; // "suerte" o "caja"

    public Carta(String descripcion, String tipo) {
        this.descripcion = descripcion;
        this.tipo = tipo.toLowerCase();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void aplicarAccion(Jugador jugador, Tablero tablero) {
        switch (descripcion) {
            // Cartas de Suerte
            case "Recibes 1.000.000€ por inversión exitosa":
                jugador.sumarFortuna(1000000);
                System.out.println("!Suerte! ¡Has recibido 1.000.000€ por inversión exitosa!");
                break;

            case "Has ganado una carta para salir de la cárcel":
                jugador.añadirCartaSalirCarcel();
                System.out.println("¡Has ganado una carta para salir de la cárcel! Ahora tienes " + jugador.getCartasSalirCarcel() + ".");
                break;


            case "Paga 500.000€ por reparación de propiedades":
                jugador.restarFortuna(500000);
                jugador.sumarGastos(500000);
                System.out.println("Pagas 500.000€ por reparaciones.");
                break;

            case "Ve directamente a la cárcel":
                jugador.irACarcel(tablero);
                break;

            // Cartas de Caja de Comunidad
            case "Recibes 200.000€ por devolución de impuestos":
                jugador.sumarFortuna(200000);
                System.out.println("¡Has recibido 200.000€ por devolución de impuestos!");
                break;

            case "Paga 300.000€ por gastos médicos":
                jugador.restarFortuna(300000);
                jugador.sumarGastos(300000);
                System.out.println("Pagas 300.000€ por gastos médicos.");
                break;

            case "Avanza a la casilla 'Sol10'":
                Casilla destino = tablero.encontrar_casilla("Sol10");
                if (destino != null) {
                    jugador.getAvatar().setCasilla(destino);
                    destino.evaluarCasilla(jugador);
                    System.out.println("Avanzas a la casilla Sol10.");
                }
                break;

            default:
                System.out.println("La carta no tiene una acción definida.");
        }
    }
}

