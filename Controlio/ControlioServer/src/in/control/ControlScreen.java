package in.control;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControlScreen {

  private Robot control = null;
  private ExecutorService unpressPool = Executors.newFixedThreadPool(5);

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

  public boolean execute(Command command) {
    if(command!=null) {
      System.out.println("Robot: I am going to execute command");
      char[] sequence = command.getSequence();
      String[] instructions = command.getInstructions();
      int order = 0;
      Class clazz = KeyEvent.class;
      Field field;
      Set<Field> unpressedKeys = new HashSet<>();
      try {
        for (int i = 0; i < sequence.length; i++) {
          order = ((char) (sequence[i]) - (char) ('a'));
          String f = instructions[order / 2];
          field = clazz.getField(f);
          if (order % 2 == 0) {
            //System.out.println("Printing"+field.get(null));
            control.keyPress((int) field.get(null));//press "KeyEvent."+instructions[(order/2)]
            unpressedKeys.add(field);
          } else {
            //System.out.println("Printing"+field.get(null));
            control.keyRelease((int) field.get(null));//press "KeyEvent."+instructions[(order/2)]
            unpressedKeys.remove(field);
          }
        }
        System.out.println("Robot: I have executed command");
        unpressKeys(unpressedKeys);
        return true;
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
    }else{
      return false;
    }
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
