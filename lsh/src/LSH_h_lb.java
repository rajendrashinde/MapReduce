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
public class LSH_h_lb extends LSH_h {
       
		public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
			public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException { 
			      HashMap<String, ArrayList<Vector_ID>> lsh_table = new HashMap<String, ArrayList<Vector_ID>> ();
			      //HashMap<String, HashSet<String>> query_buckets = new HashMap<String, HashSet<String>> ();
			      HashMap<String, String> query_container = new HashMap<String, String> (); 
				
				int data_count = 0; 
				int query_count = 0; 

				while (values.hasNext()) {
					String line = values.next().toString();
					String[] result = line.split(", ");
					String point_type = result[1];
				    
				  //  System.out.println(h_bucket + " :" + result[1] + ": " + result[2]); 
					if (point_type.equals("data")){
						reporter.incrCounter(ReducerLoad.DATA, 1);
						data_count += 1;
					 }
				    
					if (point_type.equals("query")){
						reporter.incrCounter(ReducerLoad.QUERY, 1);
						query_count += 1; 
					}
				}
				
				Text dc = new Text(); dc.set("Data points: " + data_count); 
				Text qc = new Text(); qc.set("Query points: " + query_count);  
				output.collect(dc, qc); 

	        }
	        
	      
    }

	public static void main(String[] args) throws Exception {
        JobConf conf = new JobConf (lsh.LSH_h_lb.class);
        //conf.setJobName("H_job");
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);
        conf.setMapperClass(lsh.LSH_h_lb.Map.class);
        conf.setReducerClass(lsh.LSH_h_lb.Reduce.class);
        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);
	conf.setLong("mapred.task.timeout", 0L); 
	int l = args.length; 
        FileInputFormat.setInputPaths(conf, new Path(args[l-2]) );
        FileOutputFormat.setOutputPath(conf, new Path(args[l-1]) );
	if (l == 3) {
		//conf.setQueueName(args[0]); 
		conf.set("mapred.child.java.opts", args[0]);
        	conf.setJobName("Load Balance H:");
	}
	if (l == 4) {
		conf.set("mapred.child.java.opts", args[0]);
		//conf.setQueueName(args[0]); 
		conf.setNumReduceTasks( Integer.parseInt(args[1]) );
        	conf.setJobName("Load Balance H:");
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
