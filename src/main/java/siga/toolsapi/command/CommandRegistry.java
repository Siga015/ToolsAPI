package siga.toolsapi.command;

import org.bukkit.plugin.java.JavaPlugin;
import siga.toolsapi.command.configuration.CommandConfiguration;

import java.util.Objects;

/**
 * Registers commands and tab completer
 */
public final class CommandRegistry {

    private final JavaPlugin plugin;
    private final CommandConfiguration commandConfiguration;

    public CommandRegistry(JavaPlugin plugin, CommandConfiguration commandConfiguration) {
        this.plugin = plugin;
        this.commandConfiguration = commandConfiguration;
    }


    /**
     * Registers command
     *
     * @param command command to register
     */
    public void registerCommand(ParentCommand command) {
        command.setCommandConfiguration(commandConfiguration);

        plugin.getCommand(command.getName()).setExecutor(command);
    }

    /**
     * Registers command with tab completer
     *
     * @param command command to register
     */
    public void registerCommandWithTabCompleter(ParentCommand command) {
        if (command.getTabCompleter() == null) return;

        registerCommand(command);
        Objects.requireNonNull(plugin.getCommand(command.getName())).setTabCompleter(command.getTabCompleter());
    }
}