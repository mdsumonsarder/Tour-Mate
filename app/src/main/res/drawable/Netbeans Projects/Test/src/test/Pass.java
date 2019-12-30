
package test;

import java.util.Scanner;

public class Pass {
    
    public void getMark(){
        
        int[] a = new int[100];
        
        Scanner sc = new Scanner(System.in);
        
        int n, sum=0;
        System.out.println("How many number?");
        n = sc.nextInt();
        
        for (int i = 0; i < n; i++) {
            a[i] = sc.nextInt();  
            sum = sum + a[i];
        }
        
        int avg = sum/n;
        
        System.out.println("average= "+avg);
        
        
      
        
        
        
    }
}
