package monopoly;

import monopoly.Casillas.Casilla;
import monopoly.Casillas.Propiedades.Solar;
import monopoly.Casillas.Propiedades.Transporte;
import monopoly.Casillas.Propiedades.Servicio;
import monopoly.Casillas.Impuesto;
import monopoly.Casillas.Especial;
import monopoly.Casillas.Grupo;
import monopoly.Casillas.Acciones.CajaComunidad;
import monopoly.Casillas.Acciones.Suerte;
import partida.Jugador;
import partida.Avatar;


import java.util.ArrayList;
import java.util.HashMap;

public class Tablero {

    private ArrayList<ArrayList<Casilla>> posiciones;
    private ArrayList<Casilla> casillas;
    private HashMap<String, Grupo> grupos;
    private Jugador banca;
    int ultimaTirada = 0;
    private float fondoParking = 0;
    private static final int SUR = 0;
    private static final int OESTE = 1;
    private static final int NORTE = 2;
    private static final int ESTE = 3;

    public Tablero(Jugador banca) {
        this.banca = banca;
        posiciones = new ArrayList<>();
        for (int i = 0; i < 4; i++) posiciones.add(new ArrayList<>());
        grupos = new HashMap<>();
        generarCasillas();
        crearGrupos();
    }

    private void generarCasillas() {
        insertarLadoSur();
        insertarLadoOeste();
        insertarLadoNorte();
        insertarLadoEste();

        casillas = new ArrayList<>();
        for (ArrayList<Casilla> lado : posiciones) {
            casillas.addAll(lado);
        }
    }

    // ======== LADO SUR ========
    private void insertarLadoSur() {
        ArrayList<Casilla> sur = posiciones.get(SUR);

        sur.add(new Especial("Carcel", 10, "carcel"));

        Solar sol5 = new Solar("Solar5", 9, 1200000, 80000);
        sol5.setHipoteca(600000);
        sol5.setPrecioCasa(500000); sol5.setPrecioHotel(500000);
        sol5.setPrecioPiscina(100000); sol5.setPrecioPista(200000);
        sol5.setAlquilerCasa(1250000); sol5.setAlquilerHotel(6000000);
        sol5.setAlquilerPiscina(1200000); sol5.setAlquilerPista(1200000);
        sur.add(sol5);

        Solar sol4 = new Solar("Solar4", 8, 1000000, 60000);
        sol4.setHipoteca(500000);
        sol4.setPrecioCasa(500000); sol4.setPrecioHotel(500000);
        sol4.setPrecioPiscina(100000); sol4.setPrecioPista(200000);
        sol4.setAlquilerCasa(1000000); sol4.setAlquilerHotel(5500000);
        sol4.setAlquilerPiscina(1100000); sol4.setAlquilerPista(1100000);
        sur.add(sol4);

        sur.add(new Suerte("Suerte", 7));

        Solar sol3 = new Solar("Solar3", 6, 1000000, 60000);
        sol3.setHipoteca(500000);
        sol3.setPrecioCasa(500000); sol3.setPrecioHotel(500000);
        sol3.setPrecioPiscina(100000); sol3.setPrecioPista(200000);
        sol3.setAlquilerCasa(1000000); sol3.setAlquilerHotel(5500000);
        sol3.setAlquilerPiscina(1100000); sol3.setAlquilerPista(1100000);
        sur.add(sol3);

        Transporte tran1 = new Transporte("Tran1", 5, 500000);
        tran1.setAlquilerBase(250000);
        sur.add(tran1);

        Impuesto imp1 = new Impuesto("Imp1", 4, 2000000);
        sur.add(imp1);

        Solar sol2 = new Solar("Solar2", 3, 600000, 40000);
        sol2.setHipoteca(300000);
        sol2.setPrecioCasa(500000); sol2.setPrecioHotel(500000);
        sol2.setPrecioPiscina(100000); sol2.setPrecioPista(200000);
        sol2.setAlquilerCasa(800000); sol2.setAlquilerHotel(4500000);
        sol2.setAlquilerPiscina(900000); sol2.setAlquilerPista(900000);
        sur.add(sol2);


        sur.add(new CajaComunidad("Caja", 2));

        Solar sol1 = new Solar("Solar1", 1, 600000, 20000);
        sol1.setHipoteca(300000);
        sol1.setPrecioCasa(500000); sol1.setPrecioHotel(500000);
        sol1.setPrecioPiscina(100000); sol1.setPrecioPista(200000);
        sol1.setAlquilerCasa(400000); sol1.setAlquilerHotel(2500000);
        sol1.setAlquilerPiscina(500000); sol1.setAlquilerPista(500000);
        sur.add(sol1);


        sur.add(new Especial("Salida", 0, "salida"));
    }

