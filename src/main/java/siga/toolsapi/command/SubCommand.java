package siga.toolsapi.command;

import java.util.ArrayList;
import java.util.List;

public class SubCommand {

    private final CommandAction action;
    private final String name;
    private final List<SubCommand> subCommands;
    private final boolean numeric;
    private final boolean playerAffected;

    private SubCommand(String name, List<SubCommand> subCommands, CommandAction action, boolean numeric, boolean playerAffected) {
        this.name = name;
        this.action = action;
        this.subCommands = subCommands;
        this.numeric = numeric;
        this.playerAffected = playerAffected;
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
        private boolean numeric = false;
        private boolean playerAff = false;
        private final List<SubCommand> subCommands = new ArrayList<>();

        public Builder(String name) {
            this.name = name;
        }

        public Builder addSubCommand(SubCommand command) {
            subCommands.add(command);
            return this;
        }

        public Builder setNumeric() {
            numeric = true;
            return this;
        }

        public Builder setPlayerAffected() {
            playerAff = true;
            return this;
        }

        public SubCommand onUse(CommandAction action) {
            return new SubCommand(name, subCommands, action, numeric, playerAff);
        }
    }

    public boolean isNumeric() {
        return numeric;
    }

    public boolean isPlayerAffected() {
        return playerAffected;
    }

    public void addSubCommand(SubCommand command) {
        subCommands.add(command);
    }

}
