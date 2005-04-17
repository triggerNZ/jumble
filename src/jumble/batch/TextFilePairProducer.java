package jumble.batch;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class TextFilePairProducer  implements ClassTestPairProducer {
    private Map mMap;
    
    public TextFilePairProducer(String fileName) 
    throws IOException {
        mMap = new HashMap();
        
        File f = new File(fileName);
        if(f.exists() && f.isFile()) {
            BufferedReader in = new BufferedReader(new FileReader(f));
                
            String cur;
            while((cur = in.readLine()) != null) {
                StringTokenizer tokens = new StringTokenizer(cur);
                String className = tokens.nextToken();
                String testName = tokens.nextToken();
                
                //either add the test to the test set for the class
                //or create a new set
                if(mMap.containsKey(className)) {
                    Set s = (Set)mMap.get(className);
                    s.add(testName);
                } else {
                    Set s = new HashSet();
                    s.add(testName);
                    mMap.put(className, s);
                } 
            }
        } else throw new RuntimeException("Invalid file given");
        
    }
    
    public ClassTestPair [] producePairs() {
        List pairs = new ArrayList();
        Set classes = mMap.keySet();
        
        Iterator classIt = classes.iterator();
        while(classIt.hasNext()) {
            String className = (String)classIt.next();
            
            Set tests = (Set)mMap.get(className);
            Iterator testIt = tests.iterator();
            while(testIt.hasNext()) {
                String testName = (String)testIt.next();
                pairs.add(new ClassTestPair(className, testName));
            }
        }
        return (ClassTestPair [])pairs.toArray(new ClassTestPair[pairs.size()]);
    }
}
