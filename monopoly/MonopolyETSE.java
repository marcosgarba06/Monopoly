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
            System.out.println("Comandos disponibles:");
            System.out.println("  - 'listar jugadores' / 'jugadores'");
            System.out.println("  - 'jugador' (ver turno actual)");
            System.out.println("  - 'tirar dados'");
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






















