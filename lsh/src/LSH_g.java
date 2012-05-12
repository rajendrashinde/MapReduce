/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lsh;

import java.io.IOException;
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
import org.apache.hadoop.mapred.Partitioner; 
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter; 
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.TextInputFormat; 
import org.apache.hadoop.mapred.TextOutputFormat;
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

public class LSH_g extends Configured{

	public static class FirstPartitioner extends HashPartitioner<Text, Text> implements Partitioner<Text, Text>{
	
		@Override
		public void configure(JobConf job){}

		@Override
		public int getPartition( Text key, Text Value, int numPartitions) {
			String g_bucket = key.toString(); 
			String [] junk;
			junk = g_bucket.split(" ");
			
			Text key2 = new Text (); 
			key2.set(junk[0]); 
			return super.getPartition(key2, Value, numPartitions); 
		}
	}

	public static class GroupComparator extends WritableComparator {
		protected GroupComparator() {
			super(Text.class, true);
		}
		@Override
		public int compare (WritableComparable w1, WritableComparable w2) {
			Text t1 = (Text) w1;
			Text t2 = (Text) w2; 
			String[] junk;
			junk = t1.toString().split(" "); 
			String t1_str = junk[0];
			t1.set(t1_str); 		

			junk = t2.toString().split(" ");
			String t2_str = junk[0];
 			t2.set(t2_str);
			return super.compare(t1, t2);
		}
	}
      	public static enum MapperLoad {
		DATA, QUERY, FANOUT
	};

