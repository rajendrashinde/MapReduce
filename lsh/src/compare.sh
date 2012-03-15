hadoop fs -cat $1/part-000* > temp_1.txt
hadoop fs -cat $2/part-000* > temp_2.txt
cat temp_1.txt | awk '{print $1}' | sort | uniq > temp_1_1.txt
cat temp_2.txt | awk '{print $1}' | sort | uniq > temp_2_2.txt
diff temp_1_1.txt temp_2_2.txt
