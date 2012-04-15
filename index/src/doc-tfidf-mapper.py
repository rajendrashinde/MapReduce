#!/usr/bin/env python

import sys
import math

for line in sys.stdin:
        word_doc_tfidf = line.strip().split('\t', 2)
        if len(word_doc_tfidf) != 2:
                continue
        (word_doc, tfidf) = word_doc_tfidf

        word_doc = word_doc.split('@-@')
        if len(word_doc) != 2:
                continue
        (word, doc) = word_doc

        print '%s\t%s@-@%s' % (doc, word, tfidf)
