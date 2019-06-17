package in.control;

import in.server.util.CleanupUtility;
import in.server.util.ControlioConstants;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * @author Zafar Ansari
 */
public abstract class Controller {
  public abstract boolean execute(String command) throws CommandNotFoundException;
  public abstract boolean isControllable(String command);

  public Command interpret(String command) throws CommandNotFoundException {
    Command[] commands = null;
    if (isTest(CleanupUtility.sanitize(command))) {
      showTestStatus();
    }
    else {
      commands = getCommands(CleanupUtility.sanitize(command));
      if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
        for (int i = 0; i < commands.length; i++) {
          if (commands[i].getPlatform().toString().equals("win")) {
            return commands[i];
          }
        }
      } else if (System.getProperty("os.name").toLowerCase().indexOf("nux") >= 0) {
        for (int i = 0; i < commands.length; i++) {
          if (commands[i].getPlatform().toString().equals("nix")) {
            return commands[i];
          }
        }
      } else if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) {
        for (int i = 0; i < commands.length; i++) {
          if (commands[i].getPlatform().toString().equals("osx")) {
            return commands[i];
          }
        }
      }
    }
    return null;

  }

  protected abstract Command[] getCommands(String com) throws CommandNotFoundException;

  private boolean isTest(String command) {
    return command.equals(ControlioConstants.TEST_COMMAND.toLowerCase());
  }

  private void showTestStatus() {
    JFrame alert = new JFrame();
    alert.setSize(300, 70);
    alert.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    alert.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 200,
        Toolkit.getDefaultToolkit().getScreenSize().height / 2 - 70);
    alert.setTitle("Setup");
    try {
      alert.add(new JLabel("You are all set!\nYou can now close this popup."));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    alert.setAutoRequestFocus(true);
    alert.setVisible(true);
  }


}
