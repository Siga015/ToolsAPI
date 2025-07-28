package siga.toolsapi.item;


import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ItemAction {

    void execute(Player player, ClickType type, ItemStack item);


}
