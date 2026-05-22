package org.example;

public class Piedra implements Opcion {
    public String enfrentoPiedra() {
        return "Empate";
    }

    public String enfrentoPapel() {
        return "Papel";
    }

    public String enfrentoTijera() {
        return "Piedra";
    }

    public String enfrentar(Opcion op) {
        return op.enfrentoPiedra();
    }
}
