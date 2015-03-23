/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fighton;


public class JulianDatePrint {
    
    static int[] daysCount = {31,28,31,30,31,30,31,31,30,31,30,31};
    
    public static boolean isLeapYear(int year){
        
        if(year % 4 != 0)
        {return false;}
        if(year % 100 != 0)
        {return false;}
         if(year % 400 != 0)
        {return false;}
                  
        return true;
       
        
    }
    
    public static int getDayofDate(String date){
        
        String[] datearr = date.split("-");

        int year = Integer.parseInt(datearr[0]);
        int months = Integer.parseInt(datearr[1])-1;
        int days = Integer.parseInt(datearr[2]);
        
        boolean leapyear = isLeapYear(year);
        
        int dayOfYear = 0;

        for (int m = 0; m < months; m++) {

            dayOfYear = dayOfYear + daysCount[m];

            // for february
            if (m == 1 && leapyear) {

                dayOfYear++;

            }

        }
        dayOfYear = dayOfYear + days;
        return dayOfYear;
    }

    
    public static void main(String[] args) {
       
        System.out.println("getDayofDate = " + getDayofDate("2011-12-31"));
        
        
    }

}
