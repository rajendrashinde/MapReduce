for D in 0.1 0.5 1.0 1.5 2.0 2.5
do
rm -f Parameters.java
cat Parameters_D_random > Parameters.java
echo "double D = ${D}; }" >> Parameters.java
cat Parameters.java
./compile
scheme='G'
scheme_l='g'
dataset='input'
L=200
job=${scheme}_${dataset}_W0.5_k10_L${L}_d100_u0.6_c2.0_D${D}_n1M_nq100K
output=${scheme}_${dataset}_W0.5_k10_L${L}_d100_u0.6_c2.0_D${D}_n1M_nq100K
echo $job
echo $output
hadoop fs -rmr $output
/usr/local/hadoop-0.20.2/bin/hadoop jar ./lsh.jar lsh.LSH_${scheme_l} -Xmx512m 64 $job $dataset $output
hadoop fs -copyToLocal $output $OUTDIR/ghdfsoutputs
hadoop fs -rmr $output
done
