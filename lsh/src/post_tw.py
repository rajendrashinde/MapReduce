import tweepy
CONSUMER_KEY = 'cW6RzaPAv6BgoK1G26xh2A'
CONSUMER_SECRET = 'qof7TG0Erkpmt5qIpKUMV41ROT4ActDnygcRJwsg'
auth = tweepy.OAuthHandler(CONSUMER_KEY, CONSUMER_SECRET)
auth_url = auth.get_authorization_url()
print 'Please authorize: ' + auth_url
verifier = raw_input('PIN: ').strip()
auth.get_access_token(verifier)
print "ACCESS_KEY = '%s'" % auth.access_token.key
print "ACCESS_SECRET = '%s'" % auth.access_token.secret
