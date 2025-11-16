package monopoly;

import partida.Jugador;

public class Edificacion {

    private String id;
    private Jugador propietario;
    private Casilla casilla;
    private String tipo; // casa, hotel, piscina, pista
    private float coste;

    //constructor con parámetros → recibe datos desde fuera cuando alguien crea una nueva edificación.
    public Edificacion(String id, Jugador propietario, Casilla casilla, String tipo, float coste) {
        this.id = id;
        this.propietario = propietario;
        this.casilla = casilla;
        this.tipo = tipo;
        this.coste = coste;
    }

    //getters -> solo tiene getters por que es una clase de datos, es decir que una vez incializada los valores de sus atributos no deberian cambias

    public String getId() { return id; }
    public Jugador getPropietario() { return propietario; }
    public Casilla getCasilla() { return casilla; }
    public String getTipo() { return tipo; }
    public float getCoste() { return coste; }

}