    // ======== LADO OESTE ========
    private void insertarLadoOeste() {
        ArrayList<Casilla> oeste = posiciones.get(OESTE);

        Solar sol6 = new Solar("Solar6", 11, 1400000, 100000);
        sol6.setHipoteca(700000);
        sol6.setPrecioCasa(1000000); sol6.setPrecioHotel(1000000);
        sol6.setPrecioPiscina(200000); sol6.setPrecioPista(400000);
        sol6.setAlquilerCasa(1500000); sol6.setAlquilerHotel(7500000);
        sol6.setAlquilerPiscina(1500000); sol6.setAlquilerPista(1500000);
        oeste.add(sol6);

        Servicio serv1 = new Servicio("Serv1", 12, 500000);
        oeste.add(serv1);


        Solar sol7 = new Solar("Solar7", 13, 1400000, 100000);
        sol7.setHipoteca(700000);
        sol7.setPrecioCasa(1000000); sol7.setPrecioHotel(1000000);
        sol7.setPrecioPiscina(200000); sol7.setPrecioPista(400000);
        sol7.setAlquilerCasa(1500000); sol7.setAlquilerHotel(7500000);
        sol7.setAlquilerPiscina(1500000); sol7.setAlquilerPista(1500000);
        oeste.add(sol7);


        Solar sol8 = new Solar("Solar8", 14, 1600000, 120000);
        sol8.setHipoteca(800000);
        sol8.setPrecioCasa(1000000); sol8.setPrecioHotel(1000000);
        sol8.setPrecioPiscina(200000); sol8.setPrecioPista(400000);
        sol8.setAlquilerCasa(1750000); sol8.setAlquilerHotel(9000000);
        sol8.setAlquilerPiscina(1800000); sol8.setAlquilerPista(1800000);
        oeste.add(sol8);

        Transporte tran2 = new Transporte("Tran2", 15, 500000);
        oeste.add(tran2);

        Solar sol9 = new Solar("Solar9", 16, 1800000, 140000);
        sol9.setHipoteca(900000);
        sol9.setPrecioCasa(1000000); sol9.setPrecioHotel(1000000);
        sol9.setPrecioPiscina(200000); sol9.setPrecioPista(400000);
        sol9.setAlquilerCasa(1850000); sol9.setAlquilerHotel(9500000);
        sol9.setAlquilerPiscina(1900000); sol9.setAlquilerPista(1900000);
        oeste.add(sol9);

        oeste.add(new CajaComunidad("Caja", 17));

        Solar sol10 = new Solar("Solar10", 18, 1800000, 140000);
        sol10.setHipoteca(900000);
        sol10.setPrecioCasa(1000000); sol10.setPrecioHotel(1000000);
        sol10.setPrecioPiscina(200000); sol10.setPrecioPista(400000);
        sol10.setAlquilerCasa(1850000); sol10.setAlquilerHotel(9500000);
        sol10.setAlquilerPiscina(1900000); sol10.setAlquilerPista(1900000);
        oeste.add(sol10);

        Solar sol11 = new Solar("Solar11", 19, 2200000, 160000);
        sol11.setHipoteca(1000000);
        sol11.setPrecioCasa(1000000); sol11.setPrecioHotel(1000000);
        sol11.setPrecioPiscina(200000); sol11.setPrecioPista(400000);
        sol11.setAlquilerCasa(2000000); sol11.setAlquilerHotel(10000000);
        sol11.setAlquilerPiscina(2000000); sol11.setAlquilerPista(2000000);
        oeste.add(sol11);
    }

