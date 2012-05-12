package lsh; 

import java.util.ArrayList;
import java.util.Random;
import java.util.HashSet;
import java.util.Set; 
import java.util.HashMap; 
import java.util.Arrays;
import java.util.Collections; 
import java.util.Iterator; 
import java.util.Comparator;
import java.lang.Math;

public class LSH_functions {
	
	static String mode = "L2"; 
	static boolean debug = true;
	
	public static String hbucket(Vector s, ArrayList<Vector> direction, ArrayList<Double> shift, int k, double W) {
		String bucket = "";
		for (int i = 0; i < k; i++) {
			double dot = s.dot(direction.get(i));
			dot += shift.get(i);
			int id = (int)(dot/W); 
			bucket += Integer.toString(id);
			if (i < k - 1)
				bucket += " ";                    
		}   
		return bucket;
	}
	
	public static String hbucket(Vector s, int k, double W) {
		String bucket = "";
		int d = s.dimension();
		Random Rng = new Random();
		Vector u;  
		for (int i = 0; i < k; i++) {
			Rng.setSeed(i);
			u = Math_functions.random_vector(Rng, d, 0.0, 1.0); 
			double shift = W*Rng.nextDouble();
			double dot = s.dot(u);			
			dot += shift;
			int id = (int)(dot/W); 
			bucket += Integer.toString(id);
			if (i < k - 1)
				bucket += " ";                    
		}   
		return bucket;
	}
	
	public static String[] hbucket(Vector_sparse s, int k, double W) {
		String bucket = "";
		int d = s.dimension();
		Random Rng = new Random();
		String[] out = {"", ""}; 
		String x = "";  
		
		for (int i = 0; i < k; i++) {
			Iterator <Integer> it_j = s.data.keySet().iterator(); 
			double shift = W*Rng.nextDouble();
			double dot = 0.0; 
			while (it_j.hasNext()) {
				int j = it_j.next(); 
				Rng.setSeed(7919*i+j);
				dot += Rng.nextGaussian()*s.data.get(j);
			}	
			
			if (s.norm > 0)
				dot = dot/s.norm;  //Normalization of input vector
			dot += shift; 
			
			if (mode.equals("L2")) { 
				int id = (int) Math.floor(dot/W);
			
				x += String.valueOf(dot - id*W);
				bucket += Integer.toString(id);
				if (i < k - 1) {
					bucket += " ";
					x += " "; 
				}   
			}   
			
			if (mode.equals("cosine")) { 
				int id; 
				if (dot > 0)
					id = 1; 
				else
					id = 0; 
				bucket += Integer.toString(id);
				x += String.valueOf(Math.abs(dot)); 
				if (i < k - 1) {
					bucket += " ";
					x += " ";
				}
			   //x does not make sense in this case.
			}
			              
		}   
		out[0] = bucket;
		out[1] = x; 
		return out; 
	}
	
	public static String gbucket(Vector s, ArrayList<Vector> direction, Vector direction2, ArrayList<Double> shift, double shift2, int k, double W, double D){
			Vector hs = new Vector(hbucket(s, direction, shift, k , W)); 
			String bucket = "";
			double dot = hs.dot(direction2);
			dot += shift2; 
			int id = (int)(dot/D); 
			bucket += Integer.toString(id);
			return bucket;
	}
	
	public static String gbucket(Vector s, Vector direction2, double shift2, int k, double W, double D){
		Vector hs = new Vector(hbucket(s, k , W)); 
		String bucket = "";
		double dot = hs.dot(direction2);
		dot += shift2; 
		int id = (int)(dot/D); 
		bucket += Integer.toString(id);
		return bucket;
	}
	
	public static String gbucket(String hbucket, Vector direction2, double shift2, double D){
		Vector hs = new Vector(hbucket); 
		String bucket = "";
		double dot = hs.dot(direction2);
		dot += shift2; 
		int id = (int)(dot/D); 
		bucket += Integer.toString(id);
		return bucket;
	}
	
