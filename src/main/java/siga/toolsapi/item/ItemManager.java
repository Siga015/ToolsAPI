package siga.toolsapi.item;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import siga.toolsapi.util.ColorTranslator;
import siga.toolsapi.util.CustomTag;

import java.util.HashSet;
import java.util.Set;

public class ItemManager {

    private final Set<ItemBase> itemRegistry = new HashSet<>();


    public void registerItem(ItemBase item, JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(item, plugin);
        itemRegistry.add(item);
    }



    public <T> T getItemBase(Class<? extends ItemBase> clazz) {
        for (ItemBase handler : itemRegistry) {
            if (handler.getClass().equals(clazz)) {
                return (T) handler;
            }
            else if (handler.getClass().getSuperclass().equals(clazz)) {
                return (T) handler;
            }
        }

        return null;
    }

    public ItemBase getItemBase(String itemID) {
        for (ItemBase handler : itemRegistry) {
            if (handler.getItemID().equals(itemID)) return handler;
        }
        return null;
    }

    public void showItems(Player player, String command) {
        if (itemRegistry.isEmpty()) {
            player.sendMessage("§cNo items are registered.");
            return;
        }

        player.sendMessage("§6Items List:");

        for (ItemBase item : itemRegistry) {
            TextComponent itemComponent = new TextComponent(ColorTranslator.translate("§e- " + item.getItemID()));
            itemComponent.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(
                    net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(ColorTranslator.translate("&aClick to receive!")).create()
            ));

            itemComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + " " + item.getItemID()));

            player.spigot().sendMessage(itemComponent);
        }
    }

    public String getItemID(ItemStack item, JavaPlugin plugin) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        return container.get(new NamespacedKey(plugin, CustomTag.ITEM_ID), PersistentDataType.STRING);
    }


    public Set<ItemBase> getItemRegistry() {
        return itemRegistry;
    }

    private boolean isModernVersion() {
        try {
            Class.forName("org.bukkit.persistence.PersistentDataContainer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
