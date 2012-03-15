#/usr/bin/python
import sys
infinity = 10000000000
t_min = [infinity, infinity]
t_max = [0, 0]
t_sum = [0, 0]
count = 0
for line in sys.stdin:
	line = line.strip().split()
	data = float(line[0])
	query = float(line[1])
	if data < t_min[0]:
		t_min[0] = data
	if query < t_min[1]:
		t_min[1] = query
	if data > t_max[0]:
		t_max[0] = data
	if query > t_max[1]:
		t_max[1] = query
	t_sum[0] += data
	t_sum[1] += query
	count += 1
print "Data: Min: ", t_min[0], ", Max: ", t_max[0], ", Avg: ", t_sum[0]/count
print "Query: Min: ", t_min[1], ", Max: ", t_max[1], ", Avg: ", t_sum[1]/count
