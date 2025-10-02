package TP3;

import Testers.GeneralTree;

public class Navidad {
    private GeneralTree<Integer> arbol;

    public Navidad (GeneralTree<Integer> arbol) {
        this.arbol = arbol;
    }

    public String esAbetoNavidenio() {
        if (esAbetoNavidenio(this.arbol)) {
            return("Yes");
        } else {
            return("No");
        }
    }

    public boolean esAbetoNavidenio(GeneralTree<Integer> arbol) {
        boolean aux = true;
        if (arbol.hasChildren()) {
            int cont = 0;
            for (GeneralTree<Integer> child : arbol.getChildren()) {
                if (aux) {
                    if (child.isLeaf()) {
                        cont++;
                    } else {
                        aux = aux && esAbetoNavidenio(child);
                    }
                }
            }
            if (cont >= 3) {
                return(true);
            }
        }
        return(false);
    }
}
