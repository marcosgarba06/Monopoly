package monopoly.Casillas;

import monopoly.Casillas.Propiedades.Propiedad;
import monopoly.Tablero;
import partida.Avatar;
import partida.Jugador;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase que representa un grupo de propiedades.
 * Forma parte de la jerarquía de Casilla y tiene relación de COMPOSICIÓN con Propiedad.
 */
public class Grupo extends Casilla {

    private String colorGrupo;
    private List<Propiedad> propiedades;

    // Constructor
    public Grupo(String nombre) {
        super(nombre, -1);
        this.colorGrupo = nombre;
        this.propiedades = new ArrayList<>();
    }

    public void anhadirCasilla(Casilla c) {
        if (c instanceof Propiedad) {
            Propiedad p = (Propiedad) c;
            propiedades.add(p);
            p.setGrupo(this); // relación bidireccional
        }
    }

    public void eliminarPropiedad(Propiedad propiedad) {
        if (propiedades.remove(propiedad)) {
            propiedad.setGrupo(null); // romper relación bidireccional
        }
    }

    public boolean esDuenhoGrupo(Jugador jugador) {
        if (propiedades.isEmpty()) return false;
        for (Propiedad p : propiedades) {
            if (!p.perteneceAJugador(jugador)) return false;
        }
        return true;
    }

    public int contarPropiedadesDeJugador(Jugador jugador) {
        int contador = 0;
        for (Propiedad p : propiedades) {
            if (p.perteneceAJugador(jugador)) contador++;
        }
        return contador;
    }

    public boolean tieneAlgunaHipotecada() {
        for (Propiedad p : propiedades) {
            if (p.estaHipotecada()) return true;
        }
        return false;
    }


    public List<Propiedad> getPropiedadesEnVenta() {
        return propiedades.stream()
                .filter(Propiedad::estaEnVenta)
                .collect(Collectors.toList());
    }

    // Métodos heredados de Casilla
    @Override
    public boolean estaAvatar(Avatar avatar) {
        // Un grupo no tiene avatares directamente, se delega en sus propiedades
        return propiedades.stream().anyMatch(p -> p.estaAvatar(avatar));
    }

    @Override
    public int frecuenciaVisita() {
        // Suma de las visitas a todas las propiedades del grupo
        return propiedades.stream().mapToInt(Propiedad::frecuenciaVisita).sum();
    }

    @Override
    public String toString() {
        return "Grupo " + colorGrupo + " con " + propiedades.size() + " propiedades: " +
                propiedades.stream().map(Propiedad::getNombre).collect(Collectors.joining(", "));
    }

    @Override
    public void evaluarCasilla(Jugador jugador, Tablero tablero) {

    }

    public boolean perteneceEnteramenteA(Jugador jugador) {
        return esDuenhoGrupo(jugador);
    }

    // Getters
    public String getColorGrupo() { return colorGrupo; }
    public List<Propiedad> getPropiedades() { return new ArrayList<>(propiedades); }

    public List<Propiedad> getMiembros() {
        return new ArrayList<>(propiedades);
    }
}
