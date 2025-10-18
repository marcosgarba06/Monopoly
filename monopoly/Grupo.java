package monopoly;

import partida.*;
import java.util.ArrayList;


public class Grupo {

    //Atributos
    private ArrayList<Casilla> miembros; //Casillas miembros del grupo.
    private String colorGrupo; //Color del grupo
    private int numCasillas; //Número de casillas del grupo.

    //Constructor vacío.
    public Grupo(String colorGrupo) {
        this.miembros = new ArrayList<>();
        this.colorGrupo = colorGrupo;
        this.numCasillas = 0;
    }

    public String getColorGrupo() {
        return colorGrupo;
    }

    /*Constructor para cuando el grupo está formado por DOS CASILLAS:
    * Requiere como parámetros las dos casillas miembro y el color del grupo.
     */
    public Grupo(Casilla cas1, Casilla cas2, String colorGrupo) {
        this.miembros = new ArrayList<>();
        this.miembros.add(cas1);
        this.miembros.add(cas2);
        this.colorGrupo = colorGrupo;
        this.numCasillas = 2;
    }

    /*Constructor para cuando el grupo está formado por TRES CASILLAS:
    * Requiere como parámetros las tres casillas miembro y el color del grupo.
     */
    public Grupo(Casilla cas1, Casilla cas2, Casilla cas3, String colorGrupo) {
        this.miembros = new ArrayList<>();
        this.miembros.add(cas1);
        this.miembros.add(cas2);
        this.miembros.add(cas3);
        this.colorGrupo = colorGrupo;
        this.numCasillas = 3;
    }

    /* Método que añade una casilla al array de casillas miembro de un grupo.
    * Parámetro: casilla que se quiere añadir.
     */
    public void anhadirCasilla(Casilla miembro) {
        if (miembros.size() <= 4) {
            this.miembros.add(miembro);
            this.numCasillas = miembros.size();
        }else {
            System.out.println("No se pudo insertar la casilla al grupo porque ya hay 3 elementos\n");
        }
    }

    /*Método que comprueba si el jugador pasado tiene en su haber todas las casillas del grupo:
    * Parámetro: jugador que se quiere evaluar.
    * Valor devuelto: true si es dueño de todas las casillas del grupo, false en otro caso.
     */

    //TERMINAR CUANDO ESTE HECHO SI UN JUGADOR ES DUEÑO DE UNA CASILLA
   public boolean esDuenhoGrupo(Jugador jugador) {
       for (Casilla casilla : miembros) {
           if (casilla.getDuenho() == null || !casilla.getDuenho().equals(jugador)) {
               return false;
           }
       }
       return true;
    }

    //getters
    public ArrayList<Casilla> getMiembros() {
        return miembros;
    }


    public String getNombre() {
        return colorGrupo;
    }

    public int getNumCasillas() {
        return numCasillas;
    }

    public boolean perteneceEnteramenteA(Jugador jugador) {
        for (Casilla c : miembros) {
            if (c.getDuenho() != jugador) return false;
        }
        return true;
   }
   public String toString() {
        return colorGrupo;
   }

}
