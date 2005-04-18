package experiments;
/** A class used for the testing of jumble. Includes an incorrect
 * addition function (for coverage) and a multiply function (for 
 * timeouts)
 * @author Tin Pavlinic
 */
public class JumblerExperiment {
    /** Adds x and y 
     * @param x the first argument
     * @param y the second argument
     * @return the sum of x and y
     */
    public int add(int x, int y) {
	if(x > y)
	    return x + y;
	else
	    return x - y;
    }
    /** Multiplies x and y 
     * @param x the first argument
     * @param y the second argument
     * @return the product of x and y
     */
    public int multiply(int x, int y) {
        int sum = 0;
        int counter = 0;
        while(counter < y) {
            sum = sum + x;
            int temp = counter+1;
            counter = temp;
        }
        
        return sum;
    }
}
