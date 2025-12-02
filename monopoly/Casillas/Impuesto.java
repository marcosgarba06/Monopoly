package monopoly.Casillas;


import monopoly.Tablero;
import partida.Jugador;

public class Impuesto extends Casilla {

    private float cantidadAPagar;

    public Impuesto(String nombre, int posicion, float cantidadAPagar) {
        super(nombre, posicion);
        this.cantidadAPagar = cantidadAPagar;
    }


    @Override
    public void evaluarCasilla(Jugador jugador, Tablero tablero) {
        incrementarVisita();

        System.out.println("Debes pagar un impuesto de " + (long)cantidadAPagar + "€.");

        if (jugador.getFortuna() >= cantidadAPagar) {
            jugador.restarFortuna(cantidadAPagar);
            jugador.sumarGastos(cantidadAPagar);
            jugador.sumarPagoTasasEImpuestos(cantidadAPagar);
            tablero.añadirAlParking(cantidadAPagar);
            System.out.println("El dinero se ha depositado en el parking. Total acumulado: " +
                    (long)tablero.getFondoParking() + "€.");
        } else {
            System.out.println("No tienes suficiente dinero para pagar el impuesto.");
            jugador.setDeudaPendiente(cantidadAPagar);
            jugador.setAcreedorDeuda(null); // Deuda con la banca
        }
    }

    public float getCantidadAPagar() {
        return cantidadAPagar;
    }


}

