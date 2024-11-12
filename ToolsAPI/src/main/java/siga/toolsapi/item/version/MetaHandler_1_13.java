package siga.toolsapi.item.version;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Handler for item meta in version 1.13 and above.
 */
public class MetaHandler_1_13 implements MetaHandler {
    private final NamespacedKey key;

    public MetaHandler_1_13(String key, JavaPlugin plugin) {
        this.key = new NamespacedKey(plugin, key);
    }

    @Override
    public void setPersistentData(ItemMeta meta, String key, String value) {
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(this.key, PersistentDataType.STRING, value);
    }

    @Override
    public String getPersistentData(ItemMeta meta, String key) {
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        return dataContainer.get(this.key, PersistentDataType.STRING);
    }
}