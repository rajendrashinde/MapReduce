cat Parameters.java
./compile
hadoop1=/usr/local/hadoop-0.20.2/bin/hadoop
echo $hadoop1
hadoop fs -rmr output_G
$hadoop1 jar ./lsh.jar lsh.LSH_g -Xmx300m 1024 Image_G_L50 gist-small output_G
hadoop fs -rmr output_H
$hadoop1 jar ./lsh.jar lsh.LSH_h -Xmx300m 1024 Image_H_L50 gist-small output_H
