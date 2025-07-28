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
    private final JavaPlugin plugin;

    public MetaHandler_1_13(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setPersistentData(ItemMeta meta, String key, String value) {
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(new NamespacedKey(plugin, key), PersistentDataType.STRING, value);
    }

    @Override
    public String getPersistentData(ItemMeta meta, String key) {
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        return dataContainer.get(new NamespacedKey(plugin, key), PersistentDataType.STRING);
    }
}