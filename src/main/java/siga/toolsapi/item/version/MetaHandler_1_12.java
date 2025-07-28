package siga.toolsapi.item.version;

import org.bukkit.inventory.meta.ItemMeta;

/**
 * Handler for item meta in version 1.12.
 */
public class MetaHandler_1_12 implements MetaHandler{

    @Override
    public void setPersistentData(ItemMeta meta, String key, String value) {
        meta.setLocalizedName(value);
    }

    @Override
    public String getPersistentData(ItemMeta meta, String key) {
        return meta.getLocalizedName();
    }
}
