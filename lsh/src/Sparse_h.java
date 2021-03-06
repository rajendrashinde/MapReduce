/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lsh;

import java.io.IOException;;
import java.util.ArrayList; 
import java.util.Iterator;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter; 
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.TextInputFormat; 
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.io.IntWritable; 
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text; 
import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.Counters;
public class Sparse_h {

	public static enum MapperLoad {
		DATA, QUERY, FANOUT, PERTURB
	};

	public static enum ReducerLoad {
		DATA, QUERY_MAX, QUERY	
	};
	
	static boolean debug = false;
  
	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
		private Text h_bucket = new Text();
		private Text value_out = new Text ();
		Random rnd = new Random(10042); 
		

		public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
			
			Parameters P = new Parameters ();
			double W = P.W; 
			int k = P.k; 
			int L = P.L; 
			int d = P.d; 
			double u = P.u; 
			double c = P.c; 
			double D = P.D; 
			
			String line = value.toString();
			String[] result = line.split("\\s+", 2);
			//System.out.println(result[0]); 
			
			String point_str = result[1].trim();
			String point_ID = result[0].trim();

			long t1; long t2; 
			
			if ( rnd.nextFloat() < 0.8 ) {	
				t1 = System.currentTimeMillis();	
				reporter.incrCounter(MapperLoad.DATA, 1);
				Vector_sparse point = new Vector_sparse(point_str, d);

				
				h_bucket.set(LSH_functions.hbucket(point, k, W)[0]);
				if (debug)
					value_out.set("data; " + point_ID);
				else
					value_out.set(point_str + "; data; " + point_ID);
				
				output.collect(h_bucket,value_out);
				//System.out.println(LSH_functions.hbucket(point) + " " + point_ID);
				 				
				t2 = System.currentTimeMillis();
				//System.out.println("Data: " + (t2-t1));
			}
			else {
				reporter.incrCounter(MapperLoad.QUERY, 1);
				
				t1 = System.currentTimeMillis();
				Vector_sparse query = new Vector_sparse(point_str, d);

				Set<String> f = LSH_functions.GetOffsetBuckets(query, k, L, W);				 

				if (debug)
					value_out.set("query; " + point_ID);
				else
					value_out.set(point_str + "; query; " + point_ID);

				Text key_out  = new Text(); 				
				Iterator<String> it = f.iterator();
				if (!f.isEmpty()){ 
				    while (it.hasNext()){
						
						key_out.set(it.next());
						reporter.incrCounter(MapperLoad.FANOUT, 1); 						
						output.collect(key_out, value_out);
				      	//  System.out.println(next.toString() + ": " + point_ID);
				    }
				}

				t2 = System.currentTimeMillis();
				reporter.incrCounter(MapperLoad.PERTURB, t2-t1); 
				System.out.println("Query: "+ (t2-t1) );
				
			} 

		}
	}
	
		public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
			public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException { 
			      HashMap<String, ArrayList<String>> lsh_table = new HashMap<String, ArrayList<String>> ();
			      //HashMap<String, HashSet<String>> query_buckets = new HashMap<String, HashSet<String>> ();
			      HashMap<String, String> query_container = new HashMap<String, String> (); 
				
				String h_bucket = key.toString();
			
				Parameters P = new Parameters (); double u = P.u; int d = P.d;
				
				long t1; long t2; 
				
				while (values.hasNext()) {

					t1 = System.currentTimeMillis();
					
					String line = values.next().toString();
					String[] result = line.split("; ");
					String point_type = result[1];
				    
				  //  System.out.println(h_bucket + " :" + result[1] + ": " + result[2]); 
					if (point_type.equals("data")){
						reporter.incrCounter(ReducerLoad.DATA, 1);
						String point_str = result[0];
						String point_ID = result[2];
						//Vector point = new Vector(point_str, mode, d); 

						//normalize if necessary!
						//point = point.normalize() ; 

						String point_id = point_str + ":" + point_ID; 
						if (lsh_table.containsKey(h_bucket)){
							ArrayList<String> junk = lsh_table.get(h_bucket); 
							junk.add(point_id);
							lsh_table.put(h_bucket, junk); 
						}

						else	{
						ArrayList<String> junk = new ArrayList<String> ();
						junk.add(point_id); 
						lsh_table.put(h_bucket, junk);
						}   

					 }
				    
					if (point_type.equals("query")){
						reporter.incrCounter(ReducerLoad.QUERY, 1);
						String query_str = result[0];
						String query_ID = result[2];
						query_container.put(query_ID, query_str);
					}
					
					t2 = System.currentTimeMillis(); 
					System.out.println(point_type + " (stream): " + (t2 - t1)); 
				}

					
				Iterator <String> it_query = query_container.keySet().iterator(); 
 
				String[] NN;
 
				while (it_query.hasNext()){
					String query_ID = it_query.next();
					String query_str = query_container.get(query_ID);  
				   	  											
						if (lsh_table.containsKey(h_bucket)) {
							
							//Vector query = new Vector(query_str, mode, d); 
							//normalize if necessary!
							//query = query.normalize() ; 

							t1 = System.currentTimeMillis();
							NN = Math_functions.nearest_neighbor(query_str, query_ID, lsh_table.get(h_bucket), u, d);  
							//NN = Math_functions.nearest_neighbor(query, lsh_table.get(h_bucket), u, d);  
							String nn_distance = NN[0]; 
							t2 = System.currentTimeMillis();

							if (!NN[1].equals("-1")){
								Text query_txt = new Text(); 
								query_txt.set("Query:" + query_ID);
								Text id_txt = new Text(); 
								 
								id_txt.set("Data point: " + NN[1] + " in " +  h_bucket + ", Dist = "+ nn_distance + ", Time: " + Long.toString(t2 - t1) );

								output.collect(query_txt, id_txt); 

								//System.out.println("Key: " + key.toString() + " Query: " + 
								//query_container.get(query_str) + " " + NN.id + " " + 
								//Double.toString(Math_functions.nearest_neighbor(query, lsh_table.get(h_bucket), u, NN)) ); 
							}   
							System.out.println("Query: " + query_ID + " Time: " + (t2 - t1));
						}
						 
						     
				} 
	        }
	        
	      
    }
  
	public static class IdentityMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
		public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
			String line = value.toString();
			String[] result = line.split("\\t");
			String point_str = result[1];
			String point_ID = result[0];
			Text junk = new Text ();
			junk.set(point_ID);
			output.collect(junk, value); 
		}
	}
	
	public static class IdentityReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException { 
			while (values.hasNext()) {
				String line = values.next().toString();
				String[] result = line.split("; ");
				String point_type = result[1];
				   
				Text junk = new Text (); 
				  //  System.out.println(h_bucket + " :" + result[1] + ": " + result[2]); 
				if (point_type.equals("data")){
					junk.set(result[2] + ", data");  
				}
				if (point_type.equals("query")) {
					junk.set(result[2] + ", query"); 
				}
				
				output.collect(key, junk);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		
        JobConf conf = new JobConf (lsh.Sparse_h.class);
        conf.setJobName("H_job");
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);
        conf.setMapperClass(lsh.Sparse_h.Map.class);
		
        if(!debug) 
			conf.setReducerClass(lsh.Sparse_h.Reduce.class);
        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);
	conf.setLong("mapred.task.timeout", 0L); 
	int l = args.length; 
        FileInputFormat.setInputPaths(conf, new Path(args[l-2]) );
        FileOutputFormat.setOutputPath(conf, new Path(args[l-1]) );
	if (l == 2) {
		conf.setJobName("H_job"); 
		conf.setNumReduceTasks(256);
	}

	if (l == 3) {
		//conf.setQueueName(args[0]); 
		conf.set("mapred.child.java.opts", args[0]);
        	conf.setJobName("H_job");
	}
	if (l == 4) {
		conf.set("mapred.child.java.opts", args[0]);
		//conf.setQueueName(args[0]); 
		conf.setNumReduceTasks( Integer.parseInt(args[1]) );
        	conf.setJobName("H_job");
	}
	if (l == 5) {
		conf.set("mapred.child.java.opts", args[0]);
		conf.setNumReduceTasks( Integer.parseInt(args[1]) );
		conf.setJobName(args[2]);
	}
        RunningJob job = JobClient.runJob(conf); 
	Counters counters = job.getCounters();
	for (Counters.Group group : counters) {
    		System.out.println("- Counter Group: " + group.getDisplayName() + " (" + group.getName() + ")");
    		System.out.println("  number of counters in this group: " + group.size());
    		for (Counters.Counter counter : group) {
        		System.out.println("  - " + counter.getDisplayName() + ": " + counter.getCounter());
    		}
	}

       }
}
