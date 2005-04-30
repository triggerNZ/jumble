/*
 * Created on Apr 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble.util;

/** Class which simply prints out its command line arguments.
 * Used for the testing of JavaRunner
 * @author Tin Pavlinic
 */
public class DisplayArguments {

    public static void main(String[] args) {
        for(int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
        }
    }
}
