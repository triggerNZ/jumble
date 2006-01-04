package jumble.ui;

import java.util.List;

import jumble.fast.MutationResult;

/**
 * Interface to be implemented by every Jumble UI which displays mutations as
 * they are run and not at the end.
 * 
 * @author Tin Pavlinic
 * @version $Revision 1.0 $
 */
public interface JumbleListener {
  public void jumbleRunStarted(String className, List testNames);
  public void performedInitialTest(int mutationCount, int status, long timeout);
  public void jumbleRunEnded();
  public void finishedMutation(MutationResult res);
}
