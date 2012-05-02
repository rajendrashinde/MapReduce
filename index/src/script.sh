TXT=wiki/txt
TF=wiki/tf
DF=wiki/df
TFIDF=wiki/tfidf
DOC-TFIDF=wiki/doc-tfidf
TERMINDEX-DOC-TFIDF=wiki/termindex-doc-tfidf

hadoop fs -rmr $TF
hadoop jar $HADOOP_HOME/contrib/streaming/hadoop-0.19.0-streaming.jar -D mapred.reduce.tasks=512 -file tf-mapper.py -file tf-reducer.py -file english.stop -file PorterStemmer.py -input $TXT -output $TF -mapper tf-mapper.py -reducer tf-reducer.py
hadoop fs -rmr $DF
hadoop jar $HADOOP_HOME/contrib/streaming/hadoop-0.19.0-streaming.jar -D mapred.reduce.tasks=512 -file df-mapper.py -file df-reducer.py -input $TF -output $DF -mapper df-mapper.py -reducer df-reducer.py
hadoop fs -rmr $TFIDF
hadoop jar $HADOOP_HOME/contrib/streaming/hadoop-0.19.0-streaming.jar -D mapred.reduce.tasks=512 -file tfidf-mapper.py -file tfidf-reducer.py -input $DF -output $TFIDF -mapper tfidf-mapper.py -reducer tfidf-reducer.py
hadoop fs -rmr $DOC-TFIDF
hadoop jar $HADOOP_HOME/contrib/streaming/hadoop-0.19.0-streaming.jar -D mapred.reduce.tasks=512 -file doc-tfidf-mapper.py -file doc-tfidf-reducer.py -input $TFIDF -output $DOC-TFIDF -mapper doc-tfidf-mapper.py -reducer doc-tfidf-reducer.py
hadoop fs -rmr $TERMINDEX-DOC-TFIDF
hadoop jar $HADOOP_HOME/contrib/streaming/hadoop-0.19.0-streaming.jar -D mapred.reduce.tasks=512 -file term-to-index-mapper.py -file term-to-index-reducer.py -file dictionary_sorted.txt -input $DOC-TFIDF -output $TERMINDEX-DOC-TFIDF -mapper term-to-index-mapper.py -reducer term-to-index-reducer.py


hadoop fs -rmr wiki