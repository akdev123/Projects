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
public class ShuffleAgain {
    
    public static void main (String args[]){
            ArrayList<Integer> cards = new ArrayList<Integer>();
            for(int i=1;i<=11;i++)
            {
            cards.add(i);
            }
            Collections.shuffle(cards.subList(0, 3));
            Collections.shuffle(cards.subList(3, cards.size()));
            Collections.rotate(cards, new Random().nextInt(cards.size() - 3));
            
            
            
            System.out.println (cards);
    
    }
    
    
    
    
}
