package edu.ycp.cs320.TBAG.model;

public class Item {
    private String name;
    private int effect;
    private int ID;

    public Item(String name, int effect, int ID) {
        this.name = name;
        this.effect = effect;
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public int getEffect() {   // 🔥 FIXED naming
        return effect;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEffect(int effect) {   // 🔥 FIXED naming
        this.effect = effect;
    }

    public void setID(int ID) {this.ID = ID;}

    public int getID() {return ID;}

}