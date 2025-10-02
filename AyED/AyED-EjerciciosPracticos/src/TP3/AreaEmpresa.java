package TP3;

import java.util.function.Supplier;
import java.util.Scanner;

public class AreaEmpresa {
    private String id;
    private int delay;

    public AreaEmpresa(String id, int del) {
        this.id = id;
        this.delay = del;
    }

    public int getDelay() {
        return delay;
    }

    public static Supplier<AreaEmpresa> leerArea() {
        return() -> {
            System.out.print("ID: ");
            Scanner s = new Scanner(System.in);
            String id = s.nextLine();
            System.out.print("Delay: ");
            int del = s.nextInt();
            return(new AreaEmpresa(id, del));
        };
    }

    @Override
    public String toString() {
        return(this.id);
    }
}
