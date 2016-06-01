package com.ellsworthcreations.vbtp20;

public class Constraint {
    public Player player1;
    public Player player2;
    public Boolean split = false;

    public Constraint(Player p1, Player p2, Boolean split) {
        player1 = p1;
        player2 = p2;
        this.split = split;
    }

    public Boolean equals(Constraint c) {
        if(c.player1.equals(this.player1) && c.player2.equals(this.player2) && c.split == this.split)
        { return true; }
        else
        { return false; }
    }

    public String toString() {
        if(this.split)
        { return player1.getName() + " will be separated from " + player2.getName(); }
        else
        { return player1.getName() + " will be paired with " + player2.getName(); }
    }
}
