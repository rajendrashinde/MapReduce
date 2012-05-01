# Experimental -- not intended to be used.
# Written only for laying out pseudocode of GenOffsetBuckets (LSH_functions) and MinHeap

class min_heap:
	def __init__(self, a, s):
		self.heap = [set([1])]
		self.min_score = s
		self.size = 1
		self.score = {}
		self.score[a] = s
		

	def add(self, a, s):
		self.size += 1
		self.heap[size] = a
		current = size
		while (self.score)

def score(A, sorted_order, x):
	score = 0.0 
	for i in A:
		score += x[sorted_order[i]]
	return score

def MySort(x):
	# returns list which contains 
	# elements in range(len(x)) sorted by corrseponding x values
	y = sort(range(len(x)), x)
	return y

def ToDelta(A, k):
	out = [0 for j in range(k)]
	for i in A:
		if i < k:
			out[i] += 1
		else:
			out[i] -= 1
	return out
	
def shift(A):
	pass

def expand(A):
	pass
		
def isValid(A, k):
	for i in A:
		if i > 2*k:
			return False
	for i in A:
		if 2*k+1 - i in A:
			return False
	return True		

def GetOffSetBuckets(hbucket, y, k, W):
	out = set(hbucket)
	hbucket = hbucket.strip().split(" ")
	y = y.strip().split(" ")
	hbucket_int = []
	for i in range(k):
		hbucket_int += [int(hbucket[i])]
		
		
	x = [0 for i in range(2*k)]
	for i in range(k):
		x[i] = float(y[i])
	for i in range(k,2k):
		x[i] = W - float(y[i-k])
	
	sorted_order = MySort(x) 
		
	A = set(0)
	minheap.insert(A, score(A, sorted_order, x))
# 	score should be hidden
# 	minheap.insert(A) -- internally inserts should happen according to score(A)

	for i in range(T):
		A = minheap.pop()
		If isValid(A, k):
			Delta = ToDelta(A, k)
			temp = [Hbucket_int[j] + Delta[j] for j in range(k)]
			out.add(" ".join(temp))
		minheap.add(shift(A))
		minheap.add(expand(A))	
