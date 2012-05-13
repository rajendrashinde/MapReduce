hadoop fs -cat G_LB/* > lb.txt
cat lb.txt | awk 'BEGIN{max = 0}{if (max < $3) max = $3}END {print max}'
cat lb.txt | awk 'BEGIN{max = 0}{if (max < $6) max = $6}END {print max}'
