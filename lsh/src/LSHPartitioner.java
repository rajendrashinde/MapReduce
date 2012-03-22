package lsh; 

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Partitioner; 
import org.apache.hadoop.mapred.Reporter; 
import org.apache.hadoop.mapred.lib.HashPartitioner; 
import org.apache.hadoop.io.WritableComparator; 
import org.apache.hadoop.io.WritableComparable; 
import org.apache.hadoop.io.Text; 

public class LSHPartitioner {
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

}
