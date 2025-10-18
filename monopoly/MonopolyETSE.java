package monopoly;


import java.awt.*;
import java.util.Scanner;
import partida.Jugador;

public class MonopolyETSE {

    public static void main(String[] args) {
        Menu menu = new Menu();
        //Llamada al metodo para iniciar la partida
        menu.iniciarPartida();

        Jugador banca = new Jugador();
        // Creamos el menú (que gestiona jugadores)
        Tablero tablero = new Tablero(banca);
        menu.iniciarJuego();

        Scanner sc = new Scanner(System.in);

        System.out.println("Bienvenido a Monopoly ETSE.");

        boolean continuar = true;
        while (continuar) {
            System.out.println("Comandos disponibles:");
            System.out.println("  - 'listar jugadores' / 'jugadores'");
            System.out.println("  - 'jugador' (ver turno actual)");
            System.out.println("  - 'tirar dado'");
            System.out.println("  - 'acabar turno'");
            System.out.println("  - 'ver tablero'");
            System.out.println("  - 'describir <casilla>'");
            System.out.println("  - 'describir jugador <nombre>'");
            System.out.println("  - 'describir avatarX'");
            System.out.println("  - 'listar venta' (casillas disponibles)");
            System.out.println("  - 'listar avatares'");
            System.out.println("  - 'comprar <casilla>'");
            System.out.println("  - 'hipotecar' (solo si estás en bancarrota)");
            System.out.println("  - 'edificar <solar> <tipo> [cantidad]' (casa, hotel, piscina, pista)");
            System.out.println("  - 'salir carcel'");
            System.out.println("  - 'comandos <ruta/al/archivo.txt>' (ejecutar comandos desde archivo)");
            System.out.println("  - 'salir' (cerrar el juego)");
            String comando = sc.nextLine().trim();

            if (comando.equalsIgnoreCase("salir")) {
                System.out.println("Saliendo del juego...");
                continuar = false;
            } else {
                menu.analizarComando(comando);
            }
        }
        sc.close(); // Cerramos el scanner al final del programa
    }
}






















