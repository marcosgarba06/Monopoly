package monopoly;

import java.util.Scanner;
import partida.Jugador;
import partida.Dado;
import partida.Avatar;

public class MonopolyETSE {

    public static void main(String[] args) {
        // Creamos la banca y el tablero
        Jugador banca = new Jugador();
        // Creamos el menú (que gestiona jugadores)
        Tablero tablero = new Tablero(banca);
        Menu menu = new Menu(tablero);

        Scanner sc = new Scanner(System.in);

        System.out.println("Bienvenido a Monopoly ETSE.");

        boolean continuar = true;
        while (continuar) {
            System.out.println("Comandos disponibles: listar jugadores, jugador, acabar turno, ver tablero, describir jugador <nombre>, salir");
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


