package TP1;

import TP1.Ejercicio8.Queue;

import java.util.ArrayList;

public class Ej9_BalanceChecker {
    public static boolean estaBalanceado(String s) {
        ArrayList<Character> stack = new ArrayList<>();
        boolean balanceado = true;
        int i = 0;
        while (balanceado && (i < s.length())) {
            char evaluado = s.charAt(i);
            if (esApertura(evaluado)) {
                stack.addLast(evaluado);
            } else {
                char comp = stack.removeLast();
                balanceado = sonContrapartes(comp, evaluado);
            }
            i++;
        }
        return(balanceado);
    }

    private static boolean sonContrapartes(char Ap, char Ce) {
        boolean aux = false;
        if ((Ap == '{') && (Ce == '}')) {aux = true;}
        else if ((Ap == '[') && (Ce == ']')) {aux = true;}
        else if ((Ap == '(') && (Ce == ')')) {aux = true;}
        return(aux);
    }

    private static boolean esApertura(char X) {
        if ((X == '(') || (X == '[') || (X == '{')) {
            return(true);
        }
        return(false);
    }
}
