package monopoly;

import java.util.Scanner;
import partida.Jugador;
import partida.Dado;
import partida.Avatar;

public class MonopolyETSE {

    public static void main(String[] args) {
        // Creamos la banca y el tablero
        Jugador banca = new Jugador();
        Tablero tablero = new Tablero(banca);

        // Creamos el menú (que gestiona jugadores)
        Menu menu = new Menu();

        Scanner sc = new Scanner(System.in);

        System.out.println("Bienvenido a Monopoly ETSE.");

        boolean continuar = true;
        while (continuar) {
        System.out.println("Comandos disponibles: listar jugadores, ver tablero, salir");
            System.out.print("> ");
            String comando = sc.nextLine().trim();

            switch (comando.toLowerCase()) {
                case "listar jugadores":
                    menu
                            .listarJugadores();
                    break;
                case "ver tablero":
                    System.out.println(tablero);
                    break;
                case "salir":
                    continuar = false;
                    System.out.println("Saliendo del juego...");
                    break;
                default:
                    System.out.println("Comando no reconocido. Prueba con 'listar jugadores', 'ver tablero' o 'salir'.");
            }
        }

        sc.close();
    }
}
