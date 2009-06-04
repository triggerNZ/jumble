package example;

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;

public class MoverTest
{
  private Mover mover;

  @Before
  public void init()
  {
    mover = new Mover();
  }

  @Test
  public void up()
  {
    mover.move("up", 2);
    Assert.assertEquals("0,-2", mover.toString());
  }

  @Test
  public void down()
  {
    mover.move("down", 2);
    Assert.assertEquals("0,2", mover.toString());
  }

  @Test
  public void left()
  {
    mover.move("left", 2);
    Assert.assertEquals("0,0", mover.toString());
  }

  @Test
  public void right()
  {
    mover.move("right", 2);
    Assert.assertEquals("0,0", mover.toString());
  }
}
