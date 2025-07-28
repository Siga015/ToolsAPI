package siga.toolsapi.command;

import org.bukkit.plugin.java.JavaPlugin;
import siga.toolsapi.command.configuration.CommandConfiguration;
import siga.toolsapi.command.configuration.DefaultCommandConfigurator;

public final class CommandsModule {

    private final JavaPlugin plugin;
    private final CommandConfiguration commandConfiguration;
    private final CommandRegistry commandRegistration;

    public CommandsModule(JavaPlugin plugin) {
        this(plugin, new DefaultCommandConfigurator().configure());
    }

    public CommandsModule(JavaPlugin plugin, CommandConfiguration commandConfiguration) {
        this.plugin = plugin;
        this.commandConfiguration = commandConfiguration;
        this.commandRegistration = new CommandRegistry(plugin, commandConfiguration);
    }


    public String getName() {
        return plugin.getName() + "-commands";
    }

    public CommandConfiguration getCommandConfiguration() {
        return commandConfiguration;
    }

    public CommandRegistry getCommandRegistration() {
        return commandRegistration;
    }
}