package TP3;

import Testers.GeneralTree;
import java.util.List;
import java.util.ArrayList;

public class Caminos {
    private GeneralTree<Integer> arbol;

    public Caminos (GeneralTree<Integer> arb) {
        this.arbol = arb;
    }

    public List<Integer> caminoAHojaMasLejana() {
        return(caminoAHojaMasLejana(this.arbol));
    }

    public ArrayList<Integer> caminoAHojaMasLejana(GeneralTree<Integer> arbol) {
        ArrayList<Integer> ret = new ArrayList<>();
        if (arbol.hasChildren()) {
            int longAct = -1;
            for (GeneralTree<Integer> child : arbol.getChildren()) {
                ArrayList<Integer> listaHijo = caminoAHojaMasLejana(child);
                if (listaHijo.size() > longAct) {
                    ret = listaHijo;
                    longAct = listaHijo.size();
                }
            }
        }
        ret.addFirst(arbol.getData());
        return(ret);
    }
}
