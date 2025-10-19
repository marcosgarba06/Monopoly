package partida;

import java.util.ArrayList;
import java.util.Random;

import monopoly.*;

public class Jugador {

    //Atributos:
    private String nombre;
    private int cartasSalirCarcel = 0;
    private Avatar avatar;
    private float fortuna;
    private float gastos;
    private boolean enCarcel;
    private int tiradasCarcel;
    private int vueltas;
    private ArrayList<Casilla> propiedades;
    private boolean bancarrota;
    private boolean tieneCartaSalirCarcel = false;
    private boolean activo = true;

    private ArrayList<Carta> mazoSuerte = new ArrayList<>();
    private ArrayList<Carta> mazoCaja = new ArrayList<>();
    private Tablero tablero;

    //Constructor vacío. Se usará para crear la banca.
    public Jugador() {
        this.nombre = "BANCA";
        this.avatar = null;
        this.fortuna = Valor.FORTUNA_BANCA;
        this.gastos = 0;
        this.enCarcel = false;
        this.tiradasCarcel = 0;
        this.vueltas = 0;
        this.propiedades = new ArrayList<>();
        this.bancarrota = false;
        this.tieneCartaSalirCarcel = false;
    }

    /*Constructor principal. Requiere parámetros:
     * Nombre del jugador, tipo del avatar que tendrá, casilla en la que empezará y ArrayList de
     * avatares creados (usado para dos propósitos: evitar que dos jugadores tengan el mismo nombre y
     * que dos avatares tengan mismo ID). Desde este constructor también se crea el avatar.
     */
    public Jugador(String nombre, String tipoAvatar, Casilla inicio, ArrayList<Avatar> avCreados) {
        this.nombre = nombre;
        this.avatar = new Avatar(tipoAvatar, this, inicio, avCreados);
        this.fortuna = Valor.FORTUNA_INICIAL;
        this.gastos = 0;
        this.enCarcel = false;
        this.vueltas = 0;
        this.tiradasCarcel = 0;
        this.propiedades = new ArrayList<>();
        this.bancarrota = false;
        this.tieneCartaSalirCarcel = false;
    }

    // Métodos de fortuna
    public void sumarFortuna(float valor) {
        this.fortuna += valor;
    }

    public void restarFortuna(float cantidad) {
        this.fortuna -= cantidad;
        if (fortuna < 0) { // ✅ Correcto
            this.bancarrota = true;
            System.out.println(nombre + " ha caído en bancarrota.");
        }
    }

    public void añadirCartaSalirCarcel() {
        cartasSalirCarcel++;
    }

    public boolean usarCartaSalirCarcel() {
        if (cartasSalirCarcel > 0) {
            cartasSalirCarcel--;
            return true;
        }
        return false;
    }

    public int getCartasSalirCarcel() {
        return cartasSalirCarcel;
    }

    public void sumarGastos(float valor) {
        this.gastos += valor;
    }

    // Métodos de propiedades
    public void anadirPropiedad(Casilla casilla) {
        if (!propiedades.contains(casilla)) {
            this.propiedades.add(casilla);
        }
    }

    public void eliminarPropiedad(Casilla casilla) {
        this.propiedades.remove(casilla);
    }

    // Métodos de cartas
    public void darCartaSalirCarcel() {
        this.tieneCartaSalirCarcel = true;
    }

    public boolean tienePropiedades() {
        return !propiedades.isEmpty();
    }


    // Método para enviar al jugador a la cárcel
    public void irACarcel(Tablero tablero) {
        this.enCarcel = true;
        this.tiradasCarcel = 0;

        Casilla carcel = tablero.encontrar_casilla("Carcel");
        if (carcel == null) {
            System.out.println("Error: no se encontró la casilla de Cárcel.");
            return;
        }

        // Eliminar el avatar de su casilla actual
        if (avatar.getCasilla() != null) {
            avatar.getCasilla().eliminarAvatar(avatar);
        }

        // Colocar el avatar en la cárcel
        avatar.setCasilla(carcel);
        avatar.setPosicion(carcel.getPosicion());
        carcel.anhadirAvatar(avatar);
        avatar.setEnCarcel(true);
        avatar.setTurnosEnCarcel(0);

        System.out.println(nombre + " ha sido enviado a la cárcel.");
    }

    // Métodos de pago

    public void pagar(float cantidad, Jugador receptor) {
        if (fortuna >= cantidad) {
            this.fortuna -= cantidad;
            this.gastos += cantidad;
            receptor.sumarFortuna(cantidad);
        } else {
            System.out.println(nombre + " no tiene suficiente dinero para pagar " + cantidad);
            // Aquí se podría implementar lógica de bancarrota, hipoteca, etc.
        }
    }

    public void cobrar(float cantidad, Jugador pagador) {
        this.fortuna += cantidad;
        pagador.restarFortuna(cantidad);
        pagador.sumarGastos(cantidad);
    }


    public void robarCarta(String tipo) {
        Carta carta = tablero.sacarCarta(tipo);
        System.out.println(nombre + " roba una carta: " + carta.getDescripcion());
        carta.aplicarAccion(this, tablero);
    }




    public void anhadirPropiedad(Casilla c) {
        propiedades.add(c);
    }

    public void declararBancarrota(Jugador acreedor) {
        System.out.println(this.nombre + " se ha declarado en bancarrota.");

        // Transferir propiedades
        for (Casilla propiedad : propiedades) {
            propiedad.setDuenho(acreedor);
            acreedor.anhadirPropiedad(propiedad);
        }

        propiedades.clear();
        fortuna = 0;
        activo = false;

        // si usas un flag para saber si sigue en juego
    }

    public boolean estaActivo() {
        return activo;
    }


    public boolean poseeGrupoCompleto(Casilla casilla, Tablero tablero) {
        String nombreGrupo = casilla.getGrupo().getNombre();
        int total = tablero.getCantidadCasillasGrupo(nombreGrupo);

        int propias = 0;
        for (Casilla c : propiedades) {
            if (c.getGrupo().getNombre().equalsIgnoreCase(nombreGrupo)) {
                propias++;
            }
        }
        return propias == total;
    }

    // Getters

    public String getNombre() {
        return nombre;
    }
    public Avatar getAvatar() {
        return avatar;
    }
    public float getFortuna() {
        return fortuna;
    }
    public float getGastos() {
        return gastos;
    }
    public boolean isEnCarcel() {
        return enCarcel;
    }
    public int getTiradasCarcel() {
        return tiradasCarcel;
    }
    public int getVueltas() {
        return vueltas;
    }
    public ArrayList<Casilla> getPropiedades() {
        return propiedades;
    }
    public boolean isBancarrota() {
        return bancarrota;
    }
    public boolean tieneCartaSalirCarcel() {
        return tieneCartaSalirCarcel;
    }
    public boolean getTieneCartaSalirCarcel() {
        return tieneCartaSalirCarcel;
    }
    // Setters

    public void setTieneCartaSalirCarcel(boolean valor) {
        tieneCartaSalirCarcel = valor;
    }
    public void setEnCarcel(boolean enCarcel) {
        this.enCarcel = enCarcel;
    }
    public void setBancarrota(boolean bancarrota) {
        this.bancarrota = bancarrota;
    }
    public void incrementarVueltas() {
        this.vueltas++;
    }
    public void setActivo(boolean estado) {
        this.activo = estado;
    }
    public void setTablero(Tablero t) {
        this.tablero = t;
    }


}