package siga.toolsapi.api;

import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import siga.toolsapi.gui.GUI;
import siga.toolsapi.gui.GUIShape;
import siga.toolsapi.item.ItemAction;
import siga.toolsapi.item.ItemBase;
import siga.toolsapi.item.ItemManager;
import siga.toolsapi.util.ColorTranslator;

import java.util.List;

public class ToolsAPI {

    private static ToolsAPI instance;
    private final ItemManager itemManager;

    public ToolsAPI() {
        throw new IllegalArgumentException();
    }


    private ToolsAPI(Object dummy) {
        this.itemManager = new ItemManager();
    }

    public static synchronized ToolsAPI getInstance() {
        if (instance == null) {
            instance = new ToolsAPI(null);
        }
        return instance;
    }


    public GUI createGUI(JavaPlugin plugin, String title, GUIShape shape) {
        GUI gui = new GUI(plugin) {
            @Override
            protected String setName() {
                return ColorTranslator.translate(title);
            }

            @Override
            protected GUIShape setShape() {
                return shape;
            }
        };

        return gui;
    }


    public void registerItem(ItemBase item, JavaPlugin plugin) {
        itemManager.registerItem(item, plugin);
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public ItemBase createCustomItem(JavaPlugin plugin, String itemID, Material material, String title) {
        ItemBase item = new ItemBase(plugin, itemID, material) {
            @Override
            protected String setName() {
                return ColorTranslator.translate(title);
            }

            @Override
            protected int setCustomModel() {
                return -1;
            }

            @Override
            protected List<String> setLore() {
                return null;
            }

            @Override
            protected void customizeMeta(ItemMeta meta) {

            }

            @Override
            protected ItemAction onClick() {
                return null;
            }
        };

        return item;
    }



}
