package monopoly;

import partida.*;
import java.util.ArrayList;

public class Casilla {

    // Atributos
    private String nombre;
    private String tipo;//Tipo de casilla (Solar, Especial, Transporte, Servicio, Comunidad).
    private float valor;
    private int posicion;
    private Jugador duenho;
    private Grupo grupo;
    private float impuesto;
    private Jugador propietario;
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
    public void setGrupo(Grupo grupo){
        this.grupo = grupo;
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
        switch (tipo) {
            case "Impuesto": // si es de tipo impuesto
                //El jugador paga a la banca
                return true;

            case "Servicio":
            case "Transporte":
            case "Solar":
                if(this.duenho == null){
                    //si no tiene ducño la casilla a la que se cae
                    return true;
                }if(this.duenho == actual){
                    //no paga si la casilla es suya
                    return true;
                }
                // 3) Calcular y cobrar alquiler
                float renta = evaluarAlquiler(tirada);   // tu método ya implementado
                if (renta > 0f) {
                    actual.pagar(renta, this.duenho);    // actual paga al dueño
                }
                return true;
            case "Suerte":
                actual.robarCarta("Suerte");
                return true;
            case "Comunidad":
                actual.robarCarta("Comunidad");

            case "Especial":
                if ("Ir a Cárcel".equals(this.nombre)) {
                    actual.irACarcel();
                } else if ("Salida".equals(this.nombre)) {
                    actual.cobrar(200, banca);
                }
                return true;

            default:
                return true;
        }
    }

    private float evaluarAlquiler(int tirada) {
        switch (tipo) {
            case "Solar":
                return valor * 0.1f;
            case "Transporte":
                return 25f;
            case "Servicio":
                return tirada * 4f;
            default:
                return 0f;
        }
    }

    public void comprarCasilla(Jugador solicitante, Jugador banca) {
        // Solo se puede comprar si la casilla es de tipo comprable y no tiene dueño
        if ((tipo.equals("Solar") || tipo.equals("Servicio") || tipo.equals("Transporte")) && duenho == null) {
            // Verificamos si el jugador tiene suficiente dinero
            if (solicitante.getFortuna() >= valor) {
                // El jugador paga a la banca
                solicitante.pagar(valor, banca);

                // Se asigna el nuevo dueño
                setDuenho(solicitante);

                // Se añade la propiedad al jugador
                solicitante.anadirPropiedad(this);

                System.out.println(solicitante.getNombre() + " ha comprado la casilla " + nombre + " por " + valor);
            } else {
                System.out.println(solicitante.getNombre() + " no tiene suficiente dinero para comprar " + nombre);
            }
        } else {
            System.out.println("La casilla " + nombre + " no está disponible para compra.");
        }
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

    public void evaluar(Jugador jugador) {
        System.out.println("Evaluando la casilla: " + this.nombre);

        switch (this.tipo.toLowerCase()) {
            case "solar":
            case "transporte":
            case "servicio":
                if (this.propietario == null || this.propietario.getNombre().equalsIgnoreCase("Banca")) {
                    System.out.println("La casilla está en venta por " + this.valor + ".");
                } else if (!this.propietario.equals(jugador)) {
                    System.out.println("La casilla pertenece a " + this.propietario.getNombre() + ". Debes pagar alquiler.");
                    // Aquí podrías calcular y restar el alquiler
                } else {
                    System.out.println("Has caído en tu propia propiedad.");
                }
                break;

            case "impuesto":
                System.out.println("Debes pagar un impuesto de " + this.valor);
                //jugador.pagar(renta, this.duenho);
                break;

            case "suerte":
            case "caja":
                System.out.println("Has caído en una casilla de " + this.tipo + ". Roba una carta (no implementado aún).");
                break;

            case "especial":
                if (this.nombre.equalsIgnoreCase("IrCarcel")) {
                    System.out.println("¡Vas a la cárcel!");
                    jugador.irACarcel();
                } else {
                    System.out.println("Casilla especial: " + this.nombre);
                }
                break;

            default:
                System.out.println("Tipo de casilla desconocido.");
        }
    }

    public String describir() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  tipo: ").append(tipo.toLowerCase()).append(",\n");

        switch (tipo.toLowerCase()) {
            case "solar":
            case "servicio":
            case "transporte":
                if (grupo != null)
                    sb.append("  grupo: ").append(grupo.getNombre()).append(",\n");
                sb.append("  propietario: ").append(duenho != null ? duenho.getNombre() : "Sin propietario").append(",\n");
                sb.append("  valor: ").append((int) valor).append(",\n");
                sb.append("  alquiler: ").append((int) evaluarAlquiler(1)).append("\n"); // puedes ajustar tirada
                break;

            case "impuesto":
                sb.append("  apagar: ").append((int) impuesto).append("\n");
                break;
            case "suerte":

                break;

            case "comunidad":

                break;

            case "especial":
                if (nombre.equalsIgnoreCase("Parking")) {
                    sb.append("  bote: ").append((int) valor).append(",\n");
                    sb.append("  jugadores: [");
                    for (int i = 0; i < avatares.size(); i++) {
                        sb.append(avatares.get(i).getJugador().getNombre());
                        if (i < avatares.size() - 1) sb.append(", ");
                    }
                    sb.append("]\n");
                } else if (nombre.equalsIgnoreCase("Carcel")) {
                    sb.append("  salir: 500000,\n"); // puedes parametrizarlo
                    sb.append("  jugadores: ");
                    for (Avatar av : avatares) {
                        sb.append("[").append(av.getJugador().getNombre()).append(",").append(av.getJugador().getTiradasCarcel()).append("] ");
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

