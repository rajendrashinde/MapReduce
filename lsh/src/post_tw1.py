#!/usr/bin/python
import tweepy, sys

CONSUMER_KEY = 'cW6RzaPAv6BgoK1G26xh2A'
CONSUMER_SECRET = 'qof7TG0Erkpmt5qIpKUMV41ROT4ActDnygcRJwsg'
ACCESS_KEY = '421398377-dYt402eBt0LoDL9cazbjCYnFmTsqWIeobmOLVeZD'
ACCESS_SECRET = 'UpN6Avqf3IzycYfv6DJbOHtnfIiWCEogFahFLnd73A'
auth = tweepy.OAuthHandler(CONSUMER_KEY, CONSUMER_SECRET)
auth.set_access_token(ACCESS_KEY, ACCESS_SECRET)
api = tweepy.API(auth)
file = open('twitter.txt')
s = ''
for line in file.readlines():
	line = line.strip()
	s = s + line + ", "
file.close() 
print s
api.update_status(s)
