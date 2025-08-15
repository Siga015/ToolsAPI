package siga.toolsapi.item;

import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import siga.toolsapi.item.version.MetaHandler;
import siga.toolsapi.item.version.MetaHandler_1_12;
import siga.toolsapi.item.version.MetaHandler_1_13;
import siga.toolsapi.util.CustomTag;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public abstract class ItemBase implements Listener {

    protected final JavaPlugin plugin;
    private final String itemID;
    private final Material material;
    private final MetaHandler handler;

    private final List<String> lore;
    private final String category;

    private static final Set<Class<?>> registeredListeners = new HashSet<>();


    public ItemBase(JavaPlugin plugin, String itemID, Material material) {
        this.plugin = plugin;
        this.itemID = itemID;
        this.material = material;
        List<String> lore = setLore();
        if (lore != null) lore.forEach(ColorTranslator::translate);
        this.lore = lore;
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
        if (isModernVersion() && setCustomModel() != null) {
            CustomModelDataComponent modelDataComp = meta.getCustomModelDataComponent();
            List<String> strings = new ArrayList<>();
            strings.add(setCustomModel());
            modelDataComp.setStrings(strings);

            meta.setCustomModelDataComponent(modelDataComp);
        }
        meta.setLore(lore);

        for (ItemFlag flag : ItemFlag.values()) {
            meta.addItemFlags(flag);
        }



        handler.setPersistentData(meta, CustomTag.ITEM_UUID, UUID.randomUUID().toString());
        handler.setPersistentData(meta, CustomTag.ITEM_ID, itemID);

        finalizeMeta(meta);

        itemStack.setItemMeta(meta);

        return itemStack;
    }



    public String getItemID() {
        return itemID;
    }

    public String getItemUUID(ItemStack item) {
        return handler.getPersistentData(item.getItemMeta(), CustomTag.ITEM_UUID);
    }


    protected abstract String setName();
    protected abstract String setCustomModel();
    protected abstract String setCategory();
    protected abstract List<String> setLore();
    protected abstract void customizeMeta(ItemMeta meta);
    protected abstract ItemAction onClick();


    protected void finalizeMeta(ItemMeta meta) {
        customizeMeta(meta);
        saveSerializableFields(meta, this);
    }



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

    protected void saveSerializableFields(ItemMeta meta, Object instance) {
        PersistentDataContainer container = meta.getPersistentDataContainer();
        Class<?> current = instance.getClass();

        while (current != null) {
            for (Field field : current.getDeclaredFields()) {
                if (!field.isAnnotationPresent(SerializableField.class)) continue;

                field.setAccessible(true);
                SerializableField annotation = field.getAnnotation(SerializableField.class);
                String key = annotation.key().isEmpty() ? field.getName() : annotation.key();
                NamespacedKey namespacedKey = new NamespacedKey(plugin, key);

                try {
                    Object value = field.get(instance);
                    if (value == null) continue;

                    if (value instanceof Integer) {
                        container.set(namespacedKey, PersistentDataType.INTEGER, (Integer) value);
                    } else if (value instanceof String) {
                        container.set(namespacedKey, PersistentDataType.STRING, (String) value);
                    } else if (value instanceof Double) {
                        container.set(namespacedKey, PersistentDataType.DOUBLE, (Double) value);
                    } else if (value instanceof Enum<?>) {
                        container.set(namespacedKey, PersistentDataType.STRING, ((Enum<?>) value).name());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            current = current.getSuperclass();
        }
    }


    protected void loadSerializableFields(ItemStack item, Object instance) {
        if (!item.hasItemMeta()) return;

        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        Class<?> current = instance.getClass();


        while (current != null) {
            for (Field field : current.getDeclaredFields()) {
                if (!field.isAnnotationPresent(SerializableField.class)) continue;

                field.setAccessible(true);
                SerializableField annotation = field.getAnnotation(SerializableField.class);
                String key = annotation.key().isEmpty() ? field.getName() : annotation.key();
                NamespacedKey namespacedKey = new NamespacedKey(plugin, key);

                try {
                    Class<?> type = field.getType();
                    if (type == Integer.class || type == int.class) {
                        if (container.has(namespacedKey, PersistentDataType.INTEGER)) {
                            field.set(instance, container.get(namespacedKey, PersistentDataType.INTEGER));
                        }
                    } else if (type == String.class) {
                        if (container.has(namespacedKey, PersistentDataType.STRING)) {
                            field.set(instance, container.get(namespacedKey, PersistentDataType.STRING));
                        }
                    } else if (type == Double.class || type == double.class) {
                        if (container.has(namespacedKey, PersistentDataType.DOUBLE)) {
                            field.set(instance, container.get(namespacedKey, PersistentDataType.DOUBLE));
                        }
                    } else if (type.isEnum()) {
                        if (container.has(namespacedKey, PersistentDataType.STRING)) {
                            String enumName = container.get(namespacedKey, PersistentDataType.STRING);
                            Object enumValue = Enum.valueOf((Class<Enum>) type, enumName);
                            field.set(instance, enumValue);
                        }
                    }
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            current = current.getSuperclass();
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
