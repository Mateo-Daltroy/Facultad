package org.example;

public class Papel implements Opcion {
    public String enfrentoPiedra() {
        return "Papel";
    }

    public String enfrentoPapel() {
        return "Empate";
    }

    public String enfrentoTijera() {
        return "Tijera";
    }

    public String enfrentar(Opcion op) {
        return op.enfrentoPapel();
    }
}
