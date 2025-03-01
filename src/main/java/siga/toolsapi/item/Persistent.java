package siga.toolsapi.item;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public interface Persistent {

    JavaPlugin getPlugin();

    default void set(ItemStack itemStack, String key, Object value) {
        if (itemStack == null || !itemStack.hasItemMeta()) return;

        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;

        PersistentDataContainer container = meta.getPersistentDataContainer();

        NamespacedKey namespacedKey = new NamespacedKey(getPlugin(), key);
        if (value instanceof Integer) {
            container.set(namespacedKey, PersistentDataType.INTEGER, (Integer) value);
        } else if (value instanceof String) {
            container.set(namespacedKey, PersistentDataType.STRING, (String) value);
        } else if (value instanceof Double) {
            container.set(namespacedKey, PersistentDataType.DOUBLE, (Double) value);
        }

        itemStack.setItemMeta(meta);
    }

    default Object get(ItemStack itemStack, String key) {
        if (itemStack == null || !itemStack.hasItemMeta()) return null;

        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;

        PersistentDataContainer container = meta.getPersistentDataContainer();

        NamespacedKey namespacedKey = new NamespacedKey(getPlugin(), key);
        if (container.has(namespacedKey, PersistentDataType.INTEGER)) {
            return container.get(namespacedKey, PersistentDataType.INTEGER);
        } else if (container.has(namespacedKey, PersistentDataType.STRING)) {
            return container.get(namespacedKey, PersistentDataType.STRING);
        } else if (container.has(namespacedKey, PersistentDataType.DOUBLE)) {
            return container.get(namespacedKey, PersistentDataType.DOUBLE);
        }

        return null;
    }


}
