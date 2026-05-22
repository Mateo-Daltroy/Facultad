/*
Implemente un programa que cargue un vector con a lo sumo 
20 partidos disputados en el campeonato. La información de 
cada partido se lee desde teclado hasta ingresar uno con
nombre de visitante “ZZZ” o alcanzar los 20 partidos. Luego de 
la carga:
- Para cada partido, armar e informar una representación 
  String del estilo:
{EQUIPO-LOCAL golesLocal VS EQUIPO-VISITANTE golesVisitante }
- Calcular e informar la cantidad de partidos que ganó River.
- Calcular e informar el total de goles que realizó Boca jugando 
de local.
 */
package tema2;

/**
 *
 * @author mateo
 */
import PaqueteLectura.Lector;

public class Ej05 {
    
    public static void main(String [] args){
        Partido [] vector = new Partido[20]; // Java inicializa en null
        String nomL; String nomV;
        int golL; int golV; int dimL = 0;
        int contRiv = 0; int golBoca = 0;
        System.out.print("Ingresar nombre de visitante: ");
        nomV = Lector.leerString();
        while ((dimL < 20) && !(nomV.equals("ZZZ"))){
            System.out.print("Ingresar nombre de local: ");
            nomL = Lector.leerString();
            System.out.print("Ingresar goles del visitante: ");
            golV = Lector.leerInt();
            System.out.print("Ingresar goles del local: ");
            golL = Lector.leerInt();
            vector[dimL] = new Partido(nomL, nomV, golL, golV);
            dimL++;
            if (dimL < 20) {
                System.out.print("Ingresar nombre de visitante: ");
                nomV = Lector.leerString();
            }
        }
        for (int i = 0; i < dimL; i++){
            if (vector[i].getGanador().equals("River"))
                contRiv++;
            if (vector[i].getLocal().equals("Boca"))
                golBoca += vector[i].getGolesLocal();
            System.out.println("{" + vector[i].getLocal().toUpperCase() + " " 
                    + vector[i].getGolesLocal() + "  VS  " +
                    vector[i].getVisitante().toUpperCase() + " " + vector[i].getGolesVisitante()
                    + "}");
        }
        System.out.println("River gano " + contRiv + " partidos");
        System.out.println("Boca hizo " + golBoca + " goles de local");
    }
}
