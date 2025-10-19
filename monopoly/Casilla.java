package monopoly;

import partida.*;
import java.util.ArrayList;

public class Casilla {

    // Atributos
    private String nombre;
    private String tipo;       // Solar, Especial, Transporte, Servicio, Caja, Suerte, Impuesto
    private float valor;
    private int posicion;
    private Jugador duenho;    // Propietario
    private Grupo grupo;
    private float impuesto;
    private Tablero tablero;
    private ArrayList<Avatar> avatares;
    private float alquiler;
    private boolean hipotecable = true;
    private boolean hipotecada = false;

    private float hipoteca;
    private float alquilerBase;
    private float alquilerCasa;
    private float alquilerHotel;
    private float alquilerPiscina;
    private float alquilerPista;
    private float precioCasa;
    private float precioHotel;
    private float precioPiscina;
    private float precioPista;
    private int numCasas = 0;

    // ✅ Solo usar variables int (eliminar boolean duplicados)
    private int hotel = 0;
    private int piscina = 0;
    private int pista = 0;

    private float costeCasa;
    private float costeHotel;
    private float costePiscina;
    private float costePista;

    // ====== Constructores ======

    public Casilla() {
        this.avatares = new ArrayList<>();
    }

    public Casilla(String nombre, String tipo, int posicion, float valor,
                   float costeCasa, float costeHotel, float costePiscina, float costePista,
                   Jugador banca) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;

        if ("impuesto".equalsIgnoreCase(tipo)) {
            this.impuesto = valor;
        } else {
            this.valor = valor;
        }

        this.duenho = null;
        this.avatares = new ArrayList<>();

