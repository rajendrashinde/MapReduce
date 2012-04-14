#!/usr/bin/env python

import sys
import math 

for line in sys.stdin:
	doc_word_tf = line.strip().split('\t',2)
	if len(doc_word_tf) != 2:
		print doc_word_tf


