package siga.toolsapi.command.configuration;

/**
 * The class to configure your commands messages
 */
public class CommandConfiguration implements Configuration {

    private String doNotHavePermissionMessage;
    private String onlyPlayersMessage;

    private CommandConfiguration(Builder builder) {
        this.doNotHavePermissionMessage = builder.doNotHavePermissionMessage;
        this.onlyPlayersMessage = builder.onlyPlayersMessage;
    }

    public String getDoNotHavePermissionMessage() {
        return doNotHavePermissionMessage;
    }

    public String getOnlyPlayersMessage() {
        return onlyPlayersMessage;
    }

    public static class Builder {
        private String doNotHavePermissionMessage;
        private String onlyPlayersMessage;

        public Builder() {}

        public Builder doNotHavePermissionMessage(String message) {
            this.doNotHavePermissionMessage = message;
            return this;
        }

        public Builder onlyPlayersMessage(String message) {
            this.onlyPlayersMessage = message;
            return this;
        }

        public CommandConfiguration build() {
            return new CommandConfiguration(this);
        }
    }
}
