package monopoly.Casillas.Propiedades;

import monopoly.Juego;
import partida.Jugador;

/**
 * Clase final que representa un Solar (propiedad edificable).
 */
public final class Solar extends Propiedad {

    private float alquilerBase;
    private float hipoteca;
    private float alquilerCasa;
    private float alquilerHotel;
    private float alquilerPiscina;
    private float alquilerPista;

    private float precioCasa;
    private float precioHotel;
    private float precioPiscina;
    private float precioPista;

    private int numCasas;
    private int hotel;
    private int piscina;
    private int pista;

    // Constructor
    public Solar(String nombre, int posicion, float valor, float alquilerBase) {
        super(nombre, posicion, valor);
        this.alquilerBase = alquilerBase;
        setHipoteca(valor / 2); // usa el atributo heredado de Propiedad
        this.numCasas = 0;
        this.hotel = 0;
        this.piscina = 0;
        this.pista = 0;
        this.tipo = "solar";
    }


    @Override
    public float alquiler(int tirada) {
        double total = alquilerBase;
        total += numCasas * alquilerCasa;
        if (hotel > 0) total += alquilerHotel;
        if (piscina > 0) total += alquilerPiscina;
        if (pista > 0) total += alquilerPista;
        return (float) total;
    }

    @Override
    public float valor() {
        double valorTotal = valor;
        valorTotal += numCasas * precioCasa;
        if (hotel > 0) valorTotal += precioHotel;
        if (piscina > 0) valorTotal += precioPiscina;
        if (pista > 0) valorTotal += precioPista;
        return (float) valorTotal;
    }


    public void construirCasas(Jugador jugador, int cantidad) {
        if (numCasas + cantidad > 4) {
            Juego.consola.imprimir("No puedes tener más de 4 casas.");
            return;
        }
        numCasas += cantidad;
        jugador.restarFortuna(precioCasa * cantidad);
        jugador.sumarGastos(precioCasa * cantidad);
        jugador.sumarDineroInvertido(precioCasa * cantidad);
    }

    public void construirHotel(Jugador jugador) {
        numCasas = 0; // Se eliminan las 4 casas
        hotel = 1;
        jugador.restarFortuna(precioHotel);
        jugador.sumarGastos(precioHotel);
        jugador.sumarDineroInvertido(precioHotel);
    }

    public void construirPiscina(Jugador jugador) {
        piscina = 1;
        jugador.restarFortuna(precioPiscina);
        jugador.sumarGastos(precioPiscina);
        jugador.sumarDineroInvertido(precioPiscina);
    }

    public void construirPista(Jugador jugador) {
        pista = 1;
        jugador.restarFortuna(precioPista);
        jugador.sumarGastos(precioPista);
        jugador.sumarDineroInvertido(precioPista);
    }

    // Métodos para vender edificaciones
    public void venderCasa(Jugador jugador, int cantidad) {
        if (numCasas < cantidad) {
            Juego.consola.imprimir("No tienes tantas casas para vender.");
            return;
        }
        for (int i = 0; i < cantidad; i++) {
            numCasas--;
            jugador.sumarFortuna(precioCasa / 2); // se recupera la mitad del coste
            Juego.consola.imprimir("Has vendido una casa en " + nombre +
                    " por " + (precioCasa / 2) + "€.");
        }
    }

    public void venderHotel(Jugador jugador) {
        if (hotel == 0) {
            Juego.consola.imprimir("No tienes hotel para vender.");
            return;
        }
        hotel = 0;
        jugador.sumarFortuna(precioHotel / 2);
        Juego.consola.imprimir("Has vendido el hotel en " + nombre +
                " por " + (precioHotel / 2) + "€.");
        // Regla clásica: al vender un hotel se devuelven 4 casas
        numCasas = 4;
    }

    public void venderPiscina(Jugador jugador) {
        if (piscina == 0) {
            Juego.consola.imprimir("No tienes piscina para vender.");
            return;
        }
        piscina = 0;
        jugador.sumarFortuna(precioPiscina / 2);
        Juego.consola.imprimir("Has vendido la piscina en " + nombre +
                " por " + (precioPiscina / 2) + "€.");
    }

    public void venderPista(Jugador jugador) {
        if (pista == 0) {
            Juego.consola.imprimir("No tienes pista para vender.");
            return;
        }
        pista = 0;
        jugador.sumarFortuna(precioPista / 2);
        Juego.consola.imprimir("Has vendido la pista de deporte en " + nombre +
                " por " + (precioPista / 2) + "€.");
    }


    // Getters y setters
    public float getAlquilerBase() { return alquilerBase; }
    public void setAlquilerBase(float a) { alquilerBase = a; }

    public float getHipoteca() { return  hipoteca; }
    public void setHipoteca(float h) { hipoteca = h; }

    public double getPrecioCasa() { return precioCasa; }
    public void setPrecioCasa(float p) { precioCasa = p; }

    public float getPrecioHotel() { return precioHotel; }
    public void setPrecioHotel(float p) { precioHotel = p; }

    public double getPrecioPiscina() { return precioPiscina; }
    public void setPrecioPiscina(float p) { precioPiscina = p; }

    public float getPrecioPista() { return precioPista; }
    public void setPrecioPista(float p) { precioPista = p; }

    public void setAlquilerCasa(float a) { alquilerCasa = a; }
    public void setAlquilerHotel(float a) { alquilerHotel = a; }
    public void setAlquilerPiscina(float a) { alquilerPiscina = a; }
    public void setAlquilerPista(float a) { alquilerPista = a; }

    public int getNumCasas() { return numCasas; }
    public boolean tieneHotel() { return hotel > 0; }
    public boolean tienePiscina() { return piscina > 0; }
    public boolean tienePista() { return pista > 0; }

}
