package jumble.ui;

import java.util.List;

import jumble.fast.MutationResult;

/**
 * Listener which ignores all output. Used to maintain backward compatability
 * with the older non-listener output.
 * 
 * @author Tin Pavlinic
 * @version $Revision 1.0 $
 */
public class NullListener implements JumbleListener {
  public void jumbleRunStarted(String testName, List testClasses) {
  }
  public void jumbleRunEnded() {
  }

  public void finishedMutation(MutationResult res) {
  }
  
  public void performedInitialTest(int mutationCount, int status, long timeout) {
  }
}
