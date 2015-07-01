#!/usr/bin/python
# usage: cat x | python sample.py 10  
import sys
import random
random.seed(12345)

sample = []
i = 0
k = int(sys.argv[1])

for line in sys.stdin:
	if i < k:
		sample += [line.strip()]
	else:
		r = random.randint(0, i)
		if r < k:
			sample[r] = line.strip()
	i += 1

print '\n'.join(sample)
