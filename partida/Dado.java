package partida;

public class Dado {
    // Atributos para los valores de los dos dados
    private int valor1;
    private int valor2;

    // Método para lanzar ambos dados
    //se crean 2 valores para los 2 dados y se hace random
    public int[] hacerTirada() {
        valor1 = (int) (Math.random() * 6) + 1;
        valor2 = (int) (Math.random() * 6) + 1;
        return new int[]{valor1, valor2};
    }

    //Comprobar si son dobles
    public boolean sonDobles() {
        return valor1 == valor2;
    }

    //Obtener el total
    public int suma() {
        return valor1 + valor2;
    }

    //Getters de cada valor
    public int getValor1() {
        return valor1;
    }
    public int getValor2() {
        return valor2;
    }
}
