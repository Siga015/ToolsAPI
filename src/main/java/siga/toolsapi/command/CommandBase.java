package siga.toolsapi.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import siga.toolsapi.ToolsP;
import siga.toolsapi.api.ToolsAPI;
import siga.toolsapi.util.ColorTranslator;
import siga.toolsapi.util.Timing;

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
@Deprecated
public abstract class CommandBase implements CommandExecutor, TabCompleter {

    /*
    private final String name;
    private final String permission;
    private final String usage;
    private final boolean allowConsole;

    private List<SubCommand> subCommands = new ArrayList<>();

    public CommandBase(JavaPlugin plugin, String name, String permission, String usage, boolean allowConsole) {
        this.name = name;
        this.permission = permission;
        this.usage = ColorTranslator.translate("&cMisspelled! Correct usage: " + usage);
        this.allowConsole = allowConsole;

        Timing.callDelay(plugin, 5, () -> {
            this.subCommands = subCommandsList(subCommands);

            registerSubCommands();
        });
    }

    protected abstract Runnable onExecute(Player player, Integer number);

    protected abstract List<SubCommand> subCommandsList(List<SubCommand> subCommands);

    protected abstract boolean isNumeric();
    protected abstract boolean isPlayerAffected();

    protected void addSubCommand(SubCommand subCommand) {
        subCommands.add(subCommand);
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!allowConsole && !(sender instanceof Player)) return false;

        if (permission != null && !sender.hasPermission(permission) && !sender.isOp()) {
            sender.sendMessage(ColorTranslator.translate("&cYou don't have permission to use this command."));
            return false;
        }

        command.setUsage(usage);
        if (!label.equalsIgnoreCase(name)) return false;

        if (isNumeric()) {
            if (args.length == 0 || !isNumericArgument(args[0])) {
                sender.sendMessage(ColorTranslator.translate("&cYou must provide a numeric value."));
                return true;
            }
        }

        if (args.length == (isNumeric() ? 1 : 0)) {
            Runnable action = onExecute((Player) sender, isNumeric() ? Integer.parseInt(args[0]) : null);
            if (action != null) action.run();
            return true;
        }

        return handleSubCommand((Player) sender, args, 0, subCommands);
    }

    private boolean handleSubCommand(Player player, String[] args, int index, List<SubCommand> currentSubCommands) {
        if (index >= args.length) return false;

        SubCommand currentCommand = findSubCommand(currentSubCommands, args[index]);

        if (currentCommand == null) return false;

        if (currentCommand.isNumeric()) {
            if (index + 1 >= args.length || !isNumericArgument(args[index + 1])) {
                player.sendMessage(ColorTranslator.translate("&cYou must provide a numeric value for this subcommand."));
                return true;
            }
            index++;
        }

        Integer number = currentCommand.isNumeric() ? Integer.parseInt(args[index]) : null;

        if (index + 1 == args.length && currentCommand.getAction() != null) {
            currentCommand.getAction().execute(player, number);
            return true;
        }

        return handleSubCommand(player, args, index + 1, currentCommand.getSubCommands());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (subCommands.isEmpty() || args.length == 0) return null;

        return getTabCompletions(subCommands, args, 0);
    }

    private List<String> getTabCompletions(Collection<SubCommand> currentSubCommands, String[] args, int index) {
        List<String> completions = new ArrayList<>();

        if (index >= args.length) return completions;

        SubCommand nextCommand = findSubCommand(currentSubCommands, args[index]);

        if (nextCommand != null) {
            if (nextCommand.isNumeric()) {
                if (index + 1 == args.length) {
                    return List.of("<number>");
                }
                return getTabCompletions(nextCommand.getSubCommands(), args, index + 2);
            }
            return getTabCompletions(nextCommand.getSubCommands(), args, index + 1);
        }

        for (SubCommand subCommand : currentSubCommands) {
            if (subCommand.getName().toLowerCase().startsWith(args[index].toLowerCase())) {
                completions.add(subCommand.getName());
            }
        }

        return completions;
    }

    private boolean isNumericArgument(String str) {
        return str.matches("-?\\d+");
    }

    private SubCommand findSubCommand(Collection<SubCommand> subCommands, String name) {
        for (SubCommand subCommand : subCommands) {
            if (subCommand.getName().equalsIgnoreCase(name)) {
                return subCommand;
            }
        }
        return null;
    }

    private void registerSubCommands() {
        for (Method method : getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Subcommand.class)) {
                Subcommand subcommand = method.getAnnotation(Subcommand.class);
                SubCommand subCommand = new SubCommand.Builder(subcommand.value())
                        .onUse((player, number) -> {
                            try {
                                method.invoke(this, player, number);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                addSubCommand(subCommand);
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getUsage() {
        return usage;
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Subcommand {
        String value();
    }

     */
}
