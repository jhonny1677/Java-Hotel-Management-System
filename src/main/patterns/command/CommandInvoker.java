package patterns.command;

import java.util.Stack;

public class CommandInvoker {
    private final Stack<Command> commandHistory;
    private final int maxHistorySize;

    public CommandInvoker() {
        this(100); // Default max history size
    }

    public CommandInvoker(int maxHistorySize) {
        this.commandHistory = new Stack<>();
        this.maxHistorySize = maxHistorySize;
    }

    public void executeCommand(Command command) {
        command.execute();
        
        // Add to history
        commandHistory.push(command);
        
        // Maintain history size limit
        if (commandHistory.size() > maxHistorySize) {
            commandHistory.remove(0); // Remove oldest command
        }
    }

    public boolean undoLastCommand() {
        if (!commandHistory.isEmpty()) {
            Command lastCommand = commandHistory.pop();
            lastCommand.undo();
            return true;
        }
        return false;
    }

    public boolean canUndo() {
        return !commandHistory.isEmpty();
    }

    public String getLastCommandDescription() {
        if (!commandHistory.isEmpty()) {
            return commandHistory.peek().getDescription();
        }
        return "No commands in history";
    }

    public int getHistorySize() {
        return commandHistory.size();
    }

    public void clearHistory() {
        commandHistory.clear();
    }
}