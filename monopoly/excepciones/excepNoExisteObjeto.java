package monopoly.excepciones;

public class excepNoExisteObjeto extends excepcionMonopoly {
    public excepNoExisteObjeto(String tipo, String objeto) {
        super("No existe " + tipo + " '" + objeto + "'");
    }
}
