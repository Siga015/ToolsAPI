package siga.toolsapi.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import siga.toolsapi.util.ColorTranslator;

import java.util.List;

public abstract class ItemBase implements Listener {

    protected final JavaPlugin plugin;
    private final String itemID;
    private final Material material;
    private final NamespacedKey key;

    public ItemBase(JavaPlugin plugin, String itemID, Material material) {
        this.plugin = plugin;
        this.itemID = itemID;
        this.material = material;
        this.key = new NamespacedKey(plugin, itemID);
    }

    protected abstract String setName();
    protected abstract List<String> setLore();
    protected abstract ItemAction onClick();


    public ItemStack createItem() {
        ItemStack itemStack = new ItemStack(material);
        setCustomData(itemStack, "itemID", itemID);
        updateItemMeta(itemStack);
        return itemStack;
    }


    public void updateItemMeta(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorTranslator.translate(setName()));
            meta.setLore(setLore());
            item.setItemMeta(meta);
        }
    }



    public void setCustomData(ItemStack item, String key, String value) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(plugin, key), PersistentDataType.STRING, value);
            item.setItemMeta(meta);
        }
    }

    public void setCustomData(ItemStack item, String key, int value) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(plugin, key), PersistentDataType.INTEGER, value);
            item.setItemMeta(meta);
        }
    }


    public int getCustomData(ItemStack item, String key) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            NamespacedKey dataKey = new NamespacedKey(plugin, key);
            if (data.has(dataKey, PersistentDataType.INTEGER)) {
                return data.get(dataKey, PersistentDataType.INTEGER);
            }
        }
        return -1;
    }

    public String getStringCustomData(ItemStack item, String key) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            NamespacedKey dataKey = new NamespacedKey(plugin, key);
            if (data.has(dataKey, PersistentDataType.STRING)) {
                return data.get(dataKey, PersistentDataType.STRING);
            }
        }
        return null;
    }

    public boolean isApplicable(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;


        return itemID.equals(getStringCustomData(item, "itemID"));
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getItem() != null && isApplicable(event.getItem())) {

            ClickType clickType = null;

            if (event.getAction().toString().contains("RIGHT")) {
                clickType = ClickType.RIGHT;
            }
            else if (event.getAction().toString().contains("LEFT")) {
                clickType = ClickType.LEFT;
            }

            if (clickType == null) return;
            ItemAction action = onClick();
            action.execute(event.getPlayer(), clickType, event.getItem());
        }
    }

/*
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();

        if (isApplicable(item.getItemStack())) {
            ItemAction action = onClick();
            action.execute(event.getPlayer(), ClickType.DROP, event.getPlayer().getInventory().getItemInMainHand());
        }
    }

 */

}
