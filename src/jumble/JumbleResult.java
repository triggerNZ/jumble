package jumble;

/**
 * A jumble result.
 *
 * @author Tin
 * @version $Revision$
 */
public abstract class JumbleResult {
  public abstract int getMutationCount();
  public abstract Mutation[] getPassed();
  public abstract Mutation[] getTimeouts();
  public abstract Mutation[] getFailed();
  public abstract Mutation[] getAllMutations();
  public abstract boolean testFailed();
    
  public int getCoverage() {
    if (getMutationCount() == 0) {
      return 100;
    }
    return (int) ((double) (getPassed().length + getTimeouts().length) * 100 / getAllMutations().length);
  }
}
