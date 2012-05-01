#/usr/bin/python
import sys
import random
import math
query = []
data = []

def norm(a):
	a_norm = 0.0
	for i in a:
		a_norm += float(a[i])**2
	a_norm = math.sqrt(a_norm)
	return a_norm
	
def cosine_distance (a, b):
	a = a.split(" ")
	a_dict = {}
	for ai in a:
		ai = ai.split(",")
		a_dict[ai[0]] = ai[1]

	b = b.split(" ")
	b_dict = {}
	for bi in b:
		bi = bi.split(",")
		b_dict[bi[0]] = bi[1]

	dot = 0.0
	for i in a_dict:
		if i in b_dict:
			dot += float(a_dict[i])*float(b_dict[i])
	
	#print a, norm(a_dict), b, norm(b_dict)
	return 1.0 - dot/(norm(a_dict)*norm(b_dict)) 
	
for line in sys.stdin:
	line = line.strip()
	if random.random() <= 0.2:
		# Treat this point as a query
		query += [line]
	else:
		data += [line]
		
print len(data), len(query)

for q in random.sample(query,10):
	dist = 10000; 
	avg = 0
	max_dist = 0
	q = q.split(" ", 1)
	q_id = q[0]
	q_str = q[1]
	for s in random.sample(data, min(200,len(data))):
	
		s = s.split(" ", 1)
		if len(s) != 2:
			continue
		s_id = s[0]
		s_str = s[1]
		
		j = cosine_distance(q_str, s_str)
	 	if j < dist:
			dist = j
			nn = s_id
		avg += j
		if j > max_dist:
			max_dist = j	
	print q_id, nn, dist, avg/min(200,len(data)), max_dist
			
