/*
d) Realice un programa que instancie un evento ocasional y una gira, cargando la
información necesaria. Luego, para ambos, imprima el costo e invoque al mensaje actuar.
 */
package Main;

/**
 *
 * @author mateo
 */
import PaqueteLectura.GeneradorAleatorio;
import PaqueteLectura.Lector;


//EventoOcasional(String motivo, String fecha, String contratante, String nomBanda, int dimF)
//Fecha(String ciudad, String dia)

public class Implementacion {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        GeneradorAleatorio.iniciar();
        Gira G;
        EventoOcasional E;
        Fecha F;
        String nomBanda; String motivo; String contratante;
        int dimF; int dimFechas; String nomGira; String fecha;
        String ciudad; String dia;
        System.out.print("Intoducir motivo del EventoOc: ");
        motivo = Lector.leerString();
        System.out.print("Intoducir contratante del EventoOc: ");
        contratante = Lector.leerString();
        System.out.print("Intoducir nombre de la banda del EventoOc: ");
        nomBanda = Lector.leerString();
        dimF = 3;
        fecha = GeneradorAleatorio.generarString(3);
        E = new EventoOcasional(motivo, fecha, contratante, nomBanda, dimF);
        for (int i = 0; i < dimF; i++){
            System.out.print("Ingresar cancion: ");
            E.agregarTema(Lector.leerString());
        }
        System.out.println(E.calcularCosto());
        E.actuar();
        System.out.println();
        
 //Gira(String nom, int dimF, String nomBanda, int dimFechas)
        System.out.print("Introducir nombre de la gira: ");
        nomGira = Lector.leerString();
        System.out.print("Introducir cantidad de fechas de la gira: ");
        dimFechas = Lector.leerInt();
        G = new Gira(nomGira, dimF, nomBanda, dimFechas);
        for (int i = 0; i < dimFechas; i++){
            System.out.print("Introducir ciudad de fecha " + (i+1) + ": ");
            ciudad = Lector.leerString();
            System.out.print("Introducir dia de fecha " + (i+1) + ": ");
            dia = Lector.leerString();
            F = new Fecha(ciudad, dia);
            G.agregarFecha(F);
        }
        for (int i = 0; i < dimF; i++){
            System.out.print("Ingresar cancion: ");
            G.agregarTema(Lector.leerString());
        }
        System.out.println(G.calcularCosto());
        G.actuar();
    }
}
