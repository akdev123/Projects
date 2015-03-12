/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fighton;

import java.util.*;

/**
 *
 * @author Adarsh
 */
public class RandomizeArray {
    

 
        public static void main(String args[]){
            
            final int[] array = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
            final int iterations = 10;
            final int groupOf = 3;

                for (int i = 0; i < array.length; i += groupOf) {
                    int groupOfRemainder = array.length - i < groupOf ? array.length - i : groupOf;
                    
                    for (int j = 0; j < iterations; j++) {
                        
                        int rnd1 = (int) (Math.random() * groupOfRemainder);
                        int rnd2 = (int) (Math.random() * groupOfRemainder);

                        Object temp = array[i + rnd1];
                        array[i + rnd1] = array[i + rnd2];
                        array[i + rnd2] = (int) temp;
                        }
                }
        
                System.out.println(Arrays.toString(array));
        
        
        }
    
}
