package monopoly.Interfaces;

import monopoly.Interfaces.Consola;
import java.util.Scanner;

public class ConsolaNormal implements Consola {

    private Scanner sc;

    public ConsolaNormal() {
        this.sc = new Scanner(System.in);
    }

    @Override
    public void imprimir(String mensaje) {
        System.out.println(mensaje);
    }

    @Override
    public String leer(String descripcion) {
        // Imprimimos la descripción sin salto de línea para que el usuario escriba a continuación
        // (O con salto si prefieres, pero el prompt sugiere "Introduce nombre: " -> input)
        System.out.print(descripcion);
        return sc.nextLine();
    }
}