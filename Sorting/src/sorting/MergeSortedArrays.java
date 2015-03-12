/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sorting;



public class MergeSortedArrays {

	
	public static void main(String[] args) {
		int []a = {2,15,22,47,51};
		int []b = {14,18,26,45,49,56,78};
		
		// Array C of sum of size of the two sorted array A and B
		int []c = new int[a.length+b.length];
		
		merge(a,b,c);
		System.out.print("Array a: ");
		printArray(a);
		System.out.println();
		System.out.print("Array b: ");
		printArray(b);
		System.out.println();
		System.out.print("Array c: ");
		printArray(c);
	}

	public static void merge(int []a, int []b, int []c){
		int cursorA = 0,cursorB = 0,cursorC = 0;
		int sizeA = a.length;
		int sizeB = b.length;
		
		// Runs until neither array is empty
		while(cursorA < sizeA && cursorB < sizeB){
			// Compare the items of two arrays and copy the smaller item into to third array
			if(a[cursorA] < b[cursorB]){
				c[cursorC] = a[cursorA];
                                cursorC++;
                                cursorA++;
			}else{
				c[cursorC++] = b[cursorB++];
			}
		}
		
		// If array B's cursor scanned and compared all the items of the array
		// but array A's is not
		while(cursorA < sizeA){
			c[cursorC++] = a[cursorA++];
		}
		
		// If array A's cursor scanned and compared all the items of the array
		// but array B's is not
		while(cursorB < sizeB){
			c[cursorC++] = b[cursorB++];
		}
	}
	
	public static void printArray(int []array){
		for(int i : array){
			System.out.print(i+" ");
		}
	}
}


//public class MergeSortedArrays {
//
//    
//    public static void main(String[] args) {
//
//        int[] a = {2, 15, 22, 47, 51};
//        int[] b = {14, 18, 26, 45, 49, 56, 78};
//
//        int[] c = new int[a.length + b.length];
//
//        merge(a, b, c);
//        System.out.println("Array A");
//        printArray(a);
//        System.out.println("Array B");
//        printArray(b);
//        System.out.println("Array C");
//        printArray(c);
//
//    }
//    
//    public static void merge (int[] a , int[] b,int[] c){
//        
//        
//        int lenA = a.length;
//        int lenB = b.length;
//        
//        int i = 0 , j = 0 , k = 0;
//        
//        while (i < lenA && j < lenB) {
//
//            if (a[i] < b[j]) {
//                c[k++] = a[i++];
//            } else {
//                c[k++] = b[j++];
//
//            }
//
//        }
//        
//        // If array B's element are scanned but there are still some elements left in array A. 
//        while (i < lenA){
//            c[k++] = a[i++];
//        }
//        
//        // If array A's element are scanned but there are still some elements left in array B. 
//        while(j< lenB){
//            
//            c[k++] = b[i++];
//        }
//        
//        
//        
//        
//        
//    }
//    public static void printArray(int[] array){
//        
//        for(int i : array){
//            
//            System.out.println(i);
//            
//        }
//    }
//
//}
