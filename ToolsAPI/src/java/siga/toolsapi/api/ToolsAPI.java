package siga.toolsapi.api;

import org.bukkit.plugin.java.JavaPlugin;
import siga.toolsapi.gui.GUI;
import siga.toolsapi.gui.GUIShape;
import siga.toolsapi.util.ColorTranslator;

public class ToolsAPI {

    private static ToolsAPI instance;

    public ToolsAPI() {
        throw new IllegalArgumentException();
    }


    private ToolsAPI(Object dummy) {

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



}
