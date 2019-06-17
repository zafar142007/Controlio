package in.server.util;

/**
 * @author Zafar Ansari
 */
public class CleanupUtility {

  public static String sanitize(String string) {
    return string.trim().toLowerCase().replace(' ', '_');
  }

}