	public static enum ReducerLoad {
		DATA, QUERY	
	};

	 
	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {

		private Text g_bucket = new Text();
        	private Text value_out = new Text(); 
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

//			Set<Integer> query_sample = Math_functions.randomSample(79302017,20000); 
//			Integer tmp = Integer.parseInt(point_ID); 

			if ( rnd.nextFloat() < 0.8 ) {		
//			if (!query_sample.contains(tmp)) {
			//if ( point_type.equals("data")) {
				reporter.incrCounter(MapperLoad.DATA, 1);
				Vector point = new Vector(point_str);

				//normalize if necessary!
                                //point = point.normalize() ;

				g_bucket.set(LSH_functions.gbucket(point, direction, direction2, shift, shift2, k, W, D) + " data");
				value_out.set(point_str + ", data, " + point_ID + ", " + LSH_functions.hbucket(point, direction, shift, k, W));
				output.collect(g_bucket, value_out);
				//System.out.println(g_bucket + " " + point_ID); 
			}
			else {
				Vector query = new Vector(point_str);
				reporter.incrCounter(MapperLoad.QUERY, 1);

				//normalize if necessary!
                                //query = query.normalize() ;

				Set<Text> f = new HashSet<Text>();
				Random Rng2 = new Random(point_str.hashCode());
				for (int i = 0; i < L; i++){
				    Text junk = new Text (); 
				    Vector perturbation = Math_functions.random_vector(Rng2, d, 0, 1.0*u/(c*Math.sqrt(d))); 
				    junk.set(LSH_functions.gbucket(query.plus(perturbation), direction, direction2, shift, shift2, k, W, D) + " query");
				    f.add(junk);
				}

				Iterator<Text> it = f.iterator();
				if (!f.isEmpty()){ 
				    while (it.hasNext()){
					Text next  = it.next(); 
					value_out.set(point_str + ", query, " + point_ID); 
					output.collect(next, value_out);
					reporter.incrCounter(MapperLoad.FANOUT, 1); 
				      //  System.out.println(next.toString() + ": " + point_ID);
				    }
				}
			} 

		}
	}
	
	public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {

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
					reporter.incrCounter(ReducerLoad.DATA, 1);
					data_count += 1;
					Vector point = new Vector(point_str); 

					//normalize if necessary!
                                	//point = point.normalize() ;

					Vector_ID point_id = new Vector_ID(point, point_ID);
					String h_bucket = result[3]; 		

					if (lsh_table.containsKey(h_bucket)){
						ArrayList<Vector_ID> junk = lsh_table.get(h_bucket); 
						junk.add(point_id);
						lsh_table.put(h_bucket, junk); 
					}

					else	{
					ArrayList<Vector_ID> junk = new ArrayList<Vector_ID> ();
					junk.add(point_id); 
					lsh_table.put(h_bucket, junk);
					}
    
				 }
			    
				if (point_type.equals("query") && data_count > 0 ){
					reporter.incrCounter(ReducerLoad.QUERY, 1);
					long t1 = System.currentTimeMillis(); 
					query_count += 1;
					String query_ID = point_ID; 
					String query_str = point_str; 
					Vector query = new Vector(query_str); 
					//normalize if necessary!
                                	//query = query.normalize() ;
					Vector perturbation = new Vector (d);
					Vector offset = new Vector (d); 
					String h_bucket = "";  
					Random Rng2 = new Random(query_str.hashCode());
					Set <String> f = new HashSet <String>();
					for (int i = 0; i < L; i++) {
						perturbation = Math_functions.random_vector(Rng2, d, 0, 1.0*u/(c*Math.sqrt(d)));
						offset = query.plus(perturbation);
						if (g_bucket.equals(LSH_functions.gbucket(offset, direction, direction2, shift, shift2, k, W, D) )) { 
							h_bucket = LSH_functions.hbucket(offset, direction, shift, k, W);
							f.add(h_bucket); 
						}
					}

					Iterator <String> it = f.iterator();
					if (!f.isEmpty()) {
							while (it.hasNext()){
								h_bucket = it.next(); 
	 
								if (lsh_table.containsKey(h_bucket)) {

									//System.out.println("Query: " + query_ID + ", Searching in: " + h_bucket + ", on machine[Gbkt]: " + g_bucket);	
									//Vector_ID NN = Math_functions.nearest_neighbor(query, lsh_table.get(h_bucket), u, d); 
									Vector_ID NN = Math_functions.nearest_neighbor(query, query_ID, lsh_table.get(h_bucket), u, d); 
									double nn_distance = query.distanceTo(NN.v); 
									if ( !NN.id.equals("-1")){
										Text query_txt = new Text(); 
										Text id_txt = new Text(); 
										query_txt.set("Query:" + query_ID);
										long t2 = System.currentTimeMillis(); 
										id_txt.set("Data point: " + NN.id + " in " + h_bucket + ", Dist = " + Double.toString(nn_distance) + ", Time: " + Long.toString(t2 - t1) + ", Gbkt: " + g_bucket); 
										output.collect(query_txt, id_txt);
										break; 
									}
								}    
							}
					} 
				}
			}
		}
	}
  
	public static void main(String[] args) throws Exception {
        JobConf conf = new JobConf (lsh.LSH_g.class);
        //conf.setJobName("G_job");
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);
        conf.setMapperClass(lsh.LSH_g.Map.class);
	conf.setPartitionerClass(FirstPartitioner.class);
	conf.setOutputValueGroupingComparator(GroupComparator.class); 
        conf.setReducerClass(lsh.LSH_g.Reduce.class);
        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

	conf.setLong("mapred.task.timeout", 0L);
	int l = args.length; 
        FileInputFormat.setInputPaths(conf, new Path(args[l-2]) );
        FileOutputFormat.setOutputPath(conf, new Path(args[l-1]) );
	if (l == 3) {
		conf.set("mapred.child.java.opts", args[0]);
		//conf.setQueueName(args[0]); 
        	conf.setJobName("G_job");
	}
	if (l == 4) {
		conf.set("mapred.child.java.opts", args[0]);
		//conf.setQueueName(args[0]); 
		conf.setNumReduceTasks( Integer.parseInt(args[1]) );
        	conf.setJobName("G_job");
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
