package experiments;

public class JumblerExperiment {
    public int add(int x, int y) {
	if(x > y)
	    return x + y;
	else
	    return x - y;
    }
    
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
