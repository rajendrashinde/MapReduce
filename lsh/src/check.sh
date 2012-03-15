./compile
hadoop fs -rmr temp/output_G
/usr/local/hadoop-0.20.2/bin/hadoop jar ./lsh.jar lsh.LSH_g -Xmx512m 64 G temp/input temp/output_G
hadoop fs -cat temp/output_G/part-000* | grep "Query:1025"
hadoop fs -cat temp/output_G/part-000* | grep "Query:3994"
