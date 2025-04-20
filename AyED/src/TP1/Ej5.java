package TP1;

public class Ej5 {
    public static int[] datosArrA(int[] arr) {
        int[] datos = new int[3];
        int max = -9999;
        int min = 9999;
        int tot = 0;
        for (int i : arr) {
            if (i < min) { min = i; }
            if (i > max) { max = i; }
            tot += i;
        }
        datos[0] = max;
        datos[1] = min;
        datos[2] = tot / arr.length;
        return(datos);
    }

    public static void datosArrB(int[] arr, Ej5_DetallesArreglo ret) {
        int max = -9999;
        int min = 9999;
        int tot = 0;
        for (int i : arr) {
            if (i < min) { min = i; }
            if (i > max) { max = i; }
            tot += i;
        }
        double prom = tot / arr.length;
        ret.setMaximo(max);
        ret.setPromedio(prom);
        ret.setMinimo(min);
    }
}
