package TP1.Ejercicio7;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

public class incisoF {
    public static void main(String[] args) {
        ArrayList<Integer> lista = lectura();
        System.out.println(esCapicua(lista));
    }

    public static ArrayList<Integer> lectura () {
        Scanner s = new Scanner(System.in);
        ArrayList<Integer> lista = new ArrayList<>();
        Integer numero = (Integer) s.nextInt();
        while (numero != 0) {
            lista.add(numero);
            numero = (Integer) s.nextInt();
        }
        return (lista);
    }

    public static boolean esCapicua (List<Integer> lista) {
        boolean aux = true;
        int i = 0;
        while (aux && i < (lista.size()/2)) {
            if (lista.get(i) != lista.get(lista.size() - (i+1))) {
                aux = false;
            }
            i++;
        }
        return(aux);
   }
}
