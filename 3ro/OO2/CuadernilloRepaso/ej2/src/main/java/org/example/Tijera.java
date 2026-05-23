package org.example;

public class Tijera implements Opcion {

    public String enfrentoPiedra() {
        return "Piedra";
    }

    public String enfrentoPapel() {
        return "Tijera";
    }

    public String enfrentoTijera() {
        return "Empate";
    }

    public String enfrentar(Opcion op) {
        return op.enfrentoTijera();
    }
}
