package siga.toolsapi.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import siga.toolsapi.api.ToolsAPI;

@Deprecated
public class ItemModifier {

    private JavaPlugin plugin;

    public ItemModifier(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    public <T extends ItemBase> T getItemBase(ItemStack item, Class<T> clazz) {
        if (item == null) return null;

        ItemBase base = ToolsAPI.getInstance().getItemManager().getItemBase(item, plugin);

        if (base == null) return null;

        if (clazz.isInstance(base)) {
            return clazz.cast(base);
        }

        return null;
    }


    public String getItemUUID(ItemStack item) {
        ItemBase base = ToolsAPI.getInstance().getItemManager().getItemBase(item, plugin);
        if (base == null) return null;

        return base.getItemUUID(item);
    }



}
