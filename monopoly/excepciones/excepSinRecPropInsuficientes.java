package monopoly.excepciones;

public class excepSinRecPropInsuficientes extends excepSinRecursos {
    public excepSinRecPropInsuficientes(String accion) {
        super("Acción de " + accion + " no permitida, porque no hay suficientes propiedades");
    }
}
