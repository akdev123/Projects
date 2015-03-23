/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fighton;


class HDTV implements Comparable<HDTV> {
        
        private int size;
        private String name;
        
        HDTV (int s , String n){
            
            this.size = s;
            this.name = n;
            
        }
        
        public String getName(){
            
            return name;
        }
        
        public int getSize(){
            
            return size;
        }

    @Override
    public int compareTo(HDTV ob){
        
        
        if(this.getSize() > ob.getSize())
            return 1;
        else if (this.getSize() < ob.getSize())
            return -1;
        else
            return 0;
        
        
    }
        
        
    }
    



public class ComparablePractice {
    
    
    
    

    
    public static void main(String[] args) {
        
        
        HDTV tv1 = new HDTV(55, "Samsung");
		HDTV tv2 = new HDTV(60, "Sony");
 
		if (tv1.compareTo(tv2) > 0) {
			System.out.println(tv1.getName() + " is better.");
		} else {
			System.out.println(tv2.getName() + " is better.");
		}
	}
       
    }


