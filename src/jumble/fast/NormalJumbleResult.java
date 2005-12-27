package jumble.fast;

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

  private List mTestClassNames;

  private MutationResult[] mAllMutations;

  private long mTimeoutLength;

  public NormalJumbleResult(String className, List testClassNames, MutationResult[] allMutations, long timeout) {
    super(className);
    mTestClassNames = testClassNames;
    mAllMutations = allMutations;
    mTimeoutLength = timeout;
  }

  /** {@inheritDoc} */
  public MutationResult[] getAllMutations() {
    return mAllMutations;
  }

  /** {@inheritDoc} */
  public int getNumberOfMutations() {
    return mAllMutations.length;
  }

  /** {@inheritDoc} */
  public MutationResult[] getCovered() {
    return filter(MutationResult.PASS);
  }

  /** {@inheritDoc} */
  public MutationResult[] getTimeouts() {
    return filter(MutationResult.TIMEOUT);
  }

  /** {@inheritDoc} */
  public MutationResult[] getMissed() {
    return filter(MutationResult.FAIL);
  }

  /** {@inheritDoc} */
  public long getTimeoutLength() {
    return mTimeoutLength;
  }

  /** {@inheritDoc} */
  public String[] getTestClasses() {
    return (String[]) mTestClassNames
        .toArray(new String[mTestClassNames.size()]);
  }

  /** {@inheritDoc} */
  private MutationResult[] filter(int mutationType) {
    MutationResult[] all = getAllMutations();
    ArrayList ret = new ArrayList();

    for (int i = 0; i < all.length; i++) {
      if (all[i].getStatus() == mutationType) {
        ret.add(all[i]);
      }
    }

    return (MutationResult[]) ret.toArray(new MutationResult[ret.size()]);
  }

}
