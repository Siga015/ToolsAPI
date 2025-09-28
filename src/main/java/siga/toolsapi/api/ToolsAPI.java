package siga.toolsapi.api;

import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import siga.toolsapi.gui.GUI;
import siga.toolsapi.gui.GUIManager;
import siga.toolsapi.gui.GUIShape;
import siga.toolsapi.gui.GuiFilter;
import siga.toolsapi.item.ItemAction;
import siga.toolsapi.item.ItemBase;
import siga.toolsapi.item.ItemManager;
import siga.toolsapi.item.ItemModifier;
import siga.toolsapi.util.ColorTranslator;

import java.util.List;
import java.util.Map;

public class ToolsAPI {

    private static ToolsAPI instance;
    private final ItemManager itemManager;
    private final GUIManager guiManager;
    private ItemModifier itemModifier;

    public ToolsAPI() {
        throw new IllegalArgumentException();
    }


    private ToolsAPI(Object dummy) {
        this.itemManager = new ItemManager();
        this.guiManager = new GUIManager();
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

            @Override
            protected GuiFilter setGuiFilter() {
                return null;
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

    public GUIManager getGUIManager() {
        return guiManager;
    }

    @Deprecated
    public ItemModifier getItemModifier(JavaPlugin plugin) {
        if (itemModifier == null) {
            itemModifier = new ItemModifier(plugin);
        }
        return itemModifier;
    }

    public ItemBase createCustomItem(JavaPlugin plugin, String itemID, Material material, String title) {
        ItemBase item = new ItemBase(plugin, itemID, material) {
            @Override
            protected String setName() {
                return ColorTranslator.translate(title);
            }

            @Override
            protected String setCustomModel() {
                return null;
            }

            @Override
            protected String setCategory() {
                return null;
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
