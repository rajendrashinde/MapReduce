output_dir=G_LB
hadoop fs -copyToLocal $output_dir .
for i in `seq 0 1023 `
do
	temp=${output_dir}/part-`printf %05d $i`
	cat $temp | awk '{sum1 += $3}{ sum2 += $6 } END {print sum1 "\t" sum2}'
done
rm -rf $output_dir
