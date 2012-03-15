import random
import math 
from math import sqrt

def random_vector(size, mu, sigma):
	out = [random.gauss(mu,sigma) for j in range(size)]
	return out

def random_vector_centered(size, center, sigma):
	out = [center[j]+random.gauss(0,sigma) for j in range(size)]
	return out

def dot_product(a,b):
	dot = 0.0
	if len(a) != len(b):
		print 'Lengths dont match: ', len(a), len(b)
	for i in range(len(a)):
		dot += a[i]*b[i];
	return dot

def l2_distance(a,b):
	dist = 0.0
	for i in range(len(a)):
		dist += (a[i] - b[i])**2
	dist = sqrt(dist)
	return dist

def l2_norm(a):
	dist = 0.0
	for i in range(len(a)):
		dist += a[i]**2
	dist = sqrt(dist)
	return dist

def normalize(a):
	norm_value = l2_norm(a)
	for i in range(len(a)):
		a[i] = a[i]/norm_value
	return a