    // ======== LADO NORTE ========
    private void insertarLadoNorte() {
        ArrayList<Casilla> norte = posiciones.get(NORTE);

        norte.add(new Especial("IrCarcel", 30, "ircarcel"));

        Solar sol17 = new Solar("Solar17", 29, 2800000, 240000);
        sol17.setHipoteca(1400000);
        sol17.setPrecioCasa(1500000); sol17.setPrecioHotel(1500000);
        sol17.setPrecioPiscina(300000); sol17.setPrecioPista(600000);
        sol17.setAlquilerCasa(2600000); sol17.setAlquilerHotel(12000000);
        sol17.setAlquilerPiscina(2400000); sol17.setAlquilerPista(2400000);
        norte.add(sol17);

        Servicio serv2 = new Servicio("Serv2", 28, 500000);
        norte.add(serv2);

        Solar sol16 = new Solar("Solar16", 27, 2600000, 220000);
        sol16.setHipoteca(1300000);
        sol16.setPrecioCasa(1500000); sol16.setPrecioHotel(1500000);
        sol16.setPrecioPiscina(300000); sol16.setPrecioPista(600000);
        sol16.setAlquilerCasa(2450000); sol16.setAlquilerHotel(11500000);
        sol16.setAlquilerPiscina(2300000);
        sol16.setAlquilerPista(2300000);
        norte.add(sol16);

        Solar sol15 = new Solar("Solar15", 26, 2600000, 220000);
        sol15.setHipoteca(1300000);
        sol15.setPrecioCasa(1500000); sol15.setPrecioHotel(1500000);
        sol15.setPrecioPiscina(300000); sol15.setPrecioPista(600000);
        sol15.setAlquilerCasa(2450000); sol15.setAlquilerHotel(11500000);
        sol15.setAlquilerPiscina(2300000); sol15.setAlquilerPista(2300000);
        norte.add(sol15);


        Transporte tran3 = new Transporte("Tran3", 25, 500000);
        norte.add(tran3);

        Solar sol14 = new Solar("Sol14", 24, 2400000, 200000);
        sol14.setHipoteca(1200000);
        sol14.setPrecioCasa(1500000); sol14.setPrecioHotel(1500000);
        sol14.setPrecioPiscina(300000); sol14.setPrecioPista(600000);
        sol14.setAlquilerCasa(2325000); sol14.setAlquilerHotel(11000000);
        sol14.setAlquilerPiscina(2200000); sol14.setAlquilerPista(2200000);
        norte.add(sol14);

        Solar sol13 = new Solar("Solar13", 23, 2200000, 180000);
        sol13.setHipoteca(1100000);
        sol13.setPrecioCasa(1500000); sol13.setPrecioHotel(1500000);
        sol13.setPrecioPiscina(300000); sol13.setPrecioPista(600000);
        sol13.setAlquilerCasa(2200000); sol13.setAlquilerHotel(10500000);
        sol13.setAlquilerPiscina(2100000); sol13.setAlquilerPista(2100000);
        norte.add(sol13);

        norte.add(new Suerte("Suerte", 22));

        Solar sol12 = new Solar("Solar12", 21, 2200000, 180000);
        sol12.setHipoteca(1100000);
        sol12.setPrecioCasa(1500000); sol12.setPrecioHotel(1500000);
        sol12.setPrecioPiscina(300000); sol12.setPrecioPista(600000);
        sol12.setAlquilerCasa(2200000); sol12.setAlquilerHotel(10500000);
        sol12.setAlquilerPiscina(2100000); sol12.setAlquilerPista(2100000);
        norte.add(sol12);

        norte.add(new Especial("Parking", 20, "parking"));
    }

