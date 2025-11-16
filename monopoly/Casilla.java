package monopoly;

import partida.*;
import java.util.ArrayList;


import static java.awt.SystemColor.menu;

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
    private boolean tieneHotel = false;
    private boolean tienePiscina = false;
    private boolean tienePista = false;
    private int hotel = 0;
    private int piscina = 0;
    private int pista = 0;
    private float costeCasa;
    private float costeHotel;
    private float costePiscina;
    private float costePista;

    private int vecesVisitada = 0;
    private float ingresosGenerados = 0;


    public Casilla() {
        this.avatares = new ArrayList<>();

    }


    public Casilla(String nombre, String tipo, int posicion, float valor, float costeCasa, float costeHotel, float costePiscina, float costePista,
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

    // Especial / Suerte / Caja
    public Casilla(String nombre, String tipo, int posicion, Jugador banca) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;
        this.duenho = null;
        this.avatares = new ArrayList<>();
    }

    public Casilla(String nombre, String tipo, int posicion, float valor, Jugador banca) {
        this(nombre, tipo, posicion, valor, 50f, 100f, 75f, 120f, banca); // valores por defecto
    }

    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public float getValor() { return valor; }
    public int getPosicion() { return posicion; }
    public Jugador getDuenho() { return duenho; }
    public Grupo getGrupo() { return grupo; }
    public float getImpuesto() { return impuesto; }
    public ArrayList<Avatar> getAvatares() { return avatares; }

    public float getIngresosGenerados() { return ingresosGenerados;}
    public int getVecesVisitada() { return vecesVisitada;}

    public void setDuenho(Jugador d) { this.duenho = d; }
    public void setGrupo(Grupo g) { this.grupo = g; }
    public void setTablero(Tablero t) { this.tablero = t; }

    public void anhadirAvatar(Avatar av) { if (av != null && !avatares.contains(av)) avatares.add(av); }
    public void eliminarAvatar(Avatar av) { avatares.remove(av); }



    public float evaluarAlquiler(int tirada) {
        String t = (this.tipo == null) ? "" : this.tipo.toLowerCase();

        switch (t) {
            case "solar":
                return getAlquiler(); // ← usa el método que suma alquiler base + edificaciones

            case "transporte":
                return 250000f;

            case "servicio":
                int k = (tirada > 0) ? tirada : 1;
                if (this.duenho == null) return 0f;

                int cantidadServicios = 0;
                for(Casilla c : this.duenho.getPropiedades()){
                    if("servicio".equalsIgnoreCase(c.getTipo()) && !c.estaHipotecada()){
                        cantidadServicios++;
                    }
                }
                if (cantidadServicios == 1) return 4 * k * 50000f;
                if (cantidadServicios >= 2){
                    return 10 * k * 50000f;
                }
                return 0f;
            default:
                return 0f;
        }
    }

    public void evaluarCasilla(Jugador jugador) {
        System.out.println("Evaluando la casilla: " + this.nombre);
        String t = (this.tipo == null) ? "" : this.tipo.toLowerCase();
        this.incrementarVisita();

        switch (t) {
            case "solar":
                if (this.getDuenho() == null) {
                    System.out.println("La casilla está en venta por " + (long)this.valor + "€.");
                    System.out.println("Puedes comprarla con el comando: comprar " + this.nombre);
                } else if (!this.getDuenho().equals(jugador)) {
                    // *** VERIFICAR SI ESTÁ HIPOTECADA ***
                    if (this.estaHipotecada()) {
                        System.out.println("La casilla pertenece a " + this.getDuenho().getNombre() +
                                " pero está hipotecada. No pagas alquiler.");
                    } else {
                        System.out.println("La casilla pertenece a " + this.getDuenho().getNombre() +
                                ". Debes pagar alquiler.");

                        float alquiler = this.evaluarAlquiler(tablero.getUltimaTirada());

                        if(!this.tieneEdificios() && jugador.poseeGrupoCompleto(this, tablero)){
                            alquiler *= 2;
                            System.out.println("El dueño posee todo el grupo, ¡así que alquiler doble!");
                        }

                        System.out.println("El alquiler total es de " + (long)alquiler + "€.");

                        if (jugador.getFortuna() < alquiler) {
                            float faltante = alquiler - jugador.getFortuna();

                            System.out.println("\n⚠¡No tienes suficiente dinero para pagar!");
                            System.out.println("Alquiler a pagar: " + (long)alquiler + "€");
                            System.out.println("Tu fortuna: " + (long)jugador.getFortuna() + "€");
                            System.out.println("Te faltan: " + (long)faltante + "€");

                            // Calcular si puede reunir el dinero hipotecando
                            float valorHipotecable = calcularValorHipotecable(jugador);

                            if (valorHipotecable >= faltante) {
                                System.out.println("\nOPCIONES:");
                                System.out.println("1. Hipoteca propiedades para reunir el dinero");
                                System.out.println("   Valor hipotecable disponible: " + (long)valorHipotecable + "€");
                                System.out.println("   Usa el comando: hipotecar <casilla>");
                                System.out.println("2. Declara bancarrota");
                                System.out.println("   Usa el comando: declarar bancarrota");

                                // Listar propiedades hipotecables
                                System.out.println("\nPropiedades que puedes hipotecar:");
                                for (Casilla c : jugador.getPropiedades()) {
                                    if (puedeHipotecar(c)) {
                                        System.out.println("  - " + c.getNombre() +
                                                " (recibes " + (long)c.getHipoteca() + "€)");
                                    }
                                }

                                // NO declarar bancarrota todavía, esperar comando del jugador
                                // Marcar que tiene una deuda pendiente
                                jugador.setDeudaPendiente(alquiler);
                                jugador.setAcreedorDeuda(this.getDuenho());

                            } else {
                                // No puede reunir el dinero ni hipotecando todo
                                System.out.println("\nNo puedes reunir el dinero necesario.");
                                System.out.println("Valor total hipotecable: " + (long)valorHipotecable + "€");
                                System.out.println("Debes declararte en BANCARROTA.");

                                jugador.declararBancarrota(this.getDuenho());
                                if (tablero != null) {
                                    tablero.notificarBancarrota(jugador);
                                }
                            }
                        } else {
                            // Tiene dinero suficiente, pagar automáticamente
                            jugador.pagar(alquiler, this.getDuenho());
                            this.sumarIngresos(alquiler);
                            System.out.println("Has pagado " + (long)alquiler + "€ a " +
                                    this.getDuenho().getNombre());
                            jugador.sumarPagoAlquiler(alquiler);
                            this.getDuenho().sumarCobroAlquiler(alquiler);
                        }
                    }
                } else {
                    System.out.println("Has caído en tu propia propiedad.");
                }
                break;

            case "transporte":
                if (this.getDuenho() == null) {
                    System.out.println("La casilla de transporte está en venta por " +
                            (long)this.valor + "€.");
                    System.out.println("Puedes comprarla con el comando: comprar " + this.nombre);
                } else if (!this.getDuenho().equals(jugador)) {
                    // *** VERIFICAR SI ESTÁ HIPOTECADA ***
                    if (this.estaHipotecada()) {
                        System.out.println("El transporte pertenece a " + this.getDuenho().getNombre() +
                                " pero está hipotecado. No pagas alquiler.");
                    } else {
                        float alquilerTotal = 0;
                        for (Casilla propiedad : this.getDuenho().getPropiedades()) {
                            if ("transporte".equalsIgnoreCase(propiedad.getTipo()) &&
                                    !propiedad.estaHipotecada()) {
                                alquilerTotal += propiedad.getAlquiler();
                            }
                        }
                        System.out.println("Debes pagar " + (long)alquilerTotal +
                                "€ por el uso del transporte.");
                        if (jugador.getFortuna() < alquilerTotal) {
                            manejarDeuda(jugador, alquilerTotal, this.getDuenho(),
                                    "Alquiler de transporte " + this.nombre);
                        } else {
                            jugador.pagar(alquilerTotal, this.getDuenho());
                            this.sumarIngresos(alquilerTotal);
                            jugador.sumarPagoAlquiler(alquilerTotal);
                            this.getDuenho().sumarCobroAlquiler(alquilerTotal);
                        }
                    }
                } else {
                    System.out.println("Has caído en tu propio transporte.");
                }
                break;


            case "servicio":
                if (this.getDuenho() == null) {
                    System.out.println("La casilla de servicio está en venta por " +
                            (long)this.valor + "€.");
                    System.out.println("Puedes comprarla con el comando: comprar " + this.nombre);
                } else if (!this.getDuenho().equals(jugador)) {
                    // *** VERIFICAR SI ESTÁ HIPOTECADA ***
                    if (this.estaHipotecada()) {
                        System.out.println("El servicio pertenece a " + this.getDuenho().getNombre() +
                                " pero está hipotecado. No pagas alquiler.");
                    } else {
                        int tirada = tablero.getUltimaTirada();
                        float alquiler = this.evaluarAlquiler(tirada);
                        System.out.println("Debes pagar " + (long)alquiler + "€ por el servicio.");
                        if (jugador.getFortuna() < alquiler) {
                            manejarDeuda(jugador, alquiler, this.getDuenho(),
                                    "Alquiler de servicio " + this.nombre);
                        } else {
                            jugador.pagar(alquiler, this.getDuenho());
                            jugador.sumarPagoAlquiler(alquiler);
                            this.getDuenho().sumarCobroAlquiler(alquiler);
                            this.sumarIngresos(alquiler);
                        }
                    }
                } else {
                    System.out.println("Has caído en tu propio servicio.");
                }
                break;


            case "impuesto":
                System.out.println("Debes pagar un impuesto de " + (long)this.impuesto + "€.");

                if (jugador.getFortuna() < impuesto) {
                    manejarDeuda(jugador, impuesto, null, "Impuesto de " + this.nombre);
                } else {
                    jugador.restarFortuna(this.impuesto);
                    jugador.sumarGastos(this.impuesto);
                    jugador.sumarPagoTasasEImpuestos(impuesto);
                    tablero.añadirAlParking(this.impuesto);
                    System.out.println("El dinero se ha depositado en el parking. Total acumulado: " +
                            (long)tablero.getFondoParking() + "€.");
                }
                break;


            case "suerte":
                System.out.println("Has caído en Suerte. Robas una carta...");
                Carta carta = Carta.seleccionarCarta("suerte");
                carta.aplicarAccion(jugador, tablero);

                break;

            case "caja":
                System.out.println("Has caído en Caja. Robas una carta...");
                carta = Carta.seleccionarCarta("caja");
                carta.aplicarAccion(jugador, tablero);

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
                            jugador.sumarPremios(premio);
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
                if (grupo != null) sb.append("  grupo: ").append(grupo.getNombre()).append(",\n");
                sb.append("  propietario: ").append(duenho != null ? duenho.getNombre() : "Sin propietario").append(",\n");
                sb.append("  valor: ").append((long) valor).append(",\n");
                sb.append("  alquiler: ").append((long) alquilerBase).append(",\n");
                sb.append("  valor hotel: ").append((long) precioHotel).append(",\n");
                sb.append("  valor casa: ").append((long) precioCasa).append(",\n");
                sb.append("  valor piscina: ").append((long) precioPiscina).append(",\n");
                sb.append("  valor pista de deporte: ").append((long) precioPista).append(",\n");
                sb.append("  alquiler casa: ").append((long) alquilerCasa).append(",\n");
                sb.append("  alquiler hotel: ").append((long) alquilerHotel).append(",\n");
                sb.append("  alquiler piscina: ").append((long) alquilerPiscina).append(",\n");
                sb.append("  alquiler pista de deporte: ").append((long) alquilerPista).append("\n");
                break;
                
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
                    sb.append("  bote: ").append((long) tablero.getFondoParking()).append(",\n");
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

    public boolean tieneEdificios() {
        if(numCasas > 0 || tieneHotel() || tienePiscina() || tienePista) {
            return true;
        } else {
            return false;
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
    public int getNumCasas() {
        return numCasas;
    }
    public void hipotecar() {
        hipotecada = true;
    }
    public void deshipotecar() {
        hipotecada = false;
    }
    public boolean esHipotecable() {
        return hipotecable;
    }
    public float getHipoteca() { return hipoteca; }
    public float getAlquilerCasa() { return alquilerCasa; }
    public float getAlquilerHotel() { return alquilerHotel; }
    public float getAlquilerPiscina() { return alquilerPiscina; }
    public float getAlquilerPista() { return alquilerPista; }
    public float getAlquilerBase() { return alquilerBase; }

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
    public boolean estaHipotecada() {
        return hipotecada;
    }


    //////////////////VERIFICACION DE CONSTRUCCION DE EDIFICACIONES

    public boolean puedeConstruirCasa(Jugador jugador) {
        return this.getDuenho() == jugador &&
                this.getGrupo() != null &&
                this.getGrupo().perteneceEnteramenteA(jugador) &&
                hotel == 0 &&  // Una vez construido hotel, no más casas
                numCasas < 4;
    }
    // Se pueden construir un máximo de 4 casas y todas al mismo tiempo
    public void construirCasas(Jugador jugador, int cantidad) {
        if (puedeConstruirCasa(jugador)) {
            numCasas += cantidad;
            jugador.restarFortuna(precioCasa * cantidad);
        } else {
            System.out.println("No se pueden construir más casas aquí.");
        }
    }
    // En un solar se puede construir un único hotel si ya se han construido 4 casas
    public boolean puedeConstruirHotel() {
        return numCasas == 4 && hotel == 0;
    }
    // Al construir hotel, se substituyen las 4 casas por el hotel
    public void construirHotel(Jugador jugador) {
        if (numCasas == 4 && hotel == 0) {
            numCasas = 0;  // Substituir las casas
            hotel = 1;
            jugador.restarFortuna(precioHotel);
        } else {
            System.out.println("No se puede construir hotel aquí.");
        }
    }
    // En un solar se puede construir una única piscina si se ha construido un hotel
    public boolean puedeConstruirPiscina() {
        return hotel > 0 && piscina == 0;
    }

    public void construirPiscina(Jugador jugador) {
        if (puedeConstruirPiscina()) {
            piscina = 1;
            jugador.restarFortuna(precioPiscina);
        }
    }
    // En un solar se puede construir una única pista de deporte si se ha construido un hotel y una piscina
    public boolean puedeConstruirPista() {
        return hotel == 1 && piscina == 1 && pista == 0; //devueleve true si hay hotel, piscina y no pista
    }

    public void construirPista(Jugador jugador) {
        if (puedeConstruirPista()) {
            pista = 1;
            jugador.restarFortuna(precioPista);
        }
    }

    ///  ////////////////VENDER EDIFICACIONES
    /*
     * Vende edificaciones en un solar.
     * - tipoEdificacion: "casas", "hotel", "piscina" o "pista" (acepta singular/plural y variantes).
     * - jugador: debe ser el dueño.
     * - cantidadSolicitada: entero (>0). Para hotel/piscina/pista solo aplica 0/1; casas 0..N.
     *
     * Reglas clave:
     * - Solo en solares.
     * - Se paga el 100% del precio de compra (getPrecioXxx()).
     * - Piscina/pista requieren hotel; por tanto, para vender el hotel deben venderse antes piscina y pista.
     * - Al vender el hotel, la casilla recupera 4 casas (regla solicitada).
     */
    public void venderEdificacion(String tipoEdificacion, Jugador jugador, int cantidadSolicitada) {

        if (tipoEdificacion == null) {
            System.out.println("Tipo de edificación no válido.");
            return;
        }

        String textoEdificacion = tipoEdificacion.trim().toLowerCase();

        //si el texto introdicido por el usuario coincide con alguno de estos, devuelve true
        boolean esCasas = false;
        if(textoEdificacion.equals("casa") || textoEdificacion.equals("casas")){
            esCasas = true;
        }
        boolean esPiscina = false;
        if(textoEdificacion.equals("piscina") || textoEdificacion.equals("piscinas")){
            esPiscina = true;
        }
        boolean esPista = false;
        if(textoEdificacion.equals("pista") || textoEdificacion.equals("pistas") || textoEdificacion.equals("pista deporte") || textoEdificacion.equals("pista_deporte") || textoEdificacion.equals("pista-deporte")) {
            esPista = true;
        }
        boolean esHotel  =  false;
        if(textoEdificacion.equals("hotel") || textoEdificacion.equals("hoteles")){
            esHotel = true;
        }

        // Solo solares admiten edificaciones, si es de tipo transporte o servicio no se puede vernder SOLO HIPOTECAR
        if (this.tipo == null || !this.tipo.equalsIgnoreCase("solar")) {
            System.out.println("No se pueden vender edificaciones en una casilla de tipo " + this.tipo + ".");
            return;
        }

        if (cantidadSolicitada <= 0) {
            System.out.println("La cantidad a vender debe ser mayor que 0.");
            return;
        }

        // Debe pertenecer al jugador
        if (this.getDuenho() == null || this.getDuenho() != jugador) {
            System.out.println("Esta propiedad no pertenece a " + jugador.getNombre() + ".");
            return;
        }

        // --- Casas ---
        if (esCasas) {

            int disponibles = this.numCasas; //numero de casas que tiene el solar
            if (disponibles <= 0) {
                System.out.println("No hay casas que vender en " + this.nombre + ".");
                return;
            }
            int aVender = Math.min(disponibles, cantidadSolicitada); // el minimo entre las disponibles y la cantidad que quieres vender, para no vender mas casas de las que tienes
            long total = (long)(aVender * this.getPrecioCasa()); // el precio que vas a ganar

            this.numCasas -= aVender; //resta las casas vendidas al numero de casas de que hay en el solar
            jugador.sumarFortuna(total); //suma el dinero ganado al jugador

            if (aVender < cantidadSolicitada) { // si las que puedes vender es menor que las solicitadas manda un mensaje de aviso, solo vamos a vender las que haiga en la casilla
                String plural = (aVender == 1) ? "casa" : "casas";
                System.out.println("Se pueden vender como máximo " + aVender + " " + plural + " aquí. Ingresas " + total + "€.");
            } else {
                String pluralVendidas = (aVender == 1) ? "casa" : "casas";
                System.out.print(jugador.getNombre() + " ha vendido " + aVender + " " + pluralVendidas + " en " + this.nombre + ", recibiendo " + total + "€.");
                int quedan = this.numCasas;
                String quedanTxt = (quedan == 1) ? " En la propiedad queda 1 casa." :
                        " En la propiedad quedan " + quedan + " casas.";
                System.out.println(quedanTxt);
            }
            return;
        }

        // --- Piscina (0/1) ---
        if (esPiscina) {
            int disponibles = this.piscina;
            if (disponibles <= 0) {
                System.out.println("No hay piscina que vender en " + this.nombre + ".");
                return;
            }
            int aVender = Math.min(disponibles, cantidadSolicitada); // 1 máx
            long total = (long)(aVender * this.getPrecioPiscina());

            this.piscina -= aVender; // tienen que ser cero por que solo se puede construir una piscina
            jugador.sumarFortuna(total);

            if (cantidadSolicitada > aVender) {
                System.out.println("Solo se puede vender 1 piscina aquí. Ingresas " + total + "€.");
            } else {
                System.out.println(jugador.getNombre() + " ha vendido 1 piscina en " + this.nombre + ", recibiendo " + total + "€.");
            }
            return;
        }

        // --- Pista (0/1) ---
        if (esPista) {
            int disponibles = this.pista;
            if (disponibles <= 0) {
                System.out.println("No hay pista que vender en " + this.nombre + ".");
                return;
            }
            int aVender = Math.min(disponibles, cantidadSolicitada); // 1 máx
            long total = (long)(aVender * this.getPrecioPista());

            this.pista -= aVender; // → 0
            jugador.sumarFortuna(total);

            if (cantidadSolicitada > aVender) {
                System.out.println("Solo se puede vender 1 pista aquí. Ingresas " + total + "€.");
            } else {
                System.out.println(jugador.getNombre() + " ha vendido 1 pista en " + this.nombre + ", recibiendo " + total + "€.");
            }
            return;
        }

        // --- Hotel (0/1) ---
        if (esHotel) {
            int disponibles = this.hotel;
            if (disponibles <= 0) {
                System.out.println("No hay hotel que vender en " + this.nombre + ".");
                return;
            }
            // Para poder vender el hotel, primero deben venderse piscina y pista
            if (this.piscina > 0 || this.pista > 0) {
                System.out.println("No puedes vender el hotel mientras existan piscina o pista en la propiedad.");
                return;
            }

            int aVender = Math.min(disponibles, cantidadSolicitada); // será 1 como máximo
            long total = (long)(aVender * this.getPrecioHotel());

            //cuando se vende el hotel no se restauran las casas, solo ganas lo del hotel(lo dijo el profe)
            this.hotel -= aVender;       // → 0
            jugador.sumarFortuna(total);

            if (cantidadSolicitada > aVender) {
                System.out.println("Solo se puede vender 1 hotel aquí. Ingresas " + total + "€.");
            } else {
                System.out.println(jugador.getNombre() + " ha vendido 1 hotel en " + this.nombre + ", recibiendo " + total + "€.");
            }
            return;
        }

        // Tipo no reconocido
        System.out.println("Tipo de edificación no reconocido: " + tipoEdificacion + ".");
    }

    /// ///////////////ESTADISTICAS
    public void incrementarVisita(){vecesVisitada++; }
    public void sumarIngresos(float cantidad){ingresosGenerados += cantidad; } //para calcular la casilla mas rentable, sumamos cada vcez que pagan un alquiler

    ///   /////////////BANCARROTA
    ///
    public float calcularValorHipotecable(Jugador jugador) { //calcula el valor de la hipoteca total de un jugador

        float total = 0;
        for (Casilla c : jugador.getPropiedades()) {
            if (puedeHipotecar(c)) {
                total += c.getHipoteca();
            }
        }
        return total;
    }

    /**
     * Verifica si una casilla puede ser hipotecada
     */
    public boolean puedeHipotecar(Casilla c) {
        if (c.estaHipotecada() || !c.esHipotecable()) {
            return false;
        }

        // Solares solo si no tienen edificios
        if ("solar".equalsIgnoreCase(c.getTipo())) {
            return !c.tieneEdificios();
        }

        // Transportes y servicios NO se hipotecan según PDF Parte 1
        return false;
    }

    public void procesarPagoDeuda(Jugador jugador, Tablero tablero) {
        float deuda = jugador.getDeudaPendiente();

        if (jugador.getFortuna() >= deuda) {
            System.out.println("\n¡Ahora tienes suficiente dinero para pagar tu deuda!");
            System.out.println("Deuda pendiente: " + (long)deuda + "€");
            System.out.println("Tu fortuna: " + (long)jugador.getFortuna() + "€");

            // Pagar automáticamente
            Jugador acreedor = jugador.getAcreedorDeuda();

            if (acreedor != null) {
                // Deuda con otro jugador (alquiler)
                jugador.pagar(deuda, acreedor);
                System.out.println("Has pagado " + (long)deuda + "€ a " + acreedor.getNombre());
                jugador.sumarPagoAlquiler(deuda);
                acreedor.sumarCobroAlquiler(deuda);

                // Registrar ingresos en la casilla que generó el alquiler
                Casilla casillaActual = jugador.getAvatar().getCasilla();
                if (casillaActual != null && casillaActual.getDuenho() == acreedor) {
                    casillaActual.sumarIngresos(deuda);
                }

            } else {
                // Deuda con la banca (impuestos, tasas, etc.)
                jugador.restarFortuna(deuda);
                jugador.sumarGastos(deuda);
                jugador.sumarPagoTasasEImpuestos(deuda);
                tablero.añadirAlParking(deuda);
                System.out.println("Has pagado " + (long)deuda + "€");
                System.out.println("El dinero ha sido depositado en el parking.");
            }

            // Limpiar deuda
            jugador.setDeudaPendiente(0);
            jugador.setAcreedorDeuda(null);

            System.out.println("\n✓ La deuda ha sido saldada.");
            System.out.println("Puedes continuar con tu turno.");
            System.out.println("Usa 'acabar turno' cuando hayas terminado.");

        } else {
            // Todavía no tiene suficiente dinero
            float faltante = deuda - jugador.getFortuna();
            System.out.println("\nAún te faltan " + (long)faltante + "€ para pagar la deuda.");
            System.out.println("Tu fortuna actual: " + (long)jugador.getFortuna() + "€");
            System.out.println("Deuda total: " + (long)deuda + "€");

            // Calcular cuánto más puede hipotecar
            float valorHipotecableRestante = calcularValorHipotecable(jugador);

            if (valorHipotecableRestante >= faltante) {
                System.out.println("\nPuedes seguir hipotecando propiedades.");
                System.out.println("Valor hipotecable restante: " + (long)valorHipotecableRestante + "€");
                System.out.println("\nPropiedades disponibles para hipotecar:");
                for (Casilla c : jugador.getPropiedades()) {
                    if (puedeHipotecar(c)) {
                        System.out.println("  - " + c.getNombre() +
                                " (recibes " + (long)c.getHipoteca() + "€)");
                    }
                }
            } else {
                System.out.println("\n⚠ No puedes reunir el dinero suficiente.");
                System.out.println("Valor hipotecable total restante: " + (long)valorHipotecableRestante + "€");
                System.out.println("Debes declararte en BANCARROTA.");
                System.out.println("Usa el comando: declarar bancarrota");
            }
        }
    }
    /**
     * Maneja una deuda: informa al jugador y marca la deuda pendiente
     */
    public void manejarDeuda(Jugador jugador, float cantidad, Jugador acreedor, String motivo) {

        float faltante = cantidad - jugador.getFortuna();

        System.out.println("\n¡No tienes suficiente dinero para pagar!");
        System.out.println("Cantidad a pagar: " + (long)cantidad + "€");
        System.out.println("Tu fortuna: " + (long)jugador.getFortuna() + "€");
        System.out.println("Te faltan: " + (long)faltante + "€");

        float valorHipotecable = calcularValorHipotecable(jugador);

        if (valorHipotecable >= faltante) { //si puede reunir el dinero para pagarlo, puede hipotecar casas o declaase en bancarpota
            System.out.println("\nPuedes hipotecar propiedades para reunir el dinero.");
            System.out.println("Valor hipotecable disponible: " + (long)valorHipotecable + "€");
            System.out.println("\nOPCIONES:");
            System.out.println("1. Hipotecar propiedades: hipotecar <casilla>");
            System.out.println("2. Declarar bancarrota: declarar bancarrota");

            // Marcar deuda pendiente
            jugador.setDeudaPendiente(cantidad);
            jugador.setAcreedorDeuda(acreedor);

        } else {
            System.out.println("\nNo puedes reunir el dinero necesario."); // si no lo puede reunir
            System.out.println("Valor total hipotecable: " + (long)valorHipotecable + "€");
            System.out.println("Te declaras en BANCARROTA.");

            jugador.declararBancarrota(acreedor);
            if (tablero != null) {
                tablero.notificarBancarrota(jugador);
            }
        }
    }
}

