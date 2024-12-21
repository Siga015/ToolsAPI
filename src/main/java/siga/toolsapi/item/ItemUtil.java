package siga.toolsapi.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import siga.toolsapi.item.version.MetaHandler_1_13;

public class ItemUtil {

    private final JavaPlugin plugin;
    private final MetaHandler_1_13 handler;


    public ItemUtil(JavaPlugin plugin) {
        this.plugin = plugin;
        handler = new MetaHandler_1_13(plugin);
    }


    public String getItemID(ItemStack item) {
        return handler.getPersistentData(item.getItemMeta(), "ITEM_ID");
    }



}
