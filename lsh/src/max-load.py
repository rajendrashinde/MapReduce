import sys
file = open(sys.argv[1])
t = [0, 0]
for line in file:
	line = line.split()
	t[0] = max(t[0], float(line[2]))
	t[1] = max(t[1], float(line[5]))
print t
