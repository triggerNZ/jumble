package jumble.fast;

import java.util.ArrayList;
import java.util.List;
import jumble.mutation.Mutation;

/**
 * <code>NormalJumbleResult</code> is a JumbleResult for a test pass
 * (that is, the mutation caused a test failure).
 *
 * @author <a href="mailto:len@reeltwo.com">Len Trigg</a>
 * @version $Revision$
 */
public class NormalJumbleResult extends AbstractJumbleResult {

  private List mTestClassNames;

  private Mutation[] mAllMutations;

  private long mTimeoutLength;

  public NormalJumbleResult(String className, List testClassNames, Mutation[] allMutations, long timeout) {
    super(className);
    mTestClassNames = testClassNames;
    mAllMutations = allMutations;
    mTimeoutLength = timeout;
  }

  /** {@inheritDoc} */
  public Mutation[] getAllMutations() {
    return mAllMutations;
  }

  /** {@inheritDoc} */
  public int getNumberOfMutations() {
    return mAllMutations.length;
  }

  /** {@inheritDoc} */
  public Mutation[] getCovered() {
    return filter(Mutation.PASS);
  }

  /** {@inheritDoc} */
  public Mutation[] getTimeouts() {
    return filter(Mutation.TIMEOUT);
  }

  /** {@inheritDoc} */
  public Mutation[] getMissed() {
    return filter(Mutation.FAIL);
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
  private Mutation[] filter(int mutationType) {
    Mutation[] all = getAllMutations();
    ArrayList ret = new ArrayList();

    for (int i = 0; i < all.length; i++) {
      if (all[i].getStatus() == mutationType) {
        ret.add(all[i]);
      }
    }

    return (Mutation[]) ret.toArray(new Mutation[ret.size()]);
  }

}
