package abc;

import junit.framework.TestCase;

public class AbcMover2Test extends TestCase
{
  private AbcMover2 mover;

  public void setUp()
  {
    mover = new AbcMover2();
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

