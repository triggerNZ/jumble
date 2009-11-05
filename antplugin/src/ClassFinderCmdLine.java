/**
 * Command line implementation
 * 
 * @author Jay Huang
 */

public class ClassFinderCmdLine {

  /**
   * The main method.
   * 
   * @param args the path of the directory
   */

  public static void main(String[] args) {
    ClassFinder cf = new ClassFinder((args.length > 0) ? args[0] : "", null);

    cf.setInlineConstants(true);
    cf.setReturnVals(true);
    cf.setStores(false);
    cf.setIncrements(true);
    cf.setCPool(true);
    cf.setSwitches(true);
    cf.setOrdered(true);
    cf.setVerbose(false);
    cf.setOuputResultsToFile(false);
    cf.setRecurScan(true);
    cf.runJumbleOnAllClasses();

  }

}