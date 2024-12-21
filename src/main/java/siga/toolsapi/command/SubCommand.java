package siga.toolsapi.command;

import java.util.ArrayList;
import java.util.List;

public class SubCommand {

    private final CommandAction action;
    private final String name;
    private final List<SubCommand> subCommands;

    private SubCommand(String name, List<SubCommand> subCommands, CommandAction action) {
        this.name = name;
        this.action = action;
        this.subCommands = subCommands;
    }

    public CommandAction getAction() {
        return action;
    }

    public String getName() {
        return name;
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }

    public static class Builder {

        private final String name;
        private final List<SubCommand> subCommands = new ArrayList<>();

        public Builder(String name) {
            this.name = name;
        }

        public Builder addSubCommand(SubCommand command) {
            subCommands.add(command);
            return this;
        }

        public SubCommand onUse(CommandAction action) {
            return new SubCommand(name, subCommands, action);
        }
    }

    public void addSubCommand(SubCommand command) {
        subCommands.add(command);
    }

}