    // ======== LADO ESTE ========
    private void insertarLadoEste() {
        ArrayList<Casilla> este = posiciones.get(ESTE);

        Solar sol18 = new Solar("Solar18", 31, 3000000, 260000);
        sol18.setHipoteca(1500000);
        sol18.setPrecioCasa(2000000); sol18.setPrecioHotel(2000000);
        sol18.setPrecioPiscina(400000); sol18.setPrecioPista(800000);
        sol18.setAlquilerCasa(2750000); sol18.setAlquilerHotel(12750000);
        sol18.setAlquilerPiscina(2550000); sol18.setAlquilerPista(2550000);
        este.add(sol18);

        Solar sol19 = new Solar("Solar19", 32, 3000000, 260000);
        sol19.setHipoteca(1500000);
        sol19.setPrecioCasa(2000000); sol19.setPrecioHotel(2000000);
        sol19.setPrecioPiscina(400000); sol19.setPrecioPista(800000);
        sol19.setAlquilerCasa(2750000); sol19.setAlquilerHotel(12750000);
        sol19.setAlquilerPiscina(2550000); sol19.setAlquilerPista(2550000);
        este.add(sol19);


        este.add(new CajaComunidad("Caja", 33));

        Solar sol20 = new Solar("Solar20", 34, 3200000, 280000);
        sol20.setHipoteca(1600000);
        sol20.setPrecioCasa(2000000); sol20.setPrecioHotel(2000000);
        sol20.setPrecioPiscina(400000); sol20.setPrecioPista(800000);
        sol20.setAlquilerCasa(3000000); sol20.setAlquilerHotel(14000000);
        sol20.setAlquilerPiscina(2800000); sol20.setAlquilerPista(2800000);
        este.add(sol20);


        Transporte tran4 = new Transporte("Tran4", 35, 500000);
        este.add(tran4);

        este.add(new Suerte("Suerte", 36));

        Solar sol21 = new Solar("Solar21", 37, 3500000, 350000);
        sol21.setHipoteca(1750000);
        sol21.setPrecioCasa(2000000); sol21.setPrecioHotel(2000000);
        sol21.setPrecioPiscina(400000); sol21.setPrecioPista(800000);
        sol21.setAlquilerCasa(3250000); sol21.setAlquilerHotel(17000000);
        sol21.setAlquilerPiscina(3400000); sol21.setAlquilerPista(3400000);
        este.add(sol21);


        Impuesto imp2 = new Impuesto("Imp2", 38, 2000000);
        este.add(imp2);

        Solar sol22 = new Solar("Solar22", 39, 4000000, 500000);
        sol22.setHipoteca(2000000);
        sol22.setPrecioCasa(2000000); sol22.setPrecioHotel(2000000);
        sol22.setPrecioPiscina(400000); sol22.setPrecioPista(800000);
        sol22.setAlquilerCasa(4250000); sol22.setAlquilerHotel(20000000);
        sol22.setAlquilerPiscina(4000000); sol22.setAlquilerPista(4000000);
        este.add(sol22);


    }

    public ArrayList<Casilla> getCasillas() { return casillas; }



// ======== Grupos ========
    private String norm(String s) {
        if (s == null) return "";
        return s.trim().toLowerCase();
    }

    private void crearGrupos() {
        grupos.clear();

        String[][] def = {
                {"marron", "Solar1", "Solar2"},
                {"cian", "Solar3", "Solar4", "Solar5"},
                {"magenta", "Solar6", "Solar7", "Solar8"},
                {"amarillo", "Solar9", "Solar10", "Solar11"},
                {"rojo", "Solar12", "Solar13", "Sol14"},   // ← Este sí es "Sol14" (sin 'ar')
                {"naranja", "Solar15", "Solar16", "Solar17"},
                {"verde", "Solar18", "Solar19", "Solar20"},
                {"azul", "Solar21", "Solar22"},
                {"transporte", "Tran1", "Tran2", "Tran3", "Tran4"},
                {"servicio", "Serv1", "Serv2"}
        };

        for (String[] fila : def) {
            String nombreGrupo = norm(fila[0]);
            Grupo g = new Grupo(nombreGrupo);
            for (int i = 1; i < fila.length; i++) {
                Casilla c = encontrarCasilla(fila[i]);
                if (c == null) {
                    Juego.consola.imprimir("Aviso: no existe la casilla " + fila[i] + " para el grupo " + nombreGrupo);
                    continue;
                }
                g.anhadirCasilla(c);
            }
            grupos.put(nombreGrupo, g);
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

    public int getNumeroCasillas() {
        return casillas.size();
    }
    private Juego juego; // asegúrate de tener una referencia al menú

    public void notificarBancarrota(Jugador jugador) {
        if (juego != null) {
            juego.verificarGanadorTrasBancarrota();
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
            //  Verificar que la casilla tenga grupo antes de acceder a él
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


    public Casilla encontrarCasilla(String nombre) {
        for (ArrayList<Casilla> lado : posiciones) {
            for (Casilla c : lado) {
                if (c.getNombre().equalsIgnoreCase(nombre)) {
                    return c;
                }
            }
        }
        return null;

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

    public void setMenu(Juego juego) {
        this.juego = juego;
    }

    public Grupo getGrupo(String nombreGrupo) {
        if (nombreGrupo == null) return null;
        return grupos.get(nombreGrupo.toLowerCase().trim());
    }

    public ArrayList<Grupo> getGrupos() { return new ArrayList<>(grupos.values());}

}