package monopoly;


import java.awt.*;
import java.util.Scanner;
import partida.Jugador;

public class MonopolyETSE {

    public static void main(String[] args) {
        Menu menu = new Menu(); //creamos el objeto menu que gestiona los comandos
        menu.iniciarPartida();// inicamos la partida

        Jugador banca = new Jugador(); //se crea el jugado de la banca
        Tablero tablero = new Tablero(banca); // se inicializa el tablero

        Scanner sc = new Scanner(System.in);

        System.out.println("Bienvenido a Monopoly ETSE.");

        boolean continuar = true;
        //el bucle se repetira hasta que se introduzca el comando salir
        while (continuar) {
            menu.menuComandos();
            String comando = sc.nextLine().trim(); // se lee lo que escribe el usuario y se limbia espacion innecesarios

            if (comando.equalsIgnoreCase("salir")) {
                System.out.println("Saliendo del juego...");
                continuar = false;
            } else {
                menu.analizarComando(comando);
            }
        }
        sc.close();
    }
}






















