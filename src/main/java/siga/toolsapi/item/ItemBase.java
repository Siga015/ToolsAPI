package siga.toolsapi.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import siga.toolsapi.item.version.MetaHandler;
import siga.toolsapi.item.version.MetaHandler_1_12;
import siga.toolsapi.item.version.MetaHandler_1_13;
import siga.toolsapi.util.CustomTag;

import java.lang.reflect.Field;
import java.util.List;

public abstract class ItemBase implements Listener {

    protected final JavaPlugin plugin;
    private final String itemID;
    private final Material material;
    private final MetaHandler handler;

    private final List<String> lore;
    private final String category;


    public ItemBase(JavaPlugin plugin, String itemID, Material material) {
        this.plugin = plugin;
        this.itemID = itemID;
        this.material = material;
        this.lore = setLore();
        this.category = setCategory();

        this.handler = isModernVersion() ? new MetaHandler_1_13(plugin) : new MetaHandler_1_12();
    }


    public boolean isCustomItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();

        String storedID = handler.getPersistentData(meta, CustomTag.ITEM_ID);

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

        PersistentDataContainer container = meta.getPersistentDataContainer();
        initializeFields(container);
        handler.setPersistentData(meta, CustomTag.ITEM_ID, itemID);

        customizeMeta(meta);

        itemStack.setItemMeta(meta);

        loadFromItemStack(itemStack);
        return itemStack;
    }


    private void initializeFields(PersistentDataContainer container) {
        for (Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(ItemVariable.class)) {
                ItemVariable annotation = field.getAnnotation(ItemVariable.class);
                String key = annotation.key().isEmpty() ? field.getName() : annotation.key();
                NamespacedKey namespacedKey = new NamespacedKey(plugin, key);

                try {
                    field.setAccessible(true);
                    Object value = field.get(this);
                    if (value instanceof Integer) {
                        container.set(namespacedKey, PersistentDataType.INTEGER, (Integer) value);
                    } else if (value instanceof String) {
                        container.set(namespacedKey, PersistentDataType.STRING, (String) value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadFromItemStack(ItemStack itemStack) {
        if (!isCustomItem(itemStack)) return;

        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        PersistentDataContainer container = meta.getPersistentDataContainer();

        for (Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(ItemVariable.class)) {
                ItemVariable annotation = field.getAnnotation(ItemVariable.class);
                String key = annotation.key().isEmpty() ? field.getName() : annotation.key();
                NamespacedKey namespacedKey = new NamespacedKey(plugin, key);

                try {
                    field.setAccessible(true);
                    if (container.has(namespacedKey, PersistentDataType.INTEGER)) {
                        field.set(this, container.get(namespacedKey, PersistentDataType.INTEGER));
                    } else if (container.has(namespacedKey, PersistentDataType.STRING)) {
                        field.set(this, container.get(namespacedKey, PersistentDataType.STRING));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void saveToItemStack(ItemStack itemStack) {
        if (itemStack == null || !isCustomItem(itemStack)) return;

        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        PersistentDataContainer container = meta.getPersistentDataContainer();

        for (Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(ItemVariable.class)) {
                ItemVariable annotation = field.getAnnotation(ItemVariable.class);
                String key = annotation.key().isEmpty() ? field.getName() : annotation.key();
                NamespacedKey namespacedKey = new NamespacedKey(plugin, key);

                try {
                    field.setAccessible(true);
                    Object value = field.get(this);
                    if (value instanceof Integer) {
                        container.set(namespacedKey, PersistentDataType.INTEGER, (Integer) value);
                    } else if (value instanceof String) {
                        container.set(namespacedKey, PersistentDataType.STRING, (String) value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        itemStack.setItemMeta(meta);
    }


    public String getItemID() {
        return itemID;
    }


    protected abstract String setName();
    protected abstract int setCustomModel();
    protected abstract String setCategory();
    protected abstract List<String> setLore();
    protected abstract void customizeMeta(ItemMeta meta);
    protected abstract ItemAction onClick();


    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (onClick() == null) return;

        ItemStack item = event.getItem();

        if (item != null && isCustomItem(item)) {
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


    public String getCategory() {
        return category;
    }

    private static class ColorTranslator {

        public static String translate(String string) {
            return ChatColor.translateAlternateColorCodes('&', string);
        }

    }
}
