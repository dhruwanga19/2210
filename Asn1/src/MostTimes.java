import java.util.Arrays;

public class MostTimes {

    public int most_times(int[] A, int n){
    // Input: Array A storing n  integer values
    // Output: The value that appears the largest number of times in $A$. If several values appear
    //         in $A$ the largest number of times, the algorithm must return the smalles among these
    //         values.
    int element = 0;
    int count = 0;
    
    Arrays.sort(A);
    
    for(int i = 0; i < n; i++) {
    	int temp = A[i];
    	int tempCount = 0; 
    	for(int j =0; j < n; j++) {
    		if(A[j] == temp) {
    			tempCount++;
    			
    			if(tempCount > count) {
    				element = temp;
    				count = tempCount;
    			}
    			
    		}
    	}
    }
    return element;	
    }
}
