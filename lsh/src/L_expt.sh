for L in 1000
do
rm -f Parameters.java
cat Parameters > Parameters.java
echo "int L = ${L}; }" >> Parameters.java
cat Parameters.java
./compile
scheme='H'
scheme_l='h'
D='NA'
dataset='tiny_n_64'
job=${dataset}_${scheme}_L${L}_D${D}
output=temp/tinyn1Mnq200K_${scheme}_L${L}_D${D}
echo $job
echo $output
hadoop fs -rmr $output
/usr/local/hadoop-0.20.2/bin/hadoop jar ./lsh.jar lsh.LSH_${scheme_l} -Xmx512m 64 $job $dataset $output > job.txt
#hadoop fs -copyToLocal $output $OUTDIR/ghdfsoutputs

echo $scheme $dataset $D $L > twitter.txt
cat job.txt | grep "Reduce shuffle bytes" >> twitter.txt
hadoop job -history $output > history.txt
cat history.txt | grep "Finished At" >> twitter.txt
hadoop fs -cat $output/part-000* | awk '{print $1}' > temp.txt
perl -ne 'for (split(" ", $_)) { $H{$_} = 1 } END { print scalar(keys%H), "\n" }' < temp.txt  >> twitter.txt
./post_tw1.py

#rm -f ~/temp.txt
#rm -f twitter.txt
#hadoop fs -rmr $output
done
