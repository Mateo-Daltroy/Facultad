package TP1;

public class Ejer1 {
    public void incisoA(int a, int b) {
        System.out.println("Inciso A");
        for (int i = a; i <= b; i++) {
            System.out.println(i);
        }
    }

    public void incisoB(int a ,int b) {
        System.out.println("Inciso B");
        while (a <= b) {
            System.out.println(a);
            a++;
        }
    }

    public void incisoC(int a, int b) {
        System.out.println("Inciso C");

    }
}

/* Implementacion Main
    public static void main(String[] args) {
        Ejer1 ej = new Ejer1();
        ej.incisoA(3, 5);
        ej.incisoB(3, 5);
        ej.incisoA(4, 1);
        ej.incisoB(4, 1);
 */