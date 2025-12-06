package partida;

import java.util.ArrayList;
import monopoly.Casillas.Casilla;
import java.util.List;
import java.util.Random;
import monopoly.Casillas.Grupo;
import monopoly.Casillas.Propiedades.Propiedad;
import monopoly.Edificios.Edificacion;

import monopoly.*;

public class Jugador {

    //Atributos:
    private String nombre;
    private int cartasSalirCarcel = 0;
    private Avatar avatar;
    private boolean enCarcel;
    private int tiradasCarcel;
    private int vecesEnLaCarcel;
    private int vueltas;
    private ArrayList<Propiedad> propiedades;
    private boolean bancarrota;
    private boolean tieneCartaSalirCarcel = false;
    private boolean activo = true;
    private Tablero tablero;

    // Respecto a la economía de la partida
    private float fortuna;
    private float gastos;
    private float dineroInvertido = 0;
    private float pagoTasasEImpuestos = 0;
    private float pagoDeAlquileres = 0;
    private float cobroDeAlquileres = 0;
    private float pasarPorCasillaDeSalida = 0;
    private float premiosInversionesOBote = 0;

    private float deudaPendiente = 0;
    private Jugador acreedorDeuda = null;

    public float getDeudaPendiente() { return deudaPendiente; }
    public void setDeudaPendiente(float deuda) { this.deudaPendiente = deuda; }
    public Jugador getAcreedorDeuda() { return acreedorDeuda; }
    public void setAcreedorDeuda(Jugador acreedor) { this.acreedorDeuda = acreedor; }


    private ArrayList<Trato> tratosRecibidos = new ArrayList<>();
    private ArrayList<Trato> tratosPropuestos = new ArrayList<>();


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


    public void sumarFortuna(float valor) {
        this.fortuna += valor;
    }

    public void restarFortuna(float cantidad) {
        this.fortuna -= cantidad;
        if (fortuna < 0) { //
            this.bancarrota = true;
            Juego.consola.imprimir(nombre + " ha caído en bancarrota.");
        }
    }

