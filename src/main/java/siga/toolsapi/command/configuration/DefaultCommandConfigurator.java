package siga.toolsapi.command.configuration;

import org.bukkit.ChatColor;


public class DefaultCommandConfigurator implements Configurator<CommandConfiguration> {

    /**
     * @return default configuration for commands
     */
    @Override
    public CommandConfiguration configure() {
        return new CommandConfiguration.Builder()
                .doNotHavePermissionMessage(ChatColor.RED + "You don't have permission")
                .onlyPlayersMessage("Only players can use this command")
                .build();
    }
}