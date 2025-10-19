package monopoly;

import partida.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class Tablero {

    private int ultimaTirada;
    private ArrayList<Casilla> casillas;

    // Lados
    private static final int SUR = 0;
    private static final int OESTE = 1;
    private static final int NORTE = 2;
    private static final int ESTE = 3;

    // Atributos
    private ArrayList<ArrayList<Casilla>> posiciones;
    private HashMap<String, Grupo> grupos;
    private Jugador banca;
    private float fondoParking = 0;
    private ArrayList<Carta> mazoSuerte = new ArrayList<>();
    private ArrayList<Carta> mazoCaja = new ArrayList<>();
    // Constructor
    public Tablero(Jugador banca) {
        this.banca = banca;
        this.posiciones = new ArrayList<>();
        for (int i = 0; i < 4; i++) this.posiciones.add(new ArrayList<>());
        this.grupos = new HashMap<>();
        generarCasillas();
        inicializarCartas();
    }

    // ======== Generación del tablero ========

    private void vincularTableroACasillas() {
        for (ArrayList<Casilla> lado : posiciones) {
            for (Casilla c : lado) {
                c.setTablero(this);
            }
        }
    }

    private void generarCasillas() {
        this.insertarLadoSur();
        this.insertarLadoOeste();
        this.insertarLadoNorte();
        this.insertarLadoEste();
        crearGrupos();
        vincularTableroACasillas();

        casillas = new ArrayList<>();
        for (ArrayList<Casilla> lado : posiciones) {
            casillas.addAll(lado);
        }
    }

    // NORTE (derecha->izquierda visual, indices 20..30)
    private void insertarLadoNorte() {
        ArrayList<Casilla> norte = posiciones.get(NORTE);

        norte.add(new Casilla("IrCarcel", "Especial", 30, banca));

        Casilla sol17 = new Casilla("Sol17", "Solar", 29, 2800000, banca);
        sol17.setHipoteca(1400000);
        sol17.setAlquilerBase(240000);
        sol17.setAlquilerCasa(2600000);
        sol17.setAlquilerHotel(12000000);
        sol17.setAlquilerPiscina(2400000);
        sol17.setAlquilerPista(2400000);
        sol17.setPrecioCasa(1500000);
        sol17.setPrecioHotel(1500000);
        sol17.setPrecioPiscina(300000);
        sol17.setPrecioPista(600000);
        norte.add(sol17);

        Casilla serv2 = new Casilla("Serv2", "Servicio", 28, 500000, banca);
        norte.add(serv2);

        Casilla sol16 = new Casilla("Sol16", "Solar", 27, 2600000, banca);
        sol16.setHipoteca(1300000);
        sol16.setAlquilerBase(220000);
        sol16.setAlquilerCasa(2450000);
        sol16.setAlquilerHotel(11500000);
        sol16.setAlquilerPiscina(2300000);
        sol16.setAlquilerPista(2300000);
        sol16.setPrecioCasa(1500000);
        sol16.setPrecioHotel(1500000);
        sol16.setPrecioPiscina(300000);
        sol16.setPrecioPista(600000);
        norte.add(sol16);

        Casilla sol15 = new Casilla("Sol15", "Solar", 26, 2600000, banca);
        sol15.setHipoteca(1300000);
        sol15.setAlquilerBase(220000);
        sol15.setAlquilerCasa(2450000);
        sol15.setAlquilerHotel(11500000);
        sol15.setAlquilerPiscina(2300000);
        sol15.setAlquilerPista(2300000);
        sol15.setPrecioCasa(1500000);
        sol15.setPrecioHotel(1500000);
        sol15.setPrecioPiscina(300000);
        sol15.setPrecioPista(600000);
        norte.add(sol15);

        Casilla tran3 = new Casilla("Tran3", "Transporte", 25, 500000, banca);
        tran3.setAlquilerBase(250000);
        norte.add(tran3);

        Casilla sol14 = new Casilla("Sol14", "Solar", 24, 2400000, banca);
        sol14.setHipoteca(1200000);
        sol14.setAlquilerBase(200000);
        sol14.setAlquilerCasa(2325000);
        sol14.setAlquilerHotel(11000000);
        sol14.setAlquilerPiscina(2200000);
        sol14.setAlquilerPista(2200000);
        sol14.setPrecioCasa(1500000);
        sol14.setPrecioHotel(1500000);
        sol14.setPrecioPiscina(300000);
        sol14.setPrecioPista(600000);
        norte.add(sol14);

        Casilla sol13 = new Casilla("Sol13", "Solar", 23, 2200000, banca);
        sol13.setHipoteca(1100000);
        sol13.setAlquilerBase(180000);
        sol13.setAlquilerCasa(2200000);
        sol13.setAlquilerHotel(10500000);
        sol13.setAlquilerPiscina(2100000);
        sol13.setAlquilerPista(2100000);
        sol13.setPrecioCasa(1500000);
        sol13.setPrecioHotel(1500000);
        sol13.setPrecioPiscina(300000);
        sol13.setPrecioPista(600000);
        norte.add(sol13);

        norte.add(new Casilla("Suerte", "Suerte", 22, banca));

        Casilla sol12 = new Casilla("Sol12", "Solar", 21, 2200000, banca);
        sol12.setHipoteca(1100000);
        sol12.setAlquilerBase(180000);
        sol12.setAlquilerCasa(2200000);
        sol12.setAlquilerHotel(10500000);
        sol12.setAlquilerPiscina(2100000);
        sol12.setAlquilerPista(2100000);
        sol12.setPrecioCasa(1500000);
        sol12.setPrecioHotel(1500000);
        sol12.setPrecioPiscina(300000);
        sol12.setPrecioPista(600000);
        norte.add(sol12);

        norte.add(new Casilla("Parking", "Especial", 20, banca));

    }

    // SUR (izquierda->derecha visual, indices 0..10)
    private void insertarLadoSur() {
        ArrayList<Casilla> sur = posiciones.get(SUR);

        sur.add(new Casilla("Carcel", "Especial", 10, banca));

        Casilla sol5 = new Casilla("Sol5", "Solar", 9, 1200000, banca);
        sol5.setHipoteca(600000);
        sol5.setAlquilerBase(80000);
        sol5.setAlquilerCasa(1250000);
        sol5.setAlquilerHotel(6000000);
        sol5.setAlquilerPiscina(1200000);
        sol5.setAlquilerPista(1200000);
        sol5.setPrecioCasa(500000);
        sol5.setPrecioHotel(500000);
        sol5.setPrecioPiscina(100000);
        sol5.setPrecioPista(200000);
        sur.add(sol5);

        Casilla sol4 = new Casilla("Sol4", "Solar", 8, 1000000, banca);
        sol4.setHipoteca(500000);
        sol4.setAlquilerBase(60000);
        sol4.setAlquilerCasa(1000000);
        sol4.setAlquilerHotel(5500000);
        sol4.setAlquilerPiscina(1100000);
        sol4.setAlquilerPista(1100000);
        sol4.setPrecioCasa(500000);
        sol4.setPrecioHotel(500000);
        sol4.setPrecioPiscina(100000);
        sol4.setPrecioPista(200000);
        sur.add(sol4);

        sur.add(new Casilla("Suerte", "Suerte", 7, banca));

        Casilla sol3 = new Casilla("Sol3", "Solar", 6, 1000000, banca);
        sol3.setHipoteca(500000);
        sol3.setAlquilerBase(60000);
        sol3.setAlquilerCasa(1000000);
        sol3.setAlquilerHotel(5500000);
        sol3.setAlquilerPiscina(1100000);
        sol3.setAlquilerPista(1100000);
        sol3.setPrecioCasa(500000);
        sol3.setPrecioHotel(500000);
        sol3.setPrecioPiscina(100000);
        sol3.setPrecioPista(200000);
        sur.add(sol3);

        Casilla tran1 = new Casilla("Tran1", "Transporte", 5, 500000, banca);
        tran1.setAlquilerBase(250000);
        sur.add(tran1);

        Casilla imp1 = new Casilla("Imp1", "Impuesto", 4, 2000000, banca);
        sur.add(imp1);

        Casilla sol2 = new Casilla("Sol2", "Solar", 3, 600000, banca);
        sol2.setHipoteca(300000);
        sol2.setAlquilerBase(40000);
        sol2.setAlquilerCasa(800000);
        sol2.setAlquilerHotel(4500000);
        sol2.setAlquilerPiscina(900000);
        sol2.setAlquilerPista(900000);
        sol2.setPrecioCasa(500000);
        sol2.setPrecioHotel(500000);
        sol2.setPrecioPiscina(100000);
        sol2.setPrecioPista(200000);
        sur.add(sol2);

        sur.add(new Casilla("Caja", "Caja", 2, banca));

        Casilla sol1 = new Casilla("Sol1", "Solar", 1, 600000, banca);
        sol1.setHipoteca(300000);
        sol1.setAlquilerBase(20000);
        sol1.setAlquilerCasa(400000);
        sol1.setAlquilerHotel(2500000);
        sol1.setAlquilerPiscina(500000);
        sol1.setAlquilerPista(500000);
        sol1.setPrecioCasa(500000);
        sol1.setPrecioHotel(500000);
        sol1.setPrecioPiscina(100000);
        sol1.setPrecioPista(200000);
        sur.add(sol1);

        sur.add(new Casilla("Salida", "Especial", 0, banca));
    }


    // OESTE (abajo->arriba visual, indices 11..19)
    private void insertarLadoOeste() {
        ArrayList<Casilla> oeste = posiciones.get(OESTE);

        Casilla sol6 = new Casilla("Sol6", "Solar", 11, 1400000, banca);
        sol6.setHipoteca(700000);
        sol6.setAlquilerBase(100000);
        sol6.setAlquilerCasa(1500000);
        sol6.setAlquilerHotel(7500000);
        sol6.setAlquilerPiscina(1500000);
        sol6.setAlquilerPista(1500000);
        sol6.setPrecioCasa(1000000);
        sol6.setPrecioHotel(1000000);
        sol6.setPrecioPiscina(200000);
        sol6.setPrecioPista(400000);
        oeste.add(sol6);

        Casilla serv1 = new Casilla("Serv1", "Servicio", 12, 500000, banca);
        oeste.add(serv1);

        Casilla sol7 = new Casilla("Sol7", "Solar", 13, 1400000, banca);
        sol7.setHipoteca(700000);
        sol7.setAlquilerBase(100000);
        sol7.setAlquilerCasa(1500000);
        sol7.setAlquilerHotel(7500000);
        sol7.setAlquilerPiscina(1500000);
        sol7.setAlquilerPista(1500000);
        sol7.setPrecioCasa(1000000);
        sol7.setPrecioHotel(1000000);
        sol7.setPrecioPiscina(200000);
        sol7.setPrecioPista(400000);
        oeste.add(sol7);

        Casilla sol8 = new Casilla("Sol8", "Solar", 14, 1600000, banca);
        sol8.setHipoteca(800000);
        sol8.setAlquilerBase(120000);
        sol8.setAlquilerCasa(1750000);
        sol8.setAlquilerHotel(9000000);
        sol8.setAlquilerPiscina(1800000);
        sol8.setAlquilerPista(1800000);
        sol8.setPrecioCasa(1000000);
        sol8.setPrecioHotel(1000000);
        sol8.setPrecioPiscina(200000);
        sol8.setPrecioPista(400000);
        oeste.add(sol8);

        Casilla tran2 = new Casilla("Tran2", "Transporte", 15, 500000, banca);
        tran2.setAlquilerBase(250000);
        oeste.add(tran2);

        Casilla sol9 = new Casilla("Sol9", "Solar", 16, 1800000, banca);
        sol9.setHipoteca(900000);
        sol9.setAlquilerBase(140000);
        sol9.setAlquilerCasa(1850000);
        sol9.setAlquilerHotel(9500000);
        sol9.setAlquilerPiscina(1900000);
        sol9.setAlquilerPista(1900000);
        sol9.setPrecioCasa(1000000);
        sol9.setPrecioHotel(1000000);
        sol9.setPrecioPiscina(200000);
        sol9.setPrecioPista(400000);
        oeste.add(sol9);

        oeste.add(new Casilla("Caja", "Caja", 17, banca));

        Casilla sol10 = new Casilla("Sol10", "Solar", 18, 1800000, banca);
        sol10.setHipoteca(900000);
        sol10.setAlquilerBase(140000);
        sol10.setAlquilerCasa(1850000);
        sol10.setAlquilerHotel(9500000);
        sol10.setAlquilerPiscina(1900000);
        sol10.setAlquilerPista(1900000);
        sol10.setPrecioCasa(1000000);
        sol10.setPrecioHotel(1000000);
        sol10.setPrecioPiscina(200000);
        sol10.setPrecioPista(400000);
        oeste.add(sol10);

        Casilla sol11 = new Casilla("Sol11", "Solar", 19, 2000000, banca);
        sol11.setHipoteca(1000000);
        sol11.setAlquilerBase(160000);
        sol11.setAlquilerCasa(2000000);
        sol11.setAlquilerHotel(10000000);
        sol11.setAlquilerPiscina(2000000);
        sol11.setAlquilerPista(2000000);
        sol11.setPrecioCasa(1000000);
        sol11.setPrecioHotel(1000000);
        sol11.setPrecioPiscina(200000);
        sol11.setPrecioPista(400000);
        oeste.add(sol11);
    }


    // ESTE (arriba->abajo visual, indices 31..39)
    private void insertarLadoEste() {
        ArrayList<Casilla> este = posiciones.get(ESTE);

        Casilla sol18 = new Casilla("Sol18", "Solar", 31, 3000000, banca);
        sol18.setHipoteca(1500000);
        sol18.setAlquilerBase(260000);
        sol18.setAlquilerCasa(2750000);
        sol18.setAlquilerHotel(12750000);
        sol18.setAlquilerPiscina(2550000);
        sol18.setAlquilerPista(2550000);
        sol18.setPrecioCasa(2000000);
        sol18.setPrecioHotel(2000000);
        sol18.setPrecioPiscina(400000);
        sol18.setPrecioPista(800000);
        este.add(sol18);

        Casilla sol19 = new Casilla("Sol19", "Solar", 32, 3000000, banca);
        sol19.setHipoteca(1500000);
        sol19.setAlquilerBase(260000);
        sol19.setAlquilerCasa(2750000);
        sol19.setAlquilerHotel(12750000);
        sol19.setAlquilerPiscina(2550000);
        sol19.setAlquilerPista(2550000);
        sol19.setPrecioCasa(2000000);
        sol19.setPrecioHotel(2000000);
        sol19.setPrecioPiscina(400000);
        sol19.setPrecioPista(800000);
        este.add(sol19);

        este.add(new Casilla("Caja", "Caja", 33, banca));

        Casilla sol20 = new Casilla("Sol20", "Solar", 34, 3200000, banca);
        sol20.setHipoteca(1600000);
        sol20.setAlquilerBase(280000);
        sol20.setAlquilerCasa(3000000);
        sol20.setAlquilerHotel(14000000);
        sol20.setAlquilerPiscina(2800000);
        sol20.setAlquilerPista(2800000);
        sol20.setPrecioCasa(2000000);
        sol20.setPrecioHotel(2000000);
        sol20.setPrecioPiscina(400000);
        sol20.setPrecioPista(800000);
        este.add(sol20);

        Casilla tran4 = new Casilla("Tran4", "Transporte", 35, 500000, banca);
        tran4.setAlquilerBase(250000);
        este.add(tran4);

        este.add(new Casilla("Suerte", "Suerte", 36, banca));

        Casilla sol21 = new Casilla("Sol21", "Solar", 37, 3500000, banca);
        sol21.setHipoteca(1750000);
        sol21.setAlquilerBase(350000);
        sol21.setAlquilerCasa(3250000);
        sol21.setAlquilerHotel(17000000);
        sol21.setAlquilerPiscina(3400000);
        sol21.setAlquilerPista(3400000);
        sol21.setPrecioCasa(2000000);
        sol21.setPrecioHotel(2000000);
        sol21.setPrecioPiscina(400000);
        sol21.setPrecioPista(800000);
        este.add(sol21);

        Casilla imp2 = new Casilla("Imp2", "Impuesto", 38, 2000000, banca);
        este.add(imp2);

        Casilla sol22 = new Casilla("Sol22", "Solar", 39, 4000000, banca);
        sol22.setHipoteca(2000000);
        sol22.setAlquilerBase(500000);
        sol22.setAlquilerCasa(4250000);
        sol22.setAlquilerHotel(20000000);
        sol22.setAlquilerPiscina(4000000);
        sol22.setAlquilerPista(4000000);
        sol22.setPrecioCasa(2000000);
        sol22.setPrecioHotel(2000000);
        sol22.setPrecioPiscina(400000);
        sol22.setPrecioPista(800000);
        este.add(sol22);
    }


    // ======== Grupos ========
    private String norm(String s) {
        if (s == null) return "";
        return s.trim().toLowerCase();
    }

    private void crearGrupos() {
        grupos.clear();

        String[][] def = {
                {"marron", "Sol1", "Sol2"},
                {"cian", "Sol3", "Sol4", "Sol5"},
                {"magenta", "Sol6", "Sol7", "Sol8"},
                {"amarillo", "Sol9", "Sol10", "Sol11"},
                {"rojo", "Sol12", "Sol13", "Sol14"},
                {"naranja", "Sol15", "Sol16", "Sol17"},
                {"verde", "Sol18", "Sol19", "Sol20"},
                {"azul", "Sol21", "Sol22"},
                {"transporte", "Tran1", "Tran2", "Tran3", "Tran4"},
                {"servicio", "Serv1", "Serv2"}
        };

        for (String[] fila : def) {
            String nombreGrupo = norm(fila[0]);
            Grupo g = new Grupo(nombreGrupo);
            for (int i = 1; i < fila.length; i++) {
                Casilla c = encontrar_casilla(fila[i]);
                if (c == null) {
                    System.out.println("Aviso: no existe la casilla " + fila[i] + " para el grupo " + nombreGrupo);
                    continue;
                }
                g.anhadirCasilla(c);
            }
            grupos.put(nombreGrupo, g);
        }

        // Asignar el grupo a cada casilla
        for (Grupo g : grupos.values()) {
            for (Casilla c : g.getMiembros()) {
                if (c != null) c.setGrupo(g);
            }
        }
    }

    public Casilla getCasilla(int posicion) {
        for (ArrayList<Casilla> lado : posiciones) {
            for (Casilla c : lado) {
                if (c.getPosicion() == posicion) {
                    return c;
                }
            }
        }
        return null;
    }
    private Menu menu; // asegúrate de tener una referencia al menú

    public void notificarBancarrota(Jugador jugador) {
        if (menu != null) {
            menu.verificarGanadorTrasBancarrota();
        }
    }

    public void inicializarCartas() {
        mazoSuerte.add(new Carta("Recibes 1.000.000€ por inversión exitosa", "suerte"));
        mazoSuerte.add(new Carta("Paga 500.000€ por reparación de propiedades", "suerte"));
        mazoSuerte.add(new Carta("Ve directamente a la cárcel", "suerte"));
        mazoSuerte.add(new Carta("Has ganado una carta para salir de la cárcel", "suerte"));

        mazoCaja.add(new Carta("Recibes 200.000€ por devolución de impuestos", "caja"));
        mazoCaja.add(new Carta("Paga 300.000€ por gastos médicos", "caja"));
        mazoCaja.add(new Carta("Avanza a la casilla 'Sol10'", "caja"));
    }

    public Carta sacarCarta(String tipo) {
        Random r = new Random();
        if (tipo.equalsIgnoreCase("suerte")) {
            return mazoSuerte.get(r.nextInt(mazoSuerte.size()));
        } else {
            return mazoCaja.get(r.nextInt(mazoCaja.size()));
        }
    }

    // ======== Utilidades de impresión ========

    private String formatCasilla(Casilla c) {
        String nombre = c.getNombre();
        String color = (c.getGrupo() != null) ? c.getGrupo().getColorGrupo() : "";

        // Recoger IDs de avatares
        StringBuilder avatares = new StringBuilder();
        for (Avatar av : c.getAvatares()) {
            avatares.append("&").append(av.getId());
        }

        // Limitar nombre a 8 caracteres y avatares a 8
        String nombreCorto = String.format("%-8.8s", nombre);
        String avataresCorto = String.format("%-8.8s", avatares.toString());

        String texto = nombreCorto + avataresCorto;
        String casillaDelimitada = "[" + texto + "]";

        return colorTexto(casillaDelimitada, color);
    }

    private String colorTexto(String texto, String colorGrupo) {
        if (colorGrupo == null) return texto;
        String k = colorGrupo.toLowerCase();
        switch (k) {
            case "marron":
                return "\033[38;5;94m" + texto + "\033[0m";  // marrón
            case "cian":
                return "\033[0;36m" + texto + "\033[0m";     // cian
            case "magenta":
                return "\033[0;35m" + texto + "\033[0m";     // magenta
            case "amarillo":
                return "\033[0;33m" + texto + "\033[0m";     // amarillo
            case "rojo":
                return "\033[0;31m" + texto + "\033[0m";     // rojo
            case "naranja":
                return "\033[38;5;208m" + texto + "\033[0m"; // naranja
            case "verde":
                return "\033[0;32m" + texto + "\033[0m";     // verde
            case "azul":
                return "\033[0;34m" + texto + "\033[0m";     // azul
            case "transporte":
                return "\033[1;37m" + texto + "\033[0m";     // blanco brillante
            case "servicio":
                return "\033[0;37m" + texto + "\033[0m";     // blanco
            default:
                return texto;
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        ArrayList<Casilla> sur = posiciones.get(SUR);
        ArrayList<Casilla> norte = posiciones.get(NORTE);
        ArrayList<Casilla> oeste = posiciones.get(OESTE);
        ArrayList<Casilla> este = posiciones.get(ESTE);

        // --- Línea superior (NORTE, de derecha a izquierda) ---
        for (int i = norte.size() - 1; i >= 0; i--) {
            sb.append(formatCasilla(norte.get(i)));
        }
        sb.append("\n");

        // --- Lados OESTE y ESTE ---
        int alto = Math.max(oeste.size(), este.size());
        for (int i = 0; i < alto; i++) {
            // OESTE (de arriba a abajo)
            if (i < oeste.size()) {
                sb.append(formatCasilla(oeste.get(oeste.size() - 1 - i)));
            } else {
                sb.append(" ".repeat(12));
            }

            // Espacios en el centro (casillas norte - 2 esquinas)
            sb.append(" ".repeat((norte.size() - 2) * 18));

            // ESTE (de arriba a abajo)
            if (i < este.size()) {
                sb.append(formatCasilla(este.get(i)));
            }
            sb.append("\n");
        }

        // --- Línea inferior (SUR, de izquierda a derecha) ---
        for (Casilla c : sur) {
            sb.append(formatCasilla(c));
        }
        sb.append("\n");

        return sb.toString();
    }

    public int getCantidadCasillasGrupo(String nombreGrupo) {
        int total = 0;
        for (Casilla c : casillas) {
            if (c.getGrupo() != null && c.getGrupo().getNombre().equalsIgnoreCase(nombreGrupo)) {
                total++;
            }
        }
        return total;
    }

    public ArrayList<Casilla> getCasillasEnVenta() {
        ArrayList<Casilla> enVenta = new ArrayList<>();

        for (Casilla c : casillas) {
            if (c.getDuenho() == null &&
                    ("solar".equalsIgnoreCase(c.getTipo()) ||
                            "transporte".equalsIgnoreCase(c.getTipo()) ||
                            "servicio".equalsIgnoreCase(c.getTipo()))) {
                enVenta.add(c);
            }
        }

        return enVenta;
    }


    public Casilla encontrar_casilla(String nombre) {
        for (ArrayList<Casilla> lado : posiciones) {
            for (Casilla c : lado) {
                if (c.getNombre().equalsIgnoreCase(nombre)) {
                    return c;
                }
            }
        }
        return null;

    }

    public Jugador getBanca() {
        return banca;
    }




    public void añadirAlParking(float cantidad) {
        fondoParking += cantidad;
    }

    public float recogerParking() {
        float cantidad = fondoParking;
        fondoParking = 0;
        return cantidad;
    }

    public float getFondoParking() {
        return fondoParking;
    }

    public int getUltimaTirada() {
        return ultimaTirada;
    }

    public void setUltimaTirada(int tirada) {
        this.ultimaTirada = tirada;
    }



    public void setMenu(Menu menu) {
        this.menu = menu;
    }
}
