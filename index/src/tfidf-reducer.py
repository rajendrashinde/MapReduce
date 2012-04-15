#!/usr/bin/env python

import sys
import math
N = 3903034

last_key = None 
df = 0
tf = {}
# For each word(key), tf is a hash map which stores tf for each document 
# So keys of tf represent the different documents containing the current word

for line in sys.stdin:
	key_val = line.strip().split('\t',2)
	if len(key_val) != 2:
		continue
	(key, val) = key_val
	# Note: key is a word here, val is a doc@-@count string

	doc_count = val.split('@-@', 2)
	if len(doc_count) != 2:
		continue
	(doc, count) = doc_count	

	if last_key and last_key != key:
		for doc in tf:
			tf_count = tf[doc]
			word_doc = "%s@-@%s" %(last_key, doc)
			print "%s\t%f" % (word_doc, tf_count*math.log(N/float(df)))
		last_key = key
		df = 1
		tf = {}
		tf[doc] = float(count)
	else:
		df += 1
		last_key = key
		tf[doc] = float(count)
if last_key:
		for doc in tf:
			count = tf[doc]
			word_doc = "%s@-@%s" % (last_key, doc)
			print "%s\t%f" % (word_doc, tf_count*math.log(N/float(df)))
