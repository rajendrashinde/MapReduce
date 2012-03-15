package lsh; 

import java.util.ArrayList;


public class LSH_functions {
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
	
	public static String gbucket(Vector s, ArrayList<Vector> direction, Vector direction2, ArrayList<Double> shift, double shift2, int k, double W, double D){
		Vector hs = new Vector(hbucket(s, direction, shift, k , W)); 
		String bucket = "";
		double dot = hs.dot(direction2);
		dot += shift2; 
		int id = (int)(dot/D); 
		bucket += Integer.toString(id);
		return bucket;
	}
}

