package siga.toolsapi.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;

public class CommandRegistry {

    private final JavaPlugin plugin;
    private final Collection<CommandBase> commandRegistry;

    public CommandRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
        this.commandRegistry = new ArrayList<>();
    }

    public void register(CommandBase command) {
        PluginCommand pCommand = plugin.getCommand(command.getName());

        pCommand.setExecutor(command);
        pCommand.setTabCompleter(command);

        commandRegistry.add(command);
    }

    public void register(CommandExecutor command, String commandName) {
        PluginCommand pCommand = plugin.getCommand(commandName);
        pCommand.setExecutor(command);
    }

    public void registerTabCompleter(TabCompleter command, String commandName) {
        PluginCommand pCommand = plugin.getCommand(commandName);
        pCommand.setTabCompleter(command);
    }

    public void unregisterAll() {

        for (CommandBase command : commandRegistry) {
            PluginCommand pCommand = plugin.getCommand(command.getName());
            pCommand.setExecutor(null);
            pCommand.setTabCompleter(null);

            commandRegistry.remove(command);
        }
    }


    public Collection<CommandBase> getCommandRegistry() {
        return commandRegistry;
    }
}
