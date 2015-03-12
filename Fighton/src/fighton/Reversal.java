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
public class Reversal {
    
    // Reversing a String
    
    public static void main (String args[]){
        
//        String Str = "adarsh";
//        
//        String rev = "";
//        
//        int length = Str.length();
//        
//        for(int i = length-1;i >= 0 ; i--){
//            
//         rev = rev + Str.charAt(i);   
//            
//        }
//        
//        System.out.println("Reverse" + ":" + rev);
        
        
        int num = 347;
        int rev = 0;
        
        
        while(num > 0){
            
            int rem = num%10;
            num = num/10;
            rev = rev*10 + rem ;
            
        }
        
        System.out.println("Reverse" + ":" + rev);
    }
    
}
