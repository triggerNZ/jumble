package experiments;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class for testing LVTT bug.
 * 
 * @author Tin Pavlinic
 * @version $Revision 1.0 $
 */
public class LVTT {
  public int countEntries() {
    final Map<Character, String> map = new HashMap<Character, String>();

    for (char c = 'a'; c < 'z'; c++) {
      map.put(c, "" + (char) (c + 1));
    }
    map.put('z', "a");

    StringBuffer buf = new StringBuffer();
    for (Entry<Character, String> e : map.entrySet()) {
      buf.append(e.getValue());
    }
    return buf.length();
  }
}
