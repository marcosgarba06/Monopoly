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
        //Menu menu = new Menu(tablero);
        menu.iniciarJuego();

        Scanner sc = new Scanner(System.in);

        System.out.println("Bienvenido a Monopoly ETSE.");

        boolean continuar = true;
        while (continuar) {
            System.out.println("Comandos disponibles: 'listar jugadores', 'jugador', 'acabar turno', 'ver tablero', 'describir <casilla>', 'describir jugador <nombre>', 'listar venta', 'tirar dado', 'salir carcel' o 'salir'");
            System.out.print("> ");
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






















