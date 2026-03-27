package edu.ycp.cs320.TBAG.model;

public class Item {
    private String name;
    private int effect;

    public Item(String name, int effect) {
        this.name = name;
        this.effect = effect;
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
}