
package sorting;


public class QuickSort {

   
    public static void QuickAlgo(int[] a, int left, int right) {

        int index = partition(a, left, right);

        if (left < index - 1) {
            QuickAlgo(a, left, index - 1);
        }
        if (index < right) {
            QuickAlgo(a, index, right);
        }

    }
    
    static int partition(int[] a, int left, int right) {

        int pivot = a[(left + right) / 2];

        while (left <= right) {

            while (a[left] < pivot) {
                left++;
            }
            while (a[right] > pivot) {
                right--;
            }

            if (left <= right) {

                int temp = a[left];
                a[left] = a[right];
                a[right] = temp;
                left++;
                right--;

            }

        }

        return left;
    }

    
    public static void main(String[] args) {

    int[] a = {9,11,45,232,78,54,23};  
    
    
    QuickAlgo(a,0,a.length-1);
    
    for ( int i = 0 ; i< a.length; i++){
        System.out.println(a[i]);
    }

    }

}
