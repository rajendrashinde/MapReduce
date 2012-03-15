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
// import org.apache.hadoop.mapred.Counters.Group;
import org.apache.hadoop.mapred.Counters;
//import org.apache.hadoop.mapred.Counters.Group;

public class LSH_g_lb extends LSH_g {
       
	
	public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {

		public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException { 
			/* 
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
		
			String g_bucket = key.toString(); */	
			int data_count = 0; int query_count = 0;
			while (values.hasNext()) {
				String line = values.next().toString();
				String[] result = line.split(", ");
				String point_str = result[0];
				String point_type = result[1];
				String point_ID = result[2];

				if (point_type.equals("data")){
					data_count += 1;
				 }
			    
				if (point_type.equals("query")){
					query_count += 1;
				}
			}
			
			Text dc = new Text (); dc.set("Data points: " + data_count); 
			Text qc = new Text (); qc.set("Query points: " + query_count); 
			output.collect(dc, qc); 

			System.out.println("Queries: " + query_count + ", Data points: " + data_count);
		}
	}
  
	public static void main(String[] args) throws Exception {
        JobConf conf = new JobConf (lsh.LSH_g_lb.class);
        //conf.setJobName("G_job");
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);
        conf.setMapperClass(lsh.LSH_g_lb.Map.class);
        conf.setReducerClass(lsh.LSH_g_lb.Reduce.class);
        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);
	conf.setLong("mapred.task.timeout", 0L);
	int l = args.length; 
        FileInputFormat.setInputPaths(conf, new Path(args[l-2]) );
        FileOutputFormat.setOutputPath(conf, new Path(args[l-1]) );
	if (l == 3) {
		conf.set("mapred.child.java.opts", args[0]);
		//conf.setQueueName(args[0]); 
        	conf.setJobName("Load Balance: G");
	}
	if (l == 4) {
		conf.set("mapred.child.java.opts", args[0]);
		//conf.setQueueName(args[0]); 
		conf.setNumReduceTasks( Integer.parseInt(args[1]) );
        	conf.setJobName("Load Balance: G");
	}
	if (l == 5) {
		conf.set("mapred.child.java.opts", args[0]);
		//conf.setQueueName(args[0]); 
		conf.setNumReduceTasks( Integer.parseInt(args[1]) );
		conf.setJobName(args[2]); 
	}

	//conf.set("mapred.child.java.opts", "-Xmx1024m" ); 
	//conf.set("mapred.job.tracker", "local");

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
