package monopoly;


import java.awt.*;
import java.util.Scanner;
import partida.Jugador;

public class MonopolyETSE {

    public static void main(String[] args) {
        Menu menu = new Menu(); //creamos el objeto menu que gestiona los comandos
        menu.iniciarPartida();// inicamos la partida

        Scanner sc = new Scanner(System.in);

        System.out.println("Bienvenido a Monopoly ETSE.");

        boolean continuar = true;
        //el bucle se repetira hasta que se introduzca el comando salir
        while (continuar) {
            System.out.println("  - 'listar jugadores' / 'jugadores'");
            System.out.println("  - 'jugador' (ver turno actual)");
            System.out.println("  - 'tirar dados' (tirada aleatoria)");
            System.out.println("  - 'tirar dados X+Y' o 'lanzar dados X+Y' (forzar valores 1-6)");
            System.out.println("  - 'acabar turno'");
            System.out.println("  - 'describir <casilla>'");
            System.out.println("  - 'ver tablero'");
            System.out.println("  - 'describir jugador <nombre>'");
            System.out.println("  - 'describir avatar X'");
            System.out.println("  - 'listar enventa' (casillas disponibles)");
            System.out.println("  - 'listar avatares'");
            System.out.println("  - 'comprar <casilla>'");
            System.out.println("  - 'hipotecar <casilla>'");
            System.out.println("  - 'deshipotecar <casilla>'");
            System.out.println("  - 'vender <tipo> <casilla> <cantidad>'");
            System.out.println("  - 'estadisticas <jugador>'");
            System.out.println("  - 'estadisticas juego'");
            System.out.println("  - 'comprar jugador <nombre>'");
            System.out.println("  - 'salir carcel'");
            System.out.println("  - 'listar edificios'");
            System.out.println("  - 'listar edificios <grupo>'");
            System.out.println("  - 'edificar <tipo>'");
            System.out.println("  - 'estadistas juego'");
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






















