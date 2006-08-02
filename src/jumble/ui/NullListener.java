package jumble.ui;


import java.util.List;
import jumble.fast.JumbleResult;
import jumble.fast.MutationResult;

/**
 * Listener which ignores all output. Used to maintain backward compatibility
 * with the older non-listener output.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class NullListener implements JumbleListener {
  public void jumbleRunStarted(String testName, List testClasses) {
  }
  public void jumbleRunEnded() {
  }

  public void finishedMutation(MutationResult res) {
  }
  
  public void performedInitialTest(JumbleResult result, int mutationCount) {
  }
  
  public void error(String message) {
  }
}
