package monopoly.Casillas.Propiedades;

import monopoly.Casillas.Casilla;
import partida.Jugador;

public final class Transporte extends Propiedad {

    private float alquilerBase;

    public Transporte(String nombre, int posicion, float valor) {
        super(nombre, posicion, valor);
        this.alquilerBase = 250000; // Valor por defecto
        this.hipoteca = valor / 2;
    }

    @Override
    public float alquiler(int tirada) {
        if (duenho == null) return 0;

        int numTransportes = contarTransportesDelDuenho();
        return alquilerBase * numTransportes;
    }

    @Override
    public float valor() {
        return valor;
    }

    private int contarTransportesDelDuenho() {
        int contador = 0;
        if (duenho == null) return 0;

        for (Casilla c : duenho.getPropiedades()) {
            if (c instanceof Transporte && !((Transporte)c).estaHipotecada()) {
                contador++;
            }
        }
        return contador;
    }

    public float getAlquilerBase() { return alquilerBase; }
    public void setAlquilerBase(float a) { alquilerBase = a; }
}
