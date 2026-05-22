package TP1.Ejercicio7;

import java.util.ArrayList;

public class IncisoJ {
    public static ArrayList<Integer> combinarOrdenado(ArrayList<Integer> list1, ArrayList<Integer> list2) {
        ArrayList<Integer> ret = new ArrayList<>();
        int cont1 = 0;
        int cont2 = 0;
        while (cont1 < list1.size() && cont2 < list2.size()) { //Combina hasta que termine una lista
            if (list1.get(cont1) <= list2.get(cont2)) {
                ret.addLast(list1.get(cont1));
                cont1++;
            } else {
                ret.addLast(list2.get(cont2));
                cont2++;
            }
        }
        if (cont1 == list1.size()) { //Se termino la lista 1
            for (int i = cont2; i < list2.size(); i++) {
                ret.addLast(list2.get(i));
            }
        } else { //Se termino la lista 2
            for (int i = cont1; i < list1.size(); i++) {
                ret.addLast(list1.get(i));
            }
        }
        return(ret);
    }
}
