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


    public void ejecutarTrato() {
        // persona que ofrece el trato da una propiedad al receptor del trato
        if (propiedadOfrece != null) {
            proponente.eliminarPropiedad(propiedadOfrece);
            receptor.anhadirPropiedad(propiedadOfrece);
            propiedadOfrece.setDuenho(receptor);
        }

        //  persona que recibe el trato da ina propiedad al que propuso el trato
        if (propiedadRecibe != null) {
            receptor.eliminarPropiedad(propiedadRecibe);
            proponente.anhadirPropiedad(propiedadRecibe);
            propiedadRecibe.setDuenho(proponente);
        }

        // damos una cantidad de dinero de la persona que ofreció el trato a la persona que recibió el trato
        if (dineroOfrece > 0) {
            proponente.restarFortuna(dineroOfrece);
            receptor.sumarFortuna(dineroRecibe);
        }

        // damos una cantidad de dinero de la persona que recibió el trato a la persona que ofreció el trato
        if (dineroRecibe > 0) {
            receptor.restarFortuna(dineroRecibe);
            proponente.sumarFortuna(dineroRecibe);
        }
    }

    // comprobamos que los jugadores tienen las propiedades y/o dinero del trato propuesto
    public boolean esTratoValido() {
        if (propiedadOfrece != null && propiedadRecibe.perteneceAJugador(proponente)) return false;

        if (dineroOfrece > 0 && proponente.getFortuna() < dineroRecibe) return false;

        if (propiedadRecibe != null && !propiedadRecibe.perteneceAJugador(receptor)) return false;

        if (dineroRecibe > 0 && receptor.getFortuna() < dineroRecibe) return false;

        if (propiedadOfrece != null && propiedadOfrece.estaHipotecada()) return false;

        if (propiedadRecibe != null && !propiedadRecibe.estaHipotecada()) return false;

        return true;
    }

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