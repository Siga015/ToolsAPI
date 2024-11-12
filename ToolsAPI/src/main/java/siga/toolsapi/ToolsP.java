package siga.toolsapi;

import org.bukkit.plugin.java.JavaPlugin;

public final class ToolsP extends JavaPlugin {

    private static ToolsP instance;

    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {

    }

    public static ToolsP getInstance() {
        return instance;
    }
}
