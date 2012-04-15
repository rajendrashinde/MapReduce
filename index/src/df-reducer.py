#!/usr/bin/env python

import sys
import math

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

        word_count = val.split('@-@', 2)
        if len(word_count) != 2:
                continue
        (word, count) = word_count

        if last_key and last_key != key:
                for word_it in tf:
                        tf_count = tf[word_it]
                        word_doc = "%s@-@%s" %(word_it, last_key)
                        print "%s\t%f" % (word_doc, float(tf_count)/float(df))
                last_key = key
                df = int(count)
                tf = {}
                tf[word] = int(count)
        else:  
                df += int(count)
                last_key = key
                tf[word] = int(count)
if last_key:   
                for word in tf:
                        tf_count = tf[word]
                        word_doc = "%s@-@%s" % (word, last_key)
                        print "%s\t%f" % (word_doc, float(tf_count)/float(df) )
