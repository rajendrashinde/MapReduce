#!/usr/bin/python
import random 
import math
from math import sqrt
from math import log
from util_functions import random_vector, dot_product, l2_norm, l2_distance
import sys

def CreateData(n,nq,d,offset,c,u):
	f = open("data.txt",'w')
	s = []
	for i in range(n):
		#if i%1000 == 0:
		#	print i
		radius = 8.0
		point = random_vector(d, 0, 1.0*radius/sqrt(d))
		s.append([j for j in point])
		for item in point:
			f.write(str(item))
			if item != point[len(point) - 1]:
				f.write(' ')
			else:
				f.write(', data, '+str(i+offset)+'\n')
				

	for i in range(nq):
		index = random.randint(0,n-1) 
		q = s[index]
		perturbation = random_vector(d, 0, (1.0*u)/(c*sqrt(d)));
		for i2 in range(d):
			q[i2] += perturbation[i2] 
		for item in q:
			f.write(str(item))
			if item != q[len(q) - 1]:
				f.write(' ')
			else:
				f.write(', query, '+str(i+offset) + '-' + str(index+offset) + '\n')
	f.close()

from problem_size import n, nq, d, c, u 
print 'Creating Data'
CreateData(n,nq,d,sys.argv[1],c,u)

