import sys
out = "output"
from problem_size import n, nq, d
for line in sys.stdin:
	line = line.rstrip().lstrip()
	line = line.split(" ")
	if len(line) == 4 and line[3] != '{':
		out += "_"
		out += line[1]
		out += line[3].rstrip(';')
out = out +  '_n' + str(n/1000000) +'M' + '_nq' + str(nq/1000000) + 'M' 
print out
