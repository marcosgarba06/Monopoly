package monopoly.excepciones;

public class excepSinRecDinero extends excepSinRecursos {
    private float dineroNecesario;
    private float dineroSaldo;

    public excepSinRecDinero(float necesario, float saldo) {
        super(String.format("Para completar esta acción necesitas %.2f, pero tienes %.2f. " +
                "Te falta %.2f para poder hacer lo que quiere.", necesario, saldo, necesario - saldo));
        this.dineroNecesario = necesario;
        this.dineroSaldo = saldo;
    }

    public float  getDineroFaltante () {
        return dineroNecesario - dineroSaldo;
    }
}
