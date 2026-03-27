package edu.ycp.cs320.TBAG.model;

import java.util.ArrayList;

public class Inventory {
    private ArrayList<Item> items;

    public Inventory() {
        items = new ArrayList<>();
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public int getSize() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void displayItems() {
        if (items.isEmpty()) {
            System.out.println("Inventory is empty");
        } else {
            for (Item item : items) {
                System.out.println(
                        item.getName() + " (Effect: " + item.getEffect() + ")"
                );
            }
        }
    }
}