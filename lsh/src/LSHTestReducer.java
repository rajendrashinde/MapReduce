package lsh; 

import java.io.IOException;;
import java.util.ArrayList; 
import java.util.Iterator;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;

import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Partitioner; 
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter; 
import org.apache.hadoop.mapred.lib.HashPartitioner; 
import org.apache.hadoop.io.IntWritable; 
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.WritableComparator; 
import org.apache.hadoop.io.WritableComparable; 
import org.apache.hadoop.io.Text; 
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured; 

import org.apache.hadoop.mapred.RunningJob;
// import org.apache.hadoop.mapred.Counters.Group;
import org.apache.hadoop.mapred.Counters;
//import org.apache.hadoop.mapred.Counters.Group;

public class LSHTestReducer {

	public static enum ReducerLoad {
		DATA, QUERY	
	};

	public static class Reduce_g extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
	
		private Text one = new Text();
		private Text two = new Text();   
		public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException { 
			Random Rng = new Random(0);
			Parameters P = new Parameters (); 
			double W = P.W; 
			int k = P.k; 
			int L = P.L; 
			int d = P.d; 
			double u = P.u; 
			double c = P.c; 
			double D = P.D; 
			ArrayList<Vector> direction = new ArrayList<Vector> (); 
			ArrayList<Double> shift = new ArrayList<Double> ();
			for(int i = 0; i < k; i++){
				direction.add (Math_functions.random_vector(Rng, d, 0.0, 1.0));
				shift.add (W*Rng.nextDouble());
			} 
			Random Rng_G = new Random(1000);
			Vector direction2 = Math_functions.random_vector(Rng_G, k, 0.0, 1.0);
			double shift2 = D*Rng_G.nextDouble();
			HashMap<String, ArrayList<Vector_ID>> lsh_table = new HashMap<String, ArrayList<Vector_ID>> ();
			HashMap<String, String> query_container = new HashMap<String, String> (); 
		
			String key_array[] = key.toString().split(" ");
			String g_bucket = key_array[0]; 
 	
			int data_count = 0; int query_count = 0;
			while (values.hasNext()) {
				String line = values.next().toString();
				String[] result = line.split(", ");
				String point_str = result[0];
				String point_type = result[1];
				String point_ID = result[2];

			   	System.out.println(g_bucket + " : Data: " + data_count + ", Query: " + query_count); 
				if (point_type.equals("data")){
					data_count += 1; 
					reporter.incrCounter(ReducerLoad.DATA, 1);
					String h_bucket = result[3]; 		
					one.set("data: " + point_ID); 
					two.set(h_bucket);
					output.collect(one, two); 
				 }
			    
				if (point_type.equals("query")){
				//if (point_type.equals("query") && data_count > 0 ){
					reporter.incrCounter(ReducerLoad.QUERY, 1);
					query_count += 1;
					String query_ID = point_ID; 
					String query_str = point_str; 
					one.set("query: " + point_ID);

					Vector query = new Vector(query_str); 
					Vector perturbation = new Vector (d);
					Vector offset = new Vector (d); 
					String h_bucket = "";  
					Random Rng2 = new Random(query_str.hashCode());
					for (int i = 0; i < L; i++) {
						perturbation = Math_functions.random_vector(Rng2, d, 0, 1.0*u/(c*Math.sqrt(d)));
						offset = query.plus(perturbation);
						if (g_bucket.equals(LSH_functions.gbucket(offset, direction, direction2, shift, shift2, k, W, D) )) { 
							h_bucket = LSH_functions.hbucket(offset, direction, shift, k, W);
							two.set(h_bucket + " in G:" + g_bucket); 
							output.collect(one, two);  
						}
					}

				}
			}
		}
	}

		public static class Reduce_h extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
			private Text one = new Text();
			private Text two = new Text();   
			public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException { 
				
				String h_bucket = key.toString();
				while (values.hasNext()) {
					String line = values.next().toString();
					String[] result = line.split(", ");
					String point_type = result[1];
					String point_ID = result[2];
				    
					if (point_type.equals("data")){
						reporter.incrCounter(ReducerLoad.DATA, 1);
						one.set("data: " + point_ID); 
						two.set(h_bucket);

					 }
				    
					if (point_type.equals("query")){
						reporter.incrCounter(ReducerLoad.QUERY, 1);
						one.set("query: " + point_ID); 
						two.set(h_bucket);
					}
					output.collect(one, two); 
				}
					
	        }
	        
	      
    }

	
}
