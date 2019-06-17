package in.control.parser;

import in.control.Command;

/**
 * @author Zafar Ansari
 */
public abstract class CommandParser {

  protected abstract String getText();

  public abstract Command[] getCommands();

  public abstract boolean isCommandEligible();
}
