package siga.toolsapi;

import org.bukkit.plugin.java.JavaPlugin;
import siga.toolsapi.gui.GUIListener;

public final class ToolsP extends JavaPlugin {


    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
    }

    @Override
    public void onDisable() {

    }
}
