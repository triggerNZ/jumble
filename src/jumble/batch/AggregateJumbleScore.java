package jumble.batch;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jumble.JumbleResult;
import jumble.JumbleScore;
import jumble.Mutation;

/**
 * Class representing the score of a jumbled class.
 * @author Tin Pavlinic
 */
public class AggregateJumbleScore implements JumbleScore {
    private String mClassName;
    private int mMutationCount = -1;
    private Mutation [] mMutations;
    private Set mFailedTests = new TreeSet();
    private Set mAllTests = new TreeSet();
    /**
     * Constructs an empty score object for the class className
     * @param className name of class
     */
    public AggregateJumbleScore(String className) {
        mClassName = className;
    }
    /**
     * Adds the results of a single Jumble run to the result set
     * @param res the result to add
     */
    public void addResult(JumbleResult res) {
        if(!res.getClassName().equals(getClassName())) {
            throw new RuntimeException();
        }
        mAllTests.add(res.getTestName());
        //If the test failed, not a lot needs to be done
        if(res.testFailed()) {
            mFailedTests.add(res.getTestName());
            return;
        }
        
        //First check that the mutation counts match
        if(getMutationCount() == -1) {
            //First result
            mMutationCount = res.getMutationCount();
            mMutations = new Mutation[mMutationCount];
        } else if(getMutationCount() != res.getMutationCount())
            throw new RuntimeException("Invalid mutation count");
        
        //Update the mutations. Passes and timeouts override 
        //failures. Passes override timeouts. Everything overrides
        //null
        Mutation [] cur = res.getAllMutations();
        for(int i = 0; i < mMutations.length; i++) {
            if(mMutations[i] == null || mMutations[i].isFailed()) {
                mMutations[i] = cur[i];
            } else if(mMutations[i].isTimedOut()) {
                if(cur[i].isPassed())
                    mMutations[i] = cur[i];
            }
            //Else we have already passed so nothing needs to be done  
        }
    }
    /** 
     * Gets the number of mutation points for the class being jumbled.
     * @return the number of mutation points.
     */
    public int getMutationCount() {
        return mMutationCount;
    }
    /** 
     * Gets the results of all the mutations of the class.
     * @return all the mutations
     */
    public Mutation [] getAllMutations() {
        return mMutations;
    }
    /**
     * Gets the names of the tests which have failed for this class
     * @return the failed tests
     */
    public String [] getFailedTests() {
        return (String[])mFailedTests.toArray(new String[0]);
    }
    /**
     * Gets the name of the class being jumbled
     * @return the class name
     */
    public String getClassName() {
        return mClassName;
    }
    
    /** 
     * Joins a set of @see JumbleResult into a set of JumbleScores
     * @param results the results to convert
     * @return an array of JumbleScores based on results
     */
    public static AggregateJumbleScore [] resultsToScores(JumbleResult [] results) {
        Map map = new HashMap();
        AggregateJumbleScore score = null;
        for(int i = 0; i < results.length; i++) {
            JumbleResult cur = results[i];
            
            if(map.containsKey(results[i].getClassName())) {
                score = (AggregateJumbleScore)map.get(results[i].getClassName());
            } else {
                score = new AggregateJumbleScore(results[i].getClassName());
                map.put(score.getClassName(), score);
            }
            score.addResult(results[i]);
        }
        
        Collection vals = map.values();
        return (AggregateJumbleScore [])vals.toArray(new AggregateJumbleScore[0]);
    }

    /**
     * Returns a string representation of this object.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        StringBuffer missed = new StringBuffer();
        
        buf.append("Class: " + getClassName() + "\n");
        Mutation [] mutations = getAllMutations();
        
        if(mutations.length > 0) {
            for(int i = 0; i < mutations.length; i++) {
                if(mutations[i].isPassed())
                    buf.append(".");
                else if(mutations[i].isTimedOut())
                    buf.append("T");
                else if(mutations[i].isFailed()) {
                    buf.append("M");
                    missed.append(mutations[i] + "\n");
                }  else throw new RuntimeException("Invalid mutation " + 
                        mutations[i]);
            }
            buf.append("\n\n");
            if(missed.length() > 0) {
                buf.append("Missed mutations:\n");
                buf.append(missed);
                buf.append("\n");
            }
        } else {
            buf.append("No possible mutations.\n");
        }
        String [] failed  = getFailedTests();
        
        if(failed.length > 0) {
            buf.append("Failed Tests: ");
        
            for(int i = 0; i < failed.length; i++) {
                buf.append(failed[i] + "\n");
            }
            buf.append("\n");
        }
        
        return buf.toString();
    }
    /**
     * Gets all the tests run for this class
     * @return the tests
     */
    public String [] getAllTests() {
        return (String [])mAllTests.toArray(new String[0]);
    }
}
