package siga.toolsapi.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import siga.toolsapi.util.ColorTranslator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Abstract class to simplify the creation of commands with subcommands.
 * Subcommands can be added using the addSubCommand method or the @Subcommand annotation.
 *
 * @author Siga
 */
public abstract class CommandBase implements CommandExecutor, TabCompleter {

    private final String name;
    private final String permission;
    private final String usage;
    private final boolean allowConsole;

    private List<SubCommand> subCommands;

    public CommandBase(String name, String permission, String usage, boolean allowConsole) {
        this.name = name;
        this.permission = "armoryWeapon." + permission;
        this.usage = ColorTranslator.translate("&cMisspelled! Correct usage: " + usage);
        this.allowConsole = allowConsole;
        this.subCommands = subCommandsList();

        registerSubCommands();
    }

    /**
     * Executes the main command when no subcommands are provided.
     * @param player the player executing the command
     * @return a Runnable that defines the action to be taken
     */
    protected abstract Runnable onExecute(Player player);


    /**
     * Initialize list of subCommands
     *
     * @return a provided subcommands of the main command
     */
    protected abstract List<SubCommand> subCommandsList();


    /**
     * Registers a subcommand for this command.
     * @param subCommand the subcommand to register
     */
    protected void addSubCommand(SubCommand subCommand) {
        subCommands.add(subCommand);
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }

    /**
     * Handles the command execution logic.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!allowConsole && !(sender instanceof Player)) return false;

        if (permission != null && !sender.hasPermission(permission) && !sender.isOp()) {
            sender.sendMessage(ColorTranslator.translate("&cYou don't have permission to use this command."));
            return false;
        }

        if (!label.equalsIgnoreCase(name)) return false;

        command.setUsage(usage);

        if (args.length == 0) {
            Runnable action = onExecute((Player) sender);
            if (action != null) action.run();
            return true;
        }


        return handleSubCommand((Player) sender, args);
    }

    /**
     * Handles subcommand execution logic.
     * @param player the player executing the command
     * @param args the arguments provided
     * @return true if the subcommand was executed, false otherwise
     */
    private boolean handleSubCommand(Player player, String[] args) {
        if (args.length == 0) return false;

        // Start with the top-level subcommands
        SubCommand currentCommand = findSubCommand(subCommands, args[0]);

        for (int i = 1; i < args.length && currentCommand != null; i++) {
            SubCommand nextCommand = findSubCommand(currentCommand.getSubCommands(), args[i]);

            if (nextCommand == null) {
                if (i == args.length - 1 && currentCommand.getAction() != null) {
                    currentCommand.getAction().execute(player);
                    return true;
                } else {
                    return false;
                }
            }

            currentCommand = nextCommand;
        }

        if (currentCommand != null && currentCommand.getAction() != null) {
            currentCommand.getAction().execute(player);
            return true;
        }

        return false;
    }

    /**
     * Helper method to find a subcommand by name in a collection of subcommands.
     * @param subCommands Collection of subcommands to search.
     * @param name Name of the subcommand to find.
     * @return The matching SubCommand if found, null otherwise.
     */
    private SubCommand findSubCommand(Collection<SubCommand> subCommands, String name) {
        for (SubCommand subCommand : subCommands) {
            if (subCommand.getName().equalsIgnoreCase(name)) {
                return subCommand;
            }
        }
        return null;
    }

    /**
     * Handles tab completion for commands and subcommands.
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (subCommands.isEmpty() || args.length == 0) return null;

        return getTabCompletions(subCommands, args, 0);
    }


    /**
     * Recursive method to get tab completions for subcommands at the specified depth.
     * @param currentSubCommands The current level of subcommands to check.
     * @param args The full args array passed in onTabComplete.
     * @param index The current depth in the args array.
     * @return A list of possible tab completions for the current argument level.
     */
    private List<String> getTabCompletions(Collection<SubCommand> currentSubCommands, String[] args, int index) {
        List<String> completions = new ArrayList<>();

        if (index == args.length - 1) {
            for (SubCommand subCommand : currentSubCommands) {
                if (subCommand.getName().toLowerCase().startsWith(args[index].toLowerCase())) {
                    completions.add(subCommand.getName());
                }
            }
            return completions;
        }

        SubCommand nextCommand = findSubCommand(currentSubCommands, args[index]);
        if (nextCommand != null) {
            return getTabCompletions(nextCommand.getSubCommands(), args, index + 1);
        }

        return completions;
    }


    /**
     * Registers subcommands from methods annotated with @Subcommand.
     */
    private void registerSubCommands() {

        for (Method method : getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Subcommand.class)) {
                Subcommand subcommand = method.getAnnotation(Subcommand.class);
                SubCommand subCommand = new SubCommand.Builder(subcommand.value())
                        .onUse(player -> {
                            try {
                                method.invoke(this, player);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                addSubCommand(subCommand);
            }
        }
        subCommands = subCommandsList();
    }

    public String getName() {
        return name;
    }

    public String getUsage() {
        return usage;
    }


    /**
     * Annotation to define a subcommand method in a command class.
     * Methods annotated with @Subcommand will be automatically registered as subcommands.
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Subcommand {
        String value();
    }


}