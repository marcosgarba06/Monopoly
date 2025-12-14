package monopoly.Interfaces;

import monopoly.excepciones.excepcionMonopoly;

public interface Comando {

    // Comandos de configuración
    void crearJugador(String nombre, String avatarElegido);
    void listarJugadores();
    void listarAvatares();

    // Comandos de juego
    void iniciarJuego() throws excepcionMonopoly;
    void indicarTurno(); // Comando 'jugador'
    void lanzarDados() throws excepcionMonopoly;
    void lanzarDadosForzados(int d1, int d2);
    void acabarTurno() throws excepcionMonopoly;
    void salirCarcel();

    // Comandos de información
    void verTablero(); // Extraído de 'ver tablero'
    void listarVenta(); // Comando 'listar enventa'
    void listarEdificaciones(); // Comando 'listar edificios'
    void listarEdificiosGrupo(String nombreGrupo);
    void descCasilla(String nombre);
    void descJugador(String nombre);
    void descAvatar(String ID);
    void mostrarEstadisticasUnJugador(String nombreJugador);
    void mostrarEstadisticasJuego();

    // Comandos de acción/transacción
    void comprarCasilla(String nombreCasilla) throws excepcionMonopoly;
    void hipotecarPropiedad(String nombreCasilla) throws excepcionMonopoly;
    void deshipotecarPropiedad(String nombreCasilla) throws excepcionMonopoly;
    void edificarPropiedad(String tipo) throws excepcionMonopoly;
    void venderPropiedad(String tipo, String nombreCasilla, int cantidad) throws excepcionMonopoly;
    void declararBancarrota(); // Extraído de 'declarar bancarrota'

    // TRATOS
    void proponerTrato(String nombreReceptor, String contenido) throws excepcionMonopoly; // Proponer trato
    void listarTratos(); //Metodo para listar los tratos
    void aceptarTrato(String IDTrato) throws excepcionMonopoly; // método para aceptar los tratos
    void eliminarTrato(String IDTrato) throws excepcionMonopoly;
}