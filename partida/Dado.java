package partida;

public class Dado {
    // Atributo del dado
    private int valor;
    private static int suma;

    // Método para simular una tirada
    public int hacerTirada() {
        valor = (int)(Math.random() * 6) + 1; // Genera un número entre 1 y 6
        return valor;
    }


    public static void setSuma(int suma) {
        Dado.suma = suma;
    }

    // Getter para el valor del dado
    public int getValor() {
        return valor;
    }

    public static int getSuma() {
        return suma;
    }
}
