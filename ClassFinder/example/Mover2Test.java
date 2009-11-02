

import junit.framework.TestCase;

public class Mover2Test extends TestCase
{
  private Mover2 mover;

  public void setUp()
  {
    mover = new Mover2();
  }

  public void testUp()
  {
    mover.move("up", 2);
    assertEquals("0,-2", mover.toString());
  }

  public void testDown()
  {
    mover.move("down", 2);
    assertEquals("0,2", mover.toString());
  }

  public void testLeft()
  {
    mover.move("left", 2);
    assertEquals("0,0", mover.toString());
  }

  public void testRight()
  {
    mover.move("right", 2);
    assertEquals("0,0", mover.toString());
  }
}

