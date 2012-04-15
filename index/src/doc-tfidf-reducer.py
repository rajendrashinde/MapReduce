#!/usr/bin/env python

import sys
import math

last_key = None
word_tfidf_list = ""

for line in sys.stdin:
        key_val = line.strip().split('\t',2)
        if len(key_val) != 2:
                continue
        (key, val) = key_val
        # Note: key is a doc here, val is a word@-@tfidf string

        word_tfidf = val.split('@-@', 2)
        if len(word_tfidf) != 2:
                continue
        (word, tfidf) = word_tfidf

        if last_key and last_key != key:
                print "%s\t%s" % (last_key, word_tfidf_list)
                last_key = key
		word_tfidf_list = "%s,%s " % (word, tfidf)
        else:  
                last_key = key
		word_tfidf_list += " %s,%s" % (word, tfidf)
if last_key:   
        print "%s\t%s" % (last_key, word_tfidf_list)
