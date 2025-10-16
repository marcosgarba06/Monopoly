package monopoly;

import partida.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Tablero {

    // Lados
    private static final int SUR = 0;
    private static final int OESTE = 1;
    private static final int NORTE = 2;
    private static final int ESTE = 3;

    // Atributos
    private ArrayList<ArrayList<Casilla>> posiciones;
    private HashMap<String, Grupo> grupos;
    private Jugador banca;

    // Constructor
    public Tablero(Jugador banca) {
        this.banca = banca;
        this.posiciones = new ArrayList<>();
        for (int i = 0; i < 4; i++) this.posiciones.add(new ArrayList<>());
        this.grupos = new HashMap<>();
        generarCasillas();
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
    }

    // NORTE (derecha->izquierda visual, indices 20..30)
    private void insertarLadoNorte() {
        ArrayList<Casilla> norte = posiciones.get(NORTE);

        norte.add(new Casilla("IrCarcel", "Especial", 30, banca));
        norte.add(new Casilla("Sol17", "Solar", 29, 2800000, banca));
        norte.add(new Casilla("Serv2", "Servicio", 28, 500000, banca));
        norte.add(new Casilla("Sol16", "Solar", 27, 2600000, banca));
        norte.add(new Casilla("Sol15", "Solar", 26, 2600000, banca));
        norte.add(new Casilla("Tran3", "Transporte", 25, 500000, banca));
        norte.add(new Casilla("Sol14", "Solar", 24, 2400000, banca));
        norte.add(new Casilla("Sol13", "Solar", 23, 2200000, banca));
        norte.add(new Casilla("Suerte", "Suerte", 22, banca));
        norte.add(new Casilla("Sol12", "Solar", 21, 2200000, banca));
        norte.add(new Casilla("Parking", "Especial", 20, banca));
    }

    // SUR (izquierda->derecha visual, indices 0..10)
    private void insertarLadoSur() {
        ArrayList<Casilla> sur = posiciones.get(SUR);

        sur.add(new Casilla("Carcel", "Especial", 10, banca));
        sur.add(new Casilla("Sol5", "Solar", 9, 1200000, banca));
        sur.add(new Casilla("Sol4", "Solar", 8, 1000000, banca));
        sur.add(new Casilla("Suerte", "Suerte", 7, banca));
        sur.add(new Casilla("Sol3", "Solar", 6, 1000000, banca));
        sur.add(new Casilla("Tran1", "Transporte", 5, 500000, banca));
        sur.add(new Casilla("Imp1", "Impuesto", 4, 200000, banca));
        sur.add(new Casilla("Sol2", "Solar", 3, 600000, banca));
        sur.add(new Casilla("Caja", "Caja", 2, banca));
        sur.add(new Casilla("Sol1", "Solar", 1, 600000, banca));
        sur.add(new Casilla("Salida", "Especial", 0, banca));
    }

    // OESTE (abajo->arriba visual, indices 11..19)
    private void insertarLadoOeste() {
        ArrayList<Casilla> oeste = posiciones.get(OESTE);

        oeste.add(new Casilla("Sol6", "Solar", 11, 1400000, banca));
        oeste.add(new Casilla("Serv1", "Servicio", 12, 500000, banca));
        oeste.add(new Casilla("Sol7", "Solar", 13, 1400000, banca));
        oeste.add(new Casilla("Sol8", "Solar", 14, 1600000, banca));
        oeste.add(new Casilla("Tran2", "Transporte", 15, 500000, banca));
        oeste.add(new Casilla("Sol9", "Solar", 16, 1800000, banca));
        oeste.add(new Casilla("Caja", "Caja", 17, banca));
        oeste.add(new Casilla("Sol10", "Solar", 18, 1800000, banca));
        oeste.add(new Casilla("Sol11", "Solar", 19, 2000000, banca));
    }

    // ESTE (arriba->abajo visual, indices 31..39)
    private void insertarLadoEste() {
        ArrayList<Casilla> este = posiciones.get(ESTE);

        este.add(new Casilla("Sol18", "Solar", 31, 3000000, banca));
        este.add(new Casilla("Sol19", "Solar", 32, 3000000, banca));
        este.add(new Casilla("Caja", "Caja", 33, banca));
        este.add(new Casilla("Sol20", "Solar", 34, 3200000, banca));
        este.add(new Casilla("Tran4", "Transporte", 35, 500000, banca));
        este.add(new Casilla("Suerte", "Suerte", 36, banca));
        este.add(new Casilla("Sol21", "Solar", 37, 3500000, banca));
        este.add(new Casilla("Imp2", "Impuesto", 38, 200000, banca));
        este.add(new Casilla("Sol22", "Solar", 39, 4000000, banca));
    }

    // ======== Grupos ========
    private String norm(String s) {
        if (s == null) return "";
        return s.trim().toLowerCase();
    }

    private void crearGrupos() {
        grupos.clear();

        String[][] def = {
                {"marron",   "Sol1", "Sol2"},
                {"cian",     "Sol3", "Sol4", "Sol5"},
                {"magenta",  "Sol6", "Sol7", "Sol8"},
                {"amarillo", "Sol9", "Sol10", "Sol11"},
                {"rojo",     "Sol12", "Sol13", "Sol14"},
                {"naranja",  "Sol15", "Sol16", "Sol17"},
                {"verde",    "Sol18", "Sol19", "Sol20"},
                {"azul",     "Sol21", "Sol22"},
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

    // ======== Utilidades de impresión ========
    private String formatCasilla(Casilla c) {
        String nombre = c.getNombre();
        String color = (c.getGrupo() != null) ? c.getGrupo().getColorGrupo() : "";

        // Recoger IDs de avatares
        StringBuilder avatares = new StringBuilder();
        for (Avatar av : c.getAvatares()) {
            avatares.append("&").append(av.getId());
        }

        // Limitar nombre a 7 caracteres y avatares a 5
        String nombreCorto = String.format("%-7.7s", nombre);
        String avataresCorto = String.format("%-5.5s", avatares.toString());

        String texto = nombreCorto + avataresCorto;
        return colorTexto(texto, color);
    }

    private String colorTexto(String texto, String colorGrupo) {
        if (colorGrupo == null) return texto;
        String k = colorGrupo.toLowerCase();
        switch (k) {
            case "marron":     return "\033[38;5;94m" + texto + "\033[0m";  // marrón
            case "cian":       return "\033[0;36m" + texto + "\033[0m";     // cian
            case "magenta":    return "\033[0;35m" + texto + "\033[0m";     // magenta
            case "amarillo":   return "\033[0;33m" + texto + "\033[0m";     // amarillo
            case "rojo":       return "\033[0;31m" + texto + "\033[0m";     // rojo
            case "naranja":    return "\033[38;5;208m" + texto + "\033[0m"; // naranja
            case "verde":      return "\033[0;32m" + texto + "\033[0m";     // verde
            case "azul":       return "\033[0;34m" + texto + "\033[0m";     // azul
            case "transporte": return "\033[1;37m" + texto + "\033[0m";     // blanco brillante
            case "servicio":   return "\033[0;37m" + texto + "\033[0m";     // blanco
            default:           return texto;
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
            sb.append(" ".repeat((norte.size() - 2) * 12));

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
}