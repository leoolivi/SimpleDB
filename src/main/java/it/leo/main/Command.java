package it.leo.main;

import java.util.Collections;
import java.util.List;

public final class Command {
    private String name;
    private List<Command> subCommands = Collections.emptyList();
    private boolean hasArg;
    private String description;

    public void setName(String name) {
        this.name = name;
    }

    public void setSubCommands(List<Command> subCommands) {
        this.subCommands = subCommands;
    }

    public void setHasArg(boolean hasArg) {
        this.hasArg = hasArg;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public List<Command> getSubCommands() {
        return subCommands;
    }

    public boolean isHasArg() {
        return hasArg;
    }

    public String getDescription() {
        return description;
    }

    
    /**
     * @return number of tokens expected after the command
     */
    public int expectedTokens() {
        int count = 1;
        count += this.hasArg ? 1 : 0;
        for (Command sub : subCommands) {
            count += sub.expectedTokens();
        }
        return count;
    }

    Command(Builder builder ){
        this.name = builder.name;
        this.description = builder.description;
        this.subCommands = builder.subCommands;
        this.hasArg = builder.hasArg;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private List<Command> subCommands = Collections.emptyList();
        private boolean hasArg;
        private String description;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setSubCommands(List<Command> subCommands) {
            this.subCommands = subCommands;
            return this;
        }

        public Builder setHasArg(boolean hasArg) {
            this.hasArg = hasArg;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;            
        }

        public Command build() {
            return new Command(this);
        }

    }

}
