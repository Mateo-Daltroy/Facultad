package TP1;

public class Ej3_Test {
    public static void Main(String[] args) {
        Ej3_Estudiante[] arrEst = new Ej3_Estudiante[2];
        Ej3_Profesor[] arrProf = new Ej3_Profesor[3];
        arrEst[0] = new Ej3_Estudiante();
        arrEst[0].setNom("pato");
        arrEst[0].setAp("Rivero");
        arrEst[0].setDireccion("Berisso");
        arrEst[0].setComision(2);
        arrEst[0].setEmail("patoputo.txt");
        arrEst[1] = new Ej3_Estudiante();
        arrEst[1].setNom("joaquin");
        arrEst[1].setAp("retamoso");
        arrEst[1].setDireccion("Bersio");
        arrEst[1].setComision(2);
        arrEst[1].setEmail("joacogil");
        System.out.println(arrEst[0].tusDatos());
        System.out.println(arrEst[1].tusDatos());
    }
}
