package com.reeltwo.jumble.fast;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>NormalJumbleResult</code> is a JumbleResult for a test pass
 * (that is, the mutation caused a test failure).
 *
 * @author <a href="mailto:len@reeltwo.com">Len Trigg</a>
 * @version $Revision$
 */
public class NormalJumbleResult extends AbstractJumbleResult {

  private List<String> mTestClassNames;

  private List<MutationResult> mAllMutations;

  private long mTimeoutLength;

  public NormalJumbleResult(String className, List<String> testClassNames, List<MutationResult> allMutations, long timeout) {
    super(className);
    mTestClassNames = testClassNames;
    mAllMutations = allMutations;
    mTimeoutLength = timeout;
  }

  /** {@inheritDoc} */
  @Override
public List<MutationResult> getAllMutations() {
    return mAllMutations;
  }

  /** {@inheritDoc} */
  @Override
public int getNumberOfMutations() {
    return mAllMutations.size();
  }

  /** {@inheritDoc} */
  @Override
public List<MutationResult> getCovered() {
    return filter(MutationResult.PASS);
  }

  /** {@inheritDoc} */
  @Override
public List<MutationResult> getTimeouts() {
    return filter(MutationResult.TIMEOUT);
  }

  /** {@inheritDoc} */
  @Override
public List<MutationResult> getMissed() {
    return filter(MutationResult.FAIL);
  }

  /** {@inheritDoc} */
  @Override
public long getTimeoutLength() {
    return mTimeoutLength;
  }

  /** {@inheritDoc} */
  @Override
public List<String> getTestClasses() {
    return mTestClassNames;
  }

  /** {@inheritDoc} */
  private List<MutationResult> filter(int mutationType) {
    List<MutationResult> all = getAllMutations();
    ArrayList<MutationResult> ret = new ArrayList<MutationResult> ();

    for (MutationResult mutationResult : all) {
      if (mutationResult.getStatus() == mutationType) {
        ret.add(mutationResult);
      }
    }

    return ret;
  }

}
