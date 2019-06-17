package in.control.parser;

import in.control.Command;
import in.control.KeyCommand;
import in.control.Platform;
import in.server.util.ControlioConstants;

/**
 * @author Zafar Ansari
 */
public class TypeCommandParser extends CommandParser {

  private String command;

  public TypeCommandParser(String command) {
    this.command = command;
  }

  @Override
  protected String getText() {
    return command.substring(command.indexOf(ControlioConstants.TYPE_COMMAND_PREFIX)
        + ControlioConstants.TYPE_COMMAND_PREFIX.length()).trim().replaceAll("_", " ");
  }

  protected String[] getInstructions(String text, Platform platform) {
    String[] instructions = new String[text.length()];
    for (int i = 0; i < text.length(); i++) {
      if (text.charAt(i) == ' ') {
        instructions[i] = "VK_SPACE";
      } else if (text.charAt(i) == '.') {
        instructions[i] = "VK_PERIOD";
      } else if (text.charAt(i) == ',') {
        instructions[i] = "VK_COMMA";
      } else if (text.charAt(i) == '-') {
        instructions[i] = "VK_MINUS";
      } else {
        instructions[i] = ("VK_" + text.charAt(i)).toUpperCase();
      }
    }
    return instructions;
  }

  protected char[] getSequence(String[] instructions) {
    char sequence[] = new char[instructions.length * 2];
    char seq = 'a';
    for (int i = 0; i < instructions.length; i++) {
      sequence[i * 2] = seq++;
      sequence[i * 2 + 1] = seq++;
    }
    return sequence;
  }

  @Override
  public Command[] getCommands() {
    if (isCommandEligible()) {
      String text = getText();
      String[] instructions = getInstructions(text, null);
      char[] sequence = getSequence(instructions);
      KeyCommand[] commands = new KeyCommand[3];
      int i = 0;
      for (Platform platform : Platform.values()) {
        commands[i] = new KeyCommand();
        commands[i].setSequence(sequence);
        commands[i].setPlatform(platform);
        commands[i++].setInstructions(instructions);
      }
      return commands;
    } else {
      return null;
    }
  }

  @Override
  public boolean isCommandEligible() {
    return command != null && command.toLowerCase()
        .startsWith(ControlioConstants.TYPE_COMMAND_PREFIX);
  }
}
