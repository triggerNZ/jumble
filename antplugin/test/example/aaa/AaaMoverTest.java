package aaa;

import junit.framework.TestCase;

public class AaaMoverTest extends TestCase
{
  private AaaMover mover;

  public void setUp()
  {
    mover = new AaaMover();
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

