package com.reeltwo.jumble.ui;


import java.util.List;

import com.reeltwo.jumble.fast.JumbleResult;
import com.reeltwo.jumble.fast.MutationResult;

/**
 * Interface to be implemented by every Jumble UI which displays mutations as
 * they are run and not at the end.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public interface JumbleListener {
  void jumbleRunStarted(String className, List<String> testNames);
  void performedInitialTest(JumbleResult result, int mutationCount);
  void jumbleRunEnded();
  void finishedMutation(MutationResult res);
  void error(String errorMessage);
}
