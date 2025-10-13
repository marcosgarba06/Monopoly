package monopoly;

import partida.*;
import java.util.ArrayList;

public class Casilla {

    // Atributos
    private String nombre;
    private String tipo;
    private float valor;
    private int posicion;
    private Jugador duenho;
    private Grupo grupo;
    private float impuesto;
    private float hipoteca;
    private ArrayList<Avatar> avatares;

    // Constructor vacío
    public Casilla() {
        this.avatares = new ArrayList<>();
    }

    // Constructor para solares, servicios o transporte
    public Casilla(String nombre, String tipo, int posicion, float valor, Jugador duenho) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;
        this.valor = valor;
        this.duenho = duenho;
        this.avatares = new ArrayList<>();
    }

    // Constructor para impuestos
    public Casilla(String nombre, int posicion, float impuesto, Jugador duenho) {
        this.nombre = nombre;
        this.tipo = "Impuesto";
        this.posicion = posicion;
        this.impuesto = impuesto;
        this.duenho = duenho;
        this.avatares = new ArrayList<>();
    }

    // Constructor para especiales, suerte, comunidad...
    public Casilla(String nombre, String tipo, int posicion, Jugador duenho) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;
        this.duenho = duenho;
        this.avatares = new ArrayList<>();
    }

    // --- GETTERS ---
    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public float getValor() {
        return valor;
    }

    public int getPosicion() {
        return posicion;
    }

    public Jugador getDuenho() {
        return duenho;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public float getImpuesto() {
        return impuesto;
    }

    public float getHipoteca() {
        return hipoteca;
    }

    public ArrayList<Avatar> getAvatares() {
        return avatares;
    }

    // --- SETTERS básicos ---
    public void setDuenho(Jugador duenho) {
        this.duenho = duenho;
    }

    // --- Métodos auxiliares ---
    public void anhadirAvatar(Avatar av) {
        avatares.add(av);
    }

    public void eliminarAvatar(Avatar av) {
        avatares.remove(av);
    }

    public void sumarValor(float suma) {
        this.valor += suma;
    }

    // Métodos aún por implementar
    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {
        // Aquí pondrás la lógica de qué pasa al caer en la casilla
        return true;
    }

    public void comprarCasilla(Jugador solicitante, Jugador banca) {
        // Aquí pondrás la lógica de compra
    }

    public String infoCasilla() {
        return "Casilla: " + nombre + " (" + tipo + "), valor=" + valor;
    }

    public String casEnVenta() {
        return nombre + " en venta por " + valor;
    }

    @Override
    public String toString() {
        return nombre;
    }
}

