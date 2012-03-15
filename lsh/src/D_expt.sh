for D in 3.0
do
rm -f Parameters.java
cat Parameters_D > Parameters.java
echo "double D = ${D}; }" >> Parameters.java
cat Parameters.java
./compile
scheme='G'
scheme_l='g'
dataset='tiny_n_64'
L=200
job=${scheme}_L${L}_D${D}
output=temp/tinyn1Mnq200K_${scheme}_L${L}_D${D}
echo $job
echo $output
hadoop fs -rmr $output
/usr/local/hadoop-0.20.2/bin/hadoop jar ./lsh.jar lsh.LSH_${scheme_l} -Xmx512m 64 $job $dataset $output > job.txt

tweet=1
if [ $tweet -eq 1 ] 
then
echo $scheme $dataset $D $L > twitter.txt
cat job.txt | grep "Reduce shuffle bytes" >> twitter.txt
hadoop job -history $output > history.txt
cat history.txt | grep "Finished At" >> twitter.txt
hadoop fs -cat $output/part-000* | awk '{print $1}' > temp.txt
echo $scheme $L $D >> twitter.txt
perl -ne 'for (split(" ", $_)) { $H{$_} = 1 } END { print scalar(keys%H), "\n" }' < temp.txt  >> twitter.txt
./post_tw1.py
fi

#rm -f twitter.txt
#hadoop fs -copyToLocal $output /data/ashish-students/hdfsoutputs
#hadoop fs -rmr $output
done
