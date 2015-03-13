/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fighton;

import java.util.Random;


public class RamdomMethodPractice {

    
    public static int randomGenerator(int min , int max){
        
        Random rm = new Random();
        
        int RandomNum = rm.nextInt((max-min)+1) + min;
        
        return RandomNum;
        
    }
    
    
    public static void main(String[] args) {
        
       
        
        System.out.println("Random Number = " + randomGenerator(5,10));
       
    }

}
