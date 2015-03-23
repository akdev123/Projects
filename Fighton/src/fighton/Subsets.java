/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fighton;


public class Subsets {

    
    public static void allSubsets(int num, String str){
        
        if(num == 0){
            
            System.out.println(str);
        } else if (num > 0){
            
            for (int i = 0 ; i <= num ; i++){
                
                allSubsets(num - i , str + "" + Integer.toString(i));
            }
            
        }
        
    }
    
    
    public static void main(String[] args) {
       
        allSubsets(4,"");
        
        
        
    }

}
