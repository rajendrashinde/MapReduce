#!/usr/bin/env python

import sys
import math 

for line in sys.stdin:
	word_doc_count = line.strip().split('\t', 2)
	if len(word_doc_count) != 2:
		continue
	(word_doc, count) = word_doc_count
	
	word_doc = word_doc.split('@-@')
	if len(word_doc) != 2:
		continue
	(word, doc) = word_doc

	print '%s\t%s@-@%s' % (word, doc, count)