    public void sumarGastos(float valor) {
        this.gastos += valor;
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


    public void anadirPropiedad(Casilla casilla) {
        if (!propiedades.contains(casilla)) {
            this.propiedades.add((Propiedad) casilla);
        }
    }

    public void eliminarPropiedad(Casilla casilla) {
        this.propiedades.remove(casilla);
    }


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

        Casilla carcel = tablero.encontrarCasilla("Carcel");
        if (carcel == null) {
            Juego.consola.imprimir("Error: no se encontró la casilla de Cárcel.");
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
        avatar.getJugador().incrementarVecesEnCarcel();

        Juego.consola.imprimir(nombre + " ha sido enviado a la cárcel.");
    }

    // Métodos de pago

    public void pagar(float cantidad, Jugador receptor) {
        if (fortuna >= cantidad) {
            this.fortuna -= cantidad;
            this.gastos += cantidad;
            receptor.sumarFortuna(cantidad);
        } else {
            Juego.consola.imprimir(nombre + " no tiene suficiente dinero para pagar " + cantidad);
            // Aquí se podría implementar lógica de bancarrota, hipoteca, etc.
        }
    }
//
//    public void cobrar(float cantidad, Jugador pagador) {
//        this.fortuna += cantidad;
//        pagador.restarFortuna(cantidad);
//        pagador.sumarGastos(cantidad);
//    }


    public void anhadirPropiedad(Casilla c) {
        propiedades.add((Propiedad) c);
    }

    // En Jugador.java
    public void declararBancarrota(Jugador acreedor) {
        Juego.consola.imprimir("\n" + this.nombre + " se ha declarado en BANCARROTA.");

        if (acreedor != null) {
            // Deuda con otro jugador
            Juego.consola.imprimir("Todas las propiedades pasan a " + acreedor.getNombre());

            for (Propiedad propiedad : new ArrayList<>(propiedades)) {
                if (propiedad.estaHipotecada()) {
                    propiedad.deshipotecar();
                }
                propiedad.setDuenho(acreedor);
                acreedor.anadirPropiedad(propiedad);
            }




            // Transferir edificaciones
            for (Edificacion edif : new ArrayList<>(edificaciones)) {
                acreedor.agregarEdificacion(edif);
            }

        } else {
            // Deuda con la banca (impuestos, multas, etc.)
            Juego.consola.imprimir("Las propiedades vuelven a la banca (quedan en venta).");

            for (Propiedad propiedad : new ArrayList<>(propiedades)) {
                if (propiedad.estaHipotecada()) {
                    propiedad.deshipotecar();
                }
                propiedad.setDuenho(null);  // vuelve a estar en venta
            }


            // Las edificaciones se destruyen (no se transfieren)
        }

        propiedades.clear();
        edificaciones.clear();
        fortuna = 0;
        activo = false;
        bancarrota = true;
        deudaPendiente = 0;
        acreedorDeuda = null;
    }

    public boolean estaActivo() {
        return activo;
    }

    public void anadirPropiedad(Propiedad propiedad) {
        if (!propiedades.contains(propiedad)) {
            propiedades.add(propiedad);
        }
    }

    public void eliminarPropiedad(Propiedad propiedad) {
        propiedades.remove(propiedad);
    }

    public ArrayList<Propiedad> getPropiedades() {
        return propiedades;
    }

    // Métodos para gestionar el dinero más detallado (para estadísticas)
    public void sumarDineroInvertido(float cantidad) {
        dineroInvertido += cantidad;}
    public void sumarPagoTasasEImpuestos(float cantidad) {
        pagoTasasEImpuestos += cantidad;}
    public void sumarPagoAlquiler(float cantidad) {
        pagoDeAlquileres += cantidad;}
    public void sumarCobroAlquiler(float cantidad) {
        cobroDeAlquileres += cantidad;}
    public void sumarSalida(float cantidad) {
        pasarPorCasillaDeSalida += cantidad;}
    public void sumarPremios(float cantidad) {
        premiosInversionesOBote += cantidad;}
    public void incrementarVecesEnCarcel() {
        vecesEnLaCarcel++; }



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
    public boolean isBancarrota() {
        return bancarrota;
    }
    public boolean tieneCartaSalirCarcel() {
        return tieneCartaSalirCarcel;
    }
    public boolean getTieneCartaSalirCarcel() {
        return tieneCartaSalirCarcel;
    }
    public float getPremiosInversionesOBote() {return premiosInversionesOBote;}
    public float getPasarPorCasillaDeSalida() {return pasarPorCasillaDeSalida;}
    public float getCobroDeAlquileres() {return cobroDeAlquileres;}
    public float getPagoDeAlquileres() {return pagoDeAlquileres;}
    public float getPagoTasasEImpuestos() {return pagoTasasEImpuestos;}
    public float getDineroInvertido() {return dineroInvertido;}
    public int getVecesEnLaCarcel() {return vecesEnLaCarcel;}


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

    public void setActivo(boolean estado) {
        this.activo = estado;
    }
    public void setTablero(Tablero t) {
        this.tablero = t;
    }

    public boolean tieneDeudaPendiente() {
        return deudaPendiente > 0;
    }

    public boolean poseeGrupoCompleto(Casilla casilla, Tablero tablero) {

        //  Verificar que la casilla tenga grupo
        if (casilla.getGrupo() == null) {
            return false;
        }
        String nombreGrupo = casilla.getGrupo().getNombre();
        int total = tablero.getCantidadCasillasGrupo(nombreGrupo);
        int propias = 0;
        for (Casilla c : propiedades) {
            // Verificar que cada propiedad tenga grupo antes de comparar
            if (c.getGrupo() != null && c.getGrupo().getNombre().equalsIgnoreCase(nombreGrupo)) {

                propias++;

            }
        }
        return propias == total;

    }

    public void setFortuna(float nuevaFortuna) {
        if (nuevaFortuna < 0) {
            this.fortuna = 0;
        } else {
            this.fortuna = nuevaFortuna;
        }
    }

    public void incrementarVueltas() {
        this.vueltas++;
    }

    ///  /////////// EDIFICACIONES DE JUGADOR
    private List<Edificacion> edificaciones = new ArrayList<>();

    public void agregarEdificacion(Edificacion e) {
        edificaciones.add(e);
    }

    public List<Edificacion> getEdificaciones() {
        return edificaciones;
    }

    public void agregarTratoRecibido(Trato trato) {
        tratosRecibidos.add(trato);
    }

    public void agregarTratoPropuesto(Trato trato) {
        tratosPropuestos.add(trato);
    }

    public void eliminarTratoRecibido(Trato trato) {
        tratosRecibidos.remove(trato);
    }

    public void eliminarTratoPropuesto(Trato trato) {
        tratosPropuestos.remove(trato);
    }

    public ArrayList<Trato> getTratosRecibidos() {
        return tratosRecibidos;
    }

    public ArrayList<Trato> getTratosPropuestos() {
        return tratosPropuestos;
    }

}