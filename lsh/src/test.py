file = open('twitter.txt')
s = ''
for line in file.readlines():
	line = line.split()
	#s = s + line + ", "
	print line
file.close() 
