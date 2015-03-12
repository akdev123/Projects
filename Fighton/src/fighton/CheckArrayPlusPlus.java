/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fighton;


public class CheckArrayPlusPlus {

    
    public static void main(String[] args) {
        
        int[] a = {12,23,35,47,58,67};
        
        
        for (int i = 0;i<a.length;i++){
        System.out.println("a = " + a[i++]);
        }
    }

}
