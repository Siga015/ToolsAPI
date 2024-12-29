package siga.toolsapi.item;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import siga.toolsapi.item.version.MetaHandler;
import siga.toolsapi.item.version.MetaHandler_1_13;
import siga.toolsapi.util.ColorTranslator;

import java.util.HashSet;
import java.util.Set;

public class ItemManager {

    private final Set<ItemBase> itemRegistry = new HashSet<>();
    private MetaHandler handler;


    public void registerItem(ItemBase item, JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(item, plugin);
        itemRegistry.add(item);
    }



    public <T> T getItemBase(Class<? extends ItemBase> clazz) {
        for (ItemBase handler : itemRegistry) {
            if (handler.getClass().equals(clazz)) return (T) handler;
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
        if (getHandler(plugin).getPersistentData(item.getItemMeta(), "ITEM_ID") == null) return null;

        return getHandler(plugin).getPersistentData(item.getItemMeta(), "ITEM_ID");
    }

    private MetaHandler getHandler(JavaPlugin plugin) {
        if (handler == null) {
            handler = new MetaHandler_1_13(plugin);
        }

        return handler;
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
