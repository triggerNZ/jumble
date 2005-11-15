/*
 * Created on 21/06/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble;

/**
 * An extension of JumbleResult adding <CODE>getClassName()</CODE> 
 * and <CODE>getTestName()</CODE>
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 *  
 */
public abstract class ExtendedJumbleResult extends JumbleResult {
  public abstract String getClassName();
  public abstract String getTestName();
}
