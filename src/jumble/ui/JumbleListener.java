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
  void jumbleRunStarted(String className, List testNames);
  void performedInitialTest(int mutationCount, int status, long timeout);
  void jumbleRunEnded();
  void finishedMutation(MutationResult res);
}
