package monopoly.Casillas.Propiedades;

import monopoly.Casillas.Casilla;

public final class Servicio extends Propiedad {

    private static final int MULTIPLICADOR_UNO = 4;
    private static final int MULTIPLICADOR_DOS = 10;
    private static final double FACTOR_BASE = 50000;

    public Servicio(String nombre, int posicion, float valor) {
        super(nombre, posicion, valor);
        setHipoteca(valor/2);
    }

    @Override
    public float alquiler(int tirada) {
        if (getDuenho() == null) return 0;

        int numServicios = contarServiciosDelDuenho();
        int multiplicador = (numServicios == 1) ? MULTIPLICADOR_UNO : MULTIPLICADOR_DOS;

        return (float)(multiplicador * tirada * FACTOR_BASE);
    }

    @Override
    public float valor() {
        return getValor();
    }

    private int contarServiciosDelDuenho() {
        int contador = 0;
        if (getDuenho() == null) return 0;

        for (Casilla c : getDuenho().getPropiedades()) {
            if (c instanceof Servicio && !((Servicio)c).estaHipotecada()) {
                contador++;
            }
        }
        return contador;
    }
}
