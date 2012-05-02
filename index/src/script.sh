TXT=wiki/txt
TF=wiki/tf
DF=wiki/df
TFIDF=wiki/tfidf
DOC_TFIDF=wiki/doc-tfidf
TERMINDEX_DOC_TFIDF=wiki/termindex-doc-tfidf
HADOOP_HOME=/usr/local/hadoop-0.20.2
STREAMING_JAR=$HADOOP_HOME/contrib/streaming/hadoop-0.20.2-streaming.jar

$HADOOP_HOME/bin/hadoop fs -rmr $TF
$HADOOP_HOME/bin/hadoop jar $STREAMING_JAR -D mapred.reduce.tasks=512 -file tf-mapper.py -file tf-reducer.py -file english.stop -file PorterStemmer.py -input $TXT -output $TF -mapper tf-mapper.py -reducer tf-reducer.py

$HADOOP_HOME/bin/hadoop fs -rmr $DF
$HADOOP_HOME/bin/hadoop jar $STREAMING_JAR -D mapred.reduce.tasks=512 -file df-mapper.py -file df-reducer.py -input $TF -output $DF -mapper df-mapper.py -reducer df-reducer.py

$HADOOP_HOME/bin/hadoop fs -rmr $TFIDF
$HADOOP_HOME/bin/hadoop jar $STREAMING_JAR -D mapred.reduce.tasks=512 -file tfidf-mapper.py -file tfidf-reducer.py -input $DF -output $TFIDF -mapper tfidf-mapper.py -reducer tfidf-reducer.py

$HADOOP_HOME/bin/hadoop fs -rmr $DOC_TFIDF
$HADOOP_HOME/bin/hadoop jar $STREAMING_JAR -D mapred.reduce.tasks=512 -file doc-tfidf-mapper.py -file doc-tfidf-reducer.py -input $TFIDF -output $DOC_TFIDF -mapper doc-tfidf-mapper.py -reducer doc-tfidf-reducer.py

$HADOOP_HOME/bin/hadoop fs -rmr $TERMINDEX_DOC_TFIDF
$HADOOP_HOME/bin/hadoop jar $STREAMING_JAR -D mapred.reduce.tasks=512 -file term-to-index-mapper.py -file term-to-index-reducer.py -file dictionary_sorted.txt -input $DOC_TFIDF -output $TERMINDEX_DOC_TFIDF -mapper term-to-index-mapper.py -reducer term-to-index-reducer.py
