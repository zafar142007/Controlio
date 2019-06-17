package in.control;

/**
 * @author Zafar Ansari
 */
public class KeyCommand extends Command {

  private String[] instructions = null;
  private char[] sequence = null;

  public KeyCommand(String[] instructions, char[] sequence) {
    this.instructions = instructions;
    this.sequence = sequence;
  }

  public char[] getSequence() {
    return sequence;
  }

  public void setSequence(char[] sequence) {
    this.sequence = sequence;
  }

  public void setInstructions(String[] instructions) {
    this.instructions = instructions;
  }

  public String[] getInstructions() {
    return instructions;
  }

  public KeyCommand() {
    super();
  }
}
