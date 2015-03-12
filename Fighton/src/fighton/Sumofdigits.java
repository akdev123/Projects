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
public class Sumofdigits {
    
    
    public static int SumofDigitsonly(int x){
            
            return x == 0 ? 0 : x % 10 + SumofDigitsonly(x/10);
            
            
        }
    
    public static void main(String args[]){
        
        
        System.out.println("Sum of digits" + SumofDigitsonly(123));
        
        
         
        
        
        
    }
    
}
