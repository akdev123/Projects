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
public class PrintNumbers {
    
    public static void main (String[] args){
        
        for (int i = 0;i <=100 ; i++){
            
            if ( i % 3 == 0 && i % 5 == 0){
                
              System.out.println("ThreeFive");
              
            }else {
                     if (i % 3 == 0){
                         
                         System.out.println("Three");
                     }
                     else { 
                            if (i % 5 == 0){
                            System.out.println("Five");
                            }
                            else{
                                System.out.println(i);
                            }
                            
                            
                     }
                
                  }
            
        }
        
    }
    
}
