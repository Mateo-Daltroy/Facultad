package TP1.Ejercicio7;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class ArrayListTest {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        ArrayList<Integer> lista1 = new ArrayList<>();
        ArrayList<Integer> lista2 = new ArrayList<>();
        Integer num = (Integer) s.nextInt();
        while (num != 0) {
            lista1.add(num);
            num = (Integer) s.nextInt();
        }
        num = (Integer) s.nextInt();
        while (num != 0) {
            lista2.add(num);
            num = (Integer) s.nextInt();
        }
        lista1 = IncisoJ.combinarOrdenado(lista1, lista2);
        lista1.stream().forEach(elem -> System.out.print(elem + ", "));
    }
}

/* Incisos A, B y C
{
        Scanner s = new Scanner(System.in);
        ArrayList<Integer> lista = new ArrayList<>(); // <- inciso A
        // LinkedList<Integer> lista = new LinkedList<>(); <- inciso B
        Integer num = (Integer) s.nextInt();
        while (num != 0) {
            lista.add(num);
            num = (Integer) s.nextInt();
        }
        // C: Distintas maneras de iterar la lista
        for (Integer i : lista) {
            System.out.print(i + ", ");
        }

        for (int i = 0; i < lista.size(); i++) {
            System.out.print(lista.get(i) + ", ");
        }

        lista.stream().forEach(numero -> System.out.print(numero + ", "));

        int i = 0;
        while (i < lista.size()) {
            System.out.print(lista.get(i) + ", ");
            i++;
        }
    }
 */

/* Inciso G
{
        IncisoG func = new IncisoG();
        LinkedList<Integer> lista = (LinkedList<Integer>) func.calcularSucesion(6);
        lista.stream().forEach(elem -> System.out.print(elem + ", "));
}
 */

/* Inciso H
{
        IncisoH algo = new IncisoH();
        ArrayList<Integer> lista = new ArrayList<>();
        Scanner s = new Scanner(System.in);
        Integer num = (Integer) s.nextInt();
        while (num != 0) {
            lista.add(num);
            num = (Integer) s.nextInt();
        }
        algo.invertirArrayList(lista);
        lista.stream().forEach(elem -> System.out.print(elem + ", "));
    }
 */

/* IncisoI
{
        IncisoI algo = new IncisoI();
        LinkedList<Integer> lista = new LinkedList<>();
        Scanner s = new Scanner(System.in);
        Integer num = (Integer) s.nextInt();
        while (num != 0) {
            lista.add(num);
            num = (Integer) s.nextInt();
        }
    }
 */

/* IncisoJ
{
        Scanner s = new Scanner(System.in);
        ArrayList<Integer> lista1 = new ArrayList<>();
        ArrayList<Integer> lista2 = new ArrayList<>();
        Integer num = (Integer) s.nextInt();
        while (num != 0) {
            lista1.add(num);
            num = (Integer) s.nextInt();
        }
        System.out.print("fin lista1");
        num = (Integer) s.nextInt();
        while (num != 0) {
            lista2.add(num);
            num = (Integer) s.nextInt();
        }
        System.out.print("fin lista2");
        lista1 = IncisoJ.combinarOrdenado(lista1, lista2);
        lista1.stream().forEach(elem -> System.out.print(elem + ", "));
    }
 */