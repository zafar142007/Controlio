package in.control;

import in.control.parser.TypeCommandParser;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Dictionary {

  private Map<String, Command[]> lookup;
  Properties properties = new Properties();

  public void loadFromPropertiesFile() {
    int j = 0;
    String instructions;
    String tokens[], atoms[];
    Set<Object> set = properties.keySet();
    KeyCommand commands[];
    for (Object i : set) {
      instructions = (String) properties.get(i);
      tokens = instructions.split(",");
      commands = new KeyCommand[tokens.length];
      j = 0;
      for (String token : tokens) {
        commands[j] = new KeyCommand();
        atoms = token.split("@");
        commands[j].setInstructions(atoms[0].split("%"));
        commands[j].setSequence(atoms[1].toCharArray());
        if (atoms[2].equals("nix")) {
          commands[j].setPlatform(Platform.nix);
        } else if (atoms[2].equals("win")) {
          commands[j].setPlatform(Platform.win);
        } else if (atoms[2].equals("osx")) {
          commands[j].setPlatform(Platform.osx);
        }
        j++;
      }
      System.out.println(
          "Dictionary: Putting in " + commands[0].getInstructions()[0] + " " + commands[1]
              .getInstructions()[0]);
      lookup.put((String) i, commands);


    }
  }

  public Dictionary() {
    lookup = new HashMap<String, Command[]>();
    try {
      properties.load(Dictionary.class.getClassLoader().getResourceAsStream(
          "dictionary.properties"));
      System.out.println("Dictionary: Loaded dictionary from file");
      loadFromPropertiesFile();
      System.out.println("Dictionary: loaded into database");
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Dictionary: Could not load dictionary from file. " + e);
    }

  }

  public Command[] getCommands(String command) {
    System.out.println("Dictionary: I am looking up command " + command);
    Command[] commands;
    if ((commands = isTypingCommand(command)) != null) {
      return commands;
    }
    commands = lookup.get(command.trim());
    if (commands == null) {
      System.out.println("Dictionary: I could not find command");
      return null;
    } else {
      System.out.println("Dictionary: I have found command");
      return commands;
    }
  }

  private Command[] isTypingCommand(String command) {
    return new TypeCommandParser(command).getCommands();
  }


}
