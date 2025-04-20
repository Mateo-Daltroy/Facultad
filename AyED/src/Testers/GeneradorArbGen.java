package Testers;

import java.util.Scanner;
import java.util.function.Supplier;

public class GeneradorArbGen implements ArbolGenerator{
    private static Scanner s = new Scanner(System.in);

    public static <T> GeneralTree<T> generarDesordenado(Supplier<T> generador) {
        GeneralTree<T> nodo = new GeneralTree<>();
        System.out.println("Ingresar dato: ");
        T dato = generador.get();
        nodo.setData(dato);
        System.out.print("Sumarle hijo a " + dato.toString() + "? S/n: ");
        String opt = s.nextLine();
        while (!opt.equalsIgnoreCase("n")) {
            System.out.print("Ingresando a hijo");
            nodo.addChild(generarDesordenado(generador));
            System.out.print("Sumarle hijo a " + dato.toString() + "? S/n: ");
            opt = s.nextLine();
        }
        return(nodo);
    }
}
