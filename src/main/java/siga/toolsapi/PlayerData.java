package siga.toolsapi;

import org.bukkit.entity.Player;
import siga.toolsapi.gui.GUI;

import java.util.HashMap;

public class PlayerData {

    private GUI currentGui;
    private static final HashMap<Player, PlayerData> playerDataHashMap = new HashMap<>();

    public PlayerData(Player player) {
        playerDataHashMap.put(player, this);
    }

    public void setCurrentGUI(GUI currentGui) {
        this.currentGui = currentGui;
    }
    public GUI getCurrentGUI() {
        return currentGui;
    }

    public static PlayerData getPlayer(Player player) {
        if (!playerDataHashMap.containsKey(player)) {
            playerDataHashMap.put(player, new PlayerData(player));
        }
        return playerDataHashMap.get(player);
    }

}
