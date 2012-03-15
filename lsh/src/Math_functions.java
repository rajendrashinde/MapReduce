package lsh; 

import java.lang.Integer; 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.HashSet; 
import java.util.Set;
import java.util.List; 
import java.util.HashMap; 

public class Math_functions {
	public static Vector_ID nearest_neighbor(Vector query, ArrayList<Vector_ID> Candidates, double u, int d) {
		double distance = 1000.0; 
		Iterator <Vector_ID> it = Candidates.iterator(); 
		Vector_ID NN = new Vector_ID(new Vector(d), "-1");
		while (it.hasNext()) {
			Vector_ID Candidate = it.next(); 
			if (query.distanceTo(Candidate.v) < u) {
				distance = query.distanceTo(Candidate.v); 
				NN = Candidate; 
				break;
			}
		}
	return NN;
	}
	
	public static Vector_ID nearest_neighbor(Vector query, String query_ID, ArrayList<Vector_ID> Candidates, double u, int d) {
		double distance = 1000.0; 
		Iterator <Vector_ID> it = Candidates.iterator(); 
		Vector_ID NN = new Vector_ID(new Vector(d), "-1");
		while (it.hasNext()) {
			Vector_ID Candidate = it.next(); 
			if (query.distanceTo(Candidate.v) < u && !query_ID.equals(Candidate.id)) {
				distance = query.distanceTo(Candidate.v); 
				NN = Candidate; 
				break;
			}
		}
	return NN;
	}

	public static Vector random_vector(Random Rng, int d, double mu, double sigma) {
		double junk[] = new double[d]; 
		for ( int i = 0; i < d; i++)
		    junk[i] = mu + sigma*Rng.nextGaussian(); 
		return new Vector(junk); 
	}

	public static <Integer> Set<Integer> randomSample( List<Integer> items, int m) {
		//Implementation from The Java Explorer: Taking Random Samples
		HashSet <Integer> res = new HashSet <Integer> (); 
		int n = items.size();
		Random rnd = new Random (10042); 
		if (m > n/2) {
			Set <Integer> negativeSet = randomSample(items, n-m);
			for ( Integer item: items) {
				if (!negativeSet.contains(item) )
					res.add(item);
			}
		}	
		else {
			while (res.size() < m) {
				int randPos = rnd.nextInt (n);
				res.add(items.get(randPos));
			}
		}
		return res; 	
	}

	public static Set<Integer> randomSample( int n, int m) {
		//Wrapper around randomSample
		// returns a sample of size m from the list (0,1,.. n-1)
		HashSet <Integer> res = new HashSet <Integer> (); 
		Random rnd = new Random (10042); 
 		
		if (m > n/2) {
			Set<Integer> negativeSet = randomSample(n, n-m);
			for ( int i = 0; i < n; i++) {
				if (!negativeSet.contains(i) )
					res.add(new Integer(10));
			}
		}	
		else {
			while (res.size() < m) {
				int randPos = rnd.nextInt (n);
				res.add(new java.lang.Integer(randPos));
			}
		}
		return res;	
	}
}
