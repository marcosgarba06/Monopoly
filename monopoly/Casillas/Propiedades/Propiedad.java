package monopoly.Casillas.Propiedades;

import monopoly.Casillas.Casilla;
import monopoly.Juego;
import partida.Jugador;
import monopoly.Tablero;
import monopoly.Casillas.Grupo;

public abstract class Propiedad extends Casilla {

    protected float valor;
    protected Grupo grupo;
    protected float ingresosGenerados;
    protected boolean hipotecada;
    protected float hipoteca;

    // Constructor
    public Propiedad(String nombre, int posicion, float valor) {
        super(nombre, posicion);
        this.valor = valor;
        this.ingresosGenerados = 0;
        this.hipotecada = false;
        this.hipoteca = valor / 2; //valor por defecto
    }

    public void deshipotecar() {

        if (!hipotecada) {
            Juego.consola.imprimir("La propiedad no está hipotecada.");
            return;
        }
        if (duenho != null) {
            if (duenho.getFortuna() < hipoteca) {
                Juego.consola.imprimir("No tienes suficiente dinero para deshipotecar " + nombre);
                return;
            }
            duenho.restarFortuna(hipoteca);
            hipotecada = false;
            Juego.consola.imprimir(duenho.getNombre() + " paga " + (long)hipoteca +
                    "€ por deshipotecar " + nombre);
        } else {
            hipotecada = false;
        }
    }

    public void hipotecar() {
        if (hipotecada) {
            Juego.consola.imprimir("La propiedad ya está hipotecada.");
            return;
        }
        hipotecada = true;
        if (duenho != null) {
            duenho.sumarFortuna(hipoteca);
            Juego.consola.imprimir(duenho.getNombre() + " recibe " + (long)hipoteca +
                    "€ por la hipoteca de " + nombre);
        }
    }

    public boolean perteneceAJugador(Jugador jugador) {
        return duenho != null && duenho.equals(jugador);
    }

    public String describir() {
        return "Propiedad: " + getNombre() + " (valor " + getValor() + ")";
    }

    public abstract float alquiler(int tirada);

    public abstract float valor();

    public void comprar(Jugador jugador) {
        if (duenho != null) {
            Juego.consola.imprimir("Esta propiedad ya tiene dueño.");
            return;
        }

        if (jugador.getFortuna() < valor) {
            Juego.consola.imprimir("No tienes suficiente dinero para comprar esta propiedad.");
            return;
        }

        jugador.restarFortuna(valor);
        jugador.sumarDineroInvertido(valor);
        this.duenho = jugador;
        jugador.anhadirPropiedad(this);

        Juego.consola.imprimir(jugador.getNombre() + " ha comprado " + nombre +
                " por " + (long)valor + "€.");
    }

    public void sumarIngresos(float cantidad) {
        ingresosGenerados += cantidad;
    }

    public boolean estaEnVenta() {
        return duenho == null;
    }

    @Override
    public void evaluarCasilla(Jugador jugador, Tablero tablero) {
        incrementarVisita();

        if (duenho == null) {
            Juego.consola.imprimir("La propiedad " + nombre + " está en venta por " +
                    (long)valor + "€.");
            Juego.consola.imprimir("Puedes comprarla con el comando: comprar " + nombre);
        } else if (!duenho.equals(jugador)) {
            if (hipotecada) {
                Juego.consola.imprimir("La propiedad pertenece a " + duenho.getNombre() +
                        " pero está hipotecada. No pagas alquiler.");
            } else {
                float alquiler = alquiler(tablero.getUltimaTirada());
                Juego.consola.imprimir("Debes pagar " + (long)alquiler + "€ a " +
                        duenho.getNombre());

                if (jugador.getFortuna() >= alquiler) {
                    jugador.pagar(alquiler, duenho);
                    sumarIngresos(alquiler);
                    jugador.sumarPagoAlquiler(alquiler);
                    duenho.sumarCobroAlquiler(alquiler);
                } else {
                    // Gestionar deuda
                    Juego.consola.imprimir("No tienes suficiente dinero.");
                    jugador.setDeudaPendiente(alquiler);
                    jugador.setAcreedorDeuda(duenho);
                }
            }
        } else {
            Juego.consola.imprimir("Has caído en tu propia propiedad.");
        }
    }

    public void setHipoteca(float hipoteca) {
        this.hipoteca = hipoteca;
    }

    public float getHipoteca() {
        return hipoteca;
    }
    // Getters y setters
    public Jugador getDuenho() { return duenho; }
    public void setDuenho(Jugador duenho) { this.duenho = duenho; }
    public Grupo getGrupo() { return grupo; }
    public void setGrupo(Grupo grupo) { this.grupo = grupo; }
    public float getIngresosGenerados() { return ingresosGenerados; }
    public boolean estaHipotecada() { return hipotecada; }
    public float getValor() { return valor; }

}
