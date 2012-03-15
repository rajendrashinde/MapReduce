#!/usr/bin/env python
"""Usage: 

"""

import sys
	
for line in sys.stdin:
	line = line.strip('\n')  
	line = line.split()
	query_ID = line[0][6:]
	point_ID = line[3]
	print '%s\t%s' %(query_ID, point_ID)
	
