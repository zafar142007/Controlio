package in.control;

import in.server.util.ControlioConstants;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Dictionary {

  private Map<String, Command[]> lookup;
  Properties properties = new Properties();

  void loadFromPropertiesFile() {
    int j = 0;
    String instructions;
    String tokens[], atoms[];
    Set<Object> set = properties.keySet();
    Command commands[];
    for (Object i : set) {
      instructions = (String) properties.get(i);
      tokens = instructions.split(",");
      commands = new Command[tokens.length];
      j = 0;
      for (String token : tokens) {
        commands[j] = new Command();
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

  public Command[] getCommands(String command) throws CommandNotFoundException {
    System.out.println("Dictionary: I am looking up command " + command);
    Command[] commands;
    if ((commands = isTypingCommand(command)) != null) {
      return commands;
    }
    commands = lookup.get(command.trim());
    if (commands == null) {
      System.out.println("Dictionary: I could not find command");
      throw new CommandNotFoundException();
    } else {
      System.out.println("Dictionary: I have found command");
      return commands;
    }
  }

  private Command[] isTypingCommand(String command) {
    if (command != null && command.toLowerCase()
        .startsWith(ControlioConstants.TYPE_COMMAND_PREFIX)) {
      String text = getTypingText(command);
      String[] instructions=getInstructions(text);
      char[] sequence=getSequence(instructions);
      Command[] commands = new Command[3];
      commands[0] = new Command();
      commands[0].setSequence(sequence);
      commands[0].setPlatform(Platform.nix);
      commands[0].setInstructions(instructions);
      commands[1] = new Command();
      commands[1].setSequence(sequence);
      commands[1].setPlatform(Platform.osx);
      commands[1].setInstructions(instructions);
      commands[2] = new Command();
      commands[2].setSequence(sequence);
      commands[2].setPlatform(Platform.win);
      commands[2].setInstructions(instructions);
      return commands;
    } else {
      return null;
    }

  }

  private char[] getSequence(String[] instructions) {
    char sequence[]=new char[instructions.length*2];
    char seq='a';
    for(int i=0; i<instructions.length; i++){
      sequence[i*2]=seq++;
      sequence[i*2+1]=seq++;
    }
    return sequence;
  }

  private String[] getInstructions(String text) {
    String[] instructions=new String[text.length()];
    for(int i=0; i<text.length(); i++){
      if(text.charAt(i)==' '){
        instructions[i]="VK_SPACE";
      } else {
        instructions[i] = ("VK_" + text.charAt(i)).toUpperCase();
      }
    }
    return instructions;
  }

  private String getTypingText(String command) {
    return command.substring(command.indexOf(ControlioConstants.TYPE_COMMAND_PREFIX)
        + ControlioConstants.TYPE_COMMAND_PREFIX.length()).trim().replaceAll("_"," " );
  }
}
