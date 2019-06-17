package in.control;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControlScreen extends Controller {

  private Robot control = null;
  private ExecutorService unpressPool = Executors.newFixedThreadPool(5);
  private Dictionary dictionary = new Dictionary();

  public ControlScreen() {
    try {
      control = new Robot();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Robot getControl() {
    return control;
  }

  public void setControl(Robot control) {
    this.control = control;
  }

  public boolean execute(String command) throws CommandNotFoundException {
    if (command != null) {
      System.out.println("Robot: I am going to execute command");
      KeyCommand com = (KeyCommand) interpret(command);
      if (com != null) {
        char[] sequence = com.getSequence();
        String[] instructions = com.getInstructions();
        int order = 0;
        Class clazz = KeyEvent.class;
        Field field;
        Set<Field> unpressedKeys = new HashSet<>();
        try {
          for (int i = 0; i < sequence.length; i++) {
            order = ((char) (sequence[i]) - (char) ('a'));
            String f = instructions[order / 2];
            try {
              field = clazz.getField(f);
              if (order % 2 == 0) {
                //System.out.println("Printing"+field.get(null));
                control.keyPress((int) field.get(null));//press "KeyEvent."+instructions[(order/2)]
                unpressedKeys.add(field);
              } else {
                //System.out.println("Printing"+field.get(null));
                control
                    .keyRelease((int) field.get(null));//press "KeyEvent."+instructions[(order/2)]
                unpressedKeys.remove(field);
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
          System.out.println("Robot: I have executed command");
          unpressKeys(unpressedKeys);
          return true;
        } catch (Exception e) {
          e.printStackTrace();
          return false;
        }
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  @Override
  public boolean isControllable(String command) {
    try {
      Command[] commands = dictionary.getCommands(command);
      if (commands != null) {
        return commands.length > 0;
      } else {
        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  protected Command[] getCommands(String com) {
    return dictionary.getCommands(com);
  }

  public void unpressKeys(Set<Field> unpressedKeys) {
    //unpress keys that are still pressed
    for (Field key : unpressedKeys) {
      unpressPool.submit(
          () -> {
            try {
              Thread.sleep(5000);
              control.keyRelease((int) key.get(null));
              System.out.println("Robot: I have unpressed key");
            } catch (Exception e) {
              e.printStackTrace();
            }
          });
    }
  }
}
