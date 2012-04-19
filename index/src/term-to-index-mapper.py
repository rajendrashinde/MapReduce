#!/usr/bin/env python

import sys
file = open('dictionary_sorted.txt','r')
dictionary = {}
for line in file:
		line = line.strip().split()
		index = line[0]
		word = line[1]
		dictionary[word]=index

for line in sys.stdin:
		line = line.strip().split('\t')
		print line
		doc = line[0]
		terms_tfidf = line[1]
		output_str = doc
		for term_tfidf in terms_tfidf.split():
				(term, tfidf) = term_tfidf.split(',')
				idx_tfidf = " %s,%s" % (dictionary[term], tfidf)
				output_str += idx_tfidf
		print output_str 
				
