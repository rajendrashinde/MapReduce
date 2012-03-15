#!/usr/bin/python
import sys

(last_key, list_val) = (None, [])
for line in sys.stdin:
	(key, value)  = line.strip().split("\t") 
	if last_key and last_key != key:
		print '%s\t%s' % (key, ", ".join(list_val) )
		last_key = key
		list_val = [value]
	else:
		last_key = key
		list_val += [value]
if last_key:
	print "%s\t%s" % (key, ", ".join(list_val) )
 
