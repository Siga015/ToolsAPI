package siga.toolsapi.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import siga.toolsapi.item.version.MetaHandler;
import siga.toolsapi.item.version.MetaHandler_1_12;
import siga.toolsapi.item.version.MetaHandler_1_13;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class ItemBase implements Listener {

    protected final JavaPlugin plugin;
    private final String itemID;
    private final Material material;
    private final MetaHandler handler;

    private final List<String> lore;


    public ItemBase(JavaPlugin plugin, String itemID, Material material) {
        this.plugin = plugin;
        this.itemID = itemID;
        this.material = material;
        this.lore = setLore();

        this.handler = isModernVersion() ? new MetaHandler_1_13(plugin) : new MetaHandler_1_12();
    }


    public boolean isInteractable(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();

        String storedID = handler.getPersistentData(meta, "ITEM_ID");

        return itemID.equals(storedID);
    }

    public ItemStack createItem() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();

        assert meta != null;
        meta.setDisplayName(ColorTranslator.translate(setName()));
        if (isModernVersion() && setCustomModel() > 0) meta.setCustomModelData(setCustomModel());
        meta.setLore(lore);

        for (ItemFlag flag : ItemFlag.values()) {
            meta.addItemFlags(flag);
        }

        assignAnnotatedElements(meta);

        handler.setPersistentData(meta, "ITEM_ID", itemID);

        customizeMeta(meta);

        itemStack.setItemMeta(meta);
        return itemStack;
    }


    public String getItemID() {
        return itemID;
    }

    public void setCustomData(ItemStack item, String key, String value) {
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        handler.setPersistentData(meta, key, value);
        item.setItemMeta(meta);
    }

    public String getCustomData(ItemStack item, String key) {
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        return handler.getPersistentData(meta, key);
    }


    protected abstract String setName();
    protected abstract int setCustomModel();
    protected abstract List<String> setLore();
    protected abstract void customizeMeta(ItemMeta meta);
    protected abstract ItemAction onClick();


    private void assignAnnotatedElements(ItemMeta meta) {
        Class<?> currentClass = this.getClass();
        while (currentClass != null) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(ItemElement.class)) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(this);
                        if (value != null) {
                            String key = field.getName();
                            handler.setPersistentData(meta, key, value.toString());
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }


    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (onClick() == null) return;

        ItemStack item = event.getItem();

        if (item != null && isInteractable(item)) {
            ClickType clickType = null;

            if (event.getAction().toString().contains("RIGHT")) {
                clickType = ClickType.RIGHT;
            } else if (event.getAction().toString().contains("LEFT")) {
                clickType = ClickType.LEFT;
            }

            if (clickType == null) return;
            ItemAction action = onClick();
            action.execute(event.getPlayer(), clickType, item);
        }
    }


    /*
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();

        if (isInteractable(item.getItemStack())) {
            ItemAction action = onClick();
            action.execute(event.getPlayer(), ClickType.DROP, item.getItemStack());
        }
    }

     */

    private boolean isModernVersion() {
        try {
            Class.forName("org.bukkit.persistence.PersistentDataContainer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }



    private static class ColorTranslator {

        public static String translate(String string) {
            return ChatColor.translateAlternateColorCodes('&', string);
        }

    }
}
