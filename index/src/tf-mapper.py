#!/usr/bin/env python

import sys
sys.path.append(".")

from PorterStemmer import PorterStemmer

file = open('english.stop')
StopWords = set()
for word in file:
	word = word.strip()
	if word != '':
		StopWords.add(word)

for line in sys.stdin:
	line = line.strip().split('\t',2)
	if len(line) != 2:
		continue
	else:
		title = line[0]
		text = line[1]
		#print text
		p = PorterStemmer() 
		word = ''
		if text == '':
		    continue
		for c in text:
		    if c.isalpha():
			word += c.lower()
		    else:
			if word:
				if word not in StopWords:
			    		output = p.stem(word, 0,len(word)-1)
					print "%s@-@%s\t1" %(output, title)
			word = ''

