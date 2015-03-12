/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fighton;

/**
 *
 * @author Adarsh
 */
public class BubbleSort {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        int[] a = {12,0,6,7,3,456};
        
        int n = a.length;
        
        for (int i = 0; i < n ; i++){
            
            for (int j=1;j<(n-1);j++){
                
                if(a[j-1]>a[j]){
                    int temp = a[j-1];
                    a[j-1] = a[j];
                    a[j] = temp;
                    
                }
                
                
            }
            
            
            
        }//END of Outer FOR
        
        for(int k = 0 ; k < n ; k++){
            
            System.out.println("Sorted Array"+a[k]);
        }
    }
    
}
