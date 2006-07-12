package jumble.util;

public class CheckAssertions2 {

  /**
   * @param args
   */
  public static void main(String[] args) {
    assert print("Assertions on");
  }

  private static boolean print(String str) {
    System.out.println(str);
    return true;
  }
}
