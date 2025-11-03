import monopoly.Casilla;
import partida.Jugador;

public class Edificacion {
        private String id;
        private Jugador propietario;
        private Casilla casilla;
        private String tipo; // casa, hotel, piscina, pista
        private float coste;

        public Edificacion(String id, Jugador propietario, Casilla casilla, String tipo, float coste) {
            this.id = id;
            this.propietario = propietario;
            this.casilla = casilla;
            this.tipo = tipo;
            this.coste = coste;
        }

        public String getId() { return id; }
        public Jugador getPropietario() { return propietario; }
        public Casilla getCasilla() { return casilla; }
        public String getTipo() { return tipo; }
        public float getCoste() { return coste; }


}