        this.costeCasa = costeCasa;
        this.costeHotel = costeHotel;
        this.costePiscina = costePiscina;
        this.costePista = costePista;
    }

    public Casilla(String nombre, String tipo, int posicion, Jugador banca) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;
        this.duenho = null;
        this.avatares = new ArrayList<>();
    }

    public Casilla(String nombre, String tipo, int posicion, float valor, Jugador banca) {
        this(nombre, tipo, posicion, valor, 50f, 100f, 75f, 120f, banca);
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

    // ====== Lógica de alquiler ======

    public float calcularAlquilerTotal() {
        float total = this.getAlquilerBase();
        total += numCasas * this.getAlquilerCasa();
        if (hotel > 0) total += this.getAlquilerHotel();
        if (piscina > 0) total += this.getAlquilerPiscina();
        if (pista > 0) total += this.getAlquilerPista();
        return total;
    }

    public float getAlquilerTransporte() {
        return alquilerBase;
    }

    public float evaluarAlquiler(int tirada) {
        String t = (this.tipo == null) ? "" : this.tipo.toLowerCase();

        switch (t) {
            case "solar":
                return calcularAlquilerTotal();

            case "transporte":
                return 250000f;

            case "servicio":
                int k = (tirada > 0) ? tirada : 1;
                return 4 * k * 50000f;

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
                if (this.getDuenho() == null) {
                    System.out.println("La casilla está en venta por " + (long)this.valor + "€.");
                    System.out.println("Puedes comprarla con el comando: comprar " + this.nombre);
                } else if (!this.getDuenho().equals(jugador)) {
                    System.out.println("La casilla pertenece a " + this.getDuenho().getNombre() + ". Debes pagar alquiler.");

                    float alquiler = this.evaluarAlquiler(tablero.getUltimaTirada());
                    System.out.println("El alquiler total es de " + (long)alquiler + "€.");

                    if (jugador.getFortuna() < alquiler) {
                        System.out.println("No puedes pagar el alquiler. Te declaras en bancarrota.");
                        jugador.declararBancarrota(this.getDuenho());

                        if (tablero != null) {
                            tablero.notificarBancarrota(jugador);
                        }
                    } else {
                        jugador.pagar(alquiler, this.getDuenho());
                        System.out.println("Has pagado " + (long)alquiler + "€ a " + this.getDuenho().getNombre());
                    }
                } else {
                    System.out.println("Has caído en tu propia propiedad.");
                }
                break;

            case "transporte":
                if (this.getDuenho() == null) {
                    System.out.println("La casilla de transporte está en venta por " + (long)this.valor + "€.");
                    System.out.println("Puedes comprarla con el comando: comprar " + this.nombre);
                } else if (!this.getDuenho().equals(jugador)) {
                    float alquilerTotal = 0;
                    for (Casilla propiedad : this.getDuenho().getPropiedades()) {
                        if ("transporte".equalsIgnoreCase(propiedad.getTipo())) {
                            alquilerTotal += propiedad.getAlquiler();
                        }
                    }
                    System.out.println("Debes pagar " + (long)alquilerTotal + "€ por el uso del transporte.");
                    jugador.pagar(alquilerTotal, this.getDuenho());
                } else {
                    System.out.println("Has caído en tu propio transporte.");
                }
                break;

            case "servicio":
                if (this.getDuenho() == null) {
                    System.out.println("La casilla de servicio está en venta por " + (long)this.valor + "€.");
                    System.out.println("Puedes comprarla con el comando: comprar " + this.nombre);
                } else if (!this.getDuenho().equals(jugador)) {
                    int tirada = tablero.getUltimaTirada();
                    float alquiler = this.evaluarAlquiler(tirada);
                    System.out.println("Debes pagar " + (long)alquiler + "€ por el servicio.");
                    jugador.pagar(alquiler, this.getDuenho());
                } else {
                    System.out.println("Has caído en tu propio servicio.");
                }
                break;

            case "impuesto":
                System.out.println("Debes pagar un impuesto de " + (long)this.impuesto + "€.");

                if (jugador.getFortuna() < impuesto) {
                    System.out.println("No puedes pagar el impuesto. Te declaras en bancarrota.");
                    jugador.declararBancarrota(this.getDuenho());

                    if (tablero != null) {
                        tablero.notificarBancarrota(jugador);
                    }
                    return;
                }

                jugador.restarFortuna(this.impuesto);
                jugador.sumarGastos(this.impuesto);
                tablero.añadirAlParking(this.impuesto);
                System.out.println("El dinero se ha depositado en el parking. Total acumulado: " + (long)tablero.getFondoParking() + "€.");
                break;

            case "suerte":
                System.out.println("Has caído en Suerte. Robas una carta...");
                if (tablero != null) {
                    Carta cartaS = tablero.sacarCarta("suerte");
                    cartaS.aplicarAccion(jugador, tablero);
                } else {
                    System.out.println("Error: tablero no asignado.");
                }
                break;

            case "caja":
                System.out.println("Has caído en Caja de Comunidad. Robas una carta...");
                if (tablero != null) {
                    Carta cartaC = tablero.sacarCarta("caja");
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

    public void setAlquiler(float valor) {
        this.alquiler = valor;
    }

    public boolean estaEnVenta() {
        String t = (this.tipo == null) ? "" : this.tipo.toLowerCase();
        return (t.equals("solar") || t.equals("servicio") || t.equals("transporte")) && duenho == null;
    }

    public float getAlquiler() {
        switch (tipo.toLowerCase()) {
            case "transporte":
                return alquilerBase;
            case "solar":
                float total = alquilerBase;
                total += numCasas * alquilerCasa;
                if (hotel > 0) total += alquilerHotel;
                if (piscina > 0) total += alquilerPiscina;
                if (pista > 0) total += alquilerPista;
                return total;
            default:
                return 0;
        }
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

    public float getHipoteca() { return hipoteca; }
    public float getAlquilerBase() { return alquilerBase; }
    public float getAlquilerCasa() { return alquilerCasa; }
    public float getAlquilerHotel() { return alquilerHotel; }
    public float getAlquilerPiscina() { return alquilerPiscina; }
    public float getAlquilerPista() { return alquilerPista; }

    public float getPrecioCasa() { return precioCasa; }
    public float getPrecioHotel() { return precioHotel; }
    public float getPrecioPiscina() { return precioPiscina; }
    public float getPrecioPista() { return precioPista; }

    public void setHipoteca(float h) { hipoteca = h; }
    public void setAlquilerBase(float a) { alquilerBase = a; }
    public void setAlquilerCasa(float a) { alquilerCasa = a; }
    public void setAlquilerHotel(float a) { alquilerHotel = a; }
    public void setAlquilerPiscina(float a) { alquilerPiscina = a; }
    public void setAlquilerPista(float a) { alquilerPista = a; }

    public void setPrecioCasa(float p) { precioCasa = p; }
    public void setPrecioHotel(float p) { precioHotel = p; }
    public void setPrecioPiscina(float p) { precioPiscina = p; }
    public void setPrecioPista(float p) { precioPista = p; }

    // ====== Métodos de construcción (según indicaciones del profesor) ======

    // ✅ En un solar se puede construir una casa si:
    // - Pertenece al jugador cuyo avatar se encuentra en la casilla
    // - El jugador posee todas las casillas del grupo
    public boolean puedeConstruirCasa(Jugador jugador) {
        return this.getDuenho() == jugador &&
                this.getGrupo() != null &&
                this.getGrupo().perteneceEnteramenteA(jugador) &&
                hotel == 0 &&  // Una vez construido hotel, no más casas
                numCasas < 4;
    }

    public String resumenEdificaciones() {
        return "Casas: " + numCasas + ", Hotel: " + hotel + ", Piscina: " + piscina + ", Pista: " + pista;
    }

    public boolean estaHipotecada() {
        return hipotecada;
    }

    public void hipotecar() {
        hipotecada = true;
    }

    public void deshipotecar() {
        hipotecada = false;
    }

    // ✅ En un solar se puede construir un único hotel si ya se han construido 4 casas
    // En ese caso, se substituyen todas las casas por el hotel
    public boolean puedeConstruirHotel() {
        return numCasas == 4 && hotel == 0;
    }

    // ✅ En un solar se puede construir una única piscina si se ha construido un hotel
    public boolean puedeConstruirPiscina() {
        return hotel > 0 && piscina == 0;
    }

    public void construirPiscina(Jugador jugador) {
        if (puedeConstruirPiscina()) {
            piscina = 1;
            jugador.restarFortuna(costePiscina);
        }
    }

    // ✅ En un solar se puede construir una única pista de deporte si se ha construido un hotel y una piscina
    public boolean puedeConstruirPista() {
        return hotel > 0 && piscina > 0 && pista == 0;
    }

    public void construirPista(Jugador jugador) {
        if (puedeConstruirPista()) {
            pista = 1;
            jugador.restarFortuna(costePista);
        }
    }

    // ✅ Se pueden construir un máximo de 4 casas y todas al mismo tiempo
    public void construirCasas(Jugador jugador, int cantidad) {
        if (puedeConstruirCasa(jugador)) {
            numCasas += cantidad;
            jugador.restarFortuna(costeCasa * cantidad);
        } else {
            System.out.println("No se pueden construir más casas aquí.");
        }
    }

    public void setHipotecable(boolean valor) {
        this.hipotecable = valor;
    }

    // ✅ Al construir hotel, se substituyen las 4 casas por el hotel
    public void construirHotel(Jugador jugador) {
        if (numCasas == 4 && hotel == 0) {
            numCasas = 0;  // Substituir las casas
            hotel = 1;
            jugador.restarFortuna(costeHotel);
        } else {
            System.out.println("No se puede construir hotel aquí.");
        }
    }

    public boolean tieneHotel() {
        return hotel > 0;
    }

    public boolean tienePiscina() {
        return piscina > 0;
    }

    public boolean tienePista() {
        return pista > 0;
    }

    public boolean esHipotecable() {
        return hipotecable;
    }

    public int getNumCasas() {
        return numCasas;
    }

    public void setNumCasas(int numCasas) {
        this.numCasas = numCasas;
    }
}