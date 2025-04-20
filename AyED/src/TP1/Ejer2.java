package TP1;

public class Ejer2 {
    public int[] arregloNum(int num) {
        int[] arr = new int[num];
        System.out.print("[");
        for (int i = 0; i < num; i++) {
            arr[i] = num * (i + 1);
            System.out.print(arr[i] + "; ");
        }
        System.out.print("]");
        return(arr);
    }
}

/*
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        int num = s.nextInt();
        Ejer2 e = new Ejer2();
        e.arregloNum(num);
    }
 */