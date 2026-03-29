package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.Monster;

public class CombatController {

    public String attackTurn(Player player, Monster monster) {
        StringBuilder combatLog = new StringBuilder();

        // 1. Player attacks
        monster.setHealth(monster.getHealth() - player.getDamage());
        combatLog.append("You hit the ").append(monster.getName())
                .append(" for ").append(player.getDamage()).append(" damage.\n");

        // 2. Check if monster dies
        if (monster.getHealth() <= 0) {
            combatLog.append("You defeated the ").append(monster.getName()).append("!\n");
            monster.setRoomID(0);
            return combatLog.toString();
        }

        // 3. Monster fights back based on its damageType
        if (monster.getDamageType().equals("sanity")) {
            // Do sanity damage
            player.setSanity(player.getSanity() - monster.getDamage());
            combatLog.append("The ").append(monster.getName())
                    .append(" drains your sanity by ").append(monster.getDamage()).append("!\n");
        } else {
            // Do normal health damage
            player.setHealth(player.getHealth() - monster.getDamage());
            combatLog.append("The ").append(monster.getName())
                    .append(" hits you for ").append(monster.getDamage()).append(" damage!\n");
        }

        // 4. Check if player dies OR goes insane
        if (player.getHealth() <= 0) {
            combatLog.append("You have died from your wounds...\n");
        } else if (player.getSanity() <= 0) {
            combatLog.append("You have completely lost your mind...\n");
        } else {
            combatLog.append("\nWhat will you do next? (Type 'attack')\n");
        }

        return combatLog.toString();
    }
}