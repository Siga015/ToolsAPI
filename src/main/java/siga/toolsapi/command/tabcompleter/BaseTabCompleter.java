package siga.toolsapi.command.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import siga.toolsapi.command.ParentCommand;
import siga.toolsapi.command.SubCommand;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;


public abstract class BaseTabCompleter implements TabCompleter {

    private final ParentCommand parentCommand;
    private BiFunction<CommandSender, String[], List<String>> conditions;

    public BaseTabCompleter(ParentCommand parentCommand) {
        this.parentCommand = parentCommand;
    }

    /**
     * @param sender  Source of the command.  For players tab-completing a
     *                command inside a command block, this will be the player, not
     *                the command block.
     * @param command Command which was executed
     * @param alias   The alias used
     * @param args    The arguments passed to the command, including final
     *                partial argument to be completed and command label
     * @return list of completions
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return parentCommand.getSubCommands()
                    .stream().map(SubCommand::getName).collect(Collectors.toList());
        }

        if (conditions != null) {
            return conditions.apply(sender, args);
        }

        return Collections.emptyList();
    }

    /**
     * Adds conditions
     *
     * @param conditions callback to define conditions
     */
    public void addConditions(BiFunction<CommandSender, String[], List<String>> conditions) {
        this.conditions = conditions;
    }
}