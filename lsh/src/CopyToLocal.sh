for iter in {1..9}
do
L=200
scheme=h
hadoop fs -copyToLocal ./output_100_${scheme}_${iter} /data/ashish-students/hdfsoutputs/
hadoop fs -rmr ./output_100_${scheme}_${iter}
done