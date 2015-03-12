/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fighton;

import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Adarsh
 */
public class ArrayManipulation {
    
    
    public static void main(String args[]){
        
        
        List<String> firstList = new ArrayList<String>();
        Collections.addAll(firstList,"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z");
        
        List<String> secondList = new ArrayList<String>();
        
        
        
        Iterator<String> it = firstList.iterator();
        
        while(it.hasNext()){
            
            
            String str = it.next();
            
            if(str.equals("A")){
                secondList.add("A");
                it.remove();
            }else {
                
                if(str.equals("E")){
                    secondList.add("E");
                    it.remove();
                }else {
                    
                   if(str.equals("I")){
                    secondList.add("I");
                    it.remove();
                    }else {
                        
                            if(str.equals("O")){
                            secondList.add("O");
                            it.remove();
                            } else {
                                
                                if(str.equals("U")){
                                 secondList.add("U");
                                 it.remove();
                                
                            }
                           }
                       }
                   }
            }
        
         
 

        

    
}
        for (String first : firstList)
                      System.out.println("First List Contains : "+ first);
        for (String second : secondList)
                       System.out.println("Second List Contains : "+ second);
        
        
       
}
}
