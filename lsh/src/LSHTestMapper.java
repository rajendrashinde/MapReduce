
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
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter; 
import org.apache.hadoop.io.IntWritable; 
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text; 

public class LSHTestMapper {
      	public static enum MapperLoad {
		DATA, QUERY, FANOUT
	};

	public static class Map_h extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
		private Text h_bucket = new Text();
		private Text value_out = new Text ();
		Random rnd = new Random(10042); 

		public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
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
			String line = value.toString();
			String[] result = line.split(", ");
			String point_str = result[0];
			String point_type = result[1];
			String point_ID = result[2];

			if ( rnd.nextFloat() < 0.8 ) {		
				reporter.incrCounter(MapperLoad.DATA, 1);
				Vector point = new Vector(point_str);
				h_bucket.set(LSH_functions.hbucket(point, direction, shift, k, W));
				value_out.set("data: " + point_ID);
				output.collect(h_bucket,value_out);
			}
			else {
				reporter.incrCounter(MapperLoad.QUERY, 1);
				Vector query = new Vector(point_str);
				Set<Text> f = new HashSet<Text>();
				Random Rng2 = new Random(point_str.hashCode());
				for (int i = 0; i < L; i++){
				    Text junk = new Text (); 
				    Vector perturbation = Math_functions.random_vector(Rng2, d, 0, 1.0*u/(c*Math.sqrt(d))); 
				    junk.set(LSH_functions.hbucket(query.plus(perturbation), direction, shift, k, W));
				    f.add(junk);
				}

				Iterator<Text> it = f.iterator();
				if (!f.isEmpty()){ 
				    while (it.hasNext()){
					Text next  = it.next(); 
					reporter.incrCounter(MapperLoad.FANOUT, 1); 
					value_out.set("query: " + point_ID); 
					output.collect(next, value_out);
				    }
				}
			} 

		}
	}

	public static class Map_g extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {

		private Text one = new Text();
        	private Text two = new Text(); 
		private	Random rnd = new Random(10042); 

		public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
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
			String line = value.toString();
			String[] result = line.split(", ");
			String point_str = result[0];
			String point_type = result[1];
			String point_ID = result[2];

			if ( rnd.nextFloat() < 0.8 ) {		
				reporter.incrCounter(MapperLoad.DATA, 1);
				Vector point = new Vector(point_str);

				one.set(LSH_functions.hbucket(point, direction, shift, k, W));
				two.set("data: " + point_ID + " G: " + LSH_functions.gbucket(point, direction, direction2, shift, shift2, k, W, D)  ); 
				output.collect(one, two);
			}
			else {
				Vector query = new Vector(point_str);
				reporter.incrCounter(MapperLoad.QUERY, 1);

				Set<Text> f = new HashSet<Text>();
				Random Rng2 = new Random(point_str.hashCode());
				for (int i = 0; i < L; i++){
				    Text junk = new Text (); 
				    Vector perturbation = Math_functions.random_vector(Rng2, d, 0, 1.0*u/(c*Math.sqrt(d))); 
				    one.set(LSH_functions.hbucket(query.plus(perturbation), direction, shift, k, W));
				    two.set("query: " + point_ID + " G: " + LSH_functions.gbucket(query.plus(perturbation), direction, direction2, shift, shift2, k, W, D)  ); 
				    output.collect(one, two); 

				    junk.set(LSH_functions.gbucket(query.plus(perturbation), direction, direction2, shift, shift2, k, W, D) + " query");
				    f.add(junk);
				}

				Iterator<Text> it = f.iterator();
				if (!f.isEmpty()){ 
				    while (it.hasNext()){
					Text next  = it.next(); 
					//value_out.set("query: " + point_ID); 
					//output.collect(next, value_out);
					reporter.incrCounter(MapperLoad.FANOUT, 1); 
				    }
				}
			} 

		}
	}

}
