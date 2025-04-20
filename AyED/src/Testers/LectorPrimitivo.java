package Testers;

import java.util.Scanner;
import java.util.function.Supplier;

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
