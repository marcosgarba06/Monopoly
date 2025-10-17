package monopoly;

import partida.*;
import java.util.ArrayList;

public class Casilla {

    // Atributos
    private String nombre;
    private String tipo;       // Solar, Especial, Transporte, Servicio, Caja, Suerte, Impuesto
    private float valor;
    private int posicion;
    private Jugador duenho;    // Propietario (usa solo este, evita "propietario")
    private Grupo grupo;
    private float impuesto;
    private Tablero tablero;   // para acciones como ir a cárcel
    private ArrayList<Avatar> avatares;

    // ====== Constructores ======

    public Casilla() {
        this.avatares = new ArrayList<>();

    }

    // Solares / Servicio / Transporte
    public Casilla(String nombre, String tipo, int posicion, float valor, Jugador banca) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;
        if ("impuesto".equalsIgnoreCase(tipo)) {
            this.impuesto = valor; // ✅ ahora sí se guarda como impuesto
        } else {
            this.valor = valor;
        }
        this.duenho = null;        // al crearse, no tiene dueño (banca no cuenta como dueño)
        this.avatares = new ArrayList<>();
    }

    // Impuesto
    public Casilla(String nombre, int posicion, float impuesto, Jugador banca) {
        this.nombre = nombre;
        this.tipo = "Impuesto";
        this.posicion = posicion;
        this.impuesto = impuesto;
        this.duenho = null;
        this.avatares = new ArrayList<>();
    }

    // Especial / Suerte / Caja
    public Casilla(String nombre, String tipo, int posicion, Jugador banca) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;
        this.duenho = null;
        this.avatares = new ArrayList<>();
    }

    // ====== Getters ======

    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public float getValor() { return valor; }
    public int getPosicion() { return posicion; }
    public Jugador getDuenho() { return duenho; }
    public Grupo getGrupo() { return grupo; }
    public float getImpuesto() { return impuesto; }
    public ArrayList<Avatar> getAvatares() { return avatares; }

    // ====== Setters ======

    public void setDuenho(Jugador d) { this.duenho = d; }
    public void setGrupo(Grupo g) { this.grupo = g; }
    public void setTablero(Tablero t) { this.tablero = t; }

    // ====== Auxiliares ======

    public void anhadirAvatar(Avatar av) { if (av != null && !avatares.contains(av)) avatares.add(av); }
    public void eliminarAvatar(Avatar av) { avatares.remove(av); }
    public void sumarValor(float suma) { this.valor += suma; }

    // ====== Lógica de alquiler sencilla ======

    public float evaluarAlquiler(int tirada) {
        String t = (this.tipo == null) ? "" : this.tipo.toLowerCase();

        switch (t) {
            case "solar":
                // alquiler base simple: 10% del valor
                return Math.max(1f, this.valor * 0.10f);

            case "transporte":
                // tarifa fija simple (ajusta si tienes varias estaciones)
                return 250000f;

            case "servicio":
                // depende de la tirada (si no hay tirada, usa 1)
                int k = (tirada > 0) ? tirada : 1;
                return 20000f * k;

            default:
                return 0f;
        }
    }

    // ====== Evaluación de casilla al caer ======

    public void evaluarCasilla(Jugador jugador) {
        System.out.println("Evaluando la casilla: " + this.nombre);
        String t = (this.tipo == null) ? "" : this.tipo.toLowerCase();

        switch (t) {
            case "solar":
            case "transporte":
            case "servicio":
                if (this.getDuenho() == null) {
                    System.out.println("La casilla está en venta por " + (long)this.valor + ".");
                } else if (!this.getDuenho().equals(jugador)) {
                    System.out.println("La casilla pertenece a " + this.getDuenho().getNombre() + ". Debes pagar alquiler.");
                    // float alquiler = evaluarAlquiler(/* tirada real */ 1);
                    // jugador.pagar(alquiler, this.getDuenho());
                } else {
                    System.out.println("Has caído en tu propia propiedad.");
                }
                break;

            case "impuesto":
                System.out.println("Debes pagar un impuesto de " + (long)this.impuesto + "€.");
                jugador.restarFortuna(this.impuesto);
                jugador.sumarGastos(this.impuesto);

                if (this.tablero != null) {
                    this.tablero.añadirAlParking(this.impuesto);
                    System.out.println("El dinero se ha depositado en el parking. Total acumulado: " + (long)this.tablero.getFondoParking() + "€.");
                } else {
                    System.out.println("Error: tablero no asignado en la casilla.");
                }
                break;


            case "suerte":
                System.out.println("Has caído en Suerte. Robas una carta...");
                if (tablero != null) {
                    Carta cartaS = tablero.robarCarta("suerte");
                    cartaS.aplicarAccion(jugador, tablero);
                } else {
                    System.out.println("Error: tablero no asignado.");
                }
                break;

            case "caja":
                System.out.println("Has caído en Caja de Comunidad. Robas una carta...");
                if (tablero != null) {
                    Carta cartaC = tablero.robarCarta("caja");
                    cartaC.aplicarAccion(jugador, tablero);
                } else {
                    System.out.println("Error: tablero no asignado.");
                }
                break;


            case "especial":
                if ("ircarcel".equalsIgnoreCase(this.nombre)) {
                    System.out.println("¡Vas a la cárcel!");
                    if (this.tablero != null) {
                        jugador.irACarcel(this.tablero);
                    } else {
                        System.out.println("Error: tablero no asignado en la casilla.");
                    }
                } else if ("parking".equalsIgnoreCase(this.nombre)) {
                    if (this.tablero != null) {
                        float premio = this.tablero.recogerParking();
                        if (premio > 0) {
                            jugador.sumarFortuna(premio);
                            System.out.println("¡Has recogido " + (long)premio + "€ del parking gratuito!");
                        } else {
                            System.out.println("El parking está vacío. No hay premio.");
                        }
                    } else {
                        System.out.println("Error: tablero no asignado en la casilla.");
                    }
                } else if ("carcel".equalsIgnoreCase(this.nombre)) {
                    if (jugador.isEnCarcel()) {
                        System.out.println("Estás en la cárcel. Tiradas fallidas: " + jugador.getTiradasCarcel());
                    } else {
                        System.out.println("Estás de visita en la cárcel.");
                    }
                } else {
                    System.out.println("Casilla especial: " + this.nombre);
                }
                break;

            default:
                System.out.println("Tipo de casilla desconocido.");
        }
    }

    // ====== Compra ======

    public boolean estaEnVenta() {
        String t = (this.tipo == null) ? "" : this.tipo.toLowerCase();
        return (t.equals("solar") || t.equals("servicio") || t.equals("transporte")) && duenho == null;
    }


    public void comprarCasilla(Jugador solicitante, Jugador banca) {
        String t = (this.tipo == null) ? "" : this.tipo.toLowerCase();
        boolean comprable = t.equals("solar") || t.equals("servicio") || t.equals("transporte");

        if (!comprable || this.duenho != null) {
            System.out.println("La casilla " + nombre + " no está disponible para compra.");
            return;
        }

        if (solicitante.getFortuna() >= valor) {
            solicitante.pagar(valor, banca);
            setDuenho(solicitante);
            solicitante.anadirPropiedad(this);
            System.out.println(solicitante.getNombre() + " ha comprado " + nombre + " por " + (long)valor);
        } else {
            System.out.println(solicitante.getNombre() + " no tiene suficiente dinero para comprar " + nombre);
        }
    }

    // ====== Info / descripción ======

    public String infoCasilla() {
        return "Casilla: " + nombre + " (" + tipo + "), valor=" + (long)valor;
    }

    public String casEnVenta() {
        return nombre + " en venta por " + (long)valor;
    }

    @Override
    public String toString() {
        return nombre;
    }

    public String describir() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  tipo: ").append((tipo == null) ? "-" : tipo.toLowerCase()).append(",\n");

        String t = (this.tipo == null) ? "" : this.tipo.toLowerCase();

        switch (t) {
            case "solar":
            case "servicio":
            case "transporte":
                if (grupo != null) sb.append("  grupo: ").append(grupo.getNombre()).append(",\n");
                sb.append("  propietario: ").append(duenho != null ? duenho.getNombre() : "Sin propietario").append(",\n");
                sb.append("  valor: ").append((long) valor).append(",\n");
                sb.append("  alquiler: ").append((long) evaluarAlquiler(1)).append("\n");
                break;

            case "impuesto":
                sb.append("  a_pagar: ").append((long) impuesto).append("\n");
                break;

            case "especial":
                if ("parking".equalsIgnoreCase(nombre)) {
                    sb.append("  bote: ").append((long) valor).append(",\n");
                    sb.append("  jugadores: [");
                    for (int i = 0; i < avatares.size(); i++) {
                        sb.append(avatares.get(i).getJugador().getNombre());
                        if (i < avatares.size() - 1) sb.append(", ");
                    }
                    sb.append("]\n");
                } else if ("carcel".equalsIgnoreCase(nombre)) {
                    sb.append("  salir: 500000,\n");
                    sb.append("  jugadores: ");
                    for (Avatar av : avatares) {
                        sb.append("[").append(av.getJugador().getNombre())
                                .append(",").append(av.getJugador().getTiradasCarcel()).append("] ");
                    }
                    sb.append("\n");
                } else {
                    sb.append("  casilla especial sin descripción detallada\n");
                }
                break;

            default:
                sb.append("  casilla no reconocida\n");
        }

        sb.append("}");
        return sb.toString();
    }
}

