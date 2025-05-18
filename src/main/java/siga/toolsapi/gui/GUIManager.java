package siga.toolsapi.gui;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class GUIManager {

    private final Set<GUI> guiRegistry = new HashSet<>();

    public void registerGUI(GUI gui) {
        guiRegistry.add(gui);
    }



    public <T> T getGUIBase(Class<? extends GUI> clazz) {
        for (GUI handler : guiRegistry) {
            if (handler.getClass().equals(clazz)) {
                return (T) handler;
            }
        }

        return null;
    }


    public Set<GUI> getGUIRegistry() {
        return guiRegistry;
    }

}
