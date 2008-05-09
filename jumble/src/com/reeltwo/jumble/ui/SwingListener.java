package com.reeltwo.jumble.ui;




import com.reeltwo.jumble.Jumble;
import com.reeltwo.jumble.fast.FastRunner;
import com.reeltwo.jumble.fast.JumbleResult;
import com.reeltwo.jumble.fast.MutationResult;
import com.reeltwo.util.CLIFlags.Flag;
import com.reeltwo.util.CLIFlags;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * A Jumble listener which displays a GUI representation of the run.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class SwingListener implements JumbleListener {
  private JPanel mGUI = null;

  private JLabel mLabel = null;

  private JLabel mScoreLabel = null;

  private JLabel mDoneLabel = null;

  private JProgressBar mProgressBar = null;

  private int mMutationCount = 0;

  private int mCurrentScore = 0;

  public void jumbleRunStarted(String className, List < String > testNames) {
    JFrame frame = new JFrame("Jumble");
    frame.setSize(300, 100);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mGUI = new JPanel();
    frame.getContentPane().add(mGUI);
    mLabel = new JLabel("Jumbling " + className);
    mGUI.add(mLabel);

    mProgressBar = new JProgressBar();
    mGUI.add(mProgressBar);

    mScoreLabel = new JLabel();
    mGUI.add(mScoreLabel);

    mDoneLabel = new JLabel();
    mGUI.add(mDoneLabel);

    frame.setVisible(true);
  }

  public void performedInitialTest(JumbleResult result, int mutationCount) {
    mCurrentScore = 0;
    mMutationCount = mutationCount;
    mProgressBar.setMaximum(mMutationCount);
    mProgressBar.setValue(0);
    mProgressBar.setForeground(Color.GREEN);
    mScoreLabel.setText("Score: 0/" + mMutationCount);
  }

  public void jumbleRunEnded() {
    mDoneLabel.setText("DONE");
  }

  public void finishedMutation(MutationResult res) {
    mProgressBar.setValue(mProgressBar.getValue() + 1);
    if (!res.isFailed()) {
      mCurrentScore++;
      mScoreLabel.setText("Score: " + mCurrentScore + "/" + mMutationCount);
    }
  }

  public void error(String errorMsg) {
    JOptionPane.showMessageDialog(null, errorMsg, "ERROR", JOptionPane.ERROR_MESSAGE);
  }
  
  public static void main(String[] args) throws Exception {
    // Process arguments
    FastRunner jumble = new FastRunner();
    CLIFlags flags = new CLIFlags("SwingListener");
    Flag verboseFlag = flags.registerOptional('v', "verbose", "Provide extra output during run.");
    Flag exFlag = flags.registerOptional('x', "exclude", String.class, "METHOD", "Comma-separated list of methods to exclude.");
    Flag retFlag = flags.registerOptional('r', "return-vals", "Mutate return values.");
    Flag inlFlag = flags.registerOptional('k', "inline-consts", "Mutate inline constants.");
    Flag incFlag = flags.registerOptional('i', "increments", "Mutate increments.");
    Flag orderFlag = flags.registerOptional('o', "no-order", "Do not order tests by runtime.");
    Flag saveFlag = flags.registerOptional('s', "no-save-cache", "Do not save cache.");
    Flag loadFlag = flags.registerOptional('l', "no-load-cache", "Do not load cache.");
    Flag useFlag = flags.registerOptional('u', "no-use-cache", "Do not use cache.");
    Flag classFlag = flags.registerRequired(String.class, "CLASS", "Name of the class to mutate.");
    Flag testClassFlag = flags.registerRequired(String.class, "TESTCLASS", "Name of the unit test classes for testing the supplied class.");
    testClassFlag.setMinCount(0);
    testClassFlag.setMaxCount(Integer.MAX_VALUE);

    flags.setFlags(args);

    jumble.setInlineConstants(inlFlag.isSet());
    jumble.setReturnVals(retFlag.isSet());
    jumble.setIncrements(incFlag.isSet());
    jumble.setOrdered(!orderFlag.isSet());
    jumble.setLoadCache(!loadFlag.isSet());
    jumble.setSaveCache(!saveFlag.isSet());
    jumble.setUseCache(!useFlag.isSet());
    jumble.setVerbose(verboseFlag.isSet());

    String className;
    List < String > testList;

    if (exFlag.isSet()) {
      String[] tokens = ((String) exFlag.getValue()).split(",");
      for (int i = 0; i < tokens.length; i++) {
        jumble.addExcludeMethod(tokens[i]);
      }
    }

    className = ((String) classFlag.getValue()).replace('/', '.');
    testList = new ArrayList < String > ();

    // We need at least one test
    if (testClassFlag.isSet()) {
      for (Iterator it = testClassFlag.getValues().iterator(); it.hasNext();) {
        testList.add(((String) it.next()).replace('/', '.'));
      }
    } else {
      // no test class given, guess its name
      testList.add(Jumble.guessTestClassName(className));
    }
    jumble.runJumble(className, testList, new SwingListener());

  }

}
