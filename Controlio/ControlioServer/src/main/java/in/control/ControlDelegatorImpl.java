package in.control;

import in.server.util.CleanupUtility;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Zafar Ansari
 */
public class ControlDelegatorImpl extends ControlDelegator {

  private List<Controller> controllers = new ArrayList<>();
  private Controller defaultController = new ControlScreen();

  public ControlDelegatorImpl() {
    controllers.add(new ControlClipboard());
    controllers.add(new ControlScreen());
  }

  @Override
  public Controller delegate(String command) {
    for (Controller controller : controllers) {
      if (controller.isControllable(CleanupUtility.sanitize(command))) {
        return controller;
      }
    }
    return defaultController;
  }
}
