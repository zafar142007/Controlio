package in.control.parser;

import in.control.Command;
import in.control.Platform;
import in.server.util.ControlioConstants;


/**
 * @author Zafar Ansari
 */
public class CopyCommandParser extends CommandParser {

  private String command;

  public CopyCommandParser(String command) {
    this.command = command;
  }

  @Override
  protected String getText() {
    return command.substring(command.indexOf(ControlioConstants.COPY_COMMAND_PREFIX)
        + ControlioConstants.COPY_COMMAND_PREFIX.length()).trim().replaceAll("_", " ");
  }

  @Override
  public boolean isCommandEligible() {
    return command != null && command.toLowerCase()
        .startsWith(ControlioConstants.COPY_COMMAND_PREFIX);
  }

  @Override
  public Command[] getCommands() {
    if (isCommandEligible()) {
      String text = getText();
      Command[] commands = new Command[Platform.values().length];
      int i = 0;
      for (Platform platform : Platform.values()) {
        commands[i] = new Command();
        commands[i].setPlatform(platform);
        commands[i++].setPayload(text);
      }
      return commands;
    } else {
      return null;
    }
  }
}
