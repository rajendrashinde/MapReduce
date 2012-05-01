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
	
	public static String[] nearest_neighbor(String query_str, String query_ID, ArrayList<String> candidates, double u, int d) {
		double distance = 1000.0; 
		Iterator <String> it = candidates.iterator(); 
		String[] NN = {"1000","-1"};
 
		while (it.hasNext()) {
			String[] junk = it.next().split(":");
 			String candidate_str = junk[0]; 
			String candidate_ID = junk[1]; 
 
			distance = cosine_distance(query_str, candidate_str); 
			 
			if (distance < u && !query_ID.equals(candidate_ID)) {	
				NN[0] = Double.toString(distance);	
				NN[1] = candidate_ID;  
				break;
			}
		}
		
	return NN;
	}
	
	public static Double cosine_distance(String u, String v){
			double dot = 0.0; 
			HashMap<Integer, String> u_comp = new HashMap<Integer, String>();
			double u_norm = 0.0; 
			String[] u_list = u.split(" "); 
			for (int i = 0; i < u_list.length; i++) {
				String[] junk = u_list[i].split(","); 
				if (junk.length == 2){
				u_comp.put(Integer.parseInt(junk[0]), junk[1]); 
				u_norm += Math.pow(Float.valueOf(junk[1].trim()),2);
				}
			}
			u_norm = Math.sqrt(u_norm); 
			
			double v_norm = 0.0; 
			HashMap<Integer, String> v_comp = new HashMap<Integer, String>(); 
			String[] v_list = v.split(" ");
			for (int i = 0; i < v_list.length; i++){
				String[] junk = v_list[i].trim().split(",");
				if (junk.length == 2) {
					v_comp.put(Integer.parseInt(junk[0]), junk[1]); 
					v_norm += Math.pow(Float.valueOf(junk[1].trim()),2);
				}
			}
			v_norm = Math.sqrt(v_norm);
			
			Iterator<Integer> it_ucomp = u_comp.keySet().iterator();
			while (it_ucomp.hasNext()){
				int dir = it_ucomp.next(); 
				if (v_comp.containsKey(dir)) {
					dot += Float.valueOf(u_comp.get(dir).trim())*Float.valueOf(v_comp.get(dir).trim()); 
				}
			}
			
			return 1.0 - dot/(u_norm*v_norm);		
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
