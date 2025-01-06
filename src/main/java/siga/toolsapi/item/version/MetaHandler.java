package siga.toolsapi.item.version;

import org.bukkit.inventory.meta.ItemMeta;

public interface MetaHandler {

    void setPersistentData(ItemMeta meta, String key, String value);

    String getPersistentData(ItemMeta meta, String key);
}
