package partida;

import java.util.ArrayList;

import monopoly.*;

import javax.print.attribute.standard.MediaSize;
import java.util.ArrayList;
import java.util.Scanner;


public class Jugador {

    //Atributos:
    private String nombre; //Nombre del jugador
    private Jugador jugador;
    private Avatar avatar; //Avatar que tiene en la partida.
    private float fortuna; //Dinero que posee.
    private float gastos; //Gastos realizados a lo largo del juego.
    private boolean enCarcel; //Será true si el jugador está en la carcel
    private int tiradasCarcel; //Cuando está en la carcel, contará las tiradas sin éxito que ha hecho allí para intentar salir (se usa para limitar el número de intentos).
    private int vueltas; //Cuenta las vueltas dadas al tablero.
    private ArrayList<Casilla> propiedades; //Propiedades que posee el jugador.
    private Jugador propietario;

    //Constructor vacío. Se usará para crear la banca.
    public Jugador()  {
        this.nombre = "BANCA";
        this.avatar = null; // no juega la banca
        this.fortuna = Valor.FORTUNA_BANCA; // tiene dinero infinito
        this.gastos = 0;
        this.enCarcel = false;
        this.tiradasCarcel = 0;
        this.vueltas = 0;
        this.propiedades = new ArrayList<>();

    }

    /*Constructor principal. Requiere parámetros:
    * Nombre del jugador, tipo del avatar que tendrá, casilla en la que empezará y ArrayList de
    * avatares creados (usado para dos propósitos: evitar que dos jugadores tengan el mismo nombre y
    * que dos avatares tengan mismo ID). Desde este constructor también se crea el avatar.
     */
    public Jugador(String nombre, String tipoAvatar, Casilla inicio, ArrayList<Avatar> avCreados) {
        this.nombre = nombre;

        Avatar nuevoAvatar = new Avatar(tipoAvatar,null, inicio, avCreados);
        this.avatar = nuevoAvatar;
        avCreados.add(nuevoAvatar); //se añade al arraylist como metodo para comprobar que el avatar no se repite


        this.fortuna = Valor.FORTUNA_INICIAL;
        this.gastos = 0;
        this.enCarcel = false;
        this.vueltas = 0;
        this.tiradasCarcel = 0;
        this.propiedades = new ArrayList<>();

    }

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

    public Jugador getJugador() {
        return jugador;
    }
    public Jugador getPropietario() {
        return propietario;
    }

    public void setPropietario(Jugador propietario) {
        this.propietario = propietario;
    }



    public ArrayList<Casilla> getPropiedades() {
        return propiedades;
    }

    //Otros métodos:
    //Método para añadir una propiedad al jugador. Como parámetro, la casilla a añadir.
    public void anadirPropiedad(Casilla casilla) {
        if(!propiedades.contains(casilla)) {
            this.propiedades.add(casilla);
        }
    }

    //Método para eliminar una propiedad del arraylist de propiedades de jugador.
    public void eliminarPropiedad(Casilla casilla) {
        this.propiedades.remove(casilla);
    }

    //Método para añadir fortuna a un jugador
    //Como parámetro se pide el valor a añadir. Si hay que restar fortuna, se pasaría un valor negativo.
    public void sumarFortuna(float valor) {
        this.fortuna += valor;
    }

    //Método para sumar gastos a un jugador.
    //Parámetro: valor a añadir a los gastos del jugador (será el precio de un solar, impuestos pagados...).
    public void sumarGastos(float valor) {
        this.gastos += valor;
    }

    /*Método para establecer al jugador en la cárcel. 
    * Se requiere disponer de las casillas del tablero para ello (por eso se pasan como parámetro).*/
    public void encarcelar(ArrayList<ArrayList<Casilla>> pos) {
        this.enCarcel = true;
        this.tiradasCarcel = 0;
        // mover la  casilla a la carcel
    }
    public void pagar(float cantidad, Jugador receptor) {
        if (fortuna >= cantidad) {
            this.fortuna -= cantidad;
            this.gastos += cantidad;
            receptor.sumarFortuna(cantidad);
        } else {
            // lógica si no tiene suficiente dinero (puede hipotecar, vender, etc.)
            System.out.println(nombre + " no tiene suficiente dinero para pagar " + cantidad);
        }
    }
    public void cobrar(float cantidad, Jugador pagador) {
        this.fortuna += cantidad;
        pagador.sumarGastos(cantidad);
    }

    public void robarCarta(String tipo) {
        System.out.println(nombre + " roba una carta de tipo " + tipo);
        // Aquí podrías invocar un método de Mazo para aplicar el efecto
    }

    public void irACarcel() {
        this.enCarcel = true;
        this.tiradasCarcel = 0;
        avatar.setPosicion(10); // suponiendo que la cárcel está en la casilla 10
        avatar.setEnCarcel(true);
        System.out.println(nombre + " ha sido enviado a la cárcel.");
    }

}
