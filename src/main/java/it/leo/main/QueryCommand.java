package it.leo.main;

import java.util.Collections;
import java.util.List;

public final class QueryCommand extends Command {
    private List<QueryCommand> subCommands = Collections.emptyList();
    private boolean hasArg;

    public void setSubCommands(List<QueryCommand> subCommands) {
        this.subCommands = subCommands;
    }

    public void setHasArg(boolean hasArg) {
        this.hasArg = hasArg;
    }

    public List<QueryCommand> getSubCommands() {
        return subCommands;
    }

    public boolean isHasArg() {
        return hasArg;
    }

    
    /**
     * @return number of tokens expected after the command
     */
    public int expectedTokens() {
        int count = 1;
        count += this.hasArg ? 1 : 0;
        for (QueryCommand sub : subCommands) {
            count += sub.expectedTokens();
        }
        return count;
    }

    QueryCommand(Builder builder ){
        super(builder.name, builder.description);
        this.subCommands = builder.subCommands;
        this.hasArg = builder.hasArg;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private List<QueryCommand> subCommands = Collections.emptyList();
        private boolean hasArg;
        private String description;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setSubCommands(List<QueryCommand> subCommands) {
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

        public QueryCommand build() {
            return new QueryCommand(this);
        }

    }

}
