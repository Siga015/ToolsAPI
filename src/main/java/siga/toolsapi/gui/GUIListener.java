package siga.toolsapi.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import siga.toolsapi.PlayerData;

public class GUIListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();

        PlayerData data = PlayerData.getPlayer(player);
        if (data == null) return;

        GUI gui = data.getCurrentGUI();
        if (gui == null) return;


        if (!event.getView().getTopInventory().equals(gui.getInventory())) return;

        gui.handleClick(event);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();


        PlayerData data = PlayerData.getPlayer(player);
        if (data == null) return;

        GUI gui = data.getCurrentGUI();
        if (gui == null) return;

        if (!event.getView().getTopInventory().equals(gui.getInventory())) return;

        gui.handleDrag(event);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();


        PlayerData data = PlayerData.getPlayer(player);
        if (data == null) return;

        GUI gui = data.getCurrentGUI();
        if (gui == null) return;

        if (!event.getInventory().equals(gui.getInventory())) return;

        gui.handleClose(player);
    }
}