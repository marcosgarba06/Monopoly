package monopoly.Edificios;

import monopoly.Casillas.Propiedades.Solar;
import partida.Jugador;

public abstract class Edificacion {

    protected String id;
    protected Jugador propietario;
    protected Solar solar;
    protected float coste;

    public Edificacion(String id, Jugador propietario, Solar solar, float coste) {
        this.id = id;
        this.propietario = propietario;
        this.solar = solar;
        this.coste = coste;
    }

    // Métodos abstractos
    public abstract String obtenerTipo();
    public abstract boolean puedeEdificar(Jugador jugador, Solar solar);

    // Getters
    public String getId() { return id; }
    public Jugador getPropietario() { return propietario; }
    public Solar getSolar() { return solar; }
    public float getCoste() { return coste; }
}
