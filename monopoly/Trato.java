package monopoly;

import monopoly.Casillas.Propiedades.Propiedad;
import partida.Jugador;

public class Trato {
    private String id;
    private Jugador proponente;
    private Jugador receptor;
    private Propiedad propiedadOfrece;
    private Propiedad propiedadRecibe;
    private float dineroOfrece;
    private float dineroRecibe;

    public Trato(String id, Jugador proponente, Jugador receptor,
                 Propiedad propiedadOfrece, Propiedad propiedadRecibe,
                 float dineroOfrece, float dineroRecibe) {
        this.id = id;
        this.proponente = proponente;
        this.receptor = receptor;
        this.propiedadOfrece = propiedadOfrece;
        this.propiedadRecibe = propiedadRecibe;
        this.dineroOfrece = dineroOfrece;
        this.dineroRecibe = dineroRecibe;
    }

    // Getters
    public String getId() { return id; }
    public Jugador getProponente() { return proponente; }
    public Jugador getReceptor() { return receptor; }
    public Propiedad getPropiedadOfrece() { return propiedadOfrece; }
    public Propiedad getPropiedadRecibe() { return propiedadRecibe; }
    public float getDineroOfrece() { return dineroOfrece; }
    public float getDineroRecibe() { return dineroRecibe; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(receptor.getNombre()).append(", ¿te doy ");

        // Lo que ofrece el proponente
        boolean primero = true;
        if (propiedadOfrece != null) {
            sb.append(propiedadOfrece.getNombre());
            primero = false;
        }
        if (dineroOfrece > 0) {
            if (!primero) sb.append(" y ");
            sb.append((long)dineroOfrece).append("€");
        }

        sb.append(" y tú me das ");

        // Lo que recibe el proponente
        primero = true;
        if (propiedadRecibe != null) {
            sb.append(propiedadRecibe.getNombre());
            primero = false;
        }
        if (dineroRecibe > 0) {
            if (!primero) sb.append(" y ");
            sb.append((long)dineroRecibe).append("€");
        }

        sb.append("?");
        return sb.toString();
    }
}