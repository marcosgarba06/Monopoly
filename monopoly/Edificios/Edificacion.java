package monopoly.Edificios;

import partida.Jugador;
import monopoly.Casilla;

import java.util.ArrayList;
import java.util.List;


public abstract class Edificacion {

    protected String id;
    protected Jugador propietario;
    protected Casilla casilla;
    protected float coste;

    public Edificacion(String id, Jugador propietario, Casilla casilla, float coste) {
        this.id = id;
        this.propietario = propietario;
        this.casilla = casilla;
        this.coste = coste;
    }


    public abstract String obtenerTipo();
    public abstract boolean puedeEdificar(Jugador jugador, Casilla casilla);

    public String getId() { return id; }
    public Jugador getPropietario() { return propietario; }
    public Casilla getCasilla() { return casilla; }
    public float getCoste() { return coste; }



}
