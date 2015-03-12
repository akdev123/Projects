/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sorting;

import java.util.Arrays;

/**
 *
 * @author Adarsh
 */
public class BubbleSort {

    
//    public static void BubbleSort(int[] a){
//        
//        
//        int size = a.length;
//        
//       for (int i = 1)
//        
//        
//    }
//    
    
    public static void main(String[] args) {
        // TODO code application logic here
        
        int[] a = { 4,2,5,6,7,9,98,109,93,77};
        
        System.out.println("length = " + a.length);
        
        for(int i = 0; i < 10;i++){
            
            for (int j=1; j < 9;j++){
                
                   if(a[j-1] > a[j]){
                       int temp = a[j-1];
                       a[j-1] = a[j];
                       a[j] = temp;
                       
                   }
                
                
            }
            
            
            
        }
        
        System.out.println("array = " + Arrays.toString(a));
        

    }

}
