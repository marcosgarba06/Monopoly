package monopoly.excepciones;

public class excepTransPropHipotecada extends excepTransaccion {
    public excepTransPropHipotecada(String propiedad){
        super("No se puede hacer esta acción porque la propiedad" + propiedad + " está hipotecada.");
    }
}
