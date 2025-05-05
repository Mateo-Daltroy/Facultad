package Testers;

import java.util.Scanner;
import java.util.function.Supplier;

/*
LectorPrimitivo es una clase hecha para proveer los Suppliers necesarios para usar los generadores de arbol
con las clases Wrapper basicas sin tener que implementar nada, si se quiere implementar un lector para una
clase programada, deberia seguir el mismo formato los que hay aca para asegurar su funcionamiento
 */
public class LectorPrimitivo {
    private static Scanner s = new Scanner(System.in);

    public static Supplier<Integer> leerInteger() {
        return () -> {
            System.out.print("Ingrese un entero: ");
            return s.nextInt();
        };
    }

    public static Supplier<Double> leerDouble() {
        return () -> {
            System.out.print("Ingrese un decimal: ");
            return s.nextDouble();
        };
    }

    public static Supplier<String> leerString() {
        return () -> {
            System.out.print("Ingrese una cadena: ");
            return s.nextLine();
        };
    }
}
