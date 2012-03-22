package lsh;

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
import org.apache.hadoop.conf.Configured; 

import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.Counters;

public class LSHTestDriver extends Configured implements Tool {

	public int run(String[] args) throws Exception {
        	JobConf conf = new JobConf(getConf(), getClass());
		conf.setJobName("Test LSH"); 
		int l = args.length; 
        	FileInputFormat.setInputPaths(conf, new Path(args[l-2]) );
        	FileOutputFormat.setOutputPath(conf, new Path(args[l-1]) );
        	conf.setOutputKeyClass(Text.class);
        	conf.setOutputValueClass(Text.class);
        	conf.setInputFormat(TextInputFormat.class);
        	conf.setOutputFormat(TextOutputFormat.class);
		conf.setLong("mapred.task.timeout", 0L); 
		
        	conf.setMapperClass(lsh.LSH_g.Map.class);
        	conf.setReducerClass(lsh.LSHTestReducer.Reduce_g.class);
		conf.setNumReduceTasks(2); 

		conf.setPartitionerClass(lsh.LSHPartitioner.FirstPartitioner.class);
		conf.setOutputValueGroupingComparator(lsh.LSHPartitioner.GroupComparator.class); 

        	JobClient.runJob(conf);
	 
		return 0; 
       }

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new LSHTestDriver(), args);
		System.exit(exitCode); 
	}
}