	public static Set<String> GetOffsetBuckets(Vector_sparse q, int k, int L, double W) {
		
		Set<String> out = new HashSet<String> ();  
		String [] hbucket_x = hbucket(q, k, W);  
		String hbucket = hbucket_x[0];
		String x_str = hbucket_x[1];
		out.add(hbucket);
		
		if (debug) System.out.println(hbucket); 
		String [] hbucket_split = hbucket.trim().split(" ");  
		String [] x_split = x_str.trim().split(" "); 
		
		int m; 
		if (mode.equals("cosine"))
			m = 1;
		else 
			m = 2; 
		
		final Float [] x = new Float[m*k]; 
		int [] hbucket_int = new int[k]; 
			
		for (int i = 0; i < k; i++){
			hbucket_int[i] = (int)Integer.parseInt(hbucket_split[i]); 
			x[i] = Float.parseFloat(x_split[i]);
			if (mode.equals("L2")) 
				x[k+i] = (float)W - x[i]; 
		}
		
		// MYSORT: Sort according to x values. 
		final Integer[] sorted_order = new Integer[m*k];
		for (int i = 0; i < m*k; i++) sorted_order[i] = i;		
		
		Arrays.sort(sorted_order, new Comparator<Integer> () {
			@Override public int compare(final Integer o1, final Integer o2) {
			        return Float.compare(x[o1], x[o2]);
			    }	
		});
		
		MinHeap H = new MinHeap(L, x, sorted_order); 
		
		Set<Integer> A = new HashSet<Integer> (); 
		A.add(0);  
		H.insert(A); 	
		if (debug) H.print(); 
		
		for (int i = 0; i < L; i++) {
			  
			 A = H.removemin();
			
			 if (debug) System.out.println(ToString(A));
			 if (isValid(A, k)){
				int [] Delta = ToDelta(A, k);
				String temp = ""; 
				for (int j = 0; j < k; j++) {
					temp += Integer.toString(hbucket_int[j] + Delta[j]);
					if (j < k-1)
						temp += " "; 
				}
				out.add(temp); 
			 } 	 
			 
			 Set<Integer> B = shift(A); 
			 if(debug) System.out.println(ToString(B)); 
			 if (isValid(B,k))	
				H.insert(B); 
			 Set<Integer> C = expand(A);
			
			 if (debug) System.out.println(ToString(C)); 
			 if (isValid(C,k))
			 	H.insert(C); 
			 if(debug) H.print();
			 if (H.size <= 1) break; 
		}
		return out;  
	}
	
	public static String ToString(Set<Integer> A){
		String out = ""; 
		for (Integer i : A) 
			out += i + " ";
		out = out.trim();  
		return out; 		
	}
	
	public static int[] ToDelta(Set<Integer> A, int k){
		int[] out = new int[k];
		for(int j = 0; j < k; j++) out[j] = 0;  
		for(Integer i : A){
			if (i < k)
				out[i] += 1;
			else
				out[i-k] -= 1; 
		}
		return out; 
	}

	public static Set<Integer> shift(Set<Integer> A){
		 
		Set<Integer> B = new HashSet<Integer> ();
		for (Integer i: A)
		 	B.add(i); 
		Integer i = Collections.max(B);
		B.remove(i);
		B.add(new Integer(i+1));
		return B;
	}

	public static Set<Integer> expand(Set<Integer> A){
		Set<Integer> B = new HashSet<Integer> ();
		for (Integer i: A)
		 	B.add(i);
		Integer i = Collections.max(B);
		B.add(Integer.valueOf(i+1));
		return B; 
	}
	
	public static boolean isValid(Set<Integer> A, int k){
		for (Integer i : A) {
			if (i >= 2*k) 
				return false;
			if (A.contains(2*k +1 - i))
				return false;
			}
		return true; 	
	}
}

