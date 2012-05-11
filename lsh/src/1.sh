S3_BUCKET=s3n://AKIAIN3YXQONKSB6YU7A:nU6EM8xKCriFJo2ddl6qG5hHzgSuZ0jgvGGgmV0d@wiki.rbs/wiki/termindex-doc-tfidf
for i in `seq 0 511`
do
	hadoop fs -cat ${S3_BUCKET}/part-`printf %05d $i` > part-`printf %05d $i`
	hadoop fs -put part-`printf %05d $i` termindex-doc-tfidf
	rm -rf part-`printf %05d $i`
	echo $i
done
