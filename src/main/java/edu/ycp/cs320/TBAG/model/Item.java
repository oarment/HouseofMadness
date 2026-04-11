package edu.ycp.cs320.TBAG.model;

public class Item {
    private String name;
    private int effect;
    private String type;
    private Item equippedWeapon;
    private Item equippedUtility;

    public Item(String name, String type, int effect) {
        this.name = name;
        this.effect = effect;
        this.type = type;
    }

    public String getType() { return type; }

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

    public Item getEquippedWeapon() { return equippedWeapon;}

    public void setEquippedWeapon(Item weapon) { this.equippedWeapon = weapon ;}

    public Item getEquippedUtility() { return equippedUtility;}

    public void setEquippedUtility(Item utility) { this.equippedUtility = utility ;}
}