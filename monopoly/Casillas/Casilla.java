package monopoly.Casillas;

import partida.Avatar;
import partida.Jugador;
import monopoly.Tablero;
import java.util.ArrayList;

//Casilla es una clase abstracta, por lo que no se puede instanciar, solo extender a Especial, Propiedad, etc.

public abstract class Casilla {

    // Atributos comunes a todas las casillas
    protected String nombre;
    protected int posicion;
    protected ArrayList<Avatar> avatares;
    protected int vecesVisitada;

    protected Tablero tablero;
    protected Grupo grupo; // Grupo al que pertenece la casilla (si aplica)
    protected Jugador duenho;
    protected String tipo;    // "solar", "servicio", "transporte", etc.

    // Constructor
    public Casilla(String nombre, int posicion) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.avatares = new ArrayList<>();
        this.vecesVisitada = 0;
    }

    // Métodos comunes
    public boolean estaAvatar(Avatar avatar) {
        return avatares.contains(avatar);
    }

    public int frecuenciaVisita() {
        return vecesVisitada;
    }

    public void incrementarVisita() {
        vecesVisitada++;
    }

    public void anhadirAvatar(Avatar avatar) {
        avatares.add(avatar);
    }

    public void eliminarAvatar(Avatar avatar) {
        avatares.remove(avatar);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Casilla: ").append(nombre);
        sb.append(" (Posición: ").append(posicion).append(")");
        if (!avatares.isEmpty()) {
            sb.append(" - Avatares: ");
            for (Avatar av : avatares) {
                sb.append(av.getId()).append(" ");
            }
        }
        return sb.toString();
    }

    // Método abstracto que debe ser implementado por las subclases
    public abstract void evaluarCasilla(Jugador jugador, Tablero tablero);

    // Getters y setters
    public String getNombre() { return nombre; }
    public int getPosicion() { return posicion; }
    public ArrayList<Avatar> getAvatares() { return avatares; }
    public int getVecesVisitada() { return vecesVisitada; }

    public void setTablero(Tablero tablero) { this.tablero = tablero; }
    public Tablero getTablero() { return tablero; }

    public void setGrupo(Grupo g) { this.grupo = g; }
    public Grupo getGrupo() { return grupo; }

    public void setDuenho(Jugador j) { this.duenho = j; }
    public Jugador getDuenho() { return duenho; }

    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getTipo() { return tipo; }
}
