/*
 * Created on May 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble.batch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import jumble.dependency.DependencyExtractor;
import jumble.util.RTSI;

/** Class which produces ClassTestPairs based on dependency analysis. Operates on
 * a given set of classpaths
 * @author Tin Pavlinic
 */
public class DependencyPairProducer implements ClassTestPairProducer {
    private HashSet mPackages;
    
    private HashSet mIgnore = new HashSet();
    /**
     *  Sets the packages to be examines
     * @param paths the package list
     */
    public void setPackages(String [] paths) {
        mPackages = new HashSet();
        for(int i = 0; i < paths.length; i++) {
            mPackages.add(paths[i]);
        }
    }
    /**
     * Gets the packages to be examined.
     * @return the list of packages.
     */
    public String [] getPackages() {
        return (String[])mPackages.toArray(new String[0]);
    }
    /**
     * Constructor.
     * Note: @see setPackages must be called before the producer can be
     * used.
     */
    public DependencyPairProducer() {
       mPackages = new HashSet();
    }
    public DependencyPairProducer(String [] classpaths) {
        mPackages = new HashSet();
        for(int i = 0; i < classpaths.length; i++) {
            mPackages.add(classpaths[i]);
        }
    }
    
    public DependencyPairProducer(String filename) throws IOException {
        this();
        
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        
        String cur;
        while((cur = reader.readLine()) != null) {
            if(!cur.trim().equals("")) {
                StringTokenizer tok = new StringTokenizer(cur);
                String command = tok.nextToken();
                String argument = tok.nextToken();
                //System.out.println("Command: " + command + " arg: " + argument);
                if(command.equals("testpackage")) {
                    mPackages.add(argument);
                } else if(command.equals("excludedependency")) {
                    this.addIgnoredPrefix(argument);
                } else {
                    throw new RuntimeException("Invalid command");
                }
            }
        }
    }

    public void addIgnoredPrefix(String ignore) {
        mIgnore.add(ignore);
    }
    
    public ClassTestPair[] producePairs() {
        HashSet h = new HashSet();
        try {
            Set testClasses = getAllTests();
            Iterator it = testClasses.iterator();
            
            while(it.hasNext()) {
                String testClass = (String)it.next();
                DependencyExtractor de = new DependencyExtractor(testClass);
                Set ignore = de.getIgnoredPackages();
                ignore.add("junit");
                ignore.addAll(mIgnore);
                Collection dependencies = de.getAllDependencies(true);
                
                Iterator it2 = dependencies.iterator();
                while(it2.hasNext()){
                    String className = (String)it2.next();
                    //we don't want inner classes
                    if(className.indexOf("$") < 0)
                        h.add(new ClassTestPair(className,testClass));
                }
            }
            
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (ClassTestPair [])h.toArray(new ClassTestPair[h.size()]);
    }
    
    public Set getAllTests() throws ClassNotFoundException {
        HashSet ret = new HashSet();
        for(int i = 0; i < getPackages().length; i++) {
            ret.addAll(RTSI.find(getPackages()[i], "junit.framework.TestCase"));
        }
        return ret;
    }

    public static void main(String[] args) {
    }
}
