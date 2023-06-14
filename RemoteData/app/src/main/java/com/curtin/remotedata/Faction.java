package com.curtin.remotedata;

public class Faction {
    private String name;
    private int strength;
    private String relationship;


    public Faction(String name, int strength, String relationship) {
        this.name = name;
        this.strength = strength;
        this.relationship = relationship;
    }

    public String toString() {
        return "Faction: " + name + ". Strength: " + strength + ". Relationship: " + relationship + ".";
    }
}
