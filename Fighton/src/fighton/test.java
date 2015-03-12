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
public class test {
    
    public static void main (String args[]){
        
        
        String output = "";
        int[][] twod = new int[][]{{1,2},{3,4},{5,6}};
        
        System.out.println("Rows" + twod.length);
        System.out.println("Columns" + twod[0].length);
        
        
        for (int row = 0; row < twod.length; row++) {
            for (int col = 0; col < twod[row].length; col++) {
                output += " " + twod[row][col];
            }
            output += "\n";
        }
        
        System.out.println(output);
    }
    
}
