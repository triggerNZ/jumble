package experiments;

/**
 * A static class.
 *
 * @author Tin
 * @version $Revision$
 */
public class StaticClass {

  private StaticClass() {}

  public static int add(int a, int b) {
    return a + b;
  }
  
  public static int sum(int[] a) {
    int sum = 0;
    for (int i = 0; i < a.length; i++) {
      sum += a[i];
    }
    return sum;
  }
}
