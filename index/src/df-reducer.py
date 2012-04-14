#!/usr/bin/env python

import sys

last_key = None
last_val = None
count = 0
for line in sys.stdin:
	(key, val) = line.strip().split('\t',2)
	if last_key and last_key != key: 
		""" This is not the first word and 
		you have detected a word boundary """
		print "%s\t%s" % (last_key, count)
		last_key = key
		last_val = val
		count = 1
	else: 
		""" This is either the first word 
		or you are not at a word boundary """
		last_key = key
		if last_val and last_val != val:
			count += 1
		else: # This is the first word and first document
			last_val = val
			count = 1
		
if last_key:
	print "%s\t%s" % (last_key, count)
