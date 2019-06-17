package in.control;

import in.control.parser.CopyCommandParser;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * @author Zafar Ansari
 */
public class ControlClipboard extends Controller {

  @Override
  public boolean execute(String command) {
    try {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(
          new StringSelection(super.interpret(command).getPayload()), null);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean isControllable(String command) {
    return new CopyCommandParser(command).isCommandEligible();
  }

  @Override
  protected Command[] getCommands(String com) {
    return new CopyCommandParser(com).getCommands();
  }
}